package com.omp.common.interceptor;

import jakarta.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** A poser sur les methodes de service qui mutent une entite tracee (creation/modification/suppression). */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Audited {

    String action() default "";

    String entityType() default "";
}
