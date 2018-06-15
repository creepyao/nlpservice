package com.yao.dao;

import com.yao.entity.Story;

public interface StoryDao {
    public void add(Story story);
    public Story getById(String id);
}
