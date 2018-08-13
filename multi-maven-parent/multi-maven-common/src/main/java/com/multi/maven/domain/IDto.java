package com.multi.maven.domain;

import java.io.Serializable;
import java.util.Map;

public interface IDto extends Serializable{

	/**
	 * 将dto 属性 信息 生成map	
	 */
	public Map<String,Object> putColumnValueToMap(); 	
	/**
	 * 将map 信息 存入dto  	
	 */
	public void doMapToDtoValue(Map<String, Object> map, boolean isDealNull);
	
	public String getCheckVersion();
	
	public void setCheckVersion(String checkVersion);
	
	public Map<String,Object> putPkToMap();
	
    public String tableName();
    
    public <T extends IDto> Class<T> getDtoClass();

	
}
