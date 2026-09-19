package com.evangeliakostop.paymentsystem.common.utils.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorLevelEnum {

    SUCCESS(0, HttpStatus.OK, "No errors Occurred"),
    VALIDATION_ERROR(1, HttpStatus.BAD_REQUEST, "Validation errors occurred"),
    NOT_FOUND_ERROR(2, HttpStatus.NOT_FOUND, "Validation errors occurred"),
    APPLICATION_ERROR(3, HttpStatus.INTERNAL_SERVER_ERROR, "Application errors Occurred"),
    DEFAULT(0, HttpStatus.OK, "No errors Occurred");

    private Integer code;
    private HttpStatus httpStatus;
    private String description;

}
