package com.multi.maven.dao.mongo.bean;

import java.io.Serializable;
import java.util.Map;

/**
 * 所有的MongoBean实现该接口
 * @author yangsongbo
 * @since v3.0
 *
 */
public interface IMongoBean extends Serializable{

	/**
	 * 获取主键_id
	 * @return
	 */
	public String getPk();
	
	/**
	 * 设置主键_id的值
	 */
	public void setPk(String pk);
	
	
	
	/**
	 * 将实体对象转换为Map
	 * @return
	 */
    public Map<String,Object> putFieldValueToMap();
    
    /**
     *  将map转换为实体对象
     * @param map
     * @param isDealNull 是否覆盖Null值   
     */
    public void doMapToDtoValue(Map<String, Object> map, boolean isDealNull) ;
}
