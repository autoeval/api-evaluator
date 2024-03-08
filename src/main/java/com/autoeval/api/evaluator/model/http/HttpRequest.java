package com.autoeval.api.evaluator.model.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties
public class HttpRequest {
    private String uri;
    private String method;
    private List<NameValuePair> headers;
    private MultiPart multipart;
    private String body;
    private FormBody form;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<NameValuePair> getHeaders() {
        return headers;
    }

    public void setHeaders(List<NameValuePair> headers) {
        this.headers = headers;
    }

    public MultiPart getMultipart() {
        return multipart;
    }

    public void setMultipart(MultiPart multipart) {
        this.multipart = multipart;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public FormBody getForm() {
        return form;
    }

    public void setForm(FormBody form) {
        this.form = form;
    }
}
