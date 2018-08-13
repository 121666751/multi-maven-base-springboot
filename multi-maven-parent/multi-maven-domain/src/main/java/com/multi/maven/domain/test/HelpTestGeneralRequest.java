package com.multi.maven.domain.test;

import com.multi.maven.annotations.JavaBean;
import com.multi.maven.domain.RequestMessage;

/**
 * @author: litao
 * @see:
 * @description:
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/10.
 */
@JavaBean
public class HelpTestGeneralRequest implements RequestMessage {
    @Override
    public String customCheck() {
        return null;
    }
}
