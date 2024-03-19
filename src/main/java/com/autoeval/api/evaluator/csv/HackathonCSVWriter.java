package com.autoeval.api.evaluator.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.autoeval.api.evaluator.model.TestCaseScore;

public class HackathonCSVWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HackathonCSVWriter.class);
    private static final String[] HEADERS = { "TeamName", "Test Name","Test Score","API Response" ,"Pdf Content"};
    
    private static final String[] HEADERS1 = { "TeamName", "Total Score", "Comment"};

    public static void writeCsv(final String outputFile, final String outputPath1, final List<TestCaseScore> testCaseScores) {

        final File file = new File(outputFile);
        final File file1 = new File(outputPath1);
        
        
        final CSVFormat csvFormat;
        if (!file.exists()) {
            csvFormat = CSVFormat.DEFAULT
                    .withHeader(HEADERS)
                    .withTrim();
        } else {
            csvFormat = CSVFormat.DEFAULT
                    .withTrim();
        }
        
        final CSVFormat csvFormat1;
        if (!file1.exists()) {
        	csvFormat1 = CSVFormat.DEFAULT
                    .withHeader(HEADERS1)
                    .withTrim();
        } else {
        	csvFormat1 = CSVFormat.DEFAULT
                    .withTrim();
        }
        
        
		
		 final double totalScore = testCaseScores.stream().mapToDouble(TestCaseScore::getTestCaseScore).sum();
		 final String comment = testCaseScores.stream().map(testCaseScore -> testCaseScore.getTestCaseId().concat(" -> ").concat(String.valueOf( testCaseScore.getTestCaseScore()))).collect(Collectors.joining("\r\n"));
		 
        try (final CSVPrinter printer = new CSVPrinter(new FileWriter(outputFile, file.exists()), csvFormat)) {
        	
        	for(TestCaseScore tc :testCaseScores) {
        		printer.printRecord(tc.getSubmissionId(), tc.getTestCaseId() , tc.getTestCaseScore() , tc.getApi_response()!= null ? tc.getApi_response().replace("\n", "").toString() : "" , tc.getPdf_content());
        		
        	}
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try (final CSVPrinter printer1 = new CSVPrinter(new FileWriter(outputPath1, file1.exists()), csvFormat1)) {
        	
        	 String teamName = testCaseScores.stream().findAny().get().getSubmissionId();
           	 printer1.printRecord(teamName, totalScore , comment );
           	 
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
   	
   	 
    }
    
}
