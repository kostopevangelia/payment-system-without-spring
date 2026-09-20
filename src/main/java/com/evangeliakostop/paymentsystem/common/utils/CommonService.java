package com.evangeliakostop.paymentsystem.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
public class CommonService {

    public HttpStatus mapErrorCodeToStatus(String errorCode) {
        return switch (errorCode) {
            case "400" -> HttpStatus.BAD_REQUEST;
            case "404" -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
