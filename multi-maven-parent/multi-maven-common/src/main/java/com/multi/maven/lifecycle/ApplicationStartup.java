package com.multi.maven.lifecycle;

import com.multi.maven.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author: litao
 * @see:
 * @description: 应用启动监听
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/8.
 */
@Component
@Slf4j
public class ApplicationStartup implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        log.info("应用启动初始化...");
        ApplicationLifecycle life = BeanUtil.getBean(ApplicationLifecycle.class);
        if(life!=null) {
            life.doInitialize();
        }
    }
}
