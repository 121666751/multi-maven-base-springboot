package com.multi.maven.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author: litao
 * @see:
 * @description: 1.An ApplicationStartingEvent is sent at the start of a run but before any processing, except for the registration of listeners and initializers.
 * 2.An ApplicationEnvironmentPreparedEvent is sent when the Environment to be used in the context is known but before the context is created.
 * 3.An ApplicationPreparedEvent is sent just before the refresh is started but after bean definitions have been loaded.
 * 4.An ApplicationStartedEvent is sent after the context has been refreshed but before any application and command-line runners have been called.
 * 5.An ApplicationReadyEvent is sent after any application and command-line runners have been called. It indicates that the application is ready to service requests.
 * 6.An ApplicationFailedEvent is sent if there is an exception on startup.
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/8.
 */
@Slf4j
public class PropertiesListener implements ApplicationListener<ApplicationPreparedEvent> {

    private String propertyFileName;


    public PropertiesListener(String propertyFileName) {
        log.info("propertiesListener运行了,初始化设置配置文件为:[{}]", propertyFileName);
        this.propertyFileName = propertyFileName;
    }

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        log.info("propertiesListener运行了,初始化加载配置文件:[{}]", propertyFileName);
        PropertiesListenerConfig.loadAllProperties(propertyFileName);
    }

}
