package com.multi.maven.config;

import com.multi.maven.service.IDGeneratorService;
import com.multi.maven.utils.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: litao
 * @Description: 唯一ID生成配置
 * @Date: 11:34 2018/8/9
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "id.generator")
public class IDGeneratorConfiguration {

	private Long workerId;

	private Long datacenterId;

	public IDGeneratorConfiguration() {

	}

	public IDGeneratorConfiguration(Long workerId, Long datacenterId) {

		log.info("Worker ID:" + workerId + ", Data Center ID:" + datacenterId);

		this.workerId = workerId;
		this.datacenterId = datacenterId;
	}

	@Bean
	public IdWorker idworkerGenerator() {

		log.info("ID Genert Worker...... WorkID:" + this.workerId + ", DataCenterID:" + this.datacenterId);

		IdWorker idWorker = new IdWorker(this.workerId, this.datacenterId);

		return idWorker;
	}

	@Bean
	public IDGeneratorService getIDGeneratorService() {
		return new IDGeneratorService(idworkerGenerator());
	}

	public Long getWorkerId() {
		return workerId;
	}

	public void setWorkerId(Long workerId) {
		this.workerId = workerId;
	}

	public Long getDatacenterId() {
		return datacenterId;
	}

	public void setDatacenterId(Long datacenterId) {
		this.datacenterId = datacenterId;
	}
}
