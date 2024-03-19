package com.autoeval.api;

import ch.qos.logback.classic.LoggerContext;
import com.autoeval.api.evaluator.Evaluator;
import com.autoeval.api.evaluator.csv.HackathonCSVReader;
import com.autoeval.api.evaluator.csv.HackathonCSVWriter;
import com.autoeval.api.evaluator.csv.HackathonSubmission;
import com.autoeval.api.evaluator.model.TestCaseScore;
import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    @Parameter(names = {"-csv"}, description = "Path to CSV file downloaded from Hackathon portal", required = true)
    private String csvFilePath = "";
    @Parameter(names = {"-tc", "--test-cases", "-f"}, description = "Path to test case file", required = true)
    private String testCaseFilePath = "";
    @DynamicParameter(names = {"-P", "-p", "--placeholder"}, description = "Placeholder values")
    private Map<String, String> placeholders = new HashMap<>();
    @Parameter(names = {"-ll", "--logging.level", "-level"}, description = "Logging Level")
    private String loggingLevel = "INFO";

    @Parameter(names = {"--out", "--output", "-o", "-O"}, description = "Path to output CSV file")
    private String outputPath = "";
    
    @Parameter(names = {"--out1", "--output1", "-o1", "-O1"}, description = "Path to output CSV file")
    private String outputPath1 = "";

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
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(ch.qos.logback.classic.Level.toLevel(loggingLevel));

        List<HackathonSubmission> submissions = new HackathonCSVReader().readHackathonCSV(csvFilePath);

        try {
            File outFile = new File(outputPath);
            if(outFile.exists()) {
                outFile.delete();
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to delete output file {}", outputPath);
        }

        final Evaluator evaluator = new Evaluator();
        
        submissions.stream()
                .map(submission -> {
                    String apiPingUrl = submission.getPingURL();
                    Map<String, String> submissionPlaceholders = new HashMap<>();
                    submissionPlaceholders.putAll(placeholders);
                    submissionPlaceholders.put("BASEPATH", apiPingUrl);
                    List<TestCaseScore> testCaseScores = evaluator.evaluate(testCaseFilePath, submissionPlaceholders);
                    return testCaseScores.stream().map(testCaseScore -> {
                        LOGGER.info("Team: '{}',TC Id: '{}', TC Name: '{}', Score: '{}'", submission.getTeamName(), testCaseScore.getTestCaseId(), testCaseScore.getTestCaseName(), testCaseScore.getTestCaseScore() , testCaseScore.getApi_response());
                        testCaseScore.setSubmissionId(submission.getTeamName());
                        return testCaseScore;
                    }).collect(Collectors.toList());
                })
                .forEach(testCaseScores -> HackathonCSVWriter.writeCsv(outputPath, outputPath1, testCaseScores));
        
    }

}