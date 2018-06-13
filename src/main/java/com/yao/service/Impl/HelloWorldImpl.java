package com.yao.service.Impl;


import com.yao.service.IHelloWorld;

import javax.jws.WebService;

@WebService(serviceName="sayHello",endpointInterface="com.yao.service.IHelloWorld",targetNamespace = "http://service.yao.com/")
public class HelloWorldImpl implements IHelloWorld {
    @Override
    public String sayHello(String userName) {
        return "Hello " + userName + " web service!!!";
    }

}