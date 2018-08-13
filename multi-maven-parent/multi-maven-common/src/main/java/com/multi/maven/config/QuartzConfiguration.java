package com.multi.maven.config;

import com.multi.maven.quartz.CustomJobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @author: litao
 * @see:
 * @description: 定时任务配置
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/8.
 */
@Configuration
public class QuartzConfiguration {

    @Autowired
    private CustomJobFactory jobFactory;

    @Bean(name = "schedulerFactoryBean")
    public SchedulerFactoryBean schedulerFactoryBean(){
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory);
        return schedulerFactoryBean;
    }
}
