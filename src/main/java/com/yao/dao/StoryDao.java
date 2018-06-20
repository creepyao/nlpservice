package com.yao.dao;

import com.yao.entity.Story;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StoryDao {
    public void add(Story story);
    public Story getById(String id);
    public Story getByUserAndRobot(@Param("userid")String userid, @Param("robotid")String robotid);
    public List<Story> getByUserAndRobotAndTrainFlag(@Param("userid")String userid, @Param("robotid")String robotid,
                                                     @Param("train_flag")int train_flag);
    public List<Story> getByLearnFlag(@Param("learn_flag")int learn_flag);
    public void update(Story story);
}
