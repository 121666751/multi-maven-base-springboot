package com.multi.maven.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 查看一个Request的请求时间
 * 
 * @author zengguangwei
 *
 */
@Slf4j
public class TimeInterceptor implements HandlerInterceptor {

	private ThreadLocal<StopWatch> threadLocal = new ThreadLocal<StopWatch>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		StopWatch watch = new StopWatch();
		threadLocal.set(watch);
		watch.start();
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView view) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
		StopWatch watch = threadLocal.get();
		watch.stop();
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(handler.toString()).append(" ").append(Thread.currentThread().getName()).append(" useTime->").append(watch.getLastTaskTimeMillis());
		log.info(sBuilder.toString());
	}

}
