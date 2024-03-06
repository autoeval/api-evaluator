package com.wellsfargo.api;

import com.wellsfargo.api.evaluator.Evaluator;
import com.wellsfargo.api.evaluator.model.TestCaseScore;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Evaluator evaluator = new Evaluator();
        List<TestCaseScore> testCaseScores = evaluator.evaluate("src/main/resources/testcases.yaml");
        testCaseScores.forEach(testCaseScore -> System.out.println(String.format("TC Id: '%s', TC Name: '%s', Score: '%f'", testCaseScore.getId(), testCaseScore.getName(), testCaseScore.getScore())));
    }
}