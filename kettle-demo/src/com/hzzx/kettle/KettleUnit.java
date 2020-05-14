package com.hzzx.kettle;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

public class KettleUnit {
	
	public static Result runKtr(String[] params, String path) throws Exception {

		Trans trans = null;

		KettleEnvironment.init();// 初始化
		EnvUtil.environmentInit();

		TransMeta transMeta = new TransMeta(path);
		// 转换
		trans = new Trans(transMeta);
		trans.execute(params);

		// 等待转换执行结束
		trans.waitUntilFinished();

		// 抛出异常
		if (trans.getErrors() > 0) {
			throw new KettleException("There are errors during transformation exception!(传输过程中发生异常)");
		}

		return trans.getResult();
	}

	public static Result runJob(String[] params, String path) throws Exception {

		KettleEnvironment.init();

		JobMeta jobMeta = new JobMeta(path, null);
		Job job = new Job(null, jobMeta);

		// 向Job 脚本传递参数，脚本中获取参数值：${参数名}
		// job.setVariable("id", params[0]);
		// job.setVariable("content", params[1]);
		// job.setVariable("file", params[2]);

		job.start();
		job.waitUntilFinished();

		if (job.getErrors() > 0) {
			throw new KettleException("There are errors during job exception!(执行job发生异常)");
		}

		return job.getResult();
	}

}
