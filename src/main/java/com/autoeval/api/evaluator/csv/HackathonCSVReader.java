package com.autoeval.api.evaluator.csv;

import com.autoeval.api.Main;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HackathonCSVReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    protected List<HackathonBean> hackathonBeanList;

    public List<HackathonBean> readHackathonCSV (String csvFilePath) throws IOException {
        List<HackathonBean> hackathonBeans = new ArrayList<>();
        CSVParser csvParser = reader(csvFilePath);
        for (CSVRecord csvRecord : csvParser) {
            HackathonBean hackathonBean = new HackathonBean(csvRecord.get("TeamName"), csvRecord.get("GitHubId"), csvRecord.get("PingURL"));
            hackathonBeans.add(hackathonBean);
        }
        return hackathonBeans;
    }

    private CSVParser reader (String csvFilePath) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
        return new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim());
    }
}
