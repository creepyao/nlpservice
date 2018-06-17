package com.yao.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.classification.classifiers.IClassifier;
import com.hankcs.hanlp.classification.classifiers.LinearSVMClassifier;
import com.hankcs.hanlp.classification.classifiers.NaiveBayesClassifier;
import com.hankcs.hanlp.classification.models.LinearSVMModel;
import com.hankcs.hanlp.classification.models.NaiveBayesModel;
import com.hankcs.hanlp.corpus.io.IOUtil;
import com.yao.dao.RuleDao;
import com.yao.dao.StoryDao;
import com.yao.entity.Rule;
import com.yao.entity.Story;
import com.yao.service.INLPClassifier;
import com.yao.util.CorpusUtil;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import javax.jws.WebService;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.hankcs.hanlp.utility.Predefine.logger;
@WebService(serviceName="nlpClassifier",endpointInterface="com.yao.service.INLPClassifier",targetNamespace = "http://service.yao.com/")
public class NLPClassifierImpl implements INLPClassifier {
    @Value("#{prop['corpus_base']}")
    private String corpus_base;
    @Value("#{prop['model_base']}")
    private String model_base;
    @Resource
    StoryDao storyDao;
    @Resource
    RuleDao ruleDao;
    @Override
    public String train(String userid, String robotid, String rulename,String corpus, int train_flag) throws IOException {
        Story story = storyDao.getByUserAndRobot(userid,robotid);
        if(story == null){
            story = new Story();
            story.setUserid(userid);
            story.setRobotid(robotid);
            story.setTrainflag(train_flag);
            story.setLearnflag(0);
            storyDao.add(story);
        }else{
            return "story 已经存在";
        }

        Rule rule = ruleDao.getByUserAndRobot(userid,robotid);
        if(rule == null){
            rule = new Rule();
            rule.setCorpus(corpus);
            rule.setUserid(userid);
            rule.setRobotid(robotid);
            rule.setRulename(rulename);
            ruleDao.add(rule);
        }else{
            return "rule 已经存在";
        }
        String corpus_base_folder = corpus_base + "/" + rulename + "/";
        story.setLearnflag(1);//提取语料
        storyDao.update(story);
        CorpusUtil.parse(corpus,corpus_base_folder);
        if(train_flag == 1){
            story.setLearnflag(2);//开始训练
            storyDao.update(story);
            String model_path = model_base + "/" + rulename + ".ser";
            IClassifier classifier = new NaiveBayesClassifier();  // 创建分类器
            classifier.train(corpus_base_folder);
            NaiveBayesModel model = (NaiveBayesModel) classifier.getModel();
            IOUtil.saveObjectTo(model, model_path);
            story.setLearnflag(3);//训练完毕
            storyDao.update(story);
        }

        if(train_flag == 2){
            story.setLearnflag(2);//开始训练
            storyDao.update(story);
            String model_path = model_base + "/" + rulename + ".ser";
            IClassifier classifier = new LinearSVMClassifier();  // 创建分类器
            classifier.train(corpus_base_folder);
            LinearSVMModel model = (LinearSVMModel) classifier.getModel();
            IOUtil.saveObjectTo(model, model_path);
            story.setLearnflag(3);//训练完毕
            storyDao.update(story);
        }

        return rulename + " 训练结束 ";
    }

    @Override
    public String predict(String userid, String robotid, String content) {
        String result = null;
        Rule rule = ruleDao.getByUserAndRobot(userid, robotid);
        Story story = storyDao.getByUserAndRobot(userid, robotid);
        //Bayes
        if(story.getLearnflag()==3 && story.getTrainflag()==1){
            String model_path = model_base + "/" + rule.getRulename() + ".ser";
            NaiveBayesModel model = (NaiveBayesModel) IOUtil.readObjectFrom(model_path);
            IClassifier classifier = new NaiveBayesClassifier(model);
            Map<String,Double> pre_map = classifier.predict(content);
            result = JSONArray.toJSONString(pre_map);
        }
        //普通SVM
        if(story.getLearnflag()==3 && story.getTrainflag()==2){
            String model_path = model_base + "/" + rule.getRulename() + ".ser";
            LinearSVMModel model = (LinearSVMModel) IOUtil.readObjectFrom(model_path);
            IClassifier classifier = new LinearSVMClassifier(model);
            Map<String,Double> pre_map = classifier.predict(content);
            result = JSONArray.toJSONString(pre_map);
        }
        return result;
    }
}


