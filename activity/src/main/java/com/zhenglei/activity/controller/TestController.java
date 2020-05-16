package com.zhenglei.activity.controller;

import com.zhenglei.activity.entity.Activiti;
import com.zhenglei.activity.entity.ActivitiTask;
import com.zhenglei.activity.service.ActivityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ActivityService activityService;
    @Resource
    private RepositoryService repositoryService;

    /**
     * 手动部署流程图
     */
    @RequestMapping(value = "/start")
    public Boolean start(Activiti vac, HttpSession session) {
        Deployment deployment = repositoryService.createDeployment()//创建一个部署对象
                .name("请假流程")
                .addClasspathResource("processes/text.bpmn")
                .deploy();
        System.out.println("部署ID："+deployment.getId());
        System.out.println("部署名称："+deployment.getName());
        return true;
    }

    /**
     * 根据流程实例ID和用户ID查询任务ID
     */
    @RequestMapping(value = "/getTaskId")
    public Task getTaskId(String processInstanceId){
        return activityService.getTaskId(processInstanceId);
    }

    @RequestMapping(value = "/createActiviti")
    public String createActiviti(Activiti vac) {
        activityService.startActiviti(vac, null);
        return "yuangong";
    }

    //我正在申请的记录
    @RequestMapping(value = "/myActiviti")
    public List<Activiti> myActiviti(String userName, Model model) {
        List<Activiti> list = activityService.myActiviti(userName);
        model.addAttribute("list",list);
        return list;
    }

    //待我审核的记录
    @RequestMapping(value = "/myApproval")
    public List<ActivitiTask> myApproval(Model model,String userName) {
        List<ActivitiTask> list3 = activityService.myApproval(userName);
        model.addAttribute("list",list3);
        return list3;
    }

    @RequestMapping(value = "/passApproval")
    public Object passApproval(String id, String result, HttpSession session) {
        ActivitiTask activitiTask = new ActivitiTask();
        Activiti activiti = new Activiti();
        activitiTask.setId(id);
        activiti.setResult(result);
        activitiTask.setActiviti(activiti);
        String userName = "zhenglei";
        activityService.passApproval(userName, activitiTask);
        return "msg";
    }

    //我申请记录
    @RequestMapping(value = "/myActivitiRecord")
    public List<Activiti> myActivitiRecord(String userName) {
        List<Activiti> list2 = activityService.myActivitiRecord(userName);
        return list2;
    }

    //我的审核记录
    @RequestMapping(value = "/myApprovalRecord", method = RequestMethod.GET)
    public List<Activiti> myApprovalRecord(HttpSession session) {
        String userName = "zhenglei";
        List<Activiti> list4 = activityService.myApprovalRecord(userName);
        return list4;
    }
}