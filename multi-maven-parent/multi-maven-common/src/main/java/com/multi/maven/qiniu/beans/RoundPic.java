package com.multi.maven.qiniu.beans;

/**
 * 图片圆角处理
 * 
 * @author yangsongbo
 *
 */
public class RoundPic {

	
	/**
	 * 圆角大小的参数，水平和垂直的值相同，可以使用像素数（如200）
	 * 或百分比（如!25p）。不能与radiusx和radiusy同时使用。
	 */
	private String radius;

	/**
	 * 圆角水平大小的参数，可以使用像素数（如200）或百分比（如!25p）。需要与radiusy同时使用。
	 */
	private String radiusx;
	
	/**
	 * 圆角垂直大小的参数，可以使用像素数（如200）或百分比（如!25p）。需要与radiusx同时使用。
	 */
	private String radiusy;

	public RoundPic() {

	}

	public RoundPic(String radius) {
		this.radius = radius;

	}

	
	public RoundPic(String radiusx,String radiusy) {
		this.radiusx = radiusx;
		this.radiusy = radiusy;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	public String getRadiusx() {
		return radiusx;
	}

	public void setRadiusx(String radiusx) {
		this.radiusx = radiusx;
	}

	public String getRadiusy() {
		return radiusy;
	}

	public void setRadiusy(String radiusy) {
		this.radiusy = radiusy;
	}

}
