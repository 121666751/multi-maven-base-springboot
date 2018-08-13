package com.multi.maven.lifecycle;


import com.multi.maven.utils.PropertyUtil;

/**
 * @Author: litao
 * @Description: 集群服务器 获取单节点ID
 * @Date: 11:40 2018/8/9
 */
public class ApplicationRuntimeInfo {

	private static final String SERVER_ID = "server.id";
	
	public static String getServerId() {
		return PropertyUtil.getProperty(SERVER_ID);
	}
}
