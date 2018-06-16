package com.yao.dao;

import com.yao.entity.Rule;

public interface RuleDao {
    public void add(Rule rule);
    public Rule getById(String id);
}
