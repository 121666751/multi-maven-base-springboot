package com.multi.maven.enums;


/**
 * 手机运营商枚举
 */
public enum MobileOperationEnum {

    /**
     * 1 中国移动 
     * 2 中国联通 
     * 3 中国电信 
     * 0 无法识别 
     */
    UNDEFINED((byte) 0), CHINA_MOBILE((byte) 1), CHINA_UNICOM((byte) 2), CHINA_TELECOM((byte) 3);
    private byte v;

    MobileOperationEnum(byte value) {
        this.v = value;
    }

    public byte getCode() {
        return this.v;
    }

}
