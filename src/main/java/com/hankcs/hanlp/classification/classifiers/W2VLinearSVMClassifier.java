package com.hankcs.hanlp.classification.classifiers;

import com.hankcs.hanlp.classification.corpus.Document;
import com.hankcs.hanlp.classification.corpus.IDataSet;
import com.hankcs.hanlp.classification.corpus.VectorDocument;
import com.hankcs.hanlp.classification.features.BaseFeatureData;
import com.hankcs.hanlp.classification.features.DfFeatureData;
import com.hankcs.hanlp.classification.features.IFeatureWeighter;
import com.hankcs.hanlp.classification.features.TfIdfFeatureWeighter;
import com.hankcs.hanlp.classification.models.AbstractModel;
import com.hankcs.hanlp.classification.models.LinearSVMModel;
import com.hankcs.hanlp.classification.tokenizers.ITokenizer;
import com.hankcs.hanlp.collection.trie.bintrie.BinTrie;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.Vector;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import de.bwaldvogel.liblinear.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class W2VLinearSVMClassifier extends AbstractClassifier{
    DocVectorModel docVectorModel;
    LinearSVMModel model;
    public W2VLinearSVMClassifier(DocVectorModel docVectorModel)
    {
        this.docVectorModel = docVectorModel;
    }

    public W2VLinearSVMClassifier(LinearSVMModel model,DocVectorModel docVectorModel) {
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
        VectorDocument vectorDocument = new VectorDocument();
        vectorDocument.setVector(docVectorModel.query(document.content).normalize());
        FeatureNode[] x = buildDocumentVector(vectorDocument);
        double[] probs = new double[model.svmModel.getNrClass()];
        Linear.predictProbability(model.svmModel, x, probs);
        return probs;
    }

    @Override
    public void train(IDataSet dataSet) throws IllegalArgumentException {
        if (dataSet.size() == 0) throw new IllegalArgumentException("训练数据集为空,无法继续训练");
        List<VectorDocument> vectorDocuments = new ArrayList<VectorDocument>();
        Iterator<Document> iterator = dataSet.iterator();
        while (iterator.hasNext()){
            Document doc = iterator.next();
            VectorDocument vectorDocument = new VectorDocument();
            vectorDocument.setContent(doc.content);
            vectorDocument.setCategory(doc.category);
            vectorDocument.setVector(docVectorModel.query(doc.content).normalize());
            vectorDocuments.add(vectorDocument);
        }
        // 构造SVM问题
        Problem problem = createLiblinearProblem(vectorDocuments);
        // 释放内存

        ITokenizer tokenizer = dataSet.getTokenizer();
        String[] catalog = dataSet.getCatalog().toArray();
        vectorDocuments = null;
        dataSet = null;
        System.gc();
        // 求解SVM问题
        Model svmModel = solveLibLinearProblem(problem);
        // 将有用的数据留下来
        model = new LinearSVMModel();
        model.tokenizer = tokenizer;
        model.catalog = catalog;
        model.svmModel = svmModel;
    }

    @Override
    public AbstractModel getModel() {
        return this.model;
    }

    private Problem createLiblinearProblem(List<VectorDocument> vectorDocuments)
    {
        Problem problem = new Problem();
        int n = vectorDocuments.size();
        problem.l = n;//训练样本数
        problem.n = docVectorModel.dimension();
        problem.x = new FeatureNode[n][];//特征数据
        problem.y = new double[n];  // 最新版liblinear的y数组是浮点数 类别
        Iterator<VectorDocument> iterator = vectorDocuments.iterator();
        for (int i = 0; i < n; i++)
        {
            // 构造文档向量
            VectorDocument document = iterator.next();
            problem.x[i] = buildDocumentVector(document);
            // 设置样本的y值
            problem.y[i] = document.category;
        }

        return problem;
    }

    private FeatureNode[] buildDocumentVector(VectorDocument document)
    {
        int termCount = document.vector.size();  // 词的个数
        FeatureNode[] x = new FeatureNode[termCount];
        for(int i=0;i < termCount;i++){
            x[i] = new FeatureNode(i + 1,  // liblinear 要求下标从1开始递增
                    document.vector.elementArray[i]);
        }

        return x;
    }

    private Model solveLibLinearProblem(Problem problem)
    {
        Parameter lparam = new Parameter(SolverType.L1R_LR,
//                                                                 grid.find_parameters(problem, 500, 505, 1),
                500.,
                0.01);
        return Linear.train(problem, lparam);
    }

    public DocVectorModel getDocVectorModel() {
        return docVectorModel;
    }

    public void setDocVectorModel(DocVectorModel docVectorModel) {
        this.docVectorModel = docVectorModel;
    }

    public void setModel(LinearSVMModel model) {
        this.model = model;
    }
}
