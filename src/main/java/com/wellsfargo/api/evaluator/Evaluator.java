package com.wellsfargo.api.evaluator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.wellsfargo.api.evaluator.model.Check;
import com.wellsfargo.api.evaluator.model.TestCase;
import com.wellsfargo.api.evaluator.model.TestCaseScore;
import com.wellsfargo.api.evaluator.model.http.HttpRequest;
import okhttp3.*;
import org.apache.commons.lang.text.StrSubstitutor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.DoubleAdder;

public class Evaluator {

    public List<TestCaseScore> evaluate(final String testCaseFile, Map<String, String> placeHolders) {
        List<TestCase> testCases = parseTestCases(testCaseFile, placeHolders);
        final OkHttpClient client = new OkHttpClient().newBuilder().build();
        final List<TestCaseScore> testCaseScores = new ArrayList<>();
        testCases.forEach(testCase -> testCaseScores.add(executeTest(client, testCase)));
        return testCaseScores;
    }
    private List<TestCase> parseTestCases(final String testCaseFile, Map<String, String> placeHolders) {
        try {
            String yamlStr = Files.readString(Path.of(testCaseFile));
            StrSubstitutor sub = new StrSubstitutor(placeHolders, "${", "}");
            String result = sub.replace(yamlStr);
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            return mapper.readValue(result, new TypeReference<>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    private TestCaseScore executeTest(final OkHttpClient client, final TestCase testCase) {
        final DoubleAdder doubleAdder = new DoubleAdder();
        try {
            HttpRequest request = testCase.getHttp().getRequest();
            AtomicReference<String> bodyContentType = new AtomicReference<>("application/json");
            final Request.Builder builder = new Request.Builder();
            request.getHeaders().forEach(header -> {
                builder.addHeader(header.getName(), header.getValue());
                if(header.getName().equalsIgnoreCase("Content-Type")) {
                    bodyContentType.set(header.getValue());
                }
            });

            RequestBody body;
            if(!Objects.isNull(request.getMultipart())) {
                MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                if(request.getMultipart().getFiles() != null) {
                    request.getMultipart().getFiles().forEach(filePart -> multipartBodyBuilder.addFormDataPart(filePart.getParamName(), filePart.getPath(),
                            RequestBody.create(MediaType.parse(filePart.getContentType()),
                                    new File(filePart.getPath()))));
                }
                if(request.getMultipart().getFields() != null) {
                    request.getMultipart().getFields().forEach(field -> {
                        multipartBodyBuilder.addFormDataPart(field.getName(), field.getValue());
                    });
                }
                body = multipartBodyBuilder.build();
            } else if(!Objects.isNull(request.getBody())) {
                body = RequestBody.create(request.getBody(), MediaType.parse(bodyContentType.get()));
            } else if(!Objects.isNull(request.getForm())) {
                FormBody.Builder formBuilder = new FormBody.Builder();
                if(request.getForm().getFields() != null) {
                    request.getForm().getFields().forEach(field -> {
                        formBuilder.addEncoded(field.getName(), field.getValue());
                    });
                }
                body = formBuilder.build();
            } else {
                body = null;
            }

            final Request httpRequest = builder
                    .url(testCase.getHttp().getBasePath().concat(request.getUri()))
                    .method(request.getMethod(), body)
                    .build();
            final Response response = client.newCall(httpRequest).execute();
            String resTxt = response.body().string();
            final DocumentContext context = JsonPath.parse(resTxt);

            testCase.getChecks().forEach(check -> doubleAdder.add(calculateScoreforCheck(context, check)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            TestCaseScore score = new TestCaseScore();
            score.setId(testCase.getId());
            score.setName(testCase.getName());
            score.setScore(doubleAdder.doubleValue());
            return score;
        }
    }
    private double calculateScoreforCheck(final DocumentContext context, final Check check) {
        try {
            List<Object> result = context.read("[?(".concat(check.getCondition()).concat(")]"));
            if(result == null || result.isEmpty()) {
                return check.getScore().getFailed();
            } else {
                return check.getScore().getPassed();
            }
        } catch (Exception e) {
            return check.getScore().getFailed();
        }
    }
}
