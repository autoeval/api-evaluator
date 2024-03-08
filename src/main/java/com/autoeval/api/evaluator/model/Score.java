package com.autoeval.api.evaluator.model;

public class Score {
    private double passed;
    private double failed;

    public double getPassed() {
        return passed;
    }

    public void setPassed(double passed) {
        this.passed = passed;
    }

    public double getFailed() {
        return failed;
    }

    public void setFailed(double failed) {
        this.failed = failed;
    }
}
