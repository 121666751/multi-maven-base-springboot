package com.multi.maven.threadpool;

import com.multi.maven.context.CacheContext;
import com.multi.maven.dao.redis.RedisClient;
import com.multi.maven.utils.BeanUtil;
import com.multi.maven.utils.LogTraceUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author: litao
 * @see:
 * @description:  线程池执行类封装
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/3.
 */
@Component("executors")
public class ThreadPoolExecutors {

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    private static ThreadPoolExecutors extend;


    @PostConstruct
    public void init() {
        extend = this;
        extend.taskExecutor = this.taskExecutor;
        extend.taskExecutor.setDaemon(true);
    }


    public static void executeTask(Runnable task) {
        String logTrace = LogTraceUtil.getLogTraceAll();
        extend.taskExecutor.execute(new Runnable() {

            @Override
            public void run() {
                //设置日志跟踪
                LogTraceUtil.setLogTraceAll(logTrace);
                task.run();
                //缓存更新后通用处理
                RedisClient rc = BeanUtil.getBean(RedisClient.class);
                if(rc == null) {
                  return;
                }
                List<String> deleteList = CacheContext.getDeleteKeyList();
                if(CollectionUtils.isNotEmpty(deleteList)) {
                    for(String key:deleteList) {
                        rc.deleteKey(key);
                    }
                }
            }
        });
    }

    public static void destroy() {
        try {
            extend.taskExecutor.destroy();
        }catch(Exception e) {
        }
    }
}
