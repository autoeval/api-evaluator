package com.wellsfargo.api;

import ch.qos.logback.classic.LoggerContext;
import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.wellsfargo.api.evaluator.Evaluator;
import com.wellsfargo.api.evaluator.model.TestCaseScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    @Parameter(names = { "-tc", "--test-cases" ,"-f"}, description = "Path to test case file", required = true)
    private String testCaseFilePath = "";
    @DynamicParameter(names = {"-P", "-p", "--placeholder"}, description = "Placeholder values")
    private Map<String, String> placeholders = new HashMap<>();
    @Parameter(names = { "-ll", "--logging.level", "-level"}, description = "Logging Level")
    private String loggingLevel = "INFO";

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);
        main.run();
    }

    private void run() {
        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(ch.qos.logback.classic.Level.toLevel(loggingLevel));

        Evaluator evaluator = new Evaluator();
        List<TestCaseScore> testCaseScores = evaluator.evaluate(testCaseFilePath, placeholders);
        double totalScore = testCaseScores.stream().mapToDouble(TestCaseScore::getScore).sum();
        testCaseScores.forEach(testCaseScore -> LOGGER.info("TC Id: '{}', TC Name: '{}', Score: '{}'", testCaseScore.getId(), testCaseScore.getName(), testCaseScore.getScore()));
        LOGGER.info("Total Score = " + totalScore);
    }
}