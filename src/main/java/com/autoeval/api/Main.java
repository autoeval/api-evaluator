package com.autoeval.api;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import com.autoeval.api.evaluator.Evaluator;
import com.autoeval.api.evaluator.csv.HackathonCSVReader;
import com.autoeval.api.evaluator.csv.HackathonCSVWriter;
import com.autoeval.api.evaluator.csv.HackathonSubmission;
import com.autoeval.api.evaluator.model.TestCaseScore;
import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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

    @Parameter(names = {"--log-config-file", "-l", "-L"}, description = "Path to log config file")
    private String logConfigFile = "";

    @Parameter(names = {"--tech-info", "--ti", "-ti", "--info", "-info"}, description = "Technical Info will be available")
    private boolean techInfo = false;

    @Parameter(names = {"--request"}, description = "Output HTTP Request JSON")
    private boolean request = false;

    @Parameter(names = {"--response"}, description = "Output HTTP Request JSON")
    private boolean response = false;

    @Parameter(names = {"--fullpath", "--full", "-fp", "-FP"}, description = "Path to full output CSV file")
    private String fullOutputPath = "";

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

        if(StringUtils.isNotBlank(logConfigFile)) {
            try(InputStream configStream = new FileInputStream(logConfigFile)) {
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(loggerContext);
                configurator.doConfigure(configStream);
            } catch (Exception ignored) {
                LOGGER.warn("Not able to configure logger from config file");
            }
        }

        List<HackathonSubmission> submissions = new HackathonCSVReader().readHackathonCSV(csvFilePath);

        try {
            File outFile = new File(outputPath);
            if(outFile.exists()) {
                outFile.delete();
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to delete output file {}", outputPath);
        }

        try {
            File outFile = new File(fullOutputPath);
            if(outFile.exists()) {
                outFile.delete();
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to delete output file {}", fullOutputPath);
        }

        final Evaluator evaluator = new Evaluator();
        submissions.stream()
                .map(submission -> {
                    String apiPingUrl = submission.getPingURL();
                    Map<String, String> submissionPlaceholders = new HashMap<>(placeholders);
                    submissionPlaceholders.put("BASEPATH", apiPingUrl);
                    List<TestCaseScore> testCaseScores = evaluator.evaluate(testCaseFilePath, submissionPlaceholders, submission, techInfo, request, response);
                    return testCaseScores.stream().peek(testCaseScore -> {
                        LOGGER.info("Team: '{}',TC Id: '{}', TC Name: '{}', Score: '{}'", submission.getTeamName(), testCaseScore.getTestCaseId(), testCaseScore.getTestCaseName(), testCaseScore.getTestCaseScore());
                        testCaseScore.setSubmission(submission);
                    }).collect(Collectors.toList());
                })
                .forEach(testCaseScores -> {
                    HackathonCSVWriter.writeCsv(outputPath, testCaseScores);
                    if(StringUtils.isNotBlank(fullOutputPath)) {
                        HackathonCSVWriter.writeFullCsv(fullOutputPath, testCaseScores);
                    }
                });
    }

}