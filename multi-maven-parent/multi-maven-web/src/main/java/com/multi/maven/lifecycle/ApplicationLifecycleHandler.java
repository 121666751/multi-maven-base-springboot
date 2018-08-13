package com.multi.maven.lifecycle;

import com.multi.maven.utils.BeanUtil;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @Author: litao
 * @Description:  初始化缓存数据
 * @Date: 17:30 2018/8/7
 */
@Component
public class ApplicationLifecycleHandler implements ApplicationLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationLifecycleHandler.class);


    @Override
    public void doInitialize() {
        logger.info("应用初始化开始");
        Map<String, Initializer> initMap = BeanUtil.getBeansOfType(Initializer.class);
        if (MapUtils.isNotEmpty(initMap)) {
            for (Map.Entry<String, Initializer> it : initMap.entrySet()) {
                logger.info("正在初始化[{}]", it.getKey());
                it.getValue().doInitialize();
            }
        }

        logger.info("应用初始化结束");
    }

    @Override
    public void doDestroy() {
        logger.info("应用销毁开始");
        logger.info("应用销毁结束");
    }

}
