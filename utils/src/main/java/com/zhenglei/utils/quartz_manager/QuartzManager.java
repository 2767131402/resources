package com.zhenglei.utils.quartz_manager;

import java.util.List;
import java.util.stream.Collectors;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 定时任务工具类
 * @Description: 
 * @Date: 2019年4月15日
 * @auther: zhenglei
 */
public class QuartzManager {

	private static String JOB_GROUP_NAME = "EXTJWEB_JOBGROUP_NAME";
	private static SchedulerFactory gSchedulerFactory = new StdSchedulerFactory();
	
	/**
	 * 获取所有的jobName
	 * 需要JDK 1.8以上
	 */
	public List<String> getAll() {
		try {
			Scheduler scheduler = gSchedulerFactory.getScheduler();
			List<JobExecutionContext> jobs = scheduler.getCurrentlyExecutingJobs();
			//获取上下文集合的jobName
			List<String> jobList = jobs.stream().map(name -> name.getJobDetail().getKey().getName()).collect(Collectors.toList());
			return jobList;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 添加Job
	 * @param jobName job名称，唯一
	 * @param cls 业务逻辑类
	 * @param corn corn表达式
	 * @param describe 描述，可为空
	 * @return 是否添加成功
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean add(String jobName, Class cls, String corn, String describe) {
		try {
			Scheduler scheduler = gSchedulerFactory.getScheduler();
			cls.newInstance();
			// 构建job信息
			JobDetail job = JobBuilder.newJob(cls)
					.withIdentity(jobName, JOB_GROUP_NAME)
					.withDescription(describe)
					.build();
			// 触发时间点
			CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(corn);
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity("trigger"+jobName, JOB_GROUP_NAME)
					.startNow()
					.withSchedule(cronScheduleBuilder)
					.build();
			// 交由Scheduler安排触发
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 添加Job
	 * @param jobName job名称，唯一
	 * @param cls 业务逻辑类
	 * @param time 执行周期，<font color='red'>单位秒</font>
	 * @param describe 描述，可为空
	 * @return 是否添加成功
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean add(String jobName, Class cls, int time, String describe) {
		try {
			Scheduler scheduler = gSchedulerFactory.getScheduler();
			cls.newInstance();
			// 构建job信息
			JobDetail job = JobBuilder.newJob(cls)
					.withIdentity(jobName, JOB_GROUP_NAME)
					.withDescription(describe)
					.build();
			// 触发时间点
			SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.repeatSecondlyForever(time);
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(jobName, JOB_GROUP_NAME)
					.startNow()
					.withSchedule(simpleScheduleBuilder)
					.build();
			// 交由Scheduler安排触发
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	  * 移除Job(停用Job)
	 * @param jobName job名称，唯一
	 */
	public boolean remove(String jobName) {
		try {  
			Scheduler scheduler = gSchedulerFactory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, JOB_GROUP_NAME);  
			// 停止触发器  
			scheduler.pauseTrigger(triggerKey);  
			// 移除触发器  
			scheduler.unscheduleJob(triggerKey);  
			// 删除任务  
			scheduler.deleteJob(JobKey.jobKey(jobName, JOB_GROUP_NAME));  
		} catch (Exception e) {  
			return false;
		}  
		return true;
	}
	
	/**
	 * 恢复任务,与停止任务相对
	 * @param jobName job名称，唯一
	 */
	public boolean regainJob(String jobName) {
		try {
			Scheduler scheduler = gSchedulerFactory.getScheduler();
			JobKey key = new JobKey(jobName, JOB_GROUP_NAME);
		    scheduler.resumeJob(key);
		} catch (Exception e) {  
			return false;
		}  
		return true;
	}
	
	/**
	 * 停止任务
	 * @param jobName job名称，唯一
	 */
	public boolean pause(String jobName) {
		try {
			Scheduler scheduler = gSchedulerFactory.getScheduler();
			JobKey key = new JobKey(jobName, JOB_GROUP_NAME);
			scheduler.pauseJob(key);
		} catch (Exception e) {  
			return false;
		}  
		return true;
	}
	
	/**
	 * 执行任务
	 * 只执行一次
	 * @param jobName job名称，唯一
	 */
	public boolean trigger(String jobName) {
		try {
			Scheduler scheduler = gSchedulerFactory.getScheduler();
			JobKey key = new JobKey(jobName, JOB_GROUP_NAME);
		    scheduler.triggerJob(key);
		} catch (Exception e) {  
			return false;
		}  
		return true;
	}
}
