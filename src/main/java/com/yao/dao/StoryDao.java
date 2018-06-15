package com.yao.dao;

import com.yao.entity.Story;
import org.apache.ibatis.annotations.Param;

public interface StoryDao {
    public void add(Story story);
    public Story getById(String id);
    public Story getByUserAndRobot(@Param("userid")String userid, @Param("robotid")String robotid);
    public void update(Story story);
}
