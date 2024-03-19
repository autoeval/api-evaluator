package com.autoeval.api.evaluator.model;

import com.fasterxml.jackson.databind.JsonNode;

public class SBConditionExecutorPrimary implements ConditionExecutor {

	public boolean apply(final JsonNode currentnode,  String[] keywords, PdfContentWrapper pdf_content, String element, String primaryValue) {

		boolean matches = false;

		if (null != currentnode) {
			try {

				if (currentnode.isArray()) {
					for (JsonNode node : currentnode) {
						String esgIndicators = node.get("esgIndicators").asText();
						String primaryDetails = node.get("primaryDetails").asText();

						if (esgIndicators != null && esgIndicators.equalsIgnoreCase(element)) {

							if (primaryDetails != null && primaryDetails.equalsIgnoreCase(primaryValue)) {

								matches = true;
							}
						}

					}
				}
			} catch (Exception ex) {

			}

		}

		return matches;
	}

}
