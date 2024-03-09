package com.autoeval.api.evaluator.model.resume;

import lombok.Data;

import java.util.List;

@Data
public class ResumeMatchResponse {

    private int count;
    private String status;
    private MetaData metadata;
    private List<Results> results;
}
