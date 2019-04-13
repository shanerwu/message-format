package io.github.shanerwu.message.format.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang3.StringUtils;

@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageDetail {

    String value() default StringUtils.EMPTY;

}
