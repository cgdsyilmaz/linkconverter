package com.trendyol.applicanttest.converter;

import static com.trendyol.applicanttest.util.LinkConverterUtil.*;

import com.trendyol.applicanttest.model.LinkType;
import com.trendyol.applicanttest.model.PageType;
import com.trendyol.applicanttest.exception.TrendyolLinkException;
import com.trendyol.applicanttest.model.ProductDetailsField;
import org.jetbrains.annotations.NotNull;

public class DeeplinkToWebURLConverter extends LinkConverter {

	@Override
	public LinkType getLinkType() {
		return LinkType.Deeplink;
	}

	/**
	 * Returns the webURL home page
	 *
	 * @return WebURL Home Page
	 */
	@Override
	public String convertHomePage() {
		pageType = PageType.OtherPage;

		return WEBURL_START;
	}

	/**
	 * Returns the WebURL product page
	 *
	 * @param originalLink Deeplink to build a WebURL product page
	 * @return WebURL Product Page
	 */
	@Override
	public String convertProductPage(String originalLink) {
		int contentIdIndex = originalLink.indexOf(DEEPLINK_CONTENT_ID);
		int lastContentIdIndex = originalLink.lastIndexOf(DEEPLINK_CONTENT_ID);

		if (contentIdIndex != lastContentIdIndex) {
			throw new TrendyolLinkException("Only one content id should be in a Web URL!");
		}
		if (contentIdIndex == -1) {
			throw new TrendyolLinkException("Web URL does not have content ID!");
		}

		String productDetails = originalLink.substring(contentIdIndex);
		int contentIdEndIndex = getContentIdEndIndex(productDetails);
		String contentId = getContentId(productDetails, contentIdEndIndex);

		pageType = PageType.ProductDetailPage;

		return String.format("%s%s%s%s%s",
			WEBURL_BRAND_PRODUCT_NAME,
			WEBURL_PRODUCT_PAGE,
			contentId,
			(productDetails.contains(AMPERSAND) ? QUESTION_MARK : ""),
			getCampaignAndMerchantID(productDetails.substring(contentIdEndIndex)));
	}

	@NotNull
	private String getContentId(String productDetails, int contentIdEndIndex) {
		return productDetails.substring(productDetails.indexOf(EQUALS) + 1, contentIdEndIndex);
	}

	private int getContentIdEndIndex(String productDetails) {
		return productDetails.contains(AMPERSAND) ? productDetails.indexOf(AMPERSAND) : productDetails.length();
	}

	/**
	 * Returns the deeplink search page
	 *
	 * @param originalLink Deeplink to build a WebURL search page
	 * @return WebURL Search Page
	 */
	@Override
	public String convertSearchPage(String originalLink) {
		if (!originalLink.contains(DEEPLINK_QUERY)) {
			throw new TrendyolLinkException("Query parameter does not exist!");
		}
		if (isQueryStringEmpty(originalLink)) {
			throw new TrendyolLinkException("Empty query string!");
		}

		String query = originalLink.substring(originalLink.indexOf(DEEPLINK_QUERY) + DEEPLINK_QUERY.length());

		if (!query.matches(QUERY_MATCHER)) {
			throw new TrendyolLinkException("Query string does not match with the rules!");
		}

		pageType = PageType.SearchPage;

		return String.format("%s%s%s",
			WEBURL_SEARCH_PAGE,
			WEBURL_SEARCH_QUERY,
			query);
	}

	private boolean isQueryStringEmpty(String originalLink) {
		return originalLink.indexOf(DEEPLINK_QUERY) == originalLink.length() - DEEPLINK_QUERY.length();
	}

	/**
	 * Returns and accepts only one content campaign and merchant id.
	 *
	 * @param productDetails Includes content campaign and merchant types and id values
	 * @return WebURL equivalent of content campaign and merchant ids or id has non matching
	 * characters
	 */
	private String getCampaignAndMerchantID(String productDetails) {
		return getAvailableProductDetails(productDetails, ProductDetailsField.DEEPLINK_CAMPAIGN_ID, ProductDetailsField.DEEPLINK_MERCHANT_ID);
	}
}
