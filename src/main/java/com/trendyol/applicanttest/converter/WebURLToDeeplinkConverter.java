package com.trendyol.applicanttest.converter;

import static com.trendyol.applicanttest.util.LinkConverterUtil.*;

import com.trendyol.applicanttest.model.LinkType;
import com.trendyol.applicanttest.model.PageType;
import com.trendyol.applicanttest.exception.TrendyolLinkException;
import com.trendyol.applicanttest.model.ProductDetailsField;
import org.jetbrains.annotations.NotNull;

public class WebURLToDeeplinkConverter extends LinkConverter {

	@Override
	public LinkType getLinkType() {
		return LinkType.WebURL;
	}

	/**
	 * Returns the deeplink home page
	 *
	 * @return Deeplink Home Page
	 */
	@Override
	public String convertHomePage() {
		pageType = PageType.OtherPage;

		return DEEPLINK_START + DEEPLINK_HOME_PAGE;
	}

	/**
	 * Returns the deeplink product page
	 *
	 * @param originalLink WebURL to build a deeplink product page
	 * @return Deeplink Product Page
	 */
	@Override
	public String convertProductPage(String originalLink) {
		int contentIdIndex = originalLink.indexOf(WEBURL_PRODUCT_PAGE);
		if (contentIdIndex == originalLink.length() - WEBURL_PRODUCT_PAGE.length()) {
			throw new TrendyolLinkException("Web URL does not have content ID!");
		}
		String productDetails = originalLink.substring(contentIdIndex + WEBURL_PRODUCT_PAGE.length());

		StringBuilder productPage = new StringBuilder()
			.append(DEEPLINK_PRODUCT_PAGE)
			.append(AMPERSAND)
			.append(DEEPLINK_CONTENT_ID)
			.append(EQUALS);

		if (!productDetails.contains(QUESTION_MARK)) {
			productPage.append(productDetails);
		} else {
			String productId = productDetails.substring(0, productDetails.indexOf(QUESTION_MARK));
			if (!productId.matches(ID_MATCHER)) {
				throw new TrendyolLinkException("Content ID has non matching characters!");
			}

			productDetails = productDetails.substring(productDetails.indexOf(QUESTION_MARK) + 1);
			productPage.append(productId)
				.append(AMPERSAND)
				.append(getBoutiqueAndMerchantID(productDetails));
		}

		pageType = PageType.ProductDetailPage;
		return productPage.toString();
	}

	/**
	 * Returns the deeplink search page
	 *
	 * @param originalLink WebURL to build a deeplink search page
	 * @return Deeplink Search Page
	 */
	@Override
	public String convertSearchPage(String originalLink) {
		if (!originalLink.contains(WEBURL_SEARCH_QUERY)) {
			throw new TrendyolLinkException("Query parameter does not exist!");
		}

		if (isQueryStringEmpty(originalLink)) {
			throw new TrendyolLinkException("Empty query string!");
		}

		String query = getQueryString(originalLink);

		if (!query.matches(QUERY_MATCHER)) {
			throw new TrendyolLinkException("Query string does not match with the rules!");
		}

		pageType = PageType.SearchPage;

		return String.format("%s%s%s%s",
			DEEPLINK_SEARCH_PAGE,
			AMPERSAND,
			DEEPLINK_QUERY,
			query);
	}

	@NotNull
	private String getQueryString(String originalLink) {
		return originalLink.substring(originalLink.indexOf(WEBURL_SEARCH_QUERY) + WEBURL_SEARCH_QUERY.length());
	}

	private boolean isQueryStringEmpty(String originalLink) {
		return originalLink.indexOf(WEBURL_SEARCH_QUERY) == (originalLink.length() - WEBURL_SEARCH_QUERY.length());
	}

	/**
	 * Returns and accepts only one boutique and merchant id.
	 *
	 * @param productDetails Includes boutique and merchant types and id values
	 * @return Deeplink equivalent of boutique and merchant ids
	 */
	private String getBoutiqueAndMerchantID(String productDetails) {
		return getAvailableProductDetails(productDetails, ProductDetailsField.WEBURL_BOUTIQUE_ID, ProductDetailsField.WEBURL_MERCHANT_ID);
	}
}
