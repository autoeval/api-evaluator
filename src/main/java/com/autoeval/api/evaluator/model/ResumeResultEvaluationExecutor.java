package com.autoeval.api.evaluator.model;

import com.autoeval.api.evaluator.model.resume.ResumeMatchResponse;
import com.google.gson.Gson;

public class ResumeResultEvaluationExecutor implements ConditionExecutor {

    public boolean apply(final String responseText) {
        Gson gson = new Gson();
        ResumeMatchResponse resumeMatchResponse = gson.fromJson(responseText, ResumeMatchResponse.class);
        return false;
    }
}
