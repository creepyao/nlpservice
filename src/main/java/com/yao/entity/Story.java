package com.yao.entity;

public class Story {
    private String id;
    private String userid;
    private String robotid;
    /*
    0 1 2 3
    1提取语料
    2开始训练
    3训练完毕
     */
    private int learnflag;
    /*
    0 1 2
    1朴素贝叶斯
    2 SVM
    3朴素贝叶斯 + 词向量
    4 SVM + 词向量
     */
    private int trainflag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRobotid() {
        return robotid;
    }

    public void setRobotid(String robotid) {
        this.robotid = robotid;
    }

    public int getLearnflag() {
        return learnflag;
    }

    public void setLearnflag(int learnflag) {
        this.learnflag = learnflag;
    }

    public int getTrainflag() {
        return trainflag;
    }

    public void setTrainflag(int trainflag) {
        this.trainflag = trainflag;
    }
}
