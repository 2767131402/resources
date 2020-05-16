package com.zhenglei.activity.entity;

import java.util.Date;

public class ActivitiTask {

    private String id;
    private String name;
    private Activiti activiti;
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Activiti getActiviti() {
        return activiti;
    }

    public void setActiviti(Activiti activiti) {
        this.activiti = activiti;
    }
}