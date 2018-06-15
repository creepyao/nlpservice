package com.yao.dao;

import com.yao.entity.Rule;
import org.apache.ibatis.annotations.Param;

public interface RuleDao {
    public void add(Rule rule);
    public Rule getById(String id);
    public Rule getByUserAndRobot(@Param("userid")String userid, @Param("robotid")String robotid);
}
