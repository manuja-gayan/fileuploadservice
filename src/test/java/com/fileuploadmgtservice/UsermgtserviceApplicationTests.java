package com.fileuploadmgtservice;

import com.fileuploadmgtservice.application.transport.request.FileUploadMetaData;
import com.fileuploadmgtservice.domain.boundary.FileUploadClient;
import com.fileuploadmgtservice.domain.entity.FileConfigEntity;
import com.fileuploadmgtservice.domain.entity.FileInfoEntity;
import com.fileuploadmgtservice.domain.entity.FileTypeMetaData;
import com.fileuploadmgtservice.domain.entity.dto.response.UploadClientResponse;
import com.fileuploadmgtservice.domain.exception.DomainException;
import com.fileuploadmgtservice.domain.service.FileManagementService;
import com.fileuploadmgtservice.domain.utils.Constants;
import com.fileuploadmgtservice.external.repository.FileConfigRepository;
import com.fileuploadmgtservice.external.repository.FileInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsermgtserviceApplicationTests {

	@InjectMocks
	FileManagementService fileManagementService;

	@Mock
	FileUploadClient fileUploadClient;

	@Mock
	FileConfigRepository fileConfigRepository;

	@Mock
	FileInfoRepository fileInfoRepository;
	MockMultipartFile firstFile;
	FileUploadMetaData metaData;
	FileConfigEntity configEntity;
	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		metaData = new FileUploadMetaData("image","png",true);
		firstFile = new MockMultipartFile("data", "filename.png", "image/png", "some xml".getBytes());

		List<String> fileFormats = Collections.singletonList("png");
		FileTypeMetaData fileTypeMetaData = new FileTypeMetaData();
		fileTypeMetaData.setFileType("image");
		fileTypeMetaData.setEligibleFileFormats(fileFormats);
		fileTypeMetaData.setBasePath("/test");
		fileTypeMetaData.setBucketName("file-store");
		fileTypeMetaData.setMaxFileSize(1000000L);

		configEntity = new FileConfigEntity();
		configEntity.setId("1");
		configEntity.setIsActive(true);
		configEntity.setSupportedFileTypes(Collections.singletonList("image"));
		configEntity.setFileTypeMetaData(Collections.singletonList(fileTypeMetaData));

		when(fileConfigRepository.getConfigEntity()).thenReturn(configEntity);
	}

	/**
	 * Test1: check the file upload success flow
	 */
	@Test
	void testFileUploadSuccess() {

		when(fileUploadClient.uploadFile(any(),any(),any(),any(),any())).thenReturn(new UploadClientResponse("1","wtre0","file-store", new Date()));
		when(fileInfoRepository.save(any())).thenReturn(new FileInfoEntity("1","filename.png","qrwre","png","image","1","rtrt","file-store",new Date()));

		MultipartFile[] files = {firstFile};
		assertEquals(Constants.ResponseData.COMMON_SUCCESS.getCode(), fileManagementService.uploadFiles(files,metaData).getResponseHeader().getCode());
	}

	/**
	 * Test2: check the invalid file type upload
	 */
	@Test
	void testInvalidFileTypeUpload() {

		metaData = new FileUploadMetaData("document","txt",true);
		firstFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());

		MultipartFile[] files = {firstFile};
		DomainException exception = assertThrows(DomainException.class, () -> fileManagementService.uploadFiles(files,metaData));

		String expectedMessage = "File type not supported";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage,actualMessage);
	}

	/**
	 * Test3: check the invalid format upload
	 */
	@Test
	void testInvalidFileFormatUpload() {

		metaData = new FileUploadMetaData("image","txt",true);
		firstFile = new MockMultipartFile("data", "filename.txt", "image/txt", "some xml".getBytes());

		MultipartFile[] files = {firstFile};
		DomainException exception = assertThrows(DomainException.class, () -> fileManagementService.uploadFiles(files,metaData));

		String expectedMessage = "File format not supported";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage,actualMessage);
	}

	/**
	 * Test4: check the File Content Not Matched Error and meta-data not found error
	 */
	@Test
	void testFileContentNotMatchedError() {

		metaData = new FileUploadMetaData("image","png",true);
		firstFile = new MockMultipartFile("data", "filename.png", "image/txt", "some xml".getBytes());

		MultipartFile[] files = {firstFile};
		DomainException exception = assertThrows(DomainException.class, () -> fileManagementService.uploadFiles(files,metaData));

		String expectedMessage = "File format is not match with given format";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage,actualMessage);

		configEntity.setFileTypeMetaData(new ArrayList<>());
		DomainException ex = assertThrows(DomainException.class, () -> fileManagementService.uploadFiles(files,metaData));
		String expectedMsg = "FileTypeMetaData entry not found for given type";
		String actualMsg = ex.getMessage();
		assertEquals(expectedMsg,actualMsg);

	}

}
