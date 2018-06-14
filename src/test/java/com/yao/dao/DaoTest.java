package com.yao.dao;

import com.yao.entity.Rule;
import com.yao.util.BaseJunit4Test;
import org.junit.Test;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

@WebAppConfiguration
public class DaoTest extends BaseJunit4Test {
    @Resource
    RuleDao ruleDao;
    @Test
    public void addrule(){
        Rule rule = new Rule();
        rule.setRulename("test1");
        rule.setRobotid("robot1");
        rule.setUserid("user1");
        rule.setCorpus("{class1:[text1,text2],class2:[text3,text4]}");
        ruleDao.add(rule);
    }
}
