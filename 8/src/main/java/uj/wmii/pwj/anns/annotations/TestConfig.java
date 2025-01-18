package uj.wmii.pwj.anns.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestConfig {
    int priority() default 0;
    int timeout() default 1000;
    int repeat() default 1;
    boolean parallel() default false;
}
