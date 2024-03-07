package com.wellsfargo.api.evaluator.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface ConditionExecutor {
    default boolean apply(final List<Object> selectedNodes) {
        return false;
    }
    default boolean apply(final JsonNode node) {
        return false;
    }

    default boolean apply(final String responseText) {
        return false;
    }
}
