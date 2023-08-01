package com.fileuploadmgtservice.application.transport.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadMetaData {

    @NotNull( message = "uploadFileType not found for operation. This action is not allowed" )
    @Valid
    private String uploadFileType;
    @NotNull( message = "fileFormat not found for operation. This action is not allowed" )
    @Valid
    private String fileFormat;
    private Boolean isReturnBucketReference = true;
}