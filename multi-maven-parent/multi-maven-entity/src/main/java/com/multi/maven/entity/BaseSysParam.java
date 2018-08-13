package com.multi.maven.entity;

import com.multi.maven.dao.mysql.bean.BaseModel;
import java.util.Date;
import javax.persistence.*;

@Table(name = "base_sys_param")
public class BaseSysParam extends BaseModel {
    /**
     * 参数Key
     */
    @Id
    @Column(name = "param_key")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String paramKey;

    /**
     * 参数值
     */
    @Column(name = "param_value")
    private String paramValue;

    /**
     * 参数描述
     */
    @Column(name = "param_desc")
    private String paramDesc;

    /**
     * 有效状态 1 有效 2 无效
     */
    @Column(name = "valid_status")
    private String validStatus;

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
     * 获取参数Key
     *
     * @return param_key - 参数Key
     */
    public String getParamKey() {
        return paramKey;
    }

    /**
     * 设置参数Key
     *
     * @param paramKey 参数Key
     */
    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    /**
     * 获取参数值
     *
     * @return param_value - 参数值
     */
    public String getParamValue() {
        return paramValue;
    }

    /**
     * 设置参数值
     *
     * @param paramValue 参数值
     */
    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    /**
     * 获取参数描述
     *
     * @return param_desc - 参数描述
     */
    public String getParamDesc() {
        return paramDesc;
    }

    /**
     * 设置参数描述
     *
     * @param paramDesc 参数描述
     */
    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    /**
     * 获取有效状态 1 有效 2 无效
     *
     * @return valid_status - 有效状态 1 有效 2 无效
     */
    public String getValidStatus() {
        return validStatus;
    }

    /**
     * 设置有效状态 1 有效 2 无效
     *
     * @param validStatus 有效状态 1 有效 2 无效
     */
    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
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