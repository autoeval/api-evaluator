package com.autoeval.api.evaluator.csv;


public class HackathonSubmission {

    private String teamName;
    private String gitURL;
    private String etpFlag;
    private String pingURL;
    private double step0Score;
    private String step0Comment;
    private double step1Score;
    private String step1Comment;

    public HackathonSubmission(String teamName, String gitURL, String etpFlag, String pingURL, double step0Score, String step0Comment, double step1Score, String step1Comment) {
        this.teamName = teamName;
        this.gitURL = gitURL;
        this.etpFlag = etpFlag;
        this.pingURL = pingURL;
        this.step0Score = step0Score;
        this.step0Comment = step0Comment;
        this.step1Score = step1Score;
        this.step1Comment = step1Comment;
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

    public Object[] toArray() {
        return new Object[] {teamName, gitURL, etpFlag, pingURL, step0Score, step0Comment, step1Score, step1Comment};
    }
}
