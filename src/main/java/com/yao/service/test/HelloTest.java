package com.yao.service.test;


import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.log4j.Logger;
import org.junit.Test;

public class HelloTest {
    Logger logger =  Logger.getLogger(HelloTest.class);
    @Test
    public void test(){
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        String wsUrl = "http://127.0.0.1:8080/services/sayHello?wsdl";
        Client client = dcf.createClient(wsUrl);
        try {
            Object[] result  = client.invoke("sayHello", "yao");//调用webservice
            logger.debug((String)result[0]);
            System.out.println(result[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
