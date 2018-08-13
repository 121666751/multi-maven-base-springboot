package com.multi.maven.utils;

import com.multi.maven.config.PropertiesListenerConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author: litao
 * @see:
 * @description: 获取属性值工具类
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/3.
 */
@Configuration
public class PropertyUtil implements EnvironmentAware {

    private static Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

    public final static String getProperty(String key) {
        return getProperty(key, null);
    }

    public final static String getProperty(String key, String defaultValue) {
        String value;
        if (null == env) {
            value = PropertiesListenerConfig.getProperty(key);
        } else {
            value = env.getProperty(key);
        }
        return StringUtils.isBlank(value)?defaultValue : value;
    }

}
