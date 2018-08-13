package com.multi.maven.config;

import com.multi.maven.dao.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: litao
 * @Description: redis 配置类
 * @Date: 11:38 2018/8/9
 */
@Component
public class RedisConfiguration {

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Bean
	public RedisClient getRedisUtil() {
		RedisClient redisClient = new RedisClient();
		redisClient.setRedisTemplate(redisTemplate);
		return redisClient;
	}
	
}
