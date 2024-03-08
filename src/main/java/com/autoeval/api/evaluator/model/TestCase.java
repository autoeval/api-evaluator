package com.autoeval.api.evaluator.model;

import com.autoeval.api.evaluator.model.http.Http;

import java.util.List;

public class TestCase {
    private String id;
    private String name;
    private Http http;
    private List<Check> checks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Http getHttp() {
        return http;
    }

    public void setHttp(Http http) {
        this.http = http;
    }

    public List<Check> getChecks() {
        return checks;
    }

    public void setChecks(List<Check> checks) {
        this.checks = checks;
    }
}
