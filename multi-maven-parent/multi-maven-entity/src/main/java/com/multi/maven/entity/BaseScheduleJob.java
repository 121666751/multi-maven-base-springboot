package com.multi.maven.entity;

import com.multi.maven.dao.mysql.bean.BaseModel;

import java.util.Date;
import javax.persistence.*;

@Table(name = "base_schedule_job")
public class BaseScheduleJob extends BaseModel {
    /**
     * 任务ID
     */
    @Id
    @Column(name = "job_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String jobId;

    /**
     * 任务名称
     */
    @Column(name = "job_name")
    private String jobName;

    /**
     * 调度表达式
     */
    private String cronexp;

    /**
     * 可执行实例列表 多实例以英文逗号分隔
     */
    private String owners;

    /**
     * 执行的beanId
     */
    @Column(name = "bean_id")
    private String beanId;

    /**
     * 启停状态 0 启动 1 停止
     */
    private String status;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 获取任务ID
     *
     * @return job_id - 任务ID
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * 设置任务ID
     *
     * @param jobId 任务ID
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * 获取任务名称
     *
     * @return job_name - 任务名称
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * 设置任务名称
     *
     * @param jobName 任务名称
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * 获取调度表达式
     *
     * @return cronexp - 调度表达式
     */
    public String getCronexp() {
        return cronexp;
    }

    /**
     * 设置调度表达式
     *
     * @param cronexp 调度表达式
     */
    public void setCronexp(String cronexp) {
        this.cronexp = cronexp;
    }

    /**
     * 获取可执行实例列表 多实例以英文逗号分隔
     *
     * @return owners - 可执行实例列表 多实例以英文逗号分隔
     */
    public String getOwners() {
        return owners;
    }

    /**
     * 设置可执行实例列表 多实例以英文逗号分隔
     *
     * @param owners 可执行实例列表 多实例以英文逗号分隔
     */
    public void setOwners(String owners) {
        this.owners = owners;
    }

    /**
     * 获取执行的beanId
     *
     * @return bean_id - 执行的beanId
     */
    public String getBeanId() {
        return beanId;
    }

    /**
     * 设置执行的beanId
     *
     * @param beanId 执行的beanId
     */
    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    /**
     * 获取启停状态 0 启动 1 停止
     *
     * @return status - 启停状态 0 启动 1 停止
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置启停状态 0 启动 1 停止
     *
     * @param status 启停状态 0 启动 1 停止
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}