package raven.modal.demo.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemForm {

    String name() default "Chưa định nghĩ kìa";

    String description() default "";

    String[] tags() default {};
}