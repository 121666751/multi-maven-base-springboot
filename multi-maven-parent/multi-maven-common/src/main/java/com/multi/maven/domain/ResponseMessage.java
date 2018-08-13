package com.multi.maven.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author: litao
 * @see:
 * @description:
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/3.
 */
public interface ResponseMessage extends Serializable {

    public String getRetCode();
    public Timestamp getCurTime();
    public void setCurTime(Timestamp curTime);
    public void setTooltip(String tooltip);
    public String getTooltip();
    public String getRetInfo();
    public void setRetInfo(String retInfo);
    public void setRetCode(String retCode);

}
