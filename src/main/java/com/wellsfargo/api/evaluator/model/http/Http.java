package com.wellsfargo.api.evaluator.model.http;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Http {
    @JsonProperty("base-path")
    private String basePath;
    private HttpRequest request;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }
}
