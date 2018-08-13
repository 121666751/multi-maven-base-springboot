package com.multi.maven.enums.redis;


import com.multi.maven.enums.PersistentEnum;

/**
 * Redis Key 有效时间配置
 * @author yangsongbo
 * @since v1.0
 *
 */
public enum RedisExpireEnum implements PersistentEnum<Long> {
	
	INFINITE(0L,"不设置有效期"),
	
	SECOND_DAY_90(3600L * 24L * 90L, "以秒计算,90天"),

	SECOND_DAY_30(3600L * 24L * 30L, "以秒计算,30天"),
	
	SECOND_DAY_10(3600L * 24L * 10L, "以秒计算,10天"),

	SECOND_DAY_7(3600L * 24L * 7L, "以秒计算,7天"),
	
	SECOND_DAY_5(3600L * 24L * 5L,"以秒计算,5天"),
	
	SECOND_DAY_3(3600L * 24L * 3L,"以秒计算,3天"),

	SECOND_DAY_2(3600L * 24L * 2L,"以秒计算,2天"),

	SECOND_DAY_1(3600L * 24L * 1L,"以秒计算,1天"),
	
	SECOND_HOUR_3(3600L * 3L,"以秒计算,3小时"),
	
	SECOND_MIN_1(60L * 1L,"以秒计算,10分钟"),

	SECOND_MIN_10(60L * 10L,"以秒计算,10分钟"),

	SECOND_MIN_30(60L * 30L,"以秒计算,30分钟"),
	
	SECOND_MIN_100(60L * 100L,"以秒计算,100分钟"),

	
	SECOND_10(10L,"以秒计算,10秒");


	private Long value;
	private String displayName;

	public void setValue(Long value) {
		this.value = value;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public Long getValue() {
		return value;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	private RedisExpireEnum(Long value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}
	
	
}
