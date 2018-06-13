package com.yao.service;

import javax.jws.WebService;

@WebService
public interface IHelloWorld {
    public String sayHello(String userName);
}
