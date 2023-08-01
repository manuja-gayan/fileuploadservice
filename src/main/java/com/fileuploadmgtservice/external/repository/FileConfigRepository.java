package com.fileuploadmgtservice.external.repository;

import com.fileuploadmgtservice.domain.entity.FileConfigEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FileConfigRepository extends MongoRepository<FileConfigEntity,String> {

    @Query("{isActive: true}")
    FileConfigEntity getConfigEntity();

}
