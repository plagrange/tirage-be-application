package com.dictao.dtp.web.servlet.mvc.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE})
@Deprecated
public @interface Path {
    String[] value();
}
