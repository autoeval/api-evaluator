package com.autoeval.api.evaluator.model;

import com.autoeval.api.evaluator.model.resume.ResumeMatchResponse;
import com.google.gson.Gson;

public class ResumeScoreCalculator implements ScoreCalculator {
    @Override
    public double calculatePassedScore(String responseText) {
        double calculatedScore = 0.0;
        Gson gson = new Gson();
        ResumeMatchResponse resumeMatchResponse = gson.fromJson(responseText, ResumeMatchResponse.class);


        return calculatedScore;
    }
}
