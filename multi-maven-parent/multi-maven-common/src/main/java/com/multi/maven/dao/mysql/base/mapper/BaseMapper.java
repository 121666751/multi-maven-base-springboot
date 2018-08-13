package com.multi.maven.dao.mysql.base.mapper;


import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
import tk.mybatis.mapper.common.condition.SelectByConditionMapper;

/**
 * 基础mapper
 * 
 * @author James
 *
 * @param <T>
 */
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T>, SelectByConditionMapper<T>, CommonSelectMapper<T>, BatchLogicDeleteMapper<T> {
//public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T>, SelectByConditionMapper<T>, BatchLogicInsertMapper<T> {
	//FIXME 特别注意，该接口不能被扫描到，否则会出错
}
