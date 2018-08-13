package com.multi.maven.spring;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author: litao
 * @Description: 无效
 * @Date: 17:22 2018/8/9
 */
public class SpringProperty extends PropertyPlaceholderConfigurer {

	private static Properties properties;

	public static Properties getProperties() {
		return SpringProperty.properties;
	}

	@Override
	protected Properties mergeProperties() throws IOException {
		SpringProperty.properties= super.mergeProperties();
		 return SpringProperty.properties;
	}
	
}
