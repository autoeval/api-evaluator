package com.autoeval.api.evaluator.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;

public class SBConditionExecutorPDF  implements ConditionExecutor {
	
	public boolean apply(final JsonNode node,  String[] keywords, PdfContentWrapper pdf_content, String element, String primaryValue) {
		
		  boolean valid_pdf = false;
		
		  if(null != node) {
			  String pdfUrlPath = node.get("pdfUrlPath").asText();
		      
		        
				try {
		            // Create URL object
		            URL url = new URL(pdfUrlPath);

		            // Open connection
		            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		            // Set request method
		            connection.setRequestMethod("GET");

		            // Check if response code is 200 (OK)
		            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
		                // Check if content type is PDF
		                String contentType = connection.getContentType();
		                if (contentType != null && (contentType.contains("application/pdf") || contentType.contains("application/octet-stream"))) {
		                    // Check if content length is greater than 0
		                    int contentLength = connection.getContentLength();
		                    if (contentLength > 0) {
		                        System.out.println("PDF file is valid and has content.");
		                        valid_pdf = true;
		                        
		                     // Read the PDF content into a byte array
		                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		                        try (InputStream inputStream = connection.getInputStream()) {
		                            byte[] buffer = new byte[1024];
		                            int bytesRead;
		                            while ((bytesRead = inputStream.read(buffer)) != -1) {
		                                outputStream.write(buffer, 0, bytesRead);
		                            }
		                        }
		                        pdf_content.setPdfContent(pdfUrlPath);
		                        
		                        
		                    } else {
		                        System.out.println("PDF file is empty.");
		                    }
		                } else {
		                    System.out.println("URL does not point to a PDF file.");
		                }
		            } else {
		                System.out.println("Failed to retrieve PDF file. Response code: " + connection.getResponseCode());
		            }

		            // Close connection
		            connection.disconnect();

		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		  }
		
        
        return valid_pdf;
    }
	

}
