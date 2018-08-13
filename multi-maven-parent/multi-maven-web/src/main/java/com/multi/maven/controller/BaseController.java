package com.multi.maven.controller;

import com.multi.maven.constant.CharsetConstants;
import com.multi.maven.dao.redis.RedisClient;
import com.multi.maven.domain.GeneralResponse;
import com.multi.maven.domain.ResponseMessage;
import com.multi.maven.enums.RetCodeEnum;
import com.multi.maven.enums.redis.SetKeyEnum;
import com.multi.maven.enums.redis.StringKeyEnum;
import com.multi.maven.utils.DateUtil;
import com.multi.maven.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class BaseController {

	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

	protected static final String BLACK_IP = SetKeyEnum.SET_BLACK_IP.getKey();

	protected static final String CONTENTTYPE_JOSN="application/json";

	
	@Autowired
	protected RedisClient redisClient;
	
	
	/**
	 * 获取访问IP地址
	 * @param request
	 * @return
	 */
  	protected String getRemoteIP(HttpServletRequest request) {
  		String ip = request.getHeader("x-forwarded-for");   
  	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
  	    	ip = request.getHeader("Proxy-Client-IP");   
  		}   
  		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
  		   ip = request.getHeader("WL-Proxy-Client-IP");   
  		 
  		}   
  	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
  	       ip = request.getRemoteAddr();   
  	    }
  	    int index = ip.indexOf(",");
		if (index != -1) {
			return ip.substring(0, index);
		} else {
			return ip;
		}
  	}
  	
  	/**
  	 * @Author: litao
  	 * @Description: 访问ip校验 访问次数和ip黑名单
  	 * @Date: 17:51 2018/8/7
  	 */
  	protected boolean checkIP(String ipAddress) {
		//如果是ip黑名单,校验失败
		if(redisClient.isSetMember(BLACK_IP, ipAddress)) {
			return false;
		}
		String key = StringKeyEnum.STRING_IP_ACCESS.getKey(ipAddress);
		
		long currentCount = redisClient.incrementAndGet(key);
		if(currentCount==1) {
			boolean expire = redisClient.expireKey(key, 10);
			if(!expire) {
				logger.error("[{}]设置过期时间失败，注意人工处理！", ipAddress);
			}
		}
		if(currentCount>200) {
			logger.error("IP[{}]校验失败,当前访问次数[{}]",ipAddress,currentCount);
			//加入ip黑名单
			redisClient.addToSet(BLACK_IP, ipAddress);
			return false;
		}
		return true;
	}
  	
  	
  	protected GeneralResponse wrapperErrorResponse(RetCodeEnum retCode) {
		GeneralResponse response = new GeneralResponse();
		response.setRetCode(retCode);
		//TODU：填充回执信息
		response.setRetInfo(retCode.getRetInfo());
		response.setTooltip(retCode.getTooltip());
		response.setCurTime(DateUtil.currentTimestamp());
		return response;
	}
	
	/**
	 * 返回response
	 * @param response
	 * @param message
	 */
  	protected String writeJsonResponse(HttpServletResponse response, ResponseMessage message) {
		boolean success = RetCodeEnum.SUCCESS.getRetCode().equals(message.getRetCode());
		//返回时全部转成String 格式
		String responseMessage = JsonUtil.toJson(message,true);
		if(success) {
			logger.info("处理成功,返回的response:{}",responseMessage);
		}else {
			if(RetCodeEnum.BIZWARN.getRetCode().equals(message.getRetCode())) {
				logger.warn("处理成功,返回的response:{}",responseMessage);
			}else {
				logger.error("处理失败,返回的response:{}",responseMessage);
			}
		}
        response.setContentType(CONTENTTYPE_JOSN);
        response.setCharacterEncoding(CharsetConstants.CHARSET_UTF8);
        PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(responseMessage);
		} catch (IOException e) {
			logger.error("writeResponse error",e);
		} finally {
			if(out!=null) {
				out.close();
			}
		}
		return responseMessage;
        
	}
	
  	
}
