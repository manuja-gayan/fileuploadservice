package com.fileuploadmgtservice.external.serviceimpl;

import com.fileuploadmgtservice.domain.boundary.FileUploadClient;
import com.fileuploadmgtservice.domain.entity.dto.response.UploadClientResponse;
import com.fileuploadmgtservice.domain.utils.Constants;
import com.fileuploadmgtservice.external.exception.ExternalException;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@Service
public class GoogleCloudStorageClientImpl implements FileUploadClient {
    @Value("${gcp.credentials.location}")
    public String FILEPATH;
    @Value("${spring.cloud.gcp.project-id}")
    public String PROJECT_ID;
    Storage storage;
    @PostConstruct
    public void init(){
        try {
            ClassPathResource resource = new ClassPathResource(FILEPATH);
            Credentials credentials = GoogleCredentials
                    .fromStream(resource.getInputStream());
            storage = StorageOptions.newBuilder().setCredentials(credentials)
                    .setProjectId(PROJECT_ID).build().getService();
        }catch (Exception ex){
            log.error("error coloured in initializing google storage service.Error-{}",ex.getMessage(),ex);
        }
    }

    @Override
    public UploadClientResponse uploadFile(String fileReference, MultipartFile file, String bucketName, String fileFormat, String fileType) {
        log.info("putObject method started bucketName:{}, key:{}", bucketName, fileReference);
        InputStream inputStream;
        try {

            BlobId blobId = BlobId.of(bucketName, fileReference);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).
                    setContentType(file.getContentType())
                    .build();
            Blob response = storage.create(blobInfo,file.getBytes(), Storage.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));

            UploadClientResponse uploadClientResponse;
            if( Objects.nonNull(response) ) {
                uploadClientResponse = new UploadClientResponse();
                uploadClientResponse.setETag(response.getEtag());
                uploadClientResponse.setVersionId(response.getBlobId().getName());
                uploadClientResponse.setBucketName(bucketName);
            }else {
                log.error("Error occurred while uploading file.FileReference:{}",fileReference);
                throw new ExternalException(Constants.ResponseData.FILE_UPLOAD_ERROR);
            }
            return uploadClientResponse;
        }
        catch ( IOException e ) {
            log.error("Error occurred while getting the input stream of Multipart file", e);
            throw new ExternalException(Constants.ResponseData.FILE_UPLOAD_ERROR);
        }
    }
}
