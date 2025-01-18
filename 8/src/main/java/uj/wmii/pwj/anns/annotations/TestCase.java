package uj.wmii.pwj.anns.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(TestCases.class)
public @interface TestCase {
    String description() default "";
    String[] params() default {};
    String expected() default "";
    Class<? extends Throwable> expectedException() default None.class; // Oczekiwany wyjątek (jeśli dotyczy)
    class None extends Throwable {}

}

