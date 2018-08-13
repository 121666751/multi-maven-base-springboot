package com.multi.maven.quartz;

import com.multi.maven.lifecycle.ApplicationRuntimeInfo;
import com.multi.maven.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.*;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 计划任务管理
 *
 * @author DIY
 *         2018年5月16日  下午5:55:52
 */
@Service
@Slf4j
public class QuartzManager {

    private static final String STATUS_RUN = "0";
    private static final String STATUS_STOP = "1";

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private volatile boolean isShutdown = false;

    /**
     * 添加任务
     *
     * @param jobBean
     * @throws SchedulerException
     */
    public void addJob(ScheduleJobBean jobBean) {
        log.info("注册定时任务开始:{}", jobBean);

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        //所属者  区分环境
        String owners = jobBean.getOwners();
        //job的唯一标识
        String jobId = jobBean.getJobId();
        //job的描述
        String jobName = jobBean.getJobName();
        //执行job的具体实现的实例
        String springBean = jobBean.getSpringBean();
        //job状态
        String jobStatus = jobBean.getJobStatus();
        //cron表达式
        String cronExpression = jobBean.getCronExpression();
        try {
            if (isShutdown || scheduler.isShutdown()) {
                return;
            }
            List<String> ownersList = Arrays.asList(owners.split(","));
            boolean isowner = ownersList.contains(ApplicationRuntimeInfo.getServerId());
            boolean allowrun = STATUS_RUN.equals(jobStatus);
            JobDetail detail = scheduler.getJobDetail(JobKey.jobKey(jobId));
            if (detail == null) {
                //未注册的任务
                if (isowner && allowrun) {
                    // 创建jobDetail实例，绑定Job实现类
                    // 指明job的名称，所在组的名称，以及绑定job类
                    //Job instance
                    Job job = BeanUtil.getBean(springBean, Job.class);
                    JobDetail jobDetail = JobBuilder
                            .newJob(job.getClass())
                            .withIdentity(jobBean.getJobId(), jobBean.getJobGroup())
                            .withDescription(jobName)
                            //存储的是当前运行job的实例,也可以是数据库配置job实例
                            .usingJobData(jobBean.getDataMap())
                            .build();
                    // 定义调度触发规则
                    // 使用cornTrigger规则
                    Trigger trigger = TriggerBuilder
                            .newTrigger()
                            // 触发器key
                            .withIdentity(jobBean.getJobId(), jobBean.getJobGroup())
                            .withDescription(jobName)
                            .withSchedule(CronScheduleBuilder.cronSchedule(jobBean.getCronExpression()))
                            .startNow()
                            .build();
                    // 把作业和触发器注册到任务调度中
                    scheduler.scheduleJob(jobDetail, trigger);
                }
            } else {
                //已经注册过任务
                TriggerKey oldTriggerKey = TriggerKey.triggerKey(jobBean.getJobId());
                if (isowner && allowrun) {
                    //本应用需要重新注册
                    CronTrigger newTrigger = TriggerBuilder
                            .newTrigger()
                            .withIdentity(jobId)
                            .withDescription(jobName)
                            .withSchedule(
                                    CronScheduleBuilder.cronSchedule(cronExpression))
                            .build();
                    scheduler.rescheduleJob(oldTriggerKey, newTrigger);
                } else {
                    //本应用需要注销
                    scheduler.unscheduleJob(oldTriggerKey);
                }

            }

            // 判断调度器是否启动
            if (!scheduler.isStarted()) {
                scheduler.start();
            }
            log.info(String.format("定时任务:%s.%s-已添加到调度器!", jobBean.getJobGroup(), jobBean.getJobName()));
        } catch (Exception e) {
            e.printStackTrace();
            log.info("添加定时任务异常,{}", e);
        }
    }


    @PreDestroy
    public void shutdownScheduler() {
        if (isShutdown) {
            return;
        }
        log.info("停止调度任务开始");
        isShutdown = true;
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        boolean waitForJobsToComplete = true;
        //先将强制关闭的任务停止
        try {
            List<JobExecutionContext> currentJobs = scheduler.getCurrentlyExecutingJobs();
            if (CollectionUtils.isNotEmpty(currentJobs)) {
                for (JobExecutionContext jobContext : currentJobs) {
                    Job job = jobContext.getJobInstance();
                    if (job instanceof AbstractScheduleJob) {
                        if (!((AbstractScheduleJob) job).isWaitForJobsToComplete()) {
                            JobKey jobKey = jobContext.getJobDetail().getKey();
                            log.info("中断任务:" + jobKey);
                            scheduler.pauseJob(jobKey);
                            scheduler.deleteJob(jobKey);
                        }
                    }
                }
            }
        } catch (SchedulerException e) {
            log.error("停止任务出现异常,将强行关闭调度任务", e);
            waitForJobsToComplete = false;
        }
        try {
            scheduler.shutdown(waitForJobsToComplete);
        } catch (SchedulerException e) {
            log.error("关闭调度任务异常！", e);
        }
        log.info("停止调度任务结束");
    }

    /**
     * 获取所有计划中的任务列表
     *
     * @return
     * @throws SchedulerException
     */
    public List<ScheduleJobBean> getAllJob() throws SchedulerException {
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys = schedulerFactoryBean.getScheduler().getJobKeys(matcher);
        List<ScheduleJobBean> jobList = new ArrayList<ScheduleJobBean>();
        for (JobKey jobKey : jobKeys) {
            ScheduleJobBean job = ScheduleJobBean.getJobFromMap(schedulerFactoryBean.getScheduler().getJobDetail(jobKey).getJobDataMap());
            jobList.add(job);
        }
        return jobList;
    }

    /**
     * 所有正在运行的job
     *
     * @return
     * @throws SchedulerException
     */
    public List<ScheduleJobBean> getRunningJob() throws SchedulerException {
        List<JobExecutionContext> executingJobs = schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs();
        List<ScheduleJobBean> jobList = new ArrayList<ScheduleJobBean>(executingJobs.size());
        for (JobExecutionContext executingJob : executingJobs) {
            ScheduleJobBean job = ScheduleJobBean.getJobFromMap(executingJob.getJobDetail().getJobDataMap());
            jobList.add(job);
        }
        return jobList;
    }

    /**
     * 暂停一个job
     *
     * @param job
     * @throws SchedulerException
     */
    public void pauseJob(ScheduleJobBean job) throws SchedulerException {
        schedulerFactoryBean.getScheduler().pauseJob(job.getJobKey());
        log.info(String.format("定时任务:%s.%s-已暂停!", job.getJobGroup(), job.getJobName()));
    }

    /**
     * 恢复一个job
     *
     * @param job
     * @throws SchedulerException
     */
    public void resumeJob(ScheduleJobBean job) throws SchedulerException {
        schedulerFactoryBean.getScheduler().resumeJob(job.getJobKey());
        log.info(String.format("定时任务:%s.%s-已重启!", job.getJobGroup(), job.getJobName()));
    }

    /**
     * 删除一个job
     *
     * @param job
     * @throws SchedulerException
     */
    public void deleteJob(ScheduleJobBean job) throws SchedulerException {
        schedulerFactoryBean.getScheduler().deleteJob(job.getJobKey());
        log.info(String.format("定时任务:%s.%s-已删除!", job.getJobGroup(), job.getJobName()));
    }

    /**
     * 立即执行job
     *
     * @param job
     * @throws SchedulerException
     */
    public void runAJobNow(ScheduleJobBean job) throws SchedulerException {
        schedulerFactoryBean.getScheduler().triggerJob(job.getJobKey());
        log.info(String.format("定时任务:%s.%s-立即启动!", job.getJobGroup(), job.getJobName()));
    }

    /**
     * 更新job时间表达式
     *
     * @param job
     * @throws SchedulerException
     */
    public void updateJobCron(ScheduleJobBean job) throws SchedulerException {

        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());

        CronTrigger trigger = (CronTrigger) schedulerFactoryBean.getScheduler().getTrigger(triggerKey);

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());

        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

        schedulerFactoryBean.getScheduler().rescheduleJob(triggerKey, trigger);
        log.info(String.format("定时任务:%s.%s-更换新的执行时间[" + job.getCronExpression() + "]!", job.getJobGroup(), job.getJobName()));
    }

    public TriggerState jobIsRun(ScheduleJobBean job) {
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
        try {
            return schedulerFactoryBean.getScheduler().getTriggerState(triggerKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return null;
    }
}