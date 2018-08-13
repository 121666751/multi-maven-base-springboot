package com.multi.maven.domain;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * @author: litao
 * @see:
 * @description:
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/3.
 */
@Setter
@Getter
public class ServiceBean {

    private Object serviceInstance;

    private String methodName;

    private Method method;

}
