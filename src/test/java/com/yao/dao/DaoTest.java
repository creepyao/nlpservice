package com.yao.dao;

import com.yao.entity.Rule;
import com.yao.entity.Story;
import com.yao.util.BaseJunit4Test;
import org.junit.Test;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

@WebAppConfiguration
public class DaoTest extends BaseJunit4Test {
    @Resource
    RuleDao ruleDao;
    @Resource
    StoryDao storyDao;
    @Test
    public void addrule(){
        Rule rule = new Rule();
        rule.setRulename("test1");
        rule.setRobotid("robot1");
        rule.setUserid("user1");
        rule.setCorpus("{class1:[text1,text2],class2:[text3,text4]}");
        ruleDao.add(rule);
    }
    @Test
    public void addstory(){
        Story story = new Story();
        story.setRobotid("robotid1");
        story.setUserid("userid1");
        story.setLearnflag(0);
        story.setTrainflag(0);

    }
}
