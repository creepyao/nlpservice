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
    private int learnflg;
    /*
    0 1 2
    1朴素贝叶斯
    2 SVM
     */
    private int tranflag;

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

    public int getLearnflg() {
        return learnflg;
    }

    public void setLearnflg(int learnflg) {
        this.learnflg = learnflg;
    }

    public int getTranflag() {
        return tranflag;
    }

    public void setTranflag(int tranflag) {
        this.tranflag = tranflag;
    }
}
