package com.multi.maven.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tango
 * @desc 基础 抽象service 提供日志
 * @date 2016-11-02
 */
@Slf4j
public abstract class BaseAbstractService {

    public void debugLogger(String msg) {
        log.debug(msg);
    }

    public void debugLogger(String msg, Throwable throwable){
        log.debug(msg, throwable);
    }

    public void debugLogger(String format, Object... arguments){
        log.debug(format, arguments);
    }
    public void infoLogger(String msg) {
        log.info(msg);
    }

    public void infoLogger(String msg, Throwable throwable) {
        log.info(msg, throwable);
    }

    public void infoLogger(String format, Object... arguments) {
        log.info(format, arguments);
    }
    public void warnLogger(String msg) {
        log.warn(msg);
    }

    public void warnLogger(String msg, Throwable throwable){
        log.warn(msg, throwable);
    }

    public void warnLogger(String format, Object... arguments){
        log.warn(format, arguments);
    }

    public void errorLogger(String msg) {
        log.error(msg);
    }

    public void errorLogger(String msg, Throwable throwable){
        log.error(msg, throwable);
    }

    public void errorLogger(String format, Object... arguments){
        log.error(format, arguments);
    }
}
