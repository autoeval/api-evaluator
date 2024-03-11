package com.autoeval.api.evaluator.model;

public interface ScoreCalculator {
    default double calculatePassedScore(final String responseText) {
        return 0.0;
    }

    default double calculateFailedScore(final String responseText) {
        return 0.0;
    }
}
