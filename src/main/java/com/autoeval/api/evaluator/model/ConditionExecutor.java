package com.autoeval.api.evaluator.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface ConditionExecutor {
	
    default boolean apply(final List<Object> selectedNodes) {
        return false;
    }
    default boolean apply(final JsonNode node, String[] keywords, PdfContentWrapper pdf_content, String element, String primaryValue) {
        return false;
    }

    default boolean apply(final String responseText) {
        return false;
    }
}
