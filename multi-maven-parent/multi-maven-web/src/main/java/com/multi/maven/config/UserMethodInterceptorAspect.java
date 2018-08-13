package com.multi.maven.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 方法切面，提供入参和出参的日志输出，方法执行时间
 */
@Component
@Aspect
public class UserMethodInterceptorAspect {
    private static final Logger log = LoggerFactory.getLogger(UserMethodInterceptorAspect.class);

    private final static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final static Validator validator = factory.getValidator();

    /**
     * 环绕通知，扫描kb 包下面的方法
     *
     * @param pjp 切入点方法
     * @throws Throwable 异常
     */
    @Around("execution(* com.tango.bizservice..*.*Controller.*(..)) || execution(* com.tango.bizservice..*.*Service.*(..))")
//    @Around("execution(* com.tango.bizservice..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = pjp.getSignature().getName();
        Object[] objects = pjp.getArgs();
        String className = pjp.getTarget().getClass().getSimpleName();
        boolean flag = true;
        for(Object object:objects){
            if(object instanceof  HttpServletRequest || object instanceof HttpServletResponse){
                flag = false;
                break;
            }
        }
        if(flag){
            log.info(String.format("method %s 入参:[%s].", className + "." + methodName, JSON.toJSONString(objects, SerializerFeature.DisableCheckSpecialChar, SerializerFeature.WriteDateUseDateFormat)));
        }
//        //JSR303校验
//        List<String> list = this.JSR303Check(objects, validator);
//        //JSR303检验结果不为空,直接返回,不执行后续业务
//        if (list.size() > 0) {
//            MethodSignature method = (MethodSignature) pjp.getSignature();
//            Class returnType = method.getReturnType();
//            log.info("方法名" + methodName + " 返回类型:" + returnType.getSimpleName());
//            long endTime = System.currentTimeMillis();
//            log.info(String.format("method %s 执行时间[%s毫秒] 出参:[%s] .", className + "." + methodName, endTime - startTime, JSON.toJSONString(list, SerializerFeature.DisableCheckSpecialChar)));
//            return list;
//        }

        Object obj = pjp.proceed(pjp.getArgs());
        long endTime = System.currentTimeMillis();
        log.info(String.format("method %s 执行时间[%s毫秒] .", className + "." + methodName, endTime - startTime));
        return obj;
    }

    /**
     * JSR303实体类校验
     *
     * @param objects 入参对象
     * @return 校验结果 list数组大小为0标识校验通过,list数组大小大于0校验失败并返回错误信息
     */
    private List<String> JSR303Check(Object[] objects, Validator validator) {
        List<String> list = new ArrayList<String>();
        //有参数时进行校验
        if (validator != null && objects != null && objects.length > 0) {
            for (Object object : objects) {
                //参数不为空进行校验【参数为空可能含有业务含义,不进行控制,开发人员自行控制】
                if (object != null) {
                    Set<ConstraintViolation<Object>> violations = validator.validate(object);
                    for (ConstraintViolation<Object> constraintViolation : violations) {
                        list.add(constraintViolation.getPropertyPath() + ":" + constraintViolation.getMessage());
                    }
                }
            }
        }
        return list;
    }

}
