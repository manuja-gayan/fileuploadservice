package com.fileuploadmgtservice.domain.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadResponse {
    private List<String> entityReference = new ArrayList<>();
    private List<String> bucketReference = new ArrayList<>();
}