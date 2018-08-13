package com.multi.maven.domain;

import com.multi.maven.enums.RetCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.sql.Timestamp;


/**
 * 通用的Response JavaBean
 * </p>
 * 对于仅需要返回成功/失败的接口, 使用该通用的JavaBean
 *
 * @author yangsongbo
 * @since 3.0
 */
public class GeneralResponse implements ResponseMessage {


    private static final long serialVersionUID = 1L;

    protected String retCode;

    protected String retInfo;

    protected String tooltip;

    protected Timestamp curTime;


    @Override
    public String getTooltip() {
        return tooltip;
    }

    @Override
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public Timestamp getCurTime() {
        return curTime;
    }

    @Override
    public void setCurTime(Timestamp curTime) {
        this.curTime = curTime;
    }

    @Override
    public String getRetInfo() {
        return retInfo;
    }

    @Override
    public void setRetInfo(String retInfo) {
        this.retInfo = retInfo;
    }

    @Override
    public String getRetCode() {
        return retCode;
    }

    @Override
    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public void setRetCode(RetCodeEnum retCode) {
        this.retCode = retCode.getRetCode();
    }

    public static GeneralResponse newSuccessResponse() {
        return newResponse(RetCodeEnum.SUCCESS, null, null);
    }


    public static GeneralResponse newResponse(RetCodeEnum retCode) {
        Assert.notNull(retCode);
        return newResponse(retCode, null, null);
    }

    public static GeneralResponse newResponse(RetCodeEnum retCode, String retInfo) {
        Assert.notNull(retCode);
        return newResponse(retCode, retInfo, null);
    }


    /**
     * new GeneralResponse
     *
     * @param retCode 处理码
     * @param retInfo 可以为空，处理信息，支持自定义的处理信息
     * @param tooltip 可以为空，提示信息，支持自定义的提示信息
     * @return
     */
    public static GeneralResponse newResponse(RetCodeEnum retCode, String retInfo, String tooltip) {
        Assert.notNull(retCode);
        GeneralResponse response = new GeneralResponse();
        response.setRetCode(retCode);
        if (StringUtils.isNotBlank(retInfo)) {
            response.setRetInfo(retInfo);
        }
        if (StringUtils.isNotBlank(tooltip)) {
            response.setTooltip(tooltip);
        }
        return response;
    }


}

