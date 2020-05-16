package com.zhenglei.activity.service.impl;

import com.zhenglei.activity.entity.Activiti;
import com.zhenglei.activity.entity.ActivitiTask;
import com.zhenglei.activity.service.ActivityService;
import com.zhenglei.activity.util.ActivitiUtil;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ActivityServiceImpl implements ActivityService {
    private Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);
    //所运行工作流的名字
    private static final String PROCESS_DEFINE_KEY = "myProcess";
    private static final String NEXT_ASSIGNEE = "huangxu2";
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private IdentityService identityService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private RepositoryService repositoryService;

    /**
     * 开始流程
     */
    @Override
    public Boolean startActiviti(Activiti vac, String userName) {
        try{
            identityService.setAuthenticatedUserId(userName);
            // 开始流程
            HashMap<String, Object> variables=new HashMap<>();
            variables.put("applyUser", vac.getApplyUser());
            variables.put("days", vac.getDays());
            variables.put("reason", vac.getReason());
            variables.put("userKey","jingli");
            ProcessInstance vacationInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINE_KEY,variables);
            System.out.println(vacationInstance.getId()+"_"+vacationInstance.getProcessDefinitionId());
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<Activiti> myActiviti(String userName) {
        List<ProcessInstance> instanceList = runtimeService.createProcessInstanceQuery().startedBy(userName).list();
        List<Activiti> activitisList = new ArrayList<>();
        for (ProcessInstance instance : instanceList) {
            Activiti activiti = getActiviti(instance);
            activitisList.add(activiti);
        }
        return activitisList;
    }
    /**
     * 查询需要自己审批
     */
    @Override
    public List<ActivitiTask> myApproval(String userName) {
        List<Task> taskList = taskService.createTaskQuery().taskAssignee(userName)
                .orderByTaskCreateTime().desc().list();
        List<ActivitiTask> activitiTaskList = new ArrayList<>();
        for (Task task : taskList) {
            ActivitiTask activitiTask = new ActivitiTask();
            activitiTask.setId(task.getId());
            activitiTask.setName(task.getName());
            activitiTask.setCreateTime(task.getCreateTime());
            String instanceId = task.getProcessInstanceId();
            ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
            Activiti activiti = getActiviti(instance);
            activitiTask.setActiviti(activiti);
            activitiTaskList.add(activitiTask);
        }
        return activitiTaskList;
    }

    private Activiti getActiviti(ProcessInstance instance) {
        Integer days = runtimeService.getVariable(instance.getId(), "days", Integer.class);
        String reason = runtimeService.getVariable(instance.getId(), "reason", String.class);
        String applyUser = runtimeService.getVariable(instance.getId(), "applyUser", String.class);
        Activiti activiti = new Activiti();
        activiti.setApplyUser(applyUser);
        activiti.setDays(days);
        activiti.setReason(reason);
        Date startTime = instance.getStartTime(); // activiti 6 才有
        activiti.setApplyTime(startTime);
        activiti.setApplyStatus(instance.isEnded() ? "申请结束" : "等待审批");
        return activiti;
    }

    /**
     * Activiti任务认领
     * taskService.setAssignee(String taskId, String userId);
     * taskService.claim(String taskId, String userId);
     * taskService.setOwner(String taskId, String userId);
     * setAssignee和claim两个的区别是在认领任务时，claim会检查该任务是否已经被认领，如果被认领则会抛出ActivitiTaskAlreadyClaimedException ,而setAssignee不会进行这样的检查，其他方面两个方法效果一致。
     * setOwner和setAssignee的区别在于,setOwner是在代理任务时使用，代表着任务的归属者，而这时，setAssignee代表的是代理办理者，
     *
     * 举个例子来说，公司总经理现在有个任务taskA，去核实一下本年度的财务报表，他现在又很忙没时间，于是将该任务委托给其助理进行办理，此时，就应该这么做
     * taskService.setOwner(taskA.getId(), 总经理.getId());
     * taskService.setAssignee/claim(taskA.getId(), 助理.getId());
     */
    /**
     * 审批操作
     * @param userName
     * @param activitiTask
     * @return
     */
    /**
     * 同理，result是审批的结果，也是在完成审批任务时需要传入的参数；taskId是刚才老板查询到的当前需要自己完成的审批任务ID。
     * （如果流程在这里设置分支，可以通过判断result的值来跳转到不同的任务）
     */
    @Override
    public Boolean passApproval(String userName, ActivitiTask activitiTask) {
        String taskId = activitiTask.getId();
        String result = activitiTask.getActiviti().getResult();
        Map<String, Object> vars = new HashMap<>();
        vars.put("result", result);
        vars.put("auditor", userName);
        vars.put("auditTime", new Date());
        //taskService.claim(taskId, userName);
        taskService.setAssignee(taskId, userName);
        taskService.complete(taskId, vars);
        return true;
    }

    /**
     * 查询已完成的请假记录
     * 由于已完成的请假在数据库runtime表中查不到（runtime表只保存正在进行的流程示例信息），所以需要在history表中查询。
     */
    @Override
    public List<Activiti> myActivitiRecord(String userName) {
        List<HistoricProcessInstance> hisProInstance = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(PROCESS_DEFINE_KEY).startedBy(userName).finished()
                .orderByProcessInstanceEndTime().desc().list();

        List<Activiti> activitiList = new ArrayList<>();
        for (HistoricProcessInstance hisInstance : hisProInstance) {
            Activiti activiti = new Activiti();
            activiti.setApplyUser(hisInstance.getStartUserId());
            activiti.setApplyTime(hisInstance.getStartTime());
            activiti.setApplyStatus("申请结束");
            List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(hisInstance.getId()).list();
            ActivitiUtil.setVars(activiti, varInstanceList);
            activitiList.add(activiti);
        }
        return activitiList;
    }

    /**
     * 我审批的记录列表
     * @param userName
     * @return
     */
    @Override
    public List<Activiti> myApprovalRecord(String userName) {
        List<HistoricProcessInstance> hisProInstance = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(PROCESS_DEFINE_KEY).involvedUser(userName).finished()
                .orderByProcessInstanceEndTime().desc().list();

        List<String> auditTaskNameList = new ArrayList<>();
        auditTaskNameList.add("经理审批");
        auditTaskNameList.add("总监审批");
        List<Activiti> activitiList = new ArrayList<>();
        for (HistoricProcessInstance hisInstance : hisProInstance) {
            Activiti activiti = new Activiti();
            activiti.setApplyUser(hisInstance.getStartUserId());
            activiti.setApplyStatus("申请结束");
            activiti.setApplyTime(hisInstance.getStartTime());
            List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(hisInstance.getId()).list();
            ActivitiUtil.setVars(activiti, varInstanceList);
            activitiList.add(activiti);
        }
        return activitiList;
    }

    @Override
    public Task getTaskId(String processInstanceId) {
        //查询当前办理人的任务ID
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)//使用流程实例ID
                .singleResult();

        System.out.println(task.getAssignee());
        return task;
    }
}
