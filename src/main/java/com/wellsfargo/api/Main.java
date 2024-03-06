package com.wellsfargo.api;

import com.wellsfargo.api.evaluator.Evaluator;
import com.wellsfargo.api.evaluator.model.TestCaseScore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Evaluator evaluator = new Evaluator();
        Map<String, String> placeHolders = new HashMap<>();
        placeHolders.put("BASEPATH", "http://localhost:8080/api");
        List<TestCaseScore> testCaseScores = evaluator.evaluate("testcases/voice-personification-testcases.yaml", placeHolders);
        double totalScore = testCaseScores.stream().mapToDouble(TestCaseScore::getScore).sum();
        testCaseScores.forEach(testCaseScore -> System.out.println(String.format("TC Id: '%s', TC Name: '%s', Score: '%f'", testCaseScore.getId(), testCaseScore.getName(), testCaseScore.getScore())));
        System.out.println("Total Score = " + totalScore);
    }
}