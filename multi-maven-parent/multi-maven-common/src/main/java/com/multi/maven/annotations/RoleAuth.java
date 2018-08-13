package com.multi.maven.annotations;

import com.multi.maven.enums.RoleEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author tango
 * @desc
 * @date 2016-11-29
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleAuth {

    /**
     * 鉴权角色类型(默认房客)
     * @return
     */
    RoleEnum value() default RoleEnum.USER;
}
