package com.zhenglei.utils.quartz_manager;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
  * 测试的JOB
 * @Description: 
 * @Date: 2019年2月21日
 * @auther: zhenglei
 */
public class Jobs implements Job{

	@Override
	public void execute(JobExecutionContext context){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.err.println("大吉大利，执行时间："+sdf.format(new Date()));
	}

}
