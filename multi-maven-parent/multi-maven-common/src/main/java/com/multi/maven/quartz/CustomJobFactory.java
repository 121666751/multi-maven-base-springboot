package com.multi.maven.quartz;

import com.multi.maven.spring.SpringContext;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomJobFactory extends AdaptableJobFactory {

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        //调用父类的方法
        Object jobInstance = super.createJobInstance(bundle);
        //进行注入
        SpringContext.getContext().getAutowireCapableBeanFactory().autowireBean(jobInstance);
        return jobInstance;
    }
}
