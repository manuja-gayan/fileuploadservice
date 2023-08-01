package com.fileuploadmgtservice.domain.entity;

import lombok.Data;

import java.util.List;

@Data
public class FileTypeMetaData {
	private String fileType;
	private List<String> eligibleFileFormats;
	private String basePath;
	private String bucketName;
	private Long maxFileSize;
}
