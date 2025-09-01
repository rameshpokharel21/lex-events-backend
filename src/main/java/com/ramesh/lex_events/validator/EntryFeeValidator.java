package com.ramesh.lex_events.validator;

import com.ramesh.lex_events.dto.request.EventRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class EntryFeeValidator implements ConstraintValidator<ValidEntryFee, EventRequest> {
    @Override
    public boolean isValid(EventRequest request, ConstraintValidatorContext context) {

        if(request.getIsFree() == null) return false;

        if(request.getIsFree()){
            //free events don't need entryFee
            return request.getEntryFee() == null || request.getEntryFee().compareTo(BigDecimal.ZERO) == 0;
        } else{
            //Not free means must provide a valid entryFee
            return request.getEntryFee() != null && request.getEntryFee().compareTo(BigDecimal.ZERO) > 0;
        }

    }
}
