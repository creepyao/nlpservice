package com.yao.entity;

import java.util.Date;

public class Rule {
    private String ruleid;
    private String robotid;
    private String rulename;
    private String corpus;
    private String userid;
    private Date insert_time;

    public Date getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(Date insert_time) {
        this.insert_time = insert_time;
    }

    public String getRobotid() {
        return robotid;
    }

    public void setRobotid(String robotid) {
        this.robotid = robotid;
    }

    public String getRuleid() {
        return ruleid;
    }

    public void setRuleid(String ruleid) {
        this.ruleid = ruleid;
    }

    public String getRulename() {
        return rulename;
    }

    public void setRulename(String rulename) {
        this.rulename = rulename;
    }

    public String getCorpus() {
        return corpus;
    }

    public void setCorpus(String corpus) {
        this.corpus = corpus;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
