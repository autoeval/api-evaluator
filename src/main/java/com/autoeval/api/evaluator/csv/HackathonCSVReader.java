package com.autoeval.api.evaluator.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HackathonCSVReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(HackathonCSVReader.class);

    public List<HackathonSubmission> readHackathonCSV (String csvFilePath) {
        try {
            List<HackathonSubmission> hackathonSubmissions = new ArrayList<>();
            CSVParser csvParser = reader(csvFilePath);
            for (CSVRecord csvRecord : csvParser) {
                HackathonSubmission hackathonSubmission = new HackathonSubmission(csvRecord.get("TeamName"), csvRecord.get("RepoLink"), csvRecord.get("PingURL"));
                hackathonSubmissions.add(hackathonSubmission);
            }
            return hackathonSubmissions;
        } catch ( Exception e) {
            LOGGER.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private CSVParser reader (String csvFilePath) throws IOException {
        Reader reader = Files.newBufferedReader(new File(csvFilePath).toPath());
        return new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim());
    }
}
