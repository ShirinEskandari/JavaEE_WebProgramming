package com.ga4w20.bookazon.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator used to validate the emailAddress in login.xhtml &
 * registration.xhtml
 *
 * @author Shirin
 */
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        return validRegex(value);
    }
/**
 * Regex found on stack over flow
 * @param email
 * @return 
 */
    private boolean validRegex(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

}
