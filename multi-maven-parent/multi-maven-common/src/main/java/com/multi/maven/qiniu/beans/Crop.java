package com.multi.maven.qiniu.beans;

/**
 * 裁剪参数
 * </p>
 * CropSizeEnum:
 * </p>
 * 		MODE_1("/crop/{w}x","指定目标图片宽度，高度不变。取值范围为0-10000。"),
 * </p>
 * 		MODE_2("/crop/x{h}","指定目标图片高度，宽度不变。取值范围为0-10000。"),
 * </p>
 * 		MODE_3("/crop/{w}x{h}","同时指定目标图片宽高。取值范围为0-10000。"),
 * </p>
 * CropOffsetEnum:
 * </p>
 * 		MODE_1("!a{dx}a{dy}","相对于偏移锚点，向右偏移dx个像素，同时向下偏移dy个像素。取值范围不限，小于原图宽高即可。"),
 * </p>
 * 		MODE_2("!-{dx}a{dy}","相对于偏移锚点，从指定宽度中减去dx个像素，同时向下偏移dy个像素。取值范围不限，小于原图宽高即可。"),
 * </p>
 * 		MODE_3("!a{dx}-{dy}","相对于偏移锚点，向右偏移dx个像素，同时从指定高度中减去dy个像素。取值范围不限，小于原图宽高即可。"),
 * </p>
 * 		MODE_4("!-{dx}-{dy}","相对于偏移锚点，从指定宽度中减去dx个像素，同时从指定高度中减去dy个像素。取值范围不限，小于原图宽高即可。"),
 * 	
 * @author yangsongbo
 *
 */
public class Crop {
	
	/**
	 * 宽度
	 */
	private long w;
	
	/**
	 * 高度
	 */
	private long h;
	
	/**
	 * x轴偏移参数（像素）
	 */
	private long dx;
	
	/**
	 * y轴偏移参数（像素）
	 */
	private long dy;
	
	
	/**
	 * 裁剪操作参数
	 */
	private CropSizeEnum cropSizeEnum;
	
	
	/**
	 * 裁剪偏移参数
	 */
	private CropOffsetEnum cropOffsetEnum;
	
	
	/**
	 * Static factory method to create a Crop instance
	 * 
	 * @param cropSizeEnum 裁剪操作参数,可以为空
	 * @param cropOffsetEnum 裁剪偏移参数，可以为空
	 * 
	 * @return a new instance
	 */
	public static Crop instance(CropSizeEnum cropSizeEnum,CropOffsetEnum cropOffsetEnum) {
		return new Crop(cropSizeEnum,cropOffsetEnum);
	}
	
	public Crop(CropSizeEnum cropSizeEnum,CropOffsetEnum cropOffsetEnum){
		this.cropSizeEnum = cropSizeEnum;
		this.cropOffsetEnum = cropOffsetEnum;
	}
	
	
	public String getParams(){
		if(cropSizeEnum == null)
			return "";
		String cropSize  = "";
		String cropOffset  = "";
		if(cropSizeEnum !=null){
			cropSize = cropSizeEnum.getValue();
			cropSize = cropSize.replaceAll("\\{w\\}", String.valueOf(w));
			cropSize = cropSize.replaceAll("\\{h\\}", String.valueOf(h));
			
			if(cropOffsetEnum !=null){
				cropOffset = cropOffsetEnum.getValue();
				cropOffset = cropOffset.replaceAll("\\{dx\\}", String.valueOf(h));
				cropOffset = cropOffset.replaceAll("\\{dy\\}", String.valueOf(h));
			}
		}
		
		return cropSize+cropOffset;
	}
	
	


	public Crop setW(long w) {
		this.w = w;
		return this;
	}

	public Crop setH(long h) {
		this.h = h;
		return this;
	}

	public Crop setDx(long dx) {
		this.dx = dx;
		return this;
	}
	
	public Crop setDy(long dy) {
		this.dy = dy;
		return this;
	}


	public long getW() {
		return w;
	}


	public long getH() {
		return h;
	}


	public long getDx() {
		return dx;
	}


	public long getDy() {
		return dy;
	}
	
	
	public enum CropSizeEnum{
		MODE_1("/crop/{w}x","指定目标图片宽度，高度不变。取值范围为0-10000。"),
		MODE_2("/crop/x{h}","指定目标图片高度，宽度不变。取值范围为0-10000。"),
		MODE_3("/crop/{w}x{h}","同时指定目标图片宽高。取值范围为0-10000。"),
		
		;


		private String value;
		private String displayName;

		
		public String getValue() {
			return value;
		}

		public String getDisplayName() {
			return displayName;
		}

		CropSizeEnum(String value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}
		
		public static CropSizeEnum getEnum(String value) {
			
			CropSizeEnum[] es = values();
			for (CropSizeEnum e : es) {
				if (e.getValue().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}
	
	
	public enum CropOffsetEnum{
		MODE_1("!a{dx}a{dy}","相对于偏移锚点，向右偏移dx个像素，同时向下偏移dy个像素。取值范围不限，小于原图宽高即可。"),
		MODE_2("!-{dx}a{dy}","相对于偏移锚点，从指定宽度中减去dx个像素，同时向下偏移dy个像素。取值范围不限，小于原图宽高即可。"),
		MODE_3("!a{dx}-{dy}","相对于偏移锚点，向右偏移dx个像素，同时从指定高度中减去dy个像素。取值范围不限，小于原图宽高即可。"),
		MODE_4("!-{dx}-{dy}","相对于偏移锚点，从指定宽度中减去dx个像素，同时从指定高度中减去dy个像素。取值范围不限，小于原图宽高即可。"),

		;


		private String value;
		private String displayName;

		
		public String getValue() {
			return value;
		}

		public String getDisplayName() {
			return displayName;
		}

		CropOffsetEnum(String value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}
		
		public static CropOffsetEnum getEnum(String value) {
			
			CropOffsetEnum[] es = values();
			for (CropOffsetEnum e : es) {
				if (e.getValue().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}
	
	

}
