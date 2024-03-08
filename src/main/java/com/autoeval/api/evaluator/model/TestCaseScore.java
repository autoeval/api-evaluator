package com.autoeval.api.evaluator.model;

public class TestCaseScore {
    private String submissionId;
    private String testCaseId;
    private String testCaseName;
    private double testCaseScore;

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
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
}
