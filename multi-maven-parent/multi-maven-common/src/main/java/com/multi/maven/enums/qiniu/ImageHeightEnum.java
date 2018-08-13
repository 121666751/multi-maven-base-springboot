package com.multi.maven.enums.qiniu;

import com.multi.maven.enums.PersistentEnum;

/**
 * 指定图片高度
 * </p>
 * @author yangsongbo
 *
 */
public enum ImageHeightEnum implements PersistentEnum<Integer> {

	SIZE_0(0,"原图"),
	SIZE_60(60,"60"),
	SIZE_68(68,"68"),
	SIZE_80(80,"80"),
	SIZE_88(88,"88"),
	SIZE_90(90,"90"),
	SIZE_100(100,"100"),
	SIZE_110(110,"110"),
	SIZE_120(120,"120"),
	SIZE_128(128,"128"),
	SIZE_140(140,"140"),
	SIZE_148(148,"148"),
	SIZE_150(150,"150"),
	SIZE_160(160,"160"),
	SIZE_168(168,"168"),
	SIZE_180(180,"180"),
	SIZE_210(210,"210"),
	SIZE_220(220,"220"),
	SIZE_226(226,"226"),
	SIZE_230(230,"230"),
	SIZE_250(250,"250"),
	SIZE_280(280,"280"),
	SIZE_300(300,"300"),
	SIZE_320(320,"320"),
	SIZE_342(342,"342"),
	SIZE_360(360,"360"),
	SIZE_400(400,"400"),
	SIZE_420(420,"420"),
	SIZE_600(600,"600"),
	SIZE_640(640,"640"),
	SIZE_612(612,"612"),
	SIZE_690(690,"690"),
	SIZE_712(712,"712"),
	SIZE_750(750,"750");
	
	
	
	
	

	private Integer value;
	private String displayName;



	public String getDisplayName() {
		return displayName;
	}

	ImageHeightEnum(Integer value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}
	

	@Override
	public Integer getValue() {
		
		return value;
	}

	public static ImageHeightEnum getEnum(Integer value) {
		
		ImageHeightEnum[] es = values();
		for (ImageHeightEnum e : es) {
			if (e.getValue() == value) {
				return e;
			}
		}
		return null;
	}
	
}
