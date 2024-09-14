package org.mounanga.securityservice.exception;

import java.util.List;

public record ExceptionResponse(Integer code, String message, List<String> fieldErrors) {
}
