package com.autoeval.api.evaluator.csv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class HackathonBean {

    private String teamName;
    private String gitURL;
    private String pingURL;


    public String getTeamName() {
        return teamName;
    }

    public String getGitURL() {
        return gitURL;
    }

    public String getPingURL() {
        return pingURL;
    }

    public HackathonBean (String teamName, String gitURL, String pingURL) {
        this.teamName = teamName;
        this.gitURL = gitURL;
        this.pingURL = pingURL;
    }

}
