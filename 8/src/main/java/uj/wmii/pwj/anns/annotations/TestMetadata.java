package uj.wmii.pwj.anns.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestMetadata {
    String name() default "";
    String author() default "";
    String version() default "1.0";
}