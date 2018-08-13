package com.multi.maven.enums.redis;


import com.multi.maven.enums.PersistentEnum;

/**
 * Redis Key 长度控制
 * @author yangsongbo
 * @since v1.0
 *
 */
public enum RedisSizeEnum implements PersistentEnum<Integer> {

	SIZE_100(100, "长度100");
	
	
	private Integer value;
	private String displayName;

	public void setValue(Integer value) {
		this.value = value;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	private RedisSizeEnum(Integer value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}


}
