package com.wellsfargo.api.evaluator.model.http;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FilePart {
    @JsonProperty("param-name")
    private String paramName;
    private String path;
    @JsonProperty("content-type")
    private String contentType;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
