

package com.ga4w20.bookazon.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator used to validate the phone number in 
 * registration.xhtml
 *
 * @author Shirin
 */
public class PhoneValidator  implements ConstraintValidator<ValidPhone, String>{

    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation); 
    }

     @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        
       return validRegex(value);
    }
   /**
 * Regex found on stack over flow
 * @param phonenumber
 * @return 
 */ 
    private boolean validRegex(String phoneNumber) {
      String regex = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$";
      return phoneNumber.matches(regex);
    }
}
