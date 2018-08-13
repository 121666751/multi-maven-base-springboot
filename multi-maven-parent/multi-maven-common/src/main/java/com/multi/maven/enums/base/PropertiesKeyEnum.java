package com.multi.maven.enums.base;


import com.multi.maven.enums.PersistentEnum;

/**
 * 参数Key
 * </p>
 * @since v1.0
 * @author yangsongbo
 *
 */
public enum PropertiesKeyEnum implements PersistentEnum<String> {
	
	WEIXIN_APPID("mp.weixin.appid","微信APPID",""),
	WEIXIN_SECRET("mp.weixin.appsecret","微信SECRET",""),
	
	WEIXIN_ACCESS_TOKEN_KEY("mp.weixin.access.token.key","accessToken的redis缓存key",""),
	WEIXIN_JSAPI_TICKET_KEY("mp.weixin.jsapi.ticket.key","jsapiTicket的redis缓存key",""),
	
	ACCESSIBLE_DOMAIN_NAMES("accessible.domain.names","限制访问接口的页面地址(域名或IP地址),多个地址以,分隔",""),
	
	REDIS_SCOPE("spring.redis.scope","reids空间",""),

	;


	private String value;
	private String displayName;
	private String defaultValue;

	
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	public String getDefaultValue() {
		return defaultValue;
	}
	
	private PropertiesKeyEnum(String value, String displayName,String defaultValue) {
		this.value = value;
		this.displayName = displayName;
		this.defaultValue = defaultValue;
	}

}
