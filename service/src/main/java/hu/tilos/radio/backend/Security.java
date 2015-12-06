package hu.tilos.radio.backend;

import hu.radio.tilos.model.Role;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Security {
    Role role() default Role.GUEST;

    String permission() default "";
}
