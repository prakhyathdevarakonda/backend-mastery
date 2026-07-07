package com.prakhyath.backendmastery.wallet.wallet;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail badRequest(IllegalArgumentException ex) {
        return problem(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    ProblemDetail conflict(IllegalStateException ex) {
        return problem(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("Validation failed");
        return problem(HttpStatus.BAD_REQUEST, message);
    }

    private static ProblemDetail problem(HttpStatus status, String message) {
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setDetail(message);
        return problem;
    }
}
