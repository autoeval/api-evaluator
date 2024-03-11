package com.autoeval.api.evaluator.csv;

import com.autoeval.api.evaluator.model.TestCaseScore;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class HackathonCSVWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HackathonCSVWriter.class);
    private static final String[] HEADERS = { "TeamName", "Step1Score", "Step1Comment"};

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
        try (final CSVPrinter printer = new CSVPrinter(new FileWriter(outputFile, file.exists()), csvFormat)) {
            String teamName = testCaseScores.stream().findAny().get().getSubmissionId();
            printer.printRecord(teamName, totalScore, "Sample Comment");
            LOGGER.info("Total Score for submission '{}' = {}", teamName, totalScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
