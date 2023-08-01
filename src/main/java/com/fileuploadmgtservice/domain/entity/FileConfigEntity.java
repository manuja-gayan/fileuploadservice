package com.fileuploadmgtservice.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "FileConfig")
public class FileConfigEntity {
	@Id
    private String id;
	private Boolean isActive=true;
	private List<String> supportedFileTypes;
	private List<FileTypeMetaData> fileTypeMetaData;
}
