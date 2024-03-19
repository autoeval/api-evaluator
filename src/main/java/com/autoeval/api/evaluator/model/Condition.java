package com.autoeval.api.evaluator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public class Condition {
    private String expression;
    @JsonProperty("expression-type")
    private ExpressionType expressionType = ExpressionType.JSON_PATH;
    private String executor;
    @JsonProperty("keywords")
    private String[] labels;
    
    private String element;
    private String primaryValue;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public ExpressionType getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(ExpressionType expressionType) {
        this.expressionType = expressionType;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

	public String[] getLabels() {
		return labels;
	}

	public void setLabels(String[] labels) {
		this.labels = labels;
	}

	public String getPrimaryValue() {
		return primaryValue;
	}

	public void setPrimaryValue(String primaryValue) {
		this.primaryValue = primaryValue;
	}

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}
}
