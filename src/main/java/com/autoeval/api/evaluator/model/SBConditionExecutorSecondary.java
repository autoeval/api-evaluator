package com.autoeval.api.evaluator.model;

import com.fasterxml.jackson.databind.JsonNode;

public class SBConditionExecutorSecondary implements ConditionExecutor {

	public boolean apply(final JsonNode currentnode, String[] keywords, PdfContentWrapper pdf_content, String element, String primaryValue) {

		boolean containsKeywords = false;

		if (null != currentnode) {

			try {

				if (currentnode.isArray()) {
					for (JsonNode node : currentnode) {
						String esgIndicators = node.get("esgIndicators").asText();
						String secondaryDetails = node.get("secondaryDetails").asText();

						if (esgIndicators != null && "esgIndicators".equalsIgnoreCase(element)) {

							if (secondaryDetails != null && null != keywords) {

								for (String k : keywords) {
									if (secondaryDetails.contains(k)) {
										containsKeywords = true;
									}
								}

							}
						}

					}
				}
			} catch (Exception ex) {

			}

		}

		return containsKeywords;
	}

}
