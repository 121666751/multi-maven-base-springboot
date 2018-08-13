package com.multi.maven.config;

import com.multi.maven.interceptor.TimeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by hbhwypw on 2017/2/16.
 * 定义MVC配置 注册拦截器 过滤器
 *
 */
@Configuration
public class CustomWebAppConfiguration extends WebMvcConfigurerAdapter {

    /**
     * @Author: litao
     * @Description: 静态资源
     * @Date: 17:43 2018/8/7
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }

    /**
     * @Author: litao
     * @Description: 添加拦截器
     * @Date: 17:43 2018/8/7
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TimeInterceptor());
    }

    /**
     * 跨域处理,允许所有的跨域，跨域放到网关处理
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedHeaders("*").allowedMethods("*").allowedOrigins("*");
    }



}
