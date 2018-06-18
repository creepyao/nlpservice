package com.yao.nlp;

import com.hankcs.hanlp.classification.classifiers.IClassifier;
import com.hankcs.hanlp.classification.classifiers.W2VLinearSVMClassifier;
import com.hankcs.hanlp.classification.classifiers.W2VNativeBayesClassifier;
import com.hankcs.hanlp.classification.models.LinearSVMModel;
import com.hankcs.hanlp.classification.models.NaiveBayesModel;
import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;

import java.io.File;
import java.io.IOException;

public class W2VNativeBayesClassifierTest {
    public static final String CORPUS_FOLDER = "data/测试训练2";
    /**
     * 模型保存路径
     */
    public static final String MODEL_PATH = "data/测试训练2_W2V_NB.ser";
    private static final String MODEL_W2V_FILE_NAME = "data/hanlp-wiki-vec-zh.txt";

    public static void main(String[] args) throws IOException
    {
        WordVectorModel wordVectorModel = new WordVectorModel( "data/hanlp-wiki-vec-zh.txt");
        // 文档向量
        DocVectorModel docVectorModel = new DocVectorModel(wordVectorModel);
        IClassifier classifier = new W2VNativeBayesClassifier(trainOrLoadModel(docVectorModel),docVectorModel);
        predict(classifier, "太平洋汽车网下设汽车报价,汽车评测以及新闻");
        predict(classifier, "苏宁易购购买手机,正品行货,超低价格,618元手机券整点开抢");
    }

    private static void predict(IClassifier classifier, String text)
    {
        System.out.printf("《%s》 属于分类 【%s】\n", text, classifier.classify(text));
    }

    private static NaiveBayesModel trainOrLoadModel(DocVectorModel docVectorModel) throws IOException
    {
        NaiveBayesModel model = (NaiveBayesModel) IOUtil.readObjectFrom(MODEL_PATH);
        if (model != null) return model;

        File corpusFolder = new File(CORPUS_FOLDER);
        if (!corpusFolder.exists() || !corpusFolder.isDirectory())
        {
            System.err.println("没有文本分类语料，请阅读IClassifier.train(java.lang.String)中定义的语料格式与语料下载：" +
                    "https://github.com/hankcs/HanLP/wiki/%E6%96%87%E6%9C%AC%E5%88%86%E7%B1%BB%E4%B8%8E%E6%83%85%E6%84%9F%E5%88%86%E6%9E%90");
            System.exit(1);
        }

        IClassifier classifier = new W2VNativeBayesClassifier(docVectorModel);  // 创建分类器，更高级的功能请参考IClassifier的接口定义
        classifier.train(CORPUS_FOLDER);                     // 训练后的模型支持持久化，下次就不必训练了
        model = (NaiveBayesModel) classifier.getModel();
        IOUtil.saveObjectTo(model, MODEL_PATH);
        return model;
    }
}
