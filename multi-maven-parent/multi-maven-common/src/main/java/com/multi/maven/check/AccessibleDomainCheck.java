package com.multi.maven.check;

import com.multi.maven.constant.Constants;
import com.multi.maven.enums.RetCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


/**
 * 限制可访问接口的页面域名
 * 
 * @since v1.0
 * @author yangsongbo
 *
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "accessible.domain.names")
public class AccessibleDomainCheck {
	
	private String accessibleDomainNames;
	
	/**
	 * 
	 * @param request
	 * @return 处理码
	 */
	public String check(HttpServletRequest request) {
		String referer = request.getHeader("referer");
		
		if(StringUtils.isBlank(referer)){
			log.error("未获取到referer，AccessibleDomainCheck校验失败");
			return RetCodeEnum.SYS0029.getRetCode();
		}
		
		if(StringUtils.isBlank(accessibleDomainNames)){
			log.info("application.properties中未配置accessible.domain.names参数，忽略AccessibleDomainCheck校验");
			return null;
		}
		
		log.debug("AccessibleDomainCheck,referer[{}],accessible.domain.names[{}]",referer, accessibleDomainNames);

		
		String checkReferer = 
				referer.replace("http://", "").replace("https://", "").replace("HTTP://", "").replace("HTTPS://", "");
		
		for(String domainName : accessibleDomainNames.split(Constants.SEPARATOR_COMMA)){
			if(checkReferer.startsWith(domainName)){
				log.debug("referer[{}],accessible.domain.names[{}],校验成功",referer, accessibleDomainNames);
				return null;
			}
		}
		log.error("AccessibleDomainCheck校验失败");

		return RetCodeEnum.SYS0029.getRetCode();
	}

	public void setAccessibleDomainNames(String accessibleDomainNames) {
		log.info("----------------设置accessibleDomainNames:{}",accessibleDomainNames);
		this.accessibleDomainNames = accessibleDomainNames;
	}

	public String getAccessibleDomainNames() {
		return accessibleDomainNames;
	}
}
