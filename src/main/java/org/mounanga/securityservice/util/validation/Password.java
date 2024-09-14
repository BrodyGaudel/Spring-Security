package org.mounanga.securityservice.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface Password {

    String message() default "Invalid password. Password must have at least 8 characters, " +
            "including 2 uppercase letters, 2 lowercase letters, and 2 digits.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
