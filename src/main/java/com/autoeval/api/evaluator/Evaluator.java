package com.autoeval.api.evaluator;

import com.autoeval.api.evaluator.csv.HackathonSubmission;
import com.autoeval.api.evaluator.model.*;
import com.autoeval.api.evaluator.model.http.HttpRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.octomix.josson.Josson;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.DoubleAdder;

public class Evaluator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Evaluator.class);
    private final Gson gson = new GsonBuilder().create();

    public List<TestCaseScore> evaluate(final String testCaseFile, Map<String, String> placeHolders, HackathonSubmission submission, boolean techInfo) {
        List<TestCase> testCases = parseTestCases(testCaseFile, placeHolders);
        OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder();
        final OkHttpClient client = clientBuilder
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
        final List<TestCaseScore> testCaseScores = new ArrayList<>();
        for(TestCase testCase : testCases) {
            TestCaseScore testCaseScore = executeTest(client, testCase, submission, techInfo);
            testCaseScores.add(testCaseScore);
        }
        return testCaseScores;
    }

    private List<TestCase> parseTestCases(final String testCaseFile, Map<String, String> placeHolders) {
        try {
            File file = new File(testCaseFile);
            String yamlStr = Files.readString(file.toPath());
            StrSubstitutor sub = new StrSubstitutor(placeHolders, "${", "}");
            String result = sub.replace(yamlStr);
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            return mapper.readValue(result, new TypeReference<>() {
            });
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private TestCaseScore executeTest(final OkHttpClient client, final TestCase testCase, HackathonSubmission submission, boolean techInfo) {
        TestCaseScore score = new TestCaseScore();
        final DoubleAdder doubleAdder = new DoubleAdder();
        long responseTimeInMs = 0;
        try {
            HttpRequest request = testCase.getHttp().getRequest();
            AtomicReference<String> bodyContentType = new AtomicReference<>("application/json");
            final Request.Builder builder = new Request.Builder();
            request.getHeaders().forEach(header -> {
                builder.addHeader(header.getName(), header.getValue());
                if (header.getName().equalsIgnoreCase("Content-Type")) {
                    bodyContentType.set(header.getValue());
                }
            });

            RequestBody body;
            if (!Objects.isNull(request.getMultipart())) {
                MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                if (request.getMultipart().getFiles() != null) {
                    request.getMultipart().getFiles().forEach(filePart -> multipartBodyBuilder.addFormDataPart(filePart.getParamName(), filePart.getPath(),
                            RequestBody.create(MediaType.parse(filePart.getContentType()),
                                    new File(filePart.getPath()))));
                }
                if (request.getMultipart().getFields() != null) {
                    request.getMultipart().getFields().forEach(field -> {
                        multipartBodyBuilder.addFormDataPart(field.getName(), field.getValue());
                    });
                }
                body = multipartBodyBuilder.build();
            } else if (!Objects.isNull(request.getBody())) {
                body = RequestBody.create(request.getBody(), MediaType.parse(bodyContentType.get()));
            } else if (!Objects.isNull(request.getForm())) {
                FormBody.Builder formBuilder = new FormBody.Builder();
                if (request.getForm().getFields() != null) {
                    request.getForm().getFields().forEach(field -> {
                        formBuilder.addEncoded(field.getName(), field.getValue());
                    });
                }
                body = formBuilder.build();
            } else {
                body = null;
            }

            final Request httpRequest = builder
                    .url(buildBasePath(testCase.getHttp().getBasePath(), testCase).concat(request.getUri()))
                    .method(request.getMethod(), body)
                    .build();
            LOGGER.info("Team Name: {}, Test Case: '{}', Request(url={}): '{}'", submission.getTeamName(), testCase.getId(), httpRequest.url(), gson.toJson(testCase.getHttp()));
            final Response response = client.newCall(httpRequest).execute();
            responseTimeInMs = response.receivedResponseAtMillis() - response.sentRequestAtMillis();
            final String responseText = response.body().string();
            LOGGER.info("Team Name: {}, Test Case: '{}', Response: '{}' took {} ms", submission.getTeamName(), testCase.getId(), responseText, responseTimeInMs);
            testCase.getChecks().forEach(check -> doubleAdder.add(executeConditionAndScore(check, responseText)));
            score.setTestCaseId(testCase.getId());
            score.setTestCaseName(testCase.getName());
            score.setTestCaseScore(doubleAdder.doubleValue());
            if(techInfo) {
                if(response.code()!= 200) {
                    score.setMessage("ERROR: HTTP Status='".concat(String.valueOf(response.code())).concat("'"));
                } else {
                    score.setMessage("SUCCESSFUL");
                }
                score.setResponseTimeInMs(responseTimeInMs);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            score.setTestCaseId(testCase.getId());
            score.setTestCaseName(testCase.getName());
            score.setTestCaseScore(0.0);
            if(techInfo) {
                score.setMessage("ERROR: ".concat(e.getMessage()));
                score.setResponseTimeInMs(responseTimeInMs);
            }
        }
        return score;
    }

    private String buildBasePath(String apiUrl, TestCase testCase) {
        if(StringUtils.endsWithIgnoreCase(apiUrl, testCase.getHttp().getRequest().getUri().concat("/ping"))) {
            return apiUrl.substring(0, apiUrl.length() - testCase.getHttp().getRequest().getUri().concat("/ping").length());
        } else if(StringUtils.endsWithIgnoreCase(apiUrl, testCase.getHttp().getRequest().getUri().concat("/ping/"))) {
            return apiUrl.substring(0, apiUrl.length() - testCase.getHttp().getRequest().getUri().concat("/ping/").length());
        } else if(StringUtils.endsWithIgnoreCase(apiUrl, "/ping")) {
            return apiUrl.substring(0, apiUrl.length() - "/ping".length());
        } else if(StringUtils.endsWithIgnoreCase(apiUrl, "/ping/")) {
            return apiUrl.substring(0, apiUrl.length() - "/ping/".length());
        } else if(StringUtils.endsWithIgnoreCase(apiUrl, testCase.getHttp().getRequest().getUri())) {
            return apiUrl.substring(0, apiUrl.length() - testCase.getHttp().getRequest().getUri().length());
        } else if(StringUtils.endsWithIgnoreCase(apiUrl, testCase.getHttp().getRequest().getUri().concat("/"))) {
            return apiUrl.substring(0, apiUrl.length() - testCase.getHttp().getRequest().getUri().concat("/").length());
        } else {
            return apiUrl;
        }
    }

    private double executeConditionAndScore(final Check check, final String responseText) {
        if (Objects.isNull(check.getCondition())) {
            return calculateScore(responseText, check.getScore(), true);
        } else if (Objects.isNull(check.getCondition().getExpression()) && !Objects.isNull(check.getCondition().getExecutor())) {
            if (buildExecutor(check.getCondition().getExecutor()).apply(responseText)) {
                return calculateScore(responseText, check.getScore(), true);
            } else {
                return calculateScore(responseText, check.getScore(), false);
            }
        } else if (!Objects.isNull(check.getCondition().getExpression()) && !Objects.isNull(check.getCondition().getExecutor())) {
            if (check.getCondition().getExpressionType().equals(ExpressionType.JSON_PATH)) {
                final List<Object> jsonPathResult = computeJsonPath(responseText, check.getCondition().getExpression());
                if (buildExecutor(check.getCondition().getExecutor()).apply(jsonPathResult)) {
                    return calculateScore(responseText, check.getScore(), true);
                } else {
                    return calculateScore(responseText, check.getScore(), false);
                }
            } else {
                final JsonNode node = computeJosson(responseText, check.getCondition().getExpression());
                if (buildExecutor(check.getCondition().getExecutor()).apply(node)) {
                    return calculateScore(responseText, check.getScore(), true);
                } else {
                    return calculateScore(responseText, check.getScore(), false);
                }
            }
        } else if (!Objects.isNull(check.getCondition().getExpression()) && Objects.isNull(check.getCondition().getExecutor())) {
            if (check.getCondition().getExpressionType().equals(ExpressionType.JSON_PATH)) {
                final List<Object> jsonPathResult = computeJsonPath(responseText, check.getCondition().getExpression());
                return calculateScoreForJsonPath(responseText, jsonPathResult, check.getScore());
            } else {
                final JsonNode node = computeJosson(responseText, check.getCondition().getExpression());
                return calculateScoreForJosson(responseText, node, check.getScore());
            }
        } else {
            return calculateScore(responseText, check.getScore(), true);
        }
    }

    private List<Object> computeJsonPath(final String responseText, final String expression) {
        try {
            final DocumentContext context = JsonPath.parse(responseText);
            return context.read("[?(".concat(expression).concat(")]"));
        } catch (Exception e) {
            return null;
        }
    }

    private JsonNode computeJosson(final String responseText, final String expression) {
        try {
            Josson josson = Josson.fromJsonString(responseText);
            return josson.getNode(expression);
        } catch (Exception e) {
            return null;
        }
    }

    private double calculateScoreForJsonPath(final String responseText, final List<Object> result, final Score score) {

        if (result == null || result.isEmpty()) {
            return calculateScore(responseText, score, false);
        } else {
            return calculateScore(responseText, score, true);
        }
    }

    private double calculateScoreForJosson(final String responseText, final JsonNode node, final Score score) {
        if (node == null || node.isNull() || node.isEmpty()) {
            return calculateScore(responseText, score, false);
        } else {
            return calculateScore(responseText, score, true);
        }
    }

    private double calculateScore(final String responseText, Score score, boolean isSuccess) {
        if(StringUtils.isNotBlank(score.getCalculator())) {
            ScoreCalculator calculator = buildScoreCalculator(score.getCalculator());
            if(isSuccess) {
                return calculator.calculatePassedScore(responseText);
            } else {
                return calculator.calculateFailedScore(responseText);
            }
        } else {
            if(isSuccess) {
                return score.getPassed();
            } else {
                return score.getFailed();
            }
        }
    }

    private ConditionExecutor buildExecutor(final String executorClass) {
        try {
            Class<?> cls = Class.forName(executorClass);
            return (ConditionExecutor) cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return new ConditionExecutor() {
            };
        }
    }

    private ScoreCalculator buildScoreCalculator(final String calculatorClass) {
        try {
            Class<?> cls = Class.forName(calculatorClass);
            return (ScoreCalculator) cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return new ScoreCalculator() {
            };
        }
    }
}
