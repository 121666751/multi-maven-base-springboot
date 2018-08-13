package com.multi.maven.filter;

import com.multi.maven.constant.CharsetConstants;
import com.multi.maven.constant.Constants;
import com.multi.maven.domain.GeneralResponse;
import com.multi.maven.domain.SessionBean;
import com.multi.maven.enums.RetCodeEnum;
import com.multi.maven.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 后台管理系统Session过滤器，用于判断用户是否登录
 */
public class SessionFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(SessionFilter.class);

	protected static final String CONTENTTYPE_JOSN = "application/json";

	public FilterConfig config;

	@Override
	public void destroy() {
		this.config = null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest hrequest = (HttpServletRequest) request;
		HttpServletResponse hresponse = (HttpServletResponse) response;

		HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) response);

		String excludeURI = config.getInitParameter("excludeURI"); // 登录页面
		String includeSuffix = config.getInitParameter("includeSuffix"); // 过滤资源后缀参数
		String redirectPath = hrequest.getContextPath() + config.getInitParameter("redirectPath");// 登录转向页面
		String disabletestfilter = config.getInitParameter("disabletestfilter");// 过滤器是否有效

		if (disabletestfilter.toUpperCase().equals("Y")) { // 过滤无效
			chain.doFilter(request, response);
			return;
		}

		if (!this.isContains(hrequest.getRequestURI(), includeSuffix.split(";"))) {// 只对指定过滤参数后缀进行过滤
			chain.doFilter(request, response);
			return;
		}

		if (this.isContains(hrequest.getRequestURI(), excludeURI.split(";"))) {//不需要过滤的资源
			chain.doFilter(request, response);
			return;
		}

		SessionBean sessionBean = (SessionBean) hrequest.getSession().getAttribute(Constants.SESSION_BEAN);// 判断用户是否登录
		
		if (sessionBean == null) {
			logger.info("session已失效,重定向到{}"+redirectPath);
			wrapper.sendRedirect(redirectPath);
			toResWriter(hresponse, JsonUtil.toJson
					(GeneralResponse.newResponse(RetCodeEnum.LOGOUT, "登录失效", "登录失效")));
			return;
		} else {
			logger.debug("当前访问的用户是：{}",sessionBean.getUserDto());
			chain.doFilter(request, response);
			return;
		}
	}

	public boolean isContains(String container, String[] regx) {

		boolean result = false;

		if (regx == null || regx.length == 0) {
			return result;
		}
		if (StringUtils.isBlank(container)) {
			return result;
		}
		for (int i = 0; i < regx.length; i++) {
			if (container.indexOf(regx[i]) != -1) {
				return true;
			}
		}
		return result;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		config = filterConfig;
	}

	/**
	 * 返回JOSN到请求端
	 * 
	 * @param response
	 * @param josnData
	 *            josn数据
	 * @throws IOException
	 */
	private void toResWriter(HttpServletResponse response, String josnData) {
		PrintWriter out = null;
		response.setContentType(CONTENTTYPE_JOSN);
		response.setCharacterEncoding(CharsetConstants.CHARSET_UTF8);
		try {
			out = response.getWriter();
		} catch (IOException e) {
		}
		out.write(josnData);
	}

}
