package com.multi.maven.utils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author: litao
 * @see:
 * @description:
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/3.
 */
public class ValidateUtil {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> Set<ConstraintViolation<T>> validate(T t){
        if(t == null) {
            return null;
        }
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);
        return constraintViolations;
    }
}
