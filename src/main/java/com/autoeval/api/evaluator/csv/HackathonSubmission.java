package com.autoeval.api.evaluator.csv;


public class HackathonSubmission {

    private String teamName;
    private String gitURL;
    private String etpFlag;
    private String pingURL;
    private double step0Score;
    private String step0Comment;
    private String testCaseId;
    private double step1Score;
    private String step1Comment;
    private int apiResponseCode;
    private String requestBody;
    private String responseBody;

    public HackathonSubmission(String teamName, String gitURL, String etpFlag, String pingURL, double step0Score, String step0Comment, String testCaseId, double step1Score, String step1Comment, int apiResponseCode, String requestBody, String responseBody) {
        this.teamName = teamName;
        this.gitURL = gitURL;
        this.etpFlag = etpFlag;
        this.pingURL = pingURL;
        this.step0Score = step0Score;
        this.step0Comment = step0Comment;
        this.testCaseId = testCaseId;
        this.step1Score = step1Score;
        this.step1Comment = step1Comment;
        this.apiResponseCode = apiResponseCode;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getGitURL() {
        return gitURL;
    }

    public void setGitURL(String gitURL) {
        this.gitURL = gitURL;
    }

    public String getEtpFlag() {
        return etpFlag;
    }

    public void setEtpFlag(String etpFlag) {
        this.etpFlag = etpFlag;
    }

    public String getPingURL() {
        return pingURL;
    }

    public void setPingURL(String pingURL) {
        this.pingURL = pingURL;
    }

    public double getStep0Score() {
        return step0Score;
    }

    public void setStep0Score(double step0Score) {
        this.step0Score = step0Score;
    }

    public String getStep0Comment() {
        return step0Comment;
    }

    public void setStep0Comment(String step0Comment) {
        this.step0Comment = step0Comment;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public double getStep1Score() {
        return step1Score;
    }

    public void setStep1Score(double step1Score) {
        this.step1Score = step1Score;
    }

    public String getStep1Comment() {
        return step1Comment;
    }

    public void setStep1Comment(String step1Comment) {
        this.step1Comment = step1Comment;
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

    public Object[] toArray(boolean full) {
        if(!full) {
            return new Object[] {teamName, gitURL, etpFlag, pingURL, step0Score, step0Comment, step1Score, step1Comment};
        } else {
            return new Object[] {teamName, gitURL, etpFlag, pingURL, step0Score, step0Comment, testCaseId, step1Score, step1Comment, apiResponseCode, requestBody, responseBody};
        }
    }
}
