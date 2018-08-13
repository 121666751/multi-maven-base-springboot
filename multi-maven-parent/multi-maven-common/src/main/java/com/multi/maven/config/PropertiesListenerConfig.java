package com.multi.maven.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author: litao
 * @see:
 * @description:
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/8.
 */
@Slf4j
public class PropertiesListenerConfig {

    private static Properties properties;

    public static void loadAllProperties(String propertyFileName) {
        //如果 propertyFileName 是模糊匹配
        try {
            properties = PropertiesLoaderUtils.loadAllProperties(propertyFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        log.info("获取配置文件属性,key是:{}", key);
        String value = properties.getProperty(key);
        log.info("获取配置文件属性,value是:{}", value);
        return value;
    }
}
