/*
 * RT MAP, Home of Professional MAP
 * Copyright 2017 Bit Main Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 */
package com.multi.maven.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import javax.annotation.PostConstruct;

/**
 * @author ShenPS
 * @date 2017/12/27
 */
@Configuration
public class FreemarkerConfiguration {

    @Autowired
    protected freemarker.template.Configuration configuration;

    @Autowired
    protected FreeMarkerViewResolver resolver;

    @Autowired
    protected InternalResourceViewResolver springResolver;

    @PostConstruct
    public void setSharedVariable() {
        resolver.setSuffix(".ftl");
        resolver.setCache(false);
        resolver.setRequestContextAttribute("request"); //为模板调用时，调用request对象的变量名</span>
        resolver.setContentType("text/html;charset=UTF-8");
        resolver.setOrder(0);
        resolver.setExposeRequestAttributes(true);
        resolver.setExposeSessionAttributes(true);
    }
}
