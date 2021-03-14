package com.trendyol.applicanttest.model;

import lombok.Getter;

public enum ProductDetailsField {
	DEEPLINK_CAMPAIGN_ID("CampaignId"),
	DEEPLINK_MERCHANT_ID("MerchantId"),
	WEBURL_BOUTIQUE_ID("boutiqueId"),
	WEBURL_MERCHANT_ID("merchantId");

	static {
		DEEPLINK_CAMPAIGN_ID.counterPart = WEBURL_BOUTIQUE_ID;
		DEEPLINK_MERCHANT_ID.counterPart = WEBURL_MERCHANT_ID;
		WEBURL_BOUTIQUE_ID.counterPart = DEEPLINK_CAMPAIGN_ID;
		WEBURL_MERCHANT_ID.counterPart = DEEPLINK_MERCHANT_ID;
	}

	@Getter
	private final String fieldName;
	@Getter
	private ProductDetailsField counterPart;

	ProductDetailsField(String fieldName) {
		this.fieldName = fieldName;
	}


}
