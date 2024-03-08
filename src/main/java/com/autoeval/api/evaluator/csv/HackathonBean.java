package com.autoeval.api.evaluator.csv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class HackathonBean {

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

    public HackathonBean (String teamName, String gitURL, String pingURL) {
        this.teamName = teamName;
        this.gitURL = gitURL;
        this.pingURL = pingURL;
    }

}
