package com.yao.util;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by wpy on 2017/11/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration({"/spring.xml","/spring-mybatis.xml"}) //加载配置文件
public class BaseJunit4Test {

}
