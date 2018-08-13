package com.multi.maven.enums.qiniu;


import com.multi.maven.enums.PersistentEnum;

/**
 * 七牛云存储空间类型
 * @since v3.0
 * @author yangsongbo
 *
 */
public enum BucketScopeEnum implements PersistentEnum<String> {
	
	SCOPE_PUBLIC("1","公开"),

	SCOPE_PRIVATE("2","私密")
	;

	
	
	
	

	private String value;
	private String displayName;

	
	public String getValue() {
		return value;
	}

	public String getDisplayName() {
		return displayName;
	}

	BucketScopeEnum(String value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}
	
	public static BucketScopeEnum getEnum(String value) {
		
		BucketScopeEnum[] es = values();
		for (BucketScopeEnum e : es) {
			if (e.getValue().equalsIgnoreCase(value)) {
				return e;
			}
		}
		return null;
	}

}
