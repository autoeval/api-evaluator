package com.wellsfargo.api.evaluator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.octomix.josson.Josson;
import com.wellsfargo.api.evaluator.model.*;
import com.wellsfargo.api.evaluator.model.http.HttpRequest;
import okhttp3.*;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.DoubleAdder;

public class Evaluator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Evaluator.class);

    public List<TestCaseScore> evaluate(final String testCaseFile, Map<String, String> placeHolders) {
        List<TestCase> testCases = parseTestCases(testCaseFile, placeHolders);
        final OkHttpClient client = new OkHttpClient().newBuilder().build();
        final List<TestCaseScore> testCaseScores = new ArrayList<>();
        testCases.forEach(testCase -> testCaseScores.add(executeTest(client, testCase)));
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
            final String responseText = response.body().string();

            testCase.getChecks().forEach(check -> doubleAdder.add(executeConditionAndScore(check, responseText)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        finally {
            TestCaseScore score = new TestCaseScore();
            score.setId(testCase.getId());
            score.setName(testCase.getName());
            score.setScore(doubleAdder.doubleValue());
            return score;
        }
    }

    private double executeConditionAndScore(final Check check, final String responseText) {
        if(Objects.isNull(check.getCondition())) {
            return check.getScore().getPassed();
        } else if(Objects.isNull(check.getCondition().getExpression()) && !Objects.isNull(check.getCondition().getExecutor())) {
            if(buildExecutor(check.getCondition().getExecutor()).apply(responseText)) {
                return check.getScore().getPassed();
            } else {
                return check.getScore().getFailed();
            }
        } else if(!Objects.isNull(check.getCondition().getExpression()) && !Objects.isNull(check.getCondition().getExecutor())) {
            if(check.getCondition().getExpressionType().equals(ExpressionType.JSON_PATH)) {
                final List<Object> jsonPathResult = computeJsonPath(responseText, check.getCondition().getExpression());
                if(buildExecutor(check.getCondition().getExecutor()).apply(jsonPathResult)) {
                    return check.getScore().getPassed();
                } else {
                    return check.getScore().getFailed();
                }
            } else {
                final JsonNode node = computeJosson(responseText, check.getCondition().getExpression());
                if(buildExecutor(check.getCondition().getExecutor()).apply(node)) {
                    return check.getScore().getPassed();
                } else {
                    return check.getScore().getFailed();
                }
            }
        } else if(!Objects.isNull(check.getCondition().getExpression()) && Objects.isNull(check.getCondition().getExecutor())) {
            if(check.getCondition().getExpressionType().equals(ExpressionType.JSON_PATH)) {
                final List<Object> jsonPathResult = computeJsonPath(responseText, check.getCondition().getExpression());
                return calculateScoreForJsonPath(jsonPathResult, check.getScore());
            } else {
                final JsonNode node = computeJosson(responseText, check.getCondition().getExpression());
                return calculateScoreForJosson(node, check.getScore());
            }
        } else {
            return check.getScore().getPassed();
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

    private double calculateScoreForJsonPath(final List<Object> result, final Score score) {
        if(result == null || result.isEmpty()) {
            return score.getFailed();
        } else {
            return score.getPassed();
        }
    }

    private double calculateScoreForJosson(final JsonNode node, final Score score) {
        if(node == null || node.isNull() || node.isEmpty()) {
            return score.getFailed();
        } else {
            return score.getPassed();
        }
    }

    private ConditionExecutor buildExecutor(final String executorClass) {
        try {
            Class<?> cls = Class.forName(executorClass);
            return (ConditionExecutor) cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return new ConditionExecutor() {};
        }
    }
}
