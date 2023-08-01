package com.fileuploadmgtservice.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseController {

    /**
     * Populate Response Header
     *
     * @param responseCode
     * @param trResponse
     *
     * @return
     */
    public ResponseEntity<Object> getResponseEntity(String responseCode, Object trResponse) {
        switch (responseCode) {
            case "200":
                return ResponseEntity.status(HttpStatus.OK).body(trResponse);
            case "201":
                return ResponseEntity.status(HttpStatus.CREATED).body(trResponse);
            case "202":
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(trResponse);
            case "400":
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(trResponse);
            case "401":
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(trResponse);
            case "404":
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(trResponse);
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(trResponse);
        }
    }
}
