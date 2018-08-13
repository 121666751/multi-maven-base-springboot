package com.multi.maven.interceptor;

import com.multi.maven.enums.RoleEnum;
import com.multi.maven.annotations.RoleAuth;
import com.multi.maven.exception.ForbiddenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Author: litao
 * @Description: 授权拦截器
 * @Date: 11:39 2018/8/9
 */
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

	/**
	 * 拦截器返回值:授权错误
	 */
	public static final String ERROR_RESPONSE_AUTH_FORBIDDEN = "无权访问";
	public static final String ERROR_RESPONSE_AUTH_TOKEN_ROLE = "token角色错误，请重新登录";
	public static final String ERROR_RESPONSE_AUTH_TOKEN_FAKE = "token签名错误，请重新登录";
	public static final String ERROR_RESPONSE_AUTH_TOKEN_TIMEOUT = "token过期，请重新登录";
	public static final String ERROR_RESPONSE_AUTH_JWTPARSE = "无效的token";

	public static final String HEADER_AUTH = "Authorization";
	public static final String X_USERID = "X-USERID";
	public static final String JWT_SECRET = "secret-tango";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (StringUtils.isEmpty(request.getHeader("from"))) {// 系统内部调用
			return true;
		}
		/**
		 * 用户权限控制
		 */
		String auth = request.getHeader(HEADER_AUTH);
		String userId = authorization(handler, auth);
		if (!StringUtils.isEmpty(userId)) {
			request.setAttribute(X_USERID, userId);
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView view) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
	}

	/**
	 * 权限控制
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2017年9月26日下午12:55:58
	 *
	 * @param handler
	 * @param auth
	 */
	private String authorization(Object handler, String auth) {
		if (handler == null) {
			return null;
		}
		
		Method method = null;
		RoleAuth roleAuth = null;
		if (handler instanceof HandlerMethod) {
			method = ((HandlerMethod) handler).getMethod();
			if (!method.isAnnotationPresent(RoleAuth.class)) {// 无权限控制，直接通过
				return null;
			}
			roleAuth = method.getAnnotation(RoleAuth.class);
			if (roleAuth.value() == null) {// 无需权限控制，直接通过
				return null;
			}
		}

		if (StringUtils.isEmpty(auth)) {
			log.debug("error:auth.empty");
			throw new ForbiddenException(AuthInterceptor.ERROR_RESPONSE_AUTH_TOKEN_FAKE);
		}
		/**
		 * 需要权限控制的api
		 */
		Jws<Claims> jwt = null;
		try {
			jwt = Jwts.parser().setSigningKey(JWT_SECRET.getBytes("UTF-8")).parseClaimsJws(auth);
		} catch (ExpiredJwtException e1) {
			throw new ForbiddenException(AuthInterceptor.ERROR_RESPONSE_AUTH_TOKEN_TIMEOUT);
		} catch (Exception e1) {
			throw new ForbiddenException(AuthInterceptor.ERROR_RESPONSE_AUTH_JWTPARSE);
		}
		Claims claims = jwt.getBody();
		String userId = String.valueOf(claims.get("id"));
		if (StringUtils.isEmpty(userId)) {
			log.debug("error:auth.userId.empty");
			throw new ForbiddenException(AuthInterceptor.ERROR_RESPONSE_AUTH_TOKEN_FAKE);
		}
		String userRole = String.valueOf(claims.get("role"));
		if (StringUtils.isEmpty(userRole)) {
			log.debug("error:auth.role.empty");
			throw new ForbiddenException(AuthInterceptor.ERROR_RESPONSE_AUTH_TOKEN_ROLE);
		}

		/**
		 * TODO：简单的权限控制，以后升级
		 */
		checkRole(roleAuth, userRole);

		return userId;
	}

	/**
	 * 根据业务需要自定义的权限控制
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2017年9月27日下午7:48:15
	 *
	 * @param roleAuth
	 * @param userRole
	 */
	private void checkRole(RoleAuth roleAuth, String userRole) {
		int authCode = getAuthCode(roleAuth.value().name());
		int code = getAuthCode(userRole);
		log.debug("authneed = {} & currcode = {}", authCode, code);
		if (authCode <= code) {// 需要的权限小于当前用户权限
			return;
		}
		log.info("error:auth.role roleauth={},and userRole={}", roleAuth, userRole);
		throw new ForbiddenException(AuthInterceptor.ERROR_RESPONSE_AUTH_FORBIDDEN);
	}

	/**
	 * 简单的权限控制方式，只要权限
	 * 
	 * @description
	 * @author zengguangwei
	 * @version 2017年9月28日上午10:10:34
	 *
	 * @param role
	 * @return
	 */
	private static int getAuthCode(String role) {
		int num = 0;
		if (role.equals(RoleEnum.SUPER_ADMIN.name())) {
			num = 100;
		} else if (role.equals(RoleEnum.ADMIN.name())) {
			num = 90;
		} else if (role.equals(RoleEnum.OWNER.name())) {
			num = 50;
		} else if (role.equals(RoleEnum.USER.name())) {
			num = 10;
		}
		return num;
	}
}
