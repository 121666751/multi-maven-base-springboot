package com.multi.maven.utils;

import com.multi.maven.spring.SpringContext;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.Map;

/**
 * @author: litao
 * @see:
 * @description: 从 springContext中获取指定的实例
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/3.
 */
public class BeanUtil {
    public BeanUtil() {
    }

    public static <T> T getBean(String beanName, Class<T> classType) {
        return SpringContext.getContext().getBean(beanName, classType);
    }

    public static <T> T getBean(Class<T> classType) {
        try {
            return SpringContext.getContext().getBean(classType);
        } catch (NoSuchBeanDefinitionException var2) {
            return null;
        }
    }

    public static Object getBean(String beanName) {
        try {
            return SpringContext.getContext().getBean(beanName);
        } catch (NoSuchBeanDefinitionException var2) {
            return null;
        }
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return SpringContext.getContext().getBeansOfType(type);
    }
}
