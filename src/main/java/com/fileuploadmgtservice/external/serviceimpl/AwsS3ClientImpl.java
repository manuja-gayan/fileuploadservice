package com.fileuploadmgtservice.external.serviceimpl;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fileuploadmgtservice.domain.boundary.FileUploadClient;
import com.fileuploadmgtservice.domain.entity.dto.response.UploadClientResponse;
import com.fileuploadmgtservice.domain.utils.Constants;
import com.fileuploadmgtservice.external.exception.ExternalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@Service
public class AwsS3ClientImpl implements FileUploadClient {

    @Value("${aws.s3.secret-key}")
    private String secretKey;
    @Value("${aws.s3.access-key}")
    private String accessKey;
    @Value("${aws.s3.region}")
    private String region;

    private AmazonS3 s3client;

    @PostConstruct
    public void initS3Client() {
        this.s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                        accessKey,
                        secretKey
                )))
                .withRegion(Regions.fromName(region))
                .build();

        getClientConfig();
    }

    private ClientConfiguration getClientConfig() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProtocol(Protocol.HTTP);
        return clientConfiguration;
    }
    @Override
    public UploadClientResponse uploadFile(String fileReference, MultipartFile file, String bucketName, String fileFormat, String fileType) {
        log.info("putObject method started bucketName:{}, key:{}", bucketName, fileReference);
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
            long contentLength = file.getSize();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(contentLength);
            objectMetadata.setContentType(fileFormat);
            PutObjectResult response = s3client.putObject(bucketName, fileReference, inputStream, objectMetadata);
            UploadClientResponse uploadClientResponse;
            if( Objects.nonNull(response) ) {
                uploadClientResponse = new UploadClientResponse();
                uploadClientResponse.setETag(response.getETag());
                uploadClientResponse.setVersionId(response.getVersionId());
                uploadClientResponse.setExpirationTime(response.getExpirationTime());
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
