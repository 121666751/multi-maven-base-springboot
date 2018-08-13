package com.multi.maven.qiniu.beans;

/**
 * 重心参数
 * </p>
 * GravityEnum : 
 * </p>
 * NorthWest("/gravity/NorthWest","西北"),
 * </p>
 * North("/gravity/North","北"),
 * </p>
 * NorthEast("/gravity/NorthEast","东北"),
 * </p>
 * West("/gravity/West","西"),
 * </p>
 * Center("/gravity/Center","中"),
 * </p>
 * East("/gravity/East","东"),
 * </p>
 * SouthWest("/gravity/SouthWest","西南"),
 * </p>
 * South("/gravity/South","南"),
 * </p>
 * SouthEast("/gravity/SouthEast","东南"),
 * 
 * @author yangsongbo
 *
 */
public class Gravity {

	/**
	 * 重心参数
	 */
	private GravityEnum gravityEnum;
	
	
	/**
	 * Static factory method to create a Gravity instance
	 * 
	 * @param gravityEnum 重心参数，可以为空
	 * 
	 * @return a new instance
	 */
	public static Gravity instance(GravityEnum gravityEnum) {
		return new Gravity(gravityEnum);
	}
	
	public Gravity(GravityEnum gravityEnum){
		this.gravityEnum = gravityEnum;
	}
	
	public String getParams(){
		if(gravityEnum == null)
			return "";
		return gravityEnum.getValue();
	}

	
	public GravityEnum getGravityEnum() {
		return gravityEnum;
	}

	
	
	
	public enum GravityEnum{
		
		NorthWest("/gravity/NorthWest","西北"),
		North("/gravity/North","北"),
		NorthEast("/gravity/NorthEast","东北"),
		West("/gravity/West","西"),
		Center("/gravity/Center","中"),
		East("/gravity/East","东"),
		SouthWest("/gravity/SouthWest","西南"),
		South("/gravity/South","南"),
		SouthEast("/gravity/SouthEast","东南"),

		
		
		;
		
		
		
		

		private String value;
		private String displayName;

		
		public String getValue() {
			return value;
		}

		public String getDisplayName() {
			return displayName;
		}

		GravityEnum(String value, String displayName) {
			this.value = value;
			this.displayName = displayName;
		}
		
		public static GravityEnum getEnum(String value) {
			
			GravityEnum[] es = values();
			for (GravityEnum e : es) {
				if (e.getValue().equalsIgnoreCase(value)) {
					return e;
				}
			}
			return null;
		}
	}

}
