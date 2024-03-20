package com.autoeval.api.evaluator.model;

import com.autoeval.api.evaluator.csv.HackathonSubmission;

public class TestCaseScore {
    private HackathonSubmission submission;
    private String testCaseId;
    private String testCaseName;
    private double testCaseScore;
    private String message;
    private long responseTimeInMs;
    private int apiResponseCode;
    private String requestBody;
    private String responseBody;


    public HackathonSubmission getSubmission() {
        return submission;
    }

    public void setSubmission(HackathonSubmission submission) {
        this.submission = submission;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public double getTestCaseScore() {
        return testCaseScore;
    }

    public void setTestCaseScore(double testCaseScore) {
        this.testCaseScore = testCaseScore;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getResponseTimeInMs() {
        return responseTimeInMs;
    }

    public void setResponseTimeInMs(long responseTimeInMs) {
        this.responseTimeInMs = responseTimeInMs;
    }

    public int getApiResponseCode() {
        return apiResponseCode;
    }

    public void setApiResponseCode(int apiResponseCode) {
        this.apiResponseCode = apiResponseCode;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
