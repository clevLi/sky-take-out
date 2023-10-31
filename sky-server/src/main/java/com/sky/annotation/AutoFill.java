package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识功能字段进行自定义填充
 */

// 只针对方法进行拦截
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

public @interface AutoFill {

    //指定数据库操作类型:UPDATE,INSERT
    OperationType value();
}
