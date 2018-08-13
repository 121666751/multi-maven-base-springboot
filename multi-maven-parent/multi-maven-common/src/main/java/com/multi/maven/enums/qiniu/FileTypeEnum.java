package com.multi.maven.enums.qiniu;


import com.multi.maven.enums.PersistentEnum;

/**
 * 文件类型
 * </p>
 * 
 * @since v1.0
 * @author yangsongbo
 *
 */
public enum FileTypeEnum implements PersistentEnum<String> {

	IMAGE("1", "图片"),

	FILE("2", "文件"),

	;

	private String value;
	private String displayName;

	public String getDisplayName() {
		return displayName;
	}

	FileTypeEnum(String value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}

	@Override
	public String getValue() {
		return value;
	}

	public static FileTypeEnum getEnum(String value) {
		FileTypeEnum[] ems = values();
		for (FileTypeEnum em : ems) {
			if (em.getValue().equalsIgnoreCase(value)) {
				return em;
			}
		}
		return null;
	}
}
