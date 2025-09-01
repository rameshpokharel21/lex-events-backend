package com.ramesh.lex_events.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EntryFeeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEntryFee {
    String message() default "if isFree is false, then need entryFee > 0";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
