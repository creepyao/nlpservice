package com.yao.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.classification.classifiers.*;
import com.hankcs.hanlp.classification.models.LinearSVMModel;
import com.hankcs.hanlp.classification.models.NaiveBayesModel;
import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
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
    private StoryDao storyDao;
    @Resource
    private RuleDao ruleDao;
    private WordVectorModel wordVectorModel;
    private DocVectorModel docVectorModel;

    public NLPClassifierImpl(String w2v_model_path) {
        try {
            wordVectorModel = new WordVectorModel(w2v_model_path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.docVectorModel = new DocVectorModel(wordVectorModel);
    }





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
        //Native Bayes
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
        //SVW
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
        //W2V + Native Bayes
        if(train_flag == 3){
            story.setLearnflag(2);//开始训练
            storyDao.update(story);
            String model_path = model_base + "/" + rulename + ".ser";
            IClassifier classifier = new W2VNativeBayesClassifier(docVectorModel);  // 创建分类器
            classifier.train(corpus_base_folder);
            NaiveBayesModel model = (NaiveBayesModel) classifier.getModel();
            IOUtil.saveObjectTo(model, model_path);
            story.setLearnflag(3);//训练完毕
            storyDao.update(story);
        }
        //W2V + SVM
        if(train_flag == 4){
            story.setLearnflag(2);//开始训练
            storyDao.update(story);
            String model_path = model_base + "/" + rulename + ".ser";
            IClassifier classifier = new W2VLinearSVMClassifier(docVectorModel);  // 创建分类器
            classifier.train(corpus_base_folder);
            LinearSVMModel model = (LinearSVMModel) classifier.getModel();
            IOUtil.saveObjectTo(model, model_path);
            story.setLearnflag(3);//训练完毕
            storyDao.update(story);
        }

        return rulename + " 训练结束 ";
    }

    @Override
    public String predict(String userid, String robotid, String content){
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
        //W2V + Bayes
        if(story.getLearnflag()==3 && story.getTrainflag()==3){
            String model_path = model_base + "/" + rule.getRulename() + ".ser";
            NaiveBayesModel model = (NaiveBayesModel) IOUtil.readObjectFrom(model_path);
            IClassifier classifier = new W2VNativeBayesClassifier(model,docVectorModel);
            Map<String,Double> pre_map = classifier.predict(content);
            result = JSONArray.toJSONString(pre_map);
        }
        //W2V + SVM
        if(story.getLearnflag()==3 && story.getTrainflag()==4){
            String model_path = model_base + "/" + rule.getRulename() + ".ser";
            LinearSVMModel model = (LinearSVMModel) IOUtil.readObjectFrom(model_path);
            IClassifier classifier = new W2VLinearSVMClassifier(model,docVectorModel);
            Map<String,Double> pre_map = classifier.predict(content);
            result = JSONArray.toJSONString(pre_map);
        }
        return result;


    }

    public String getCorpus_base() {
        return corpus_base;
    }

    public void setCorpus_base(String corpus_base) {
        this.corpus_base = corpus_base;
    }

    public String getModel_base() {
        return model_base;
    }

    public void setModel_base(String model_base) {
        this.model_base = model_base;
    }

    public StoryDao getStoryDao() {
        return storyDao;
    }

    public void setStoryDao(StoryDao storyDao) {
        this.storyDao = storyDao;
    }

    public RuleDao getRuleDao() {
        return ruleDao;
    }

    public void setRuleDao(RuleDao ruleDao) {
        this.ruleDao = ruleDao;
    }

    public WordVectorModel getWordVectorModel() {
        return wordVectorModel;
    }

    public void setWordVectorModel(WordVectorModel wordVectorModel) {
        this.wordVectorModel = wordVectorModel;
    }

    public DocVectorModel getDocVectorModel() {
        return docVectorModel;
    }

    public void setDocVectorModel(DocVectorModel docVectorModel) {
        this.docVectorModel = docVectorModel;
    }

    public NLPClassifierImpl() {
    }
}


