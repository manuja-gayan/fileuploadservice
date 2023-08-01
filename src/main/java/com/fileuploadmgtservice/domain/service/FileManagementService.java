package com.fileuploadmgtservice.domain.service;

import com.fileuploadmgtservice.application.transport.request.FileUploadMetaData;
import com.fileuploadmgtservice.domain.boundary.FileUploadClient;
import com.fileuploadmgtservice.domain.entity.FileConfigEntity;
import com.fileuploadmgtservice.domain.entity.FileInfoEntity;
import com.fileuploadmgtservice.domain.entity.FileTypeMetaData;
import com.fileuploadmgtservice.domain.entity.dto.response.CommonResponse;
import com.fileuploadmgtservice.domain.entity.dto.response.ResponseHeader;
import com.fileuploadmgtservice.domain.entity.dto.response.UploadClientResponse;
import com.fileuploadmgtservice.domain.entity.dto.response.UploadResponse;
import com.fileuploadmgtservice.domain.exception.DomainException;
import com.fileuploadmgtservice.domain.utils.Constants;
import com.fileuploadmgtservice.external.repository.FileConfigRepository;
import com.fileuploadmgtservice.external.repository.FileInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Random;

@Slf4j
@Service
public class FileManagementService {

    @Autowired
    @Qualifier(value = "googleCloudStorageClientImpl")
    FileUploadClient fileUploadClient;

    @Autowired
    FileConfigRepository fileConfigRepository;

    @Autowired
    FileInfoRepository fileInfoRepository;

    @Value("${file-name-hash.salt-value}")
    private String saltValue;

    public CommonResponse<UploadResponse> uploadFiles(MultipartFile[] files, FileUploadMetaData metaData) {

        CommonResponse<UploadResponse> response = new CommonResponse<>();

        FileConfigEntity fileConfigEntity = fileConfigRepository.getConfigEntity();

        FileTypeMetaData fileTypeMetaData = fileValidation(fileConfigEntity,files,metaData);

        try {
            UploadResponse resData = new UploadResponse();
            for (MultipartFile file : files) {
                String fileReference = generateFileReference().concat(".").concat(metaData.getFileFormat());
                UploadClientResponse uploadResponse = fileUploadClient.uploadFile(fileReference,file,fileTypeMetaData.getBucketName(), metaData.getFileFormat(), fileTypeMetaData.getFileType());
                FileInfoEntity fileInfo = new FileInfoEntity(null, Objects.requireNonNull(file.getOriginalFilename()).substring(0,file.getOriginalFilename().lastIndexOf(".")),
                        fileReference, metaData.getFileFormat(),metaData.getUploadFileType(),uploadResponse.getVersionId(),
                        uploadResponse.getETag(),uploadResponse.getBucketName(),uploadResponse.getExpirationTime());
                fileInfo = fileInfoRepository.save(fileInfo);
                if(metaData.getIsReturnBucketReference()){
                    resData.getBucketReference().add(fileReference);
                }
                resData.getEntityReference().add(fileInfo.getId());
            }

            response.setData(resData);
            response.setResponseHeader(new ResponseHeader(Constants.ResponseData.COMMON_SUCCESS));
            return response;
        }catch (Exception ex){
            log.error("Error occur while uploading file to s3 bucket.Error:{}",ex.getMessage(),ex);
            throw new DomainException(Constants.ResponseData.FILE_UPLOAD_ERROR);
        }
    }

    private String generateFileReference() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");

        Random r = new Random(System.currentTimeMillis());
        int randomNumber = r.nextInt(100000) + 100000;

        md.update((randomNumber + saltValue).getBytes());
        byte[] digest = md.digest();
        return (DatatypeConverter.printHexBinary(digest));
    }


    private FileTypeMetaData fileValidation(FileConfigEntity fileConfigEntity, MultipartFile[] files, FileUploadMetaData metaData){
        if(Boolean.FALSE.equals(fileConfigEntity.getSupportedFileTypes().contains(metaData.getUploadFileType()))){
            throw new DomainException("FUS3001","File type not supported","400");
        }

        for (FileTypeMetaData typeMetaData:fileConfigEntity.getFileTypeMetaData()) {
            if(typeMetaData.getFileType().equalsIgnoreCase(metaData.getUploadFileType())){
                if(Boolean.FALSE.equals(typeMetaData.getEligibleFileFormats().contains(metaData.getFileFormat()))){
                    throw new DomainException("FUS3002","File format not supported","400");
                }
                for (MultipartFile multipartFile: files){
                    if(Objects.isNull(multipartFile)){
                        throw new DomainException("FUS3005","File is null","400");
                    } else if(multipartFile.getSize()>typeMetaData.getMaxFileSize()){
                        throw new DomainException("FUS3003","File size too large","400");
                    } else if (!Objects.requireNonNull(multipartFile.getContentType().split("/")[1]).equalsIgnoreCase(metaData.getFileFormat())) {
                        throw new DomainException("FUS3004","File format is not match with given format","400");
                    }
                }
                return typeMetaData;
            }
        }
        throw new DomainException("FUS3000","FileTypeMetaData entry not found for given type","500");
    }
}
