package com.multi.maven.domain;

import java.io.Serializable;

/**
 * @author: litao
 * @see:
 * @description:
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/3.
 */
public interface RequestMessage extends Serializable {

    /**
     * 自定义校验
     * @return
     */
    public String customCheck();

}
