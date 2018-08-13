package com.multi.maven.controller;

import com.alibaba.fastjson.JSON;
import com.multi.maven.check.AccessibleDomainCheck;
import com.multi.maven.context.CacheContext;
import com.multi.maven.domain.GeneralResponse;
import com.multi.maven.domain.RequestMessage;
import com.multi.maven.domain.ResponseMessage;
import com.multi.maven.domain.ServiceBean;
import com.multi.maven.enums.RetCodeEnum;
import com.multi.maven.exception.BusinessException;
import com.multi.maven.utils.DateUtil;
import com.multi.maven.utils.JsonUtil;
import com.multi.maven.utils.LogTraceUtil;
import com.multi.maven.utils.LogTraceUtil.LogTrace;
import com.multi.maven.utils.ReflectionUtil;
import com.multi.maven.utils.UUIDUtil;
import com.multi.maven.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Controller
@Slf4j
public class ManageBaseController extends BaseController{

	protected static final String REQUESTBEAN = "requestBean";
	protected static final String IGNORECHECK = "ignoreCheck";
	protected static final String SERVICE = "service";
	protected static final String EXTENDLIST = "extendlist";

	//注入配置文件
	@Resource
	private Map<String,?> webConfig;
	
	@Autowired
	private AccessibleDomainCheck accessibleDomainCheck;


	//拦截所有request请求post/get
	@SuppressWarnings("all")
	@RequestMapping(value="/**")
	public void execute(@RequestBody(required = false) String message, HttpServletRequest request, HttpServletResponse response) {
		String requestMethod = request.getMethod();
		Map<String, String[]> parameterMap = request.getParameterMap();
		if ("GET".equalsIgnoreCase(requestMethod) && null != parameterMap && !parameterMap.isEmpty()) {
			Map<String, Object> paramMap = initParameter(parameterMap);
			message = JSON.toJSONString(paramMap);
		}
		//获取servletPath
		String servletPath = request.getServletPath();
//		String model = servletPath.substring(1, servletPath.indexOf("/",2)).replace("/", "");
		String uuid = UUIDUtil.getObjectId();
//		LogTraceUtil.putLogTrace(LogTrace.MODEL, model);
		LogTraceUtil.putLogTrace(LogTrace.SERVLETPATH, servletPath);
		LogTraceUtil.putLogTrace(LogTrace.UUID, uuid);
		log.info("接收到请求：{}",message);
		Map<String,?> contextMap = new ConcurrentHashMap<String, Object>();
		RequestMessage requestMessage = null;
		ResponseMessage responseMessage = null;
		try {
			//IP访问频率校验
			/*if(!checkIP(getRemoteIP(request))) {
				GeneralResponse error = super.wrapperErrorResponse(RetCodeEnum.SYS0003);
				super.writeJsonResponse(response,error);
				return;
			}*/
			//根据requestURI获得流程配置信息
			Map<String, ?> map = null;
			try {
				map = (Map<String, ?>) webConfig.get(servletPath);
			} catch (Throwable throwable) {
				log.error("spring context 没有找到 [requestBeanIndex] bean");
				GeneralResponse error = super.wrapperErrorResponse(RetCodeEnum.SYS0001);
				super.writeJsonResponse(response,error);
				return;
			}
			if(map==null) {
				log.error("requestIndex 中没有找到配置[{}]",servletPath);
				GeneralResponse error = super.wrapperErrorResponse(RetCodeEnum.SYS0001);
				super.writeJsonResponse(response,error);
				return;
			}
			String ignorCheck = (String) map.get(IGNORECHECK);
			String[] ignorCheckArray = null;
			if(StringUtils.isNotBlank(ignorCheck)) {
				ignorCheckArray = ignorCheck.split(";");
			}
			//request string to bean
			requestMessage = (RequestMessage) map.get(REQUESTBEAN);
			requestMessage = JsonUtil.fromJson(message, requestMessage.getClass());
			//set session   
//			requestMessage.setSession(request.getSession());
			//bean check
			Set<ConstraintViolation<RequestMessage>> validateResult = ValidateUtil.validate(requestMessage);
			if(validateResult!=null&&!validateResult.isEmpty()) {
				ConstraintViolation<RequestMessage> cv = validateResult.iterator().next();
				log.error("类："+cv.getLeafBean().getClass()+",属性:"+cv.getPropertyPath()+"值："+cv.getInvalidValue()+"，校验失败");
				String errorCode = cv.getMessage();
				RetCodeEnum errorEnum = RetCodeEnum.valueOf(errorCode);
				GeneralResponse error = super.wrapperErrorResponse(errorEnum);
				super.writeJsonResponse(response,error);
				return;
			}
			//自定义校验
			String errorCode = requestMessage.customCheck();
			
			if(StringUtils.isBlank(errorCode)){
				//访问域名限制校验
				errorCode = accessibleDomainCheck.check(request);
			}
			
			if(StringUtils.isNotBlank(errorCode)) {
				RetCodeEnum errorEnum = RetCodeEnum.valueOf(errorCode);
				GeneralResponse error = super.wrapperErrorResponse(errorEnum);
				super.writeJsonResponse(response,error);
				return;
			}
			
			//反射执行service方法
			ServiceBean serviceBean = (ServiceBean) map.get(SERVICE);
			Object service = serviceBean.getServiceInstance();
			//默认为post
			Method method = serviceBean.getMethod();
			if(method == null) {
				try {
					method = ReflectionUtil.getPublicMethod(service.getClass(), serviceBean.getMethodName(), new Class[] {requestMessage.getClass()});
				} catch (NoSuchMethodException e) {
					log.error("[{}]中没有找到参数为[{}],名称为[{}]的方法",service.getClass(),requestMessage.getClass(),serviceBean.getMethodName());
					GeneralResponse error = super.wrapperErrorResponse(RetCodeEnum.SYS0001);
					super.writeJsonResponse(response,error);
					return;
				}
				serviceBean.setMethod(method);
			}
			try {
				//service执行完成，返回ResponseMessage
				responseMessage = (ResponseMessage) method.invoke(service, requestMessage);
			} catch (Throwable e) {
				log.error("service执行异常",e);
				List<Throwable> throwList = ExceptionUtils.getThrowableList(e);
				if(CollectionUtils.isNotEmpty(throwList)) {
					for(Throwable th : throwList) {
						if(th instanceof BusinessException) {
							BusinessException ex = (BusinessException)th;
							if(null!=ex.getRetCode()) {
								responseMessage = new GeneralResponse();
								responseMessage.setRetCode(ex.getRetCode().name());
							}
							break;
						}
					}
				}
			}
			if(responseMessage.getRetCode()==null) {
				responseMessage.setRetCode(RetCodeEnum.SYS0001.getRetCode());
			}
			RetCodeEnum result = RetCodeEnum.valueOf(responseMessage.getRetCode());
			if(result==null) {
				throw new BusinessException("返回码["+responseMessage.getRetCode()+"]在RetCodeEnum中没有配置");
			}
			if(StringUtils.isNotBlank(result.getRetInfo())){
				responseMessage.setRetInfo(result.getRetInfo());
			}
			
			if(StringUtils.isBlank(responseMessage.getTooltip())){
				responseMessage.setTooltip(result.getTooltip());
			}
			if(responseMessage.getCurTime()==null) {
				responseMessage.setCurTime(DateUtil.currentTimestamp());
			}
			super.writeJsonResponse(response,responseMessage);
			//缓存更新后通用处理
			List<String> deleteList = CacheContext.getDeleteKeyList();
			if(CollectionUtils.isNotEmpty(deleteList)) {
				redisClient.deleteKey(deleteList.toArray(new String[deleteList.size()]));
			}
		}catch(Throwable throwable) {
			log.error("流程异常",throwable);
			GeneralResponse error = super.wrapperErrorResponse(RetCodeEnum.SYS0001);
			super.writeJsonResponse(response,responseMessage);
			return;
		}
		
	}


	/**
	 * get请求参数转换
	 *
	 * @param parameterMap
	 * @return
	 */
	private Map<String, Object> initParameter(Map<String, String[]> parameterMap) {
		Map<String, Object> params = new HashMap<>();
		for (Iterator iter = parameterMap.keySet().iterator(); iter.hasNext(); ) {
			String name = (String) iter.next();
			String[] values = (String[]) parameterMap.get(name);
			List<String> valueList = new ArrayList<>();
			if (1 == values.length) {
				params.put(name, values[0]);
			} else {
				params.put(name, Arrays.asList(values));
			}
			/*for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);*/
		}
		return params;
	}

}
