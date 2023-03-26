package com.daowei.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)//用于注入属性
@Retention(RetentionPolicy.RUNTIME)
public @interface Di {
}
