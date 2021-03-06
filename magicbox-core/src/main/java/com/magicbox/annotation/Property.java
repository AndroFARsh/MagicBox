package com.magicbox.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.CLASS)
public @interface Property {
	String id() default "";

	String tag() default "";

	String taggedBy() default "";
}
