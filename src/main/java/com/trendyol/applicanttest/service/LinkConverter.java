package com.trendyol.applicanttest.service;

import com.trendyol.applicanttest.enums.PageType;
import com.trendyol.applicanttest.exception.TrendyolLinkException;

public interface LinkConverter {

	String AMPERSAND = "&";
	String QUESTION_MARK = "?";
	String EQUALS = "=";

	String DEEPLINK_START = "ty://?Page=";
	String DEEPLINK_PRODUCT_PAGE = "Product";
	String DEEPLINK_SEARCH_PAGE = "Search";
	String DEEPLINK_HOME_PAGE = "Home";
	String DEEPLINK_CONTENT_ID = "ContentId=";
	String DEEPLINK_CAMPAIGN_ID = "CampaignId";
	String DEEPLINK_MERCHANT_ID = "MerchantId";
	String DEEPLINK_QUERY = "Query=";

	String WEBURL_START = "https://www.trendyol.com";
	String WEBURL_PRODUCT_PAGE = "-p-";
	String WEBURL_SEARCH_PAGE = "/sr";
	String WEBURL_SEARCH_QUERY = "?q=";
	String WEBURL_BOUTIQUE_ID = "boutiqueId";
	String WEBURL_MERCHANT_ID = "merchantId";

	String QUERY_MATCHER = "[a-zA-Z0-9%]+";
	String ID_MATCHER = "[0-9]+";

	/**
	 * Converts link from webURL to deeplink and vice versa using the rules that has been specified
	 * in the assignment.
	 *
	 * @param originalLink Link to be converted to its counterpart.
	 * @return Converted link from the original link.
	 */
	String convert(String originalLink);

	/**
	 * Extracts page type of the original link.
	 *
	 * @return Extracted page type from the original link.
	 */

	PageType extractPageType();

	/**
	 * Returns respective link's home page
	 *
	 * @return Respective link's home page
	 */
	String getConvertedHomePage();

	/**
	 * Returns product page conversion of the original link
	 * @param originalLink Link to build the product page of its counterpart
	 * @return Converted product page of the originalLink
	 * @throws TrendyolLinkException
	 */
	String getConvertedProductPage(String originalLink) throws TrendyolLinkException;

	/**
	 * Returns search page conversion of the original link
	 * @param originalLink Link to build the search page of its counterpart
	 * @return Converted search page of the originalLink
	 * @throws TrendyolLinkException
	 */
	String getConvertedSearchPage(String originalLink) throws TrendyolLinkException;
}