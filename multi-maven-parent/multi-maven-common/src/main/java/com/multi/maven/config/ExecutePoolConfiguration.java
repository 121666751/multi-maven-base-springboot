package com.multi.maven.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author: litao
 * @see:
 * @description: 线程池配置
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/8.
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix="threadpool")
public class ExecutePoolConfiguration {
    private int corePoolSize;

    private int maxPoolSize;

    private int queueCapacity;

    private int keepAliveSeconds;


    @Bean(name="threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setKeepAliveSeconds(keepAliveSeconds);
        pool.setCorePoolSize(corePoolSize);//核心线程池数
        pool.setMaxPoolSize(maxPoolSize); // 最大线程
        pool.setQueueCapacity(queueCapacity);//队列容量
        pool.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy()); //队列满，线程被拒绝执行策略
        return pool;
    }
}
