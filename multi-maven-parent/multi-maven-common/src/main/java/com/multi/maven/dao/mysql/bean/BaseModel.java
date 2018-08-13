package com.multi.maven.dao.mysql.bean;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * 模型的基础类
 * 所有mysql 实体继承该类
 * Created by James on 2016-08-02.
 */
@MappedSuperclass
@Setter
@Getter
public abstract class BaseModel implements IBaseModel{

	/**
     * 主键
     */
    @Id
    @Column(name = "id")
    public Long id;

    /**
     * 是否删除：0：有效，1：删除
     */
	private Byte enabled;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
	private Date createTime;

    /**
     * 创建者
     */
    @Column(name = "create_by")
	private Long createBy;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
	private Date updateTime;

    /**
     * 更新者
     */
    @Column(name = "update_by")
	private Long updateBy;

}
