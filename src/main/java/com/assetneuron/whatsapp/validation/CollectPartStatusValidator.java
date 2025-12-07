package com.assetneuron.whatsapp.validation;

import com.assetneuron.whatsapp.enums.WorkOrderPartStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CollectPartStatusValidator implements ConstraintValidator<ValidCollectPartStatus, WorkOrderPartStatus> {

    @Override
    public void initialize(ValidCollectPartStatus constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(WorkOrderPartStatus status, ConstraintValidatorContext context) {
        if (status == null) {
            return false;
        }
        return status == WorkOrderPartStatus.COLLECTED;
    }
}

