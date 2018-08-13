package com.multi.maven;

import com.multi.maven.config.PropertiesListener;
import com.multi.maven.filter.SessionFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import javax.servlet.Filter;

@SpringBootApplication
@ImportResource(value = "classpath:config/web-config.xml")
public class MultiMavenWebApplication {

	public static void main(String[] args) {
		SpringApplication springApplication =new SpringApplication(MultiMavenWebApplication.class);
		//获取配置文件的数据
		springApplication.addListeners(new PropertiesListener("application-test.properties"));
		springApplication.run(args);
	}

	/**
	 * 配置过滤器
	 * @return
	 */
	@Bean
	public FilterRegistrationBean filterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(sessionFilter());
		registration.addUrlPatterns("/*");
		registration.addInitParameter("excludeURI", "/manage/login.do;/manage/logout.do;/common/upload/token.do;/common/url/get.do");
		registration.addInitParameter("includeSuffix", ".do");
		registration.addInitParameter("redirectPath", "/web/pages/login.html");
		registration.addInitParameter("disabletestfilter", "Y");
		registration.setName("sessionFilter");
		registration.setOrder(Integer.MAX_VALUE);
		return registration;
	}

	/**
	 * 创建一个bean
	 * @return
	 */
	@Bean(name = "sessionFilter")
	public Filter sessionFilter() {
		return new SessionFilter();
	}

}