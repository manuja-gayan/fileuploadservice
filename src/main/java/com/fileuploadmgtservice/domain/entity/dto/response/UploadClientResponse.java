package com.fileuploadmgtservice.domain.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadClientResponse {
    private String versionId;
    private String eTag;
    private String bucketName;
    private Date expirationTime;
}