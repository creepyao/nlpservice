package com.yao.service;


import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;


public class NLPClassifierTest {
    Logger logger = Logger.getLogger(NLPClassifierTest.class);
    @Test
    public void addSVM() throws IOException {
        String corpus = "{'汽车':" +
                "['我国国家最新标准《汽车和挂车类型的术语和定义》(GB/T 3730．1—2001)中对汽车有如下定义：由动力驱动，具有4个或4个以上车轮的非轨道承载的车辆，主要用于：载运人员和（或）货物；牵引载运人员和(或)货物的车辆；特殊用途。'" +
                ",'商用车在设计和技术特性上用于运送人员和货物，并且可以牵引挂车，但乘用车不包括在内。主要有：客车、半挂牵引车、货车。']" +
                ",'手机':" +
                "['移动电话，或称为无线电话，通常称为手机，原本只是一种通讯工具，早期又有大哥大的俗称 [1]  ，是可以在较广范围内使用的便携式电话终端，最早是由美国贝尔实验室在1940年制造的战地移动电话机发展而来。'" +
                ",'触屏手机是现代手机市场的潮流，触屏手机分为电阻屏和电容屏手机，是指利用触摸屏的技术，将该技术应用到手机屏幕上面的一种手机类型。触屏手机和其他的手机分类没有明显的界限，最大的特点在于它那超大的屏幕，可以使用者带来视觉的享受，无论从文字还是图像方面都体现出大屏幕的特色。但是由于屏幕大，体积也就比较大，对于携带触屏手机占用的空间也大了。 同时触屏手机可以用手指操纵，完美的替代键盘。']" +
                "}";
        String userid = "win测试用户_SVM";
        String robotid = "win测试机器人_SVM";
        String rulename = "win测试训练_SVM";
        int train_flag = 2;
        /*
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        String wsUrl = "http://127.0.0.1:8080/services/nlpClassifier?wsdl";
        Client client = dcf.createClient(wsUrl);
        try {
            Object[] result  = client.invoke("train", userid,robotid,rulename,corpus,learn_flag);//调用webservice
            logger.debug((String)result[0]);
            System.out.println(result[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        JaxWsProxyFactoryBean jfb = new JaxWsProxyFactoryBean();
        jfb.setServiceClass(INLPClassifier.class);
        jfb.setAddress("http://127.0.0.1:8080/services/nlpClassifier?wsdl");
        INLPClassifier vs = (INLPClassifier) jfb.create();
        String pre_json = vs.train(userid,robotid,corpus,train_flag);
        logger.debug(pre_json);
        System.out.println(pre_json);
    }

    @Test
    /*
    {"手机":0.9940176076716231,"汽车":0.005982392328376928}
     */
    public void predictSVM(){
        String userid = "win测试用户_SVM";
        String robotid = "win测试机器人_SVM";
        int train_flag = 2;
        String content = "苏宁易购购买手机,正品行货,超低价格,618元手机券整点开抢";

        JaxWsProxyFactoryBean jfb = new JaxWsProxyFactoryBean();
        jfb.setServiceClass(INLPClassifier.class);
        jfb.setAddress("http://127.0.0.1:8080/services/nlpClassifier?wsdl");
        INLPClassifier vs = (INLPClassifier) jfb.create();
        String pre_json = vs.predict(userid,robotid,train_flag,content);
        logger.debug(pre_json);
        System.out.println(pre_json);
    }

    @Test
    public void addBayes(){
        String corpus = "{'汽车':" +
                "['我国国家最新标准《汽车和挂车类型的术语和定义》(GB/T 3730．1—2001)中对汽车有如下定义：由动力驱动，具有4个或4个以上车轮的非轨道承载的车辆，主要用于：载运人员和（或）货物；牵引载运人员和(或)货物的车辆；特殊用途。'" +
                ",'商用车在设计和技术特性上用于运送人员和货物，并且可以牵引挂车，但乘用车不包括在内。主要有：客车、半挂牵引车、货车。']" +
                ",'手机':" +
                "['移动电话，或称为无线电话，通常称为手机，原本只是一种通讯工具，早期又有大哥大的俗称 [1]  ，是可以在较广范围内使用的便携式电话终端，最早是由美国贝尔实验室在1940年制造的战地移动电话机发展而来。'" +
                ",'触屏手机是现代手机市场的潮流，触屏手机分为电阻屏和电容屏手机，是指利用触摸屏的技术，将该技术应用到手机屏幕上面的一种手机类型。触屏手机和其他的手机分类没有明显的界限，最大的特点在于它那超大的屏幕，可以使用者带来视觉的享受，无论从文字还是图像方面都体现出大屏幕的特色。但是由于屏幕大，体积也就比较大，对于携带触屏手机占用的空间也大了。 同时触屏手机可以用手指操纵，完美的替代键盘。']" +
                "}";
        String userid = "win测试用户_Bayes";
        String robotid = "win测试机器人_Bayes";
        int train_flag = 1;
        JaxWsProxyFactoryBean jfb = new JaxWsProxyFactoryBean();
        jfb.setServiceClass(INLPClassifier.class);
        jfb.setAddress("http://127.0.0.1:8080/services/nlpClassifier?wsdl");
        INLPClassifier vs = (INLPClassifier) jfb.create();
        String result_json = null;
        try {
            result_json = vs.train(userid,robotid,corpus,train_flag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug(result_json);
        System.out.println(result_json);
    }
    @Test
    /*
    恒定不变
    {"手机":0.8672131147540985,"汽车":0.1327868852459016}
     */
    public void predictBayes(){
        String userid = "win测试用户_Bayes";
        String robotid = "win测试机器人_Bayes";
        String content = "win苏宁易购购买手机,正品行货,超低价格,618元手机券整点开抢";

        JaxWsProxyFactoryBean jfb = new JaxWsProxyFactoryBean();
        jfb.setServiceClass(INLPClassifier.class);
        jfb.setAddress("http://127.0.0.1:8080/services/nlpClassifier?wsdl");
        INLPClassifier vs = (INLPClassifier) jfb.create();
        String pre_json = vs.predict(userid,robotid,1,content);
        logger.debug(pre_json);
        System.out.println(pre_json);
    }
    @Test
    public void addW2VSVM(){
        String corpus = "{'汽车':" +
                "['我国国家最新标准《汽车和挂车类型的术语和定义》(GB/T 3730．1—2001)中对汽车有如下定义：由动力驱动，具有4个或4个以上车轮的非轨道承载的车辆，主要用于：载运人员和（或）货物；牵引载运人员和(或)货物的车辆；特殊用途。'" +
                ",'商用车在设计和技术特性上用于运送人员和货物，并且可以牵引挂车，但乘用车不包括在内。主要有：客车、半挂牵引车、货车。']" +
                ",'手机':" +
                "['移动电话，或称为无线电话，通常称为手机，原本只是一种通讯工具，早期又有大哥大的俗称 [1]  ，是可以在较广范围内使用的便携式电话终端，最早是由美国贝尔实验室在1940年制造的战地移动电话机发展而来。'" +
                ",'触屏手机是现代手机市场的潮流，触屏手机分为电阻屏和电容屏手机，是指利用触摸屏的技术，将该技术应用到手机屏幕上面的一种手机类型。触屏手机和其他的手机分类没有明显的界限，最大的特点在于它那超大的屏幕，可以使用者带来视觉的享受，无论从文字还是图像方面都体现出大屏幕的特色。但是由于屏幕大，体积也就比较大，对于携带触屏手机占用的空间也大了。 同时触屏手机可以用手指操纵，完美的替代键盘。']" +
                "}";
        String userid = "win测试用户_W2VSVM";
        String robotid = "win测试机器人_W2VSVM";
        String rulename = "win测试训练_W2VSVM";
        int train_flag = 4;
        JaxWsProxyFactoryBean jfb = new JaxWsProxyFactoryBean();
        jfb.setServiceClass(INLPClassifier.class);
        jfb.setAddress("http://127.0.0.1:8080/services/nlpClassifier?wsdl");
        INLPClassifier vs = (INLPClassifier) jfb.create();
        String result_json = null;
        try {
            result_json = vs.train(userid,robotid,corpus,train_flag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug(result_json);
        System.out.println(result_json);
    }

    @Test
    /*
    {"手机":0.9807810649126548,"汽车":0.01921893508734516}
     */
    public void predictW2VSVM(){
        String userid = "win测试用户_W2VSVM";
        String robotid = "win测试机器人_W2VSVM";
        String content = "苏宁易购购买手机,正品行货,超低价格,618元手机券整点开抢";

        JaxWsProxyFactoryBean jfb = new JaxWsProxyFactoryBean();
        jfb.setServiceClass(INLPClassifier.class);
        jfb.setAddress("http://127.0.0.1:8080/services/nlpClassifier?wsdl");
        INLPClassifier vs = (INLPClassifier) jfb.create();
        String pre_json = vs.predict(userid,robotid,4,content);
        logger.debug(pre_json);
        System.out.println(pre_json);
    }

    @Test
    public void addW2VBayes(){
        String corpus = "{'汽车':" +
                "['我国国家最新标准《汽车和挂车类型的术语和定义》(GB/T 3730．1—2001)中对汽车有如下定义：由动力驱动，具有4个或4个以上车轮的非轨道承载的车辆，主要用于：载运人员和（或）货物；牵引载运人员和(或)货物的车辆；特殊用途。'" +
                ",'商用车在设计和技术特性上用于运送人员和货物，并且可以牵引挂车，但乘用车不包括在内。主要有：客车、半挂牵引车、货车。']" +
                ",'手机':" +
                "['移动电话，或称为无线电话，通常称为手机，原本只是一种通讯工具，早期又有大哥大的俗称 [1]  ，是可以在较广范围内使用的便携式电话终端，最早是由美国贝尔实验室在1940年制造的战地移动电话机发展而来。'" +
                ",'触屏手机是现代手机市场的潮流，触屏手机分为电阻屏和电容屏手机，是指利用触摸屏的技术，将该技术应用到手机屏幕上面的一种手机类型。触屏手机和其他的手机分类没有明显的界限，最大的特点在于它那超大的屏幕，可以使用者带来视觉的享受，无论从文字还是图像方面都体现出大屏幕的特色。但是由于屏幕大，体积也就比较大，对于携带触屏手机占用的空间也大了。 同时触屏手机可以用手指操纵，完美的替代键盘。']" +
                "}";
        String userid = "win测试用户_W2VBayes";
        String robotid = "win测试机器人_W2VBayes";
        String rulename = "win测试训练_W2VBayes";
        int train_flag = 3;
        JaxWsProxyFactoryBean jfb = new JaxWsProxyFactoryBean();
        jfb.setServiceClass(INLPClassifier.class);
        jfb.setAddress("http://127.0.0.1:8080/services/nlpClassifier?wsdl");
        INLPClassifier vs = (INLPClassifier) jfb.create();
        String result_json = null;
        try {
            result_json = vs.train(userid,robotid,corpus,train_flag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug(result_json);
        System.out.println(result_json);
    }
    @Test
    /*
    {"手机":0.5,"汽车":0.5}
     */
    public void predictW2VBayes(){
        String userid = "win测试用户_W2VBayes";
        String robotid = "win测试机器人_W2VBayes";
        String content = "win苏宁易购购买手机,正品行货,超低价格,618元手机券整点开抢";

        JaxWsProxyFactoryBean jfb = new JaxWsProxyFactoryBean();
        jfb.setServiceClass(INLPClassifier.class);
        jfb.setAddress("http://127.0.0.1:8080/services/nlpClassifier?wsdl");
        INLPClassifier vs = (INLPClassifier) jfb.create();
        String pre_json = vs.predict(userid,robotid,3,content);
        logger.debug(pre_json);
        System.out.println(pre_json);
    }
}
