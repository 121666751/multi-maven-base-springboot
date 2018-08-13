package com.multi.maven.enums.base;


import com.multi.maven.enums.PersistentEnum;

/**
 * 系统参数表Key
 * </p>  注册 快捷登录 查看/更换公钥  找回密码 修改信息 修改登录手机号
 *
 * @author yangsongbo
 * @since v3.0
 */
public enum ParamKeyEnum implements PersistentEnum<String> {

    /**支付宝支付支持标识
     ALIPAY_SUPPORT("ALIPAY_SUPPORT","支付宝支付支持标识"),
     */
    /**
     * 注册验证码短信话术
     */
    SMS_CONTENTE_REGISTER("SMS_CONTENTE_REGISTER", "注册验证码短信话术"),
    /**
     * 查看/更换公钥验证码短信话术
     **/
    SMS_CONTENTE_RESET_PUBKEY("SMS_CONTENTE_RESET_PUBKEY", "查看/更换公钥验证码短信话术"),
    /**
     * 找回密码验证码短信话术
     */
    SMS_CONTENTE_RESET_PASSWORD("SMS_CONTENTE_RESET_PASSWORD", "找回密码验证码短信话术"),
    /**
     * 修改信息验证码短信话术
     */
    SMS_CONTENTE_MODIFY_BASEINFO("SMS_CONTENTE_MODIFY_BASEINFO", "修改信息验证码短信话术"),
    /**
     * 更换手机号验证码短信话术
     */
    SMS_CONTENTE_REPLACE_PHONE("SMS_CONTENTE_REPLACE_PHONE", "更换手机号验证码短信话术"),
    /**
     * 登录验证码短信话术
     */
    SMS_CONTENTE_VERIFY_LOGIN("SMS_CONTENTE_VERIFY_LOGIN", "登录验证码短信话术"),
    /**
     * 申请认证验证码邮件话术
     */
    SMS_CONTENTE_DEVELOPER_AUTH("SMS_CONTENTE_DEVELOPER_AUTH", "申请认证验证码邮件话术"),
    /**
     * 更换手机号通知短信话术
     */
    SMS_CONTENTE_REPLACE_PHONE_NOTIFY("SMS_CONTENTE_REPLACE_PHONE_NOTIFY", "更换手机号通知短信话术"),
    /**
     * 更换手机号验证码短信话术
     */
    SMS_CONTENTE_REPLACE_PHONE_NEW("SMS_CONTENTE_REPLACE_PHONE_NEW", "更换手机号验证码短信话术"),
    /**
     * 申请认证通知短信话术
     */
    SMS_CONTENTE_DEVELOPER_AUTH_NOTIFY("SMS_CONTENTE_DEVELOPER_AUTH_NOTIFY", "申请认证通知短信话术"),
    /**
     * 认证通过通知短信话术
     */
    SMS_CONTENTE_AUDIT_PASS_NOTIFY("SMS_CONTENTE_AUDIT_PASS_NOTIFY", "认证通过通知短信话术"),
    /**
     * 认证失败通知短信话术
     */
    SMS_CONTENTE_AUDIT_FAILURE_NOTIFY("SMS_CONTENTE_AUDIT_FAILURE_NOTIFY", "认证失败通知短信话术"),
    
    /**
     * health ai网站地址
     */
    HEALTH_AI_WEBSITE_URL("HEALTH_AI_WEBSITE_URL", "health ai网站地址"),

    /**
     * health ai开发支持邮箱
     */
    HEALTH_AI_SUPPORT_EMAIL("HEALTH_AI_SUPPORT_EMAIL", "health ai开发支持邮箱"),
    
    
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

    private ParamKeyEnum(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

}
