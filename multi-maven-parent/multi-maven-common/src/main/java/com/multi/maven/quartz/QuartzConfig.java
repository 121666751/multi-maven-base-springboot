package com.multi.maven.quartz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.PreDestroy;

@Configuration
public class QuartzConfig {

    @Autowired
    private CustomJobFactory jobFactory;

	@Bean(name = "schedulerFactoryBean", destroyMethod = "destroy")
    public SchedulerFactoryBean schedulerFactoryBean(){
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory);
        return schedulerFactoryBean;   
    }	
	
}
