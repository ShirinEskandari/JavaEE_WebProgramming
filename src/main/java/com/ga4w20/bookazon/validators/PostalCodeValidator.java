package com.ga4w20.bookazon.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator used to validate the PostalCode in registration.xhtml
 *
 * @author Shirin
 */
public class PostalCodeValidator implements ConstraintValidator<ValidPostalCode, String> {

    @Override
    public void initialize(ValidPostalCode constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        return validRegex(value);
    }

    /**
     * Regex found on stackoverflow
     *
     * @param postalCode
     * @return
     */
    private boolean validRegex(String postalCode) {
        String regex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
        return postalCode.matches(regex);
    }
}
