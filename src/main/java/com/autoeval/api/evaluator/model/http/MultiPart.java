package com.autoeval.api.evaluator.model.http;

import java.util.List;

public class MultiPart {
    private List<FilePart> files;
    private List<NameValuePair> fields;

    public List<FilePart> getFiles() {
        return files;
    }

    public void setFiles(List<FilePart> files) {
        this.files = files;
    }

    public List<NameValuePair> getFields() {
        return fields;
    }

    public void setFields(List<NameValuePair> fields) {
        this.fields = fields;
    }
}
