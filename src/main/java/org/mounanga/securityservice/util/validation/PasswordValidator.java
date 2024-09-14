package org.mounanga.securityservice.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    @Override
    public void initialize(Password constraintAnnotation) {
        // Initialisation si nécessaire
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        if (password.length() < 8) {
            return false;
        }

        int upperCaseCount = 0;
        int lowerCaseCount = 0;
        int digitCount = 0;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                upperCaseCount++;
            } else if (Character.isLowerCase(c)) {
                lowerCaseCount++;
            } else if (Character.isDigit(c)) {
                digitCount++;
            }
        }

        return upperCaseCount >= 2 && lowerCaseCount >= 2 && digitCount >= 2;
    }
}
