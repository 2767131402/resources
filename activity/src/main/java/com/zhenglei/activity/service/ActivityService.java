package com.zhenglei.activity.service;

import com.zhenglei.activity.entity.Activiti;
import com.zhenglei.activity.entity.ActivitiTask;
import org.activiti.engine.task.Task;

import java.util.List;

public interface ActivityService {
    /**
     * 开始流程
     */
    public Boolean startActiviti(Activiti vac, String userName);

    /**
     * 查询需要自己审批
     */
    public List<ActivitiTask> myApproval(String userName);

    /**
     * 我正在申请的假
     */
    public List<Activiti> myActiviti(String userName);

    /**
     * 审批操作
     */
    public Boolean passApproval(String userName, ActivitiTask activitiTask);

    /**
     * 查询已完成的请假记录
     */
    public List<Activiti> myActivitiRecord(String userName);

    /**
     * 我审批的记录列表
     */
    public List<Activiti> myApprovalRecord(String userName);

    public Task getTaskId(String processInstanceId);
}
