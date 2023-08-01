package com.fileuploadmgtservice.external.repository;

import com.fileuploadmgtservice.domain.entity.FileInfoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileInfoRepository extends MongoRepository<FileInfoEntity,String> {
}
