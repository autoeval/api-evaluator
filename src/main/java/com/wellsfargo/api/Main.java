package com.wellsfargo.api;

import com.wellsfargo.api.evaluator.Evaluator;
import com.wellsfargo.api.evaluator.model.TestCaseScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        Evaluator evaluator = new Evaluator();
        Map<String, String> placeHolders = new HashMap<>();
        placeHolders.put("BASEPATH", "http://localhost:8080/api");
        placeHolders.put("TEST_IMAGE_DIRECTORY", "/D:/Hackathon_2024");
        List<TestCaseScore> testCaseScores = evaluator.evaluate("testcases/voice-personification-testcases.yaml", placeHolders);
        double totalScore = testCaseScores.stream().mapToDouble(TestCaseScore::getScore).sum();
        testCaseScores.forEach(testCaseScore -> LOGGER.info("TC Id: '{}', TC Name: '{}', Score: '{}'", testCaseScore.getId(), testCaseScore.getName(), testCaseScore.getScore()));
        LOGGER.info("Total Score = " + totalScore);
    }
}