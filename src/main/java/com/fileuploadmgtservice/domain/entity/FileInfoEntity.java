package com.fileuploadmgtservice.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "FileInfo")
public class FileInfoEntity {
	@Id
	private String id;
	private String actualFileName;
	private String fileReference;
	private String fileFormat;
	private String fileType;
	private String versionId;
	private String eTag;
	private String bucket;
	private Date expirationTime;
}
