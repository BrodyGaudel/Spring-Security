package org.mounanga.securityservice.exception;

public class VerificationCodeExpiredException extends RuntimeException {

    public VerificationCodeExpiredException(String message) {
        super(message);
    }
}
