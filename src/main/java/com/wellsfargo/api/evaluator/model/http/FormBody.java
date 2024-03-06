package com.wellsfargo.api.evaluator.model.http;

import java.util.List;

public class FormBody {
    private List<NameValuePair> fields;
    public List<NameValuePair> getFields() {
        return fields;
    }

    public void setFields(List<NameValuePair> fields) {
        this.fields = fields;
    }
}
