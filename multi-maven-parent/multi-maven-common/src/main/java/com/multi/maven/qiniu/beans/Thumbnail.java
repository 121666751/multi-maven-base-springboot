package com.multi.maven.qiniu.beans;

/**
 * 缩放参数
 * </p>
 * ThumbnailEnum:
 * </p>
 * MODE_1("/thumbnail/!{p}p","基于原图大小，按指定百分比缩放。Scale取值范围1-999。"),
 * </p>
 * MODE_2("/thumbnail/!{p}px","以百分比形式指定目标图片宽度，高度不变。Scale取值范围1-999。"),
 * </p>
 * MODE_3("/thumbnail/!x{p}p","以百分比形式指定目标图片高度，宽度不变。Scale取值范围1-999。"),
 * </p>
 * MODE_4("/thumbnail/{w}x","指定目标图片宽度，高度等比缩放，Width取值范围1-9999。"),
 * </p>
 * MODE_5("/thumbnail/x{h}","指定目标图片高度，宽度等比缩放，Height取值范围1-9999。"),
 * </p>
 * MODE_6("/thumbnail/{w}x{h}","等比缩放，比例值为宽缩放比和高缩放比的较小值，Width 和 Height 取值范围1-9999。"),
 * </p>
 * MODE_7("/thumbnail/!{w}x{h}r","等比缩放，比例值为宽缩放比和高缩放比的较大值，Width 和 Height 取值范围1-9999。"),
 * </p>
 * MODE_8("/thumbnail/{w}x{h}!","按指定宽高值强行缩略，可能导致目标图片变形，width和height取值范围1-9999。"),
 * </p>
 * MODE_9("/thumbnail/{w}x{h}>","等比缩小，比例值为宽缩放比和高缩放比的较小值。如果目标宽和高都大于原图宽和高，则不变，Width 和 Height 取值范围1-9999。"),
 * </p>
 * MODE_10("/thumbnail/{w}x{h}<","等比放大，比例值为宽缩放比和高缩放比的较小值。如果目标宽(高)小于原图宽(高)，则不变，Width 和 Height 取值范围1-9999。"),
 * </p>
 * MODE_11("/thumbnail/{pxArea}@","按原图高宽比例等比缩放，缩放后的像素数量不超过指定值，Area取值范围1-24999999。"),
 * 
 * @author yangsongbo
 *
 */
public class Thumbnail {
	
	/**
	 * 百分比
	 */
	private long p;
	
	/**
	 * 宽度
	 */
	private long w;
	
	/**
	 * 高度
	 */
	private long h;
	
	
	/**
	 * 像素数量不超过指定值
	 */
	private long pxArea;
	
	/**
	 *  缩放模式
	 */
	private ThumbnailEnum thumbnailEnum;
	
	
	/**
	 * Static factory method to create a Thumbnail instance
	 * @param thumbnailEnum 缩放参数，可以为空
	 * @return a new instance
	 */
	public static Thumbnail instance(ThumbnailEnum thumbnailEnum) {
		return new Thumbnail(thumbnailEnum);
	}
	
	public Thumbnail(ThumbnailEnum thumbnailEnum){
		this.thumbnailEnum = thumbnailEnum;
	}
	
	public String getParams(){
		if(thumbnailEnum == null)
			return "";
		String params = thumbnailEnum.getValue();
		params = params.replaceAll("\\{p\\}", String.valueOf(p));
		params = params.replaceAll("\\{w\\}", String.valueOf(w));
		params = params.replaceAll("\\{h\\}", String.valueOf(h));
		params = params.replaceAll("\\{pxArea\\}", String.valueOf(pxArea));

		return params;
	}
	
	public static void main(String[] args) {
		String params = "/thumbnail/{w}x{h}<";
		params = params.replace("/thumbnail/", "");
		System.out.println(params);
	}

	
	public Thumbnail setP(int p) {
		this.p = p;
		return this;
	}

	public Thumbnail setW(long w) {
		this.w = w;
		return this;
	}

	public Thumbnail setPxArea(long pxArea) {
		this.pxArea = pxArea;
		return this;
	}
	

	public Thumbnail setH(long h) {
		this.h = h;
		return this;
	}
	
	public long getP() {
		return p;
	}

	public long getW() {
		return w;
	}

	public long getH() {
		return h;
	}
	
	public long getPxArea(){
		return pxArea;
	}

	public ThumbnailEnum getMode() {
		return thumbnailEnum;
	}




	public enum ThumbnailEnum{
		
		MODE_1("/thumbnail/!{p}p","基于原图大小，按指定百分比缩放。Scale取值范围1-999。"),
		MODE_2("/thumbnail/!{p}px","以百分比形式指定目标图片宽度，高度不变。Scale取值范围1-999。"),
		MODE_3("/thumbnail/!x{p}p","以百分比形式指定目标图片高度，宽度不变。Scale取值范围1-999。"),
		MODE_4("/thumbnail/{w}x","指定目标图片宽度，高度等比缩放，Width取值范围1-9999。"),
		MODE_5("/thumbnail/x{h}","指定目标图片高度，宽度等比缩放，Height取值范围1-9999。"),
		MODE_6("/thumbnail/{w}x{h}","等比缩放，比例值为宽缩放比和高缩放比的较小值，Width 和 Height 取值范围1-9999。"),
		MODE_7("/thumbnail/!{w}x{h}r","等比缩放，比例值为宽缩放比和高缩放比的较大值，Width 和 Height 取值范围1-9999。"),
		MODE_8("/thumbnail/{w}x{h}!","按指定宽高值强行缩略，可能导致目标图片变形，width和height取值范围1-9999。"),
		MODE_9("/thumbnail/{w}x{h}>","等比缩小，比例值为宽缩放比和高缩放比的较小值。如果目标宽和高都大于原图宽和高，则不变，Width 和 Height 取值范围1-9999。"),
		MODE_10("/thumbnail/{w}x{h}<","等比放大，比例值为宽缩放比和高缩放比的较小值。如果目标宽(高)小于原图宽(高)，则不变，Width 和 Height 取值范围1-9999。"),
		MODE_11("/thumbnail/{pxArea}@","按原图高宽比例等比缩放，缩放后的像素数量不超过指定值，Area取值范围1-24999999。"),

		;

		private String value;
		private String displayName;

		
		public String getValue() {
			return value;
		}

		public String getDisplayName() {
			return displayName;
		}

		ThumbnailEnum(String value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}
		
		public static ThumbnailEnum getEnum(String value) {
			
			ThumbnailEnum[] es = values();
			for (ThumbnailEnum e : es) {
				if (e.getValue().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	} 
	
	
	
	
}
