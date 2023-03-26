package com.daowei.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)//用于创建对象
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {
}
