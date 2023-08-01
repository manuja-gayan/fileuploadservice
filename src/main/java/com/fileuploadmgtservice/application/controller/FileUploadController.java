package com.fileuploadmgtservice.application.controller;

import com.fileuploadmgtservice.application.transport.request.FileUploadMetaData;
import com.fileuploadmgtservice.domain.entity.dto.response.CommonResponse;
import com.fileuploadmgtservice.domain.entity.dto.response.UploadResponse;
import com.fileuploadmgtservice.domain.service.FileManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 *  * This is the controller class for File management.
 */
@RestController
@RequestMapping("${base-url.context}/file")
public class FileUploadController extends BaseController {

    @Autowired
    FileManagementService fileManagementService;

    @PostMapping( value = "/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity <Object> uploadFiles(@Valid @RequestParam("files") MultipartFile[] files,
                                               FileUploadMetaData uploadMetaData,
                                               HttpServletRequest request) {

        CommonResponse<UploadResponse> response= fileManagementService.uploadFiles(files, uploadMetaData);
        return getResponseEntity(response.getResponseHeader().getResponseCode(),response);
    }
}
