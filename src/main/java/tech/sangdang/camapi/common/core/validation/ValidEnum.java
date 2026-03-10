package tech.sangdang.camapi.common.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidEnumValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnum {

    Class<? extends Enum<?>> enumClass();

    String message() default "must be any of {enumClass}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

