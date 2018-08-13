package com.multi.maven.service;

import com.multi.maven.domain.GeneralResponse;
import com.multi.maven.domain.RequestMessage;
import org.springframework.stereotype.Service;

/**
 * @author: litao
 * @see:
 * @description:
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/10.
 */
@Service
public class TestRequestService extends BaseAbstractService {

    public GeneralResponse testRequest(RequestMessage request) {
        infoLogger("请求测试成功");
        return GeneralResponse.newSuccessResponse();
    }
}
