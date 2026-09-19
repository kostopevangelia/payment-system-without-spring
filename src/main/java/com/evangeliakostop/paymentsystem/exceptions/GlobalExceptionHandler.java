package com.evangeliakostop.paymentsystem.exceptions;

import com.evangeliakostop.paymentsystem.models.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponse> handleCustomException(CustomException e) {
        CommonResponse commonResponse = new CommonResponse(
                e.getErrorLevelEnum().getCode(),
                e.getMessage(),
                e.getCause() != null ? e.getCause().getMessage() : e.getMessage()
        );

        return ResponseEntity.status(e.getErrorLevelEnum().getHttpStatus()).body(commonResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handleGenericException(Exception e) {
        CommonResponse commonResponse = new CommonResponse(
                500,
                "Unexpected error occurred",
                e.getMessage()
        );
        return ResponseEntity.status(500).body(commonResponse);
    }

}
