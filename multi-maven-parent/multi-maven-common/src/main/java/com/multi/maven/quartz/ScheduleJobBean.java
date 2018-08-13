package com.multi.maven.quartz;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import java.io.Serializable;

public class ScheduleJobBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String JOB_MAP_KEY = "self";
	public static final String STATUS_RUNNING = "1";
	public static final String STATUS_NOT_RUNNING = "0";
	public static final String CONCURRENT_IS = "1";
	public static final String CONCURRENT_NOT = "0";

	/**
	 * 任务ID
	 */
	private String jobId;
	/**
	 * 所属者  以逗号分隔
	 */
	private String owners;
	/**
	 * 任务名称
	 */
	private String jobName;
	/**
	 * 任务分组
	 */
	private String jobGroup;
	/**
	 * 任务状态 是否启动任务
	 */
	private String jobStatus;
	/**
	 * cron表达式
	 */
	private String cronExpression;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 任务执行时调用哪个类的方法 包名+类名
	 */
	private String beanClass;
	/**
	 * 任务是否有状态
	 */
	private String isConcurrent;

	/**
	 * Spring bean spring管理的实例key 类名首字母小写
	 */
	private String springBean;

	/**
	 * 任务调用的方法名
	 */
	private String methodName;
	
	@JsonIgnore
	private JobDataMap dataMap = new JobDataMap();


	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(String beanClass) {
		this.beanClass = beanClass;
	}

	public String getIsConcurrent() {
		return isConcurrent;
	}

	public void setIsConcurrent(String isConcurrent) {
		this.isConcurrent = isConcurrent;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getSpringBean() {
		return springBean;
	}

	public void setSpringBean(String springBean) {
		this.springBean = springBean;
	}
	
	public JobDataMap getDataMap() {
		if(dataMap.size()==0){
			dataMap.put(JOB_MAP_KEY, this);
		}
		return dataMap;
	}

	public JobKey getJobKey(){ 
		return JobKey.jobKey(jobId);// 任务名称和组构成任务key
	}
	
	public static ScheduleJobBean getJobFromMap(JobDataMap dataMap) {
		return (ScheduleJobBean) dataMap.get(JOB_MAP_KEY);
	}
	
	@Override
	public String toString() {
		return "ScheduleJob [jobName=" + jobName + ", jobGroup=" + jobGroup + ", jobStatus=" + jobStatus
				+ ", cronExpression=" + cronExpression + ", description=" + description + ", beanClass=" + beanClass
				+ ", isConcurrent=" + isConcurrent + ", springBean=" + springBean + ", methodName=" + methodName + "]";
	}


	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getOwners() {
		return owners;
	}

	public void setOwners(String owners) {
		this.owners = owners;
	}
}