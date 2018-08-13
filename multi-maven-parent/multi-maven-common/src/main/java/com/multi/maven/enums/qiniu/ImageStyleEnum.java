package com.multi.maven.enums.qiniu;


import com.multi.maven.enums.PersistentEnum;

/**
 * 图片样式的配置，需要在七牛管理平台上进行配置
 * </p>
 * @since v1.0
 * @author yangsongbo
 *
 */
public enum ImageStyleEnum implements PersistentEnum<String> {
	
	
//	WS_MODE0_SIZE_210("-210_m0_s_wm","长边压缩210，带水印,mode 0 方式裁剪"),

	;
	
	public static final String PARTITION = "-";


	private String value;
	private String displayName;



	public String getDisplayName() {
		return displayName;
	}

	ImageStyleEnum(String value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}
	

	@Override
	public String getValue() {
		return value;
	}
	
	public static boolean hasWatermark(String url){
		ImageStyleEnum[]  values = values();
		for(ImageStyleEnum v : values){
			if(url.contains(v.getValue()))
				return true;
		}
		return false;
	}

	public static ImageStyleEnum getEnum(ImageWidthEnum w){

		return null;
	}
	
}
