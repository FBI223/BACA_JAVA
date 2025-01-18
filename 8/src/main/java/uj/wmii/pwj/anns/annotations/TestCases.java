package uj.wmii.pwj.anns.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestCases {
    TestCase[] value();
}
