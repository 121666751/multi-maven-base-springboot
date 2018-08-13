package com.multi.maven.enums.base;


import com.multi.maven.enums.PersistentEnum;

/**
 * 有效标识 Enum
 *
 */
public enum ValidStatusEnum implements PersistentEnum<String> {
	/** 有效 */
	VALID("1","有效"),

	/** 无效 */
	INVALID("2","无效");


	private String value;
	private String displayName;

	
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	private ValidStatusEnum(String value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}
	public static ValidStatusEnum getEnum(String value) {

		ValidStatusEnum[] ems = values();
		for (ValidStatusEnum em : ems) {
			if (em.getValue().equalsIgnoreCase(value)) {
				return em;
			}
		}
		return null;
	}
}
