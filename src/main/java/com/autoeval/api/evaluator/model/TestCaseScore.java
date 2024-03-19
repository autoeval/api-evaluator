package com.autoeval.api.evaluator.model;

public class TestCaseScore {
    private String submissionId;
    private String testCaseId;
    private String testCaseName;
    private double testCaseScore;
    private String api_response;
    private String pdf_content;

    public String getApi_response() {
		return api_response;
	}

	public void setApi_response(String api_response) {
		this.api_response = api_response;
	}

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

	public String getPdf_content() {
		return pdf_content;
	}

	public void setPdf_content(String pdf_content2) {
		this.pdf_content = pdf_content2;
	}
}
