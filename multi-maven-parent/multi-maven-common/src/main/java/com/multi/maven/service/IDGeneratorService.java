package com.multi.maven.service;


import com.multi.maven.utils.IdWorker;
import org.springframework.stereotype.Service;

/**
 * @Author: litao
 * @Description: 工具service 生成唯一ID
 * @Date: 11:24 2018/8/9
 */
public class IDGeneratorService {
    
	public IdWorker idWorker;
	
	public IDGeneratorService(IdWorker idWorker){
		this.idWorker = idWorker;
	}

	/**
	 * Generate the next ID using snowflake
	 * @return
	 */
	public Long nextId () {
		
		Long id = idWorker.nextId();
		
		return id;
	}
}
