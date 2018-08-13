package com.multi.maven.spring;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author: litao
 * @see:
 * @description: 获取spring上下文 ApplicationContext
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/3.
 */
@Component
@Slf4j
public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext context;

    public SpringContext() {
    }

    public static ApplicationContext getContext() {
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        log.info("系统设置ApplicationContext...");
        context = arg0;
    }

    public static <T> T getBean(String name){
        return (T) context.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz){
        return context.getBean(clazz);
    }

}
