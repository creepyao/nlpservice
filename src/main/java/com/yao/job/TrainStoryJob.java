package com.yao.job;

import com.hankcs.hanlp.classification.classifiers.*;
import com.hankcs.hanlp.classification.models.LinearSVMModel;
import com.hankcs.hanlp.classification.models.NaiveBayesModel;
import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.yao.dao.RuleDao;
import com.yao.dao.StoryDao;
import com.yao.entity.Story;
import com.yao.util.CorpusUtil;
import com.yao.util.DateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

public class TrainStoryJob {
    private Logger logger = Logger.getLogger(TrainStoryJob.class);
    @Value("#{prop['corpus_base']}")
    private String corpus_base;
    @Value("#{prop['model_base']}")
    private String model_base;
    @Value("#{prop['w2v_model']}")
    private String w2v_model;
    @Resource
    private StoryDao storyDao;


    public void runTask() throws IOException {
        //log.info("===========runTask()");
        List<Story> stories = storyDao.getByLearnFlag(0);
        WordVectorModel quartz_wordVectorModel = new WordVectorModel(w2v_model);
        DocVectorModel quartz_docVectorModel = new DocVectorModel(quartz_wordVectorModel);
        for (Story story: stories) {
            String rulename = story.getUserid() + "_" + story.getRobotid() + "_"
                    + DateUtil.DataToString(story.getTrain_time(),"yyyyMMddHHmmSS");
            String corpus_base_folder = corpus_base + "/" + rulename + "/";
            story.setLearnflag(1);//提取语料
            storyDao.update(story);
            //Native Bayes
            if(story.getTrainflag() == 1){
                story.setLearnflag(2);//开始训练
                logger.debug(rulename + " 开始训练");
                storyDao.update(story);
                String model_path = model_base + "/" + rulename + ".ser";
                IClassifier classifier = new NaiveBayesClassifier();  // 创建分类器
                classifier.train(corpus_base_folder);
                NaiveBayesModel model = (NaiveBayesModel) classifier.getModel();
                IOUtil.saveObjectTo(model, model_path);
                story.setModel_path(model_path);
                story.setLearnflag(3);//训练完毕
                storyDao.update(story);
                logger.debug(rulename + " 训练结束");
            }
            //SVW
            if(story.getTrainflag() == 2){
                story.setLearnflag(2);//开始训练
                logger.debug(rulename + " 开始训练");
                storyDao.update(story);
                String model_path = model_base + "/" + rulename + ".ser";
                IClassifier classifier = new LinearSVMClassifier();  // 创建分类器
                classifier.train(corpus_base_folder);
                LinearSVMModel model = (LinearSVMModel) classifier.getModel();
                IOUtil.saveObjectTo(model, model_path);
                story.setModel_path(model_path);
                story.setLearnflag(3);//训练完毕
                storyDao.update(story);
                logger.debug(rulename + " 训练结束");
            }
            //W2V + Native Bayes
            if(story.getTrainflag() == 3){
                story.setLearnflag(2);//开始训练
                logger.debug(rulename + " 开始训练");
                storyDao.update(story);
                String model_path = model_base + "/" + rulename + ".ser";
                IClassifier classifier = new W2VNativeBayesClassifier(quartz_docVectorModel);  // 创建分类器
                classifier.train(corpus_base_folder);
                NaiveBayesModel model = (NaiveBayesModel) classifier.getModel();
                IOUtil.saveObjectTo(model, model_path);
                story.setModel_path(model_path);
                story.setLearnflag(3);//训练完毕
                storyDao.update(story);
                logger.debug(rulename + " 训练结束");
            }
            //W2V + SVM
            if(story.getTrainflag() == 4){
                story.setLearnflag(2);//开始训练
                logger.debug(rulename + " 开始训练");
                storyDao.update(story);
                String model_path = model_base + "/" + rulename + ".ser";
                IClassifier classifier = new W2VLinearSVMClassifier(quartz_docVectorModel);  // 创建分类器
                classifier.train(corpus_base_folder);
                LinearSVMModel model = (LinearSVMModel) classifier.getModel();
                IOUtil.saveObjectTo(model, model_path);
                story.setModel_path(model_path);
                story.setLearnflag(3);//训练完毕
                storyDao.update(story);
                logger.debug(rulename + " 训练结束");
            }
        }
    }
}
