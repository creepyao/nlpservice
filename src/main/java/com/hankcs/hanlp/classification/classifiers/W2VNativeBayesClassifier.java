package com.hankcs.hanlp.classification.classifiers;

import com.hankcs.hanlp.classification.corpus.Document;
import com.hankcs.hanlp.classification.corpus.IDataSet;
import com.hankcs.hanlp.classification.corpus.VectorDocument;
import com.hankcs.hanlp.classification.features.BaseFeatureData;
import com.hankcs.hanlp.classification.models.AbstractModel;
import com.hankcs.hanlp.classification.models.LinearSVMModel;
import com.hankcs.hanlp.classification.models.NaiveBayesModel;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.utility.MathUtility;

import java.util.*;

import static com.hankcs.hanlp.classification.utilities.io.ConsoleLogger.logger;

public class W2VNativeBayesClassifier extends AbstractClassifier{
    DocVectorModel docVectorModel;
    private NaiveBayesModel model;

    public W2VNativeBayesClassifier(DocVectorModel docVectorModel)
    {
        this.docVectorModel = docVectorModel;
    }

    public W2VNativeBayesClassifier(NaiveBayesModel model, DocVectorModel docVectorModel) {
        this.docVectorModel = docVectorModel;
        this.model = model;
    }

    @Override
    public Map<String, Double> predict(String text) throws IllegalArgumentException, IllegalStateException {
        if (model == null)
        {
            throw new IllegalStateException("未训练模型！无法执行预测！");
        }
        if (text == null)
        {
            throw new IllegalArgumentException("参数 text == null");
        }

        //分词，创建文档
        Document document = new Document(model.wordIdTrie, model.tokenizer.segment(text),text);

        return predict(document);
    }


    @Override
    public double[] categorize(Document document) throws IllegalArgumentException, IllegalStateException {
        Integer category;
        Integer feature;
        Integer occurrences;
        Double logprob;



        double[] predictionScores = new double[model.catalog.length];
        for (Map.Entry<Integer, Double> entry1 : model.logPriors.entrySet())
        {
            category = entry1.getKey();
            logprob = entry1.getValue(); //用类目的对数似然初始化概率
            VectorDocument vectorDocument = new VectorDocument();
            vectorDocument.setContent(document.content);
            vectorDocument.setCategory(document.category);
            vectorDocument.setVector(docVectorModel.query(document.content));
            //对文档中的每个特征
            for (feature=0;feature<docVectorModel.dimension();feature++)
            {

                if (!model.logLikelihoods.containsKey(feature))
                {
                    continue; //如果在模型中找不到就跳过了
                }

                occurrences = (int)vectorDocument.vector.elementArray[feature]; //获取其在文档中的频次

                logprob += occurrences * model.logLikelihoods.get(feature).get(category); //将对数似然乘上频次
            }

            predictionScores[category] = logprob;
        }

        if (configProbabilityEnabled) MathUtility.normalizeExp(predictionScores);
        return predictionScores;
    }
    public int sumCatlog(List<VectorDocument> vectorDocuments,int cat){
        int sum = 0;
        for(int i=0; i< vectorDocuments.size();i++){
            if(vectorDocuments.get(i).category == cat){
                sum++;
            }
        }
        return sum;
    }
    public double featureCategoryJointCount(List<VectorDocument> vectorDocuments,int feature,int cat){
        double sum = 0.0;
        for(int i=0; i< vectorDocuments.size();i++){
            if(vectorDocuments.get(i).category == cat){
                sum = sum + vectorDocuments.get(i).vector.elementArray[feature];
            }
        }
        return sum;
    }
    public int[] featureCategoryCounts(List<VectorDocument> vectorDocuments,int feature) {
        int[] result = new int[model.c];
        for (int cat = 0; cat < model.c; cat++) {
            for (int doc = 0; doc < vectorDocuments.size(); doc++) {
                if (vectorDocuments.get(doc).category == cat) {
                    result[cat] = result[cat] + (int)vectorDocuments.get(doc).vector.elementArray[feature];
                }
            }
        }
        return result;
    }

    @Override
    public void train(IDataSet dataSet) throws IllegalArgumentException {
        logger.out("原始数据集大小:%d\n", dataSet.size());
        List<VectorDocument> vectorDocuments = new ArrayList<VectorDocument>();
        Iterator<Document> iterator = dataSet.iterator();
        while (iterator.hasNext()){
            Document doc = iterator.next();
            VectorDocument vectorDocument = new VectorDocument();
            vectorDocument.setContent(doc.content);
            vectorDocument.setCategory(doc.category);
            vectorDocument.setVector(docVectorModel.query(doc.content));
            vectorDocuments.add(vectorDocument);
        }

        //初始化分类器所用的数据
        model = new NaiveBayesModel();
        model.n = vectorDocuments.size(); //样本数量
        model.d = docVectorModel.dimension();//特征数量
        model.c = dataSet.getCatalog().size(); //类目数量
        model.logPriors = new TreeMap<Integer, Double>();

        int sumCategory = 0;
        for (int category = 0; category < dataSet.getCatalog().size(); category++)
        {
            sumCategory = sumCatlog(vectorDocuments,category);
            model.logPriors.put(category, Math.log((double) sumCategory / model.n));
        }

        //拉普拉斯平滑处理（又称加一平滑），这时需要估计每个类目下的实例
        Map<Integer, Double> featureOccurrencesInCategory = new TreeMap<Integer, Double>();

        Double featureOccSum;
        for (Integer category : model.logPriors.keySet())
        {
            featureOccSum = 0.0;
            for (int feature = 0; feature < docVectorModel.dimension(); feature++)
            {

                featureOccSum += featureCategoryJointCount(vectorDocuments,category,feature);
            }
            featureOccurrencesInCategory.put(category, featureOccSum);
        }

        //对数似然估计
        int count;
        int[] featureCategoryCounts;
        double logLikelihood;
        for (Integer category : model.logPriors.keySet())
        {
            for (int feature = 0; feature < docVectorModel.dimension(); feature++)
            {

                featureCategoryCounts = featureCategoryCounts(vectorDocuments,feature);

                count = featureCategoryCounts[category];

                logLikelihood = Math.log((count + 1.0) / (featureOccurrencesInCategory.get(category) + model.d));
                if (!model.logLikelihoods.containsKey(feature))
                {
                    model.logLikelihoods.put(feature, new TreeMap<Integer, Double>());
                }
                model.logLikelihoods.get(feature).put(category, logLikelihood);
            }
        }
        logger.out("贝叶斯统计结束\n");
        model.catalog = dataSet.getCatalog().toArray();
        model.tokenizer = dataSet.getTokenizer();
        model.wordIdTrie = null;
    }

    @Override
    public AbstractModel getModel() {
        return model;
    }
}
