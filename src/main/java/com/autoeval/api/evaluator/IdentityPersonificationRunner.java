package com.autoeval.api.evaluator;

import ch.qos.logback.classic.LoggerContext;
import com.autoeval.api.Main;
import com.autoeval.api.evaluator.csv.HackathonBean;
import com.autoeval.api.evaluator.csv.HackathonCSVReader;
import com.autoeval.api.evaluator.model.TestCaseScore;
import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdentityPersonificationRunner {

    private static String hackathonCSVFilePath = "D:\\hackathon\\testcases\\SampleSubmissionCSV.csv";
    private static String testCaseFilePath = "D:\\hackathon\\testcases\\voice-personification-testcases.yaml";
    private static Map<String, String> placeholders = new HashMap<>();
    static {
        placeholders.put("TEST_IMAGE_DIRECTORY", "D:\\hackathon\\testcases\\");
    }
    private static String loggingLevel = "INFO";

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws IOException {
        List<HackathonBean> hackathonBeanList = csv();
        LOGGER.info("Total Score = " + placeholders);

        hackathonBeanList.forEach(hackathonBean -> {
            placeholders.put("BASEPATH", hackathonBean.getPingURL());
            System.out.println(hackathonBean.getPingURL());
            run(testCaseFilePath, placeholders);
        });
    }


    private static List<HackathonBean> csv () throws IOException {
        HackathonCSVReader hackathonCSVReader = new HackathonCSVReader();
        return hackathonCSVReader.readHackathonCSV(hackathonCSVFilePath);
    }

    public static void run(String testCaseFilePath, Map<String, String> placeholders) {
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
