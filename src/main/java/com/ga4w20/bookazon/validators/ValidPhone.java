package com.ga4w20.bookazon.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *
 * @author Shirin
 */
@Constraint(validatedBy = PhoneValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {

    String message() default "{com.ga4w20.bookazon.invalidPhone}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
