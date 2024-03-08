package com.autoeval.api.evaluator.csv;


public class HackathonSubmission {

    private String teamName;
    private String gitURL;
    private String pingURL;
    private double step1Score;
    private String step1Comment;

    public String getTeamName() {
        return teamName;
    }

    public String getGitURL() {
        return gitURL;
    }

    public String getPingURL() {
        return pingURL;
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

    public HackathonSubmission(String teamName, String gitURL, String pingURL) {
        this.teamName = teamName;
        this.gitURL = gitURL;
        this.pingURL = pingURL;
    }

}
