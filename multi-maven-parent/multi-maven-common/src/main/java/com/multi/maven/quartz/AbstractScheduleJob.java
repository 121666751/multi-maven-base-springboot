package com.multi.maven.quartz;

import com.multi.maven.utils.LogTraceUtil;
import com.multi.maven.utils.LogTraceUtil.*;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @Author: litao
 * @Description: 定时任务需要实现这个类
 * @Date: 11:41 2018/8/9
 */
public abstract class AbstractScheduleJob implements Job {

	public abstract void executeJob();
	
	private boolean waitForJobsToComplete = true;
	
	public boolean isWaitForJobsToComplete() {
		return waitForJobsToComplete;
	}

	public void setWaitForJobsToComplete(boolean waitForJobsToComplete) {
		this.waitForJobsToComplete = waitForJobsToComplete;
	}


	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String jobId = context.getJobDetail().getKey().getName();
		LogTraceUtil.clearMDC();
		LogTraceUtil.putLogTrace(LogTrace.MODEL, jobId);
		executeJob();
	}

}
