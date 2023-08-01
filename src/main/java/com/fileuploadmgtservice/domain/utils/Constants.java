package com.fileuploadmgtservice.domain.utils;

import lombok.Getter;

public class Constants {
    public static final String UNHANDLED_ERROR_CODE = "FUS3000";
    @Getter
    public enum ResponseData {
        COMMON_SUCCESS("FUS1000", "Success","200"),
        QUERY_SUCCESS("FUS1001", "Query success","200"),
        COMMON_FAIL("FUS2000", "Failed","400"),
        FILE_UPLOAD_ERROR("FUS2001", "Error occurred in file upload","400"),
        INTERNAL_SERVER_ERROR("FUS3001", "Internal Server Error","500");

        private final String code;
        private final String message;
        private final String responseCode;

        ResponseData(String code, String message, String responseCode) {
            this.code = code;
            this.message = message;
            this.responseCode= responseCode;
        }
    }
}
