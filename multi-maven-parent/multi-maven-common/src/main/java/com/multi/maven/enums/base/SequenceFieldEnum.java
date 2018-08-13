package com.multi.maven.enums.base;


import com.multi.maven.enums.PersistentEnum;

/**
 * 序列 Enum
 *
 */
public enum SequenceFieldEnum implements PersistentEnum<String> {
	
	/**
	 * 排序ID
	 */
	SORT_ID("00","排序ID"),
	
	/**
	 * 开发者ID
	 */
	DEVELOPER_ID("02","开发者ID"),
	/**
	 * 应用ID
	 */
	APP_ID("03", "应用ID"),
	/**
	 * 接口ID
	 */
	INTERFACE_ID("04", "接口ID"),
	/**
	 * 应用关联接口ID
	 */
	UNION_ID("05", "应用关联接口ID"),
	
	/**
	 * 订单ID
	 */
	ORDER_ID("06","订单ID"),
	
	/**
	 * 扣费ID
	 */
	DEDUCTION_ID("07","扣费ID"),
	
	;


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

	private SequenceFieldEnum(String value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}
	public static SequenceFieldEnum getEnum(String value) {

		SequenceFieldEnum[] ems = values();
		for (SequenceFieldEnum em : ems) {
			if (em.getValue().equalsIgnoreCase(value)) {
				return em;
			}
		}
		return null;
	}
	
}
