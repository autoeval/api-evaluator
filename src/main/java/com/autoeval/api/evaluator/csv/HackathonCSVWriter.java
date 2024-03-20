package com.autoeval.api.evaluator.csv;

import com.autoeval.api.evaluator.model.TestCaseScore;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HackathonCSVWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HackathonCSVWriter.class);
    private static final String[] HEADERS = { "TEAM_NAME", "GITHUB_REPO", "ETP_FLAG", "API_PING_URL", "STEP0_SCORE" ,"STEP0_COMMENTS", "STEP1_SCORE" ,"STEP1_COMMENTS"};
    private static final String[] FULL_HEADERS = { "TEAM_NAME", "GITHUB_REPO", "ETP_FLAG", "API_PING_URL", "STEP0_SCORE" ,"STEP0_COMMENTS", "TEST_CASE", "STEP1_SCORE" ,"STEP1_COMMENTS", "API_RESPONSE_CODE", "REQUEST", "RESPONSE"};

    public static void writeCsv(final String outputFile, final List<TestCaseScore> testCaseScores) {

        final File file = new File(outputFile);
        final CSVFormat csvFormat;
        if (!file.exists()) {
            csvFormat = CSVFormat.DEFAULT
                    .withHeader(HEADERS)
                    .withTrim();
        } else {
            csvFormat = CSVFormat.DEFAULT
                    .withTrim();
        }
        final double totalScore = testCaseScores.stream().mapToDouble(TestCaseScore::getTestCaseScore).sum();
        final String comment = testCaseScores.stream().map(testCaseScore -> {
            String comt = testCaseScore.getTestCaseId().concat(" -> ").concat(String.valueOf(testCaseScore.getTestCaseScore()));
            if(StringUtils.isNotBlank(testCaseScore.getMessage())) {
                comt = comt.concat(String.format(" (Info: response-time: %d ms, message:'%s')", testCaseScore.getResponseTimeInMs(), testCaseScore.getMessage()));
            }
            return comt;
        }).collect(Collectors.joining("\r\n"));
        final HackathonSubmission submission = testCaseScores.stream().findAny().get().getSubmission();
        submission.setStep1Score(totalScore);
        submission.setStep1Comment(comment);
        try (final CSVPrinter printer = new CSVPrinter(new FileWriter(outputFile, file.exists()), csvFormat)) {
            printer.printRecord(submission.toArray(false));
            LOGGER.info("Total Score for submission '{}' = {}", submission.getTeamName(), totalScore);
        } catch (IOException e) {
            LOGGER.error("Failed writing record", e);
        }
    }

    public static void writeFullCsv(final String outputFile, final List<TestCaseScore> testCaseScores) {

        final File file = new File(outputFile);
        final CSVFormat csvFormat;
        if (!file.exists()) {
            csvFormat = CSVFormat.DEFAULT
                    .withHeader(FULL_HEADERS)
                    .withTrim();
        } else {
            csvFormat = CSVFormat.DEFAULT
                    .withTrim();
        }
        final double totalScore = testCaseScores.stream().mapToDouble(TestCaseScore::getTestCaseScore).sum();
        try (final CSVPrinter printer = new CSVPrinter(new FileWriter(outputFile, file.exists()), csvFormat)) {
            testCaseScores.forEach(testCaseScore -> {
                final String comment = String.format(" (Info: response-time: %d ms, message:'%s')", testCaseScore.getResponseTimeInMs(), testCaseScore.getMessage());
                final HackathonSubmission submission = testCaseScore.getSubmission();
                submission.setStep1Score(testCaseScore.getTestCaseScore());
                submission.setStep1Comment(comment);
                submission.setTestCaseId(testCaseScore.getTestCaseId());
                submission.setApiResponseCode(testCaseScore.getApiResponseCode());
                submission.setRequestBody(testCaseScore.getRequestBody());
                submission.setResponseBody(testCaseScore.getResponseBody());
                try {
                    printer.printRecord(submission.toArray(true));
                } catch (IOException e) {
                    LOGGER.error("Failed writing record", e);
                }
                LOGGER.info("Total Score for submission '{}' = {}", submission.getTeamName(), totalScore);
            });
        } catch (IOException e) {
            LOGGER.error("Failed writing record", e);
        }
    }
}
