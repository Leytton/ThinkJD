package com.llqqww.thinkjdbc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)//该注解用于修饰TYPE元素，也就是修饰类和接口等类型
@Retention(RetentionPolicy.RUNTIME)//该注解信息运行时保留
@Documented//该注解包含在Javadoc中
public @interface Column {
	String name() default "";
	boolean isKey() default false;
	boolean isAutoInc() default true;//pk是否自增长
	boolean isColumn() default true;
	boolean isCheckField() default true;//是否严格检查数据类型
}