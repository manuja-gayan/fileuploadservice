package com.fileuploadmgtservice.domain.boundary;

import com.fileuploadmgtservice.domain.entity.dto.response.UploadClientResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface FileUploadClient {
    UploadClientResponse uploadFile(String fileReference, MultipartFile file, String bucketName, String fileFormat, String fileType);
}
