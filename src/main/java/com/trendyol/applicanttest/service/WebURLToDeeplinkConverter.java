package com.trendyol.applicanttest.service;

import com.trendyol.applicanttest.enums.PageType;
import com.trendyol.applicanttest.exception.TrendyolLinkException;
import java.util.Arrays;
import java.util.List;

public class WebURLToDeeplinkConverter implements LinkConverter {

	private PageType pageType;

	/**
	 * Using the rules that has been given in the assignment, deeplink is
	 * built using the WebURL.
	 *
	 * @param originalLink WebURL to be converted to deeplink.
	 * @return Deeplink converted from WebURL using the rules
	 * that has been specified.
	 */
	@Override
	public String convert(String originalLink) {
		try {
			StringBuilder convertedLinkBuilder = new StringBuilder();
			convertedLinkBuilder.append(DEEPLINK_START);

			if (originalLink.startsWith(WEBURL_START)) {
				boolean containsProductPageClassifier = originalLink.contains(WEBURL_PRODUCT_PAGE);
				boolean containsSearchPageClassifier = originalLink.contains(WEBURL_SEARCH_PAGE);

				if (containsProductPageClassifier && containsSearchPageClassifier) {
					// Contains both classifiers hence will return the home page.
					throw new TrendyolLinkException("Both Product and Search Page classifiers can not be contained!");
				} else if (containsProductPageClassifier) {
					// Contains product classifier hence will return the product page.
					convertedLinkBuilder.append(getConvertedProductPage(originalLink));
				} else if (containsSearchPageClassifier){
					// Contains search classifier hence may return search page or the home page
					convertedLinkBuilder.append(getConvertedSearchPage(originalLink));
				} else {
					// Contains neither product page nor search page classifiers hence will return the home page
					return getConvertedHomePage();
				}
			}
			return convertedLinkBuilder.toString();
		} catch (TrendyolLinkException e) {
			e.printStackTrace();
			return getConvertedHomePage();
		}
	}

	/**
	 * Returns the extracted PageType in convert method
	 *
	 * @return Extracted PageType from convert method
	 */
	@Override
	public PageType extractPageType() {
		return pageType;
	}

	/**
	 * Returns the deeplink home page
	 *
	 * @return Deeplink Home Page
	 */
	@Override
	public String getConvertedHomePage() {
		pageType = PageType.OtherPage;

		return DEEPLINK_START + DEEPLINK_HOME_PAGE;
	}

	/**
	 * Returns the deeplink product page
	 *
	 * @param originalLink WebURL to build a deeplink product page
	 * @return Deeplink Product Page
	 * @throws TrendyolLinkException
	 */
	@Override
	public String getConvertedProductPage(String originalLink) throws TrendyolLinkException {
		int classifierIndex = originalLink.indexOf(WEBURL_PRODUCT_PAGE);
		if (classifierIndex != originalLink.length() - 3) {
			String product = originalLink.substring(classifierIndex + 3);

			StringBuilder productPage = new StringBuilder();
			productPage.append(DEEPLINK_PRODUCT_PAGE);
			productPage.append(AMPERSAND);
			productPage.append(DEEPLINK_CONTENT_ID);

			if (product.contains(QUESTION_MARK)) {
				String productId = product.substring(0, product.indexOf(QUESTION_MARK));
				if (productId.matches(ID_MATCHER)) {
					product = product.substring(product.indexOf(QUESTION_MARK) + 1);
					List<String> idsList = Arrays.asList(product.split("&"));
					// Content Id to convert
					productPage.append(productId);
					productPage.append(getBoutiqueAndMerchantID(Arrays.asList(product.split("&"))));
				} else {
					throw new TrendyolLinkException("Content ID has non matching characters!");
				}
			} else {
				productPage.append(product);
			}

			pageType = PageType.ProductDetailPage;
			return productPage.toString();
		} else {
			throw new TrendyolLinkException("Web URL does not have content ID!");
		}
	}

	/**
	 * Returns the deeplink search page
	 *
	 * @param originalLink WebURL to build a deeplink search page
	 * @return Deeplink Search Page
	 * @throws TrendyolLinkException
	 */
	@Override
	public String getConvertedSearchPage(String originalLink) throws TrendyolLinkException {
		if (originalLink.contains(WEBURL_SEARCH_QUERY)) {
			// Contains query string
			if(originalLink.indexOf(WEBURL_SEARCH_QUERY) != originalLink.length() - 3) {
				// Contains non empty query hence will return the search page
				String query = originalLink.substring(originalLink.indexOf(WEBURL_SEARCH_QUERY) + 3);
				if (query.matches(QUERY_MATCHER)) {
					StringBuilder searchPageAndQuery = new StringBuilder();
					searchPageAndQuery.append(DEEPLINK_SEARCH_PAGE);
					searchPageAndQuery.append(AMPERSAND);
					searchPageAndQuery.append(DEEPLINK_QUERY);
					searchPageAndQuery.append(query);

					pageType = PageType.SearchPage;
					return searchPageAndQuery.toString();
				} else {
					throw new TrendyolLinkException("Query string does not match with the rules!");
				}
			} else {
				throw new TrendyolLinkException("Empty query string!");
			}
		} else {
			throw new TrendyolLinkException("Query parameter does not exist!");
		}
	}

	/**
	 *
	 * @param idsList
	 * @return
	 * @throws TrendyolLinkException
	 */
	private String getBoutiqueAndMerchantID(List<String> idsList) throws TrendyolLinkException {
		StringBuilder idsBuilder = new StringBuilder();
		boolean boutiqueAppended = false;
		boolean merchantAppended = false;

		for (String id : idsList) {
			String[] idTypeAndValue = id.split(EQUALS);
			if (idTypeAndValue[0].contains(WEBURL_BOUTIQUE_ID)) {
				boutiqueAppended = appendIfPossible(idsBuilder, boutiqueAppended, idTypeAndValue,
					DEEPLINK_CAMPAIGN_ID,
					"Only one boutique id should be in a Web URL",
					"Boutique ID has non matching characters!");
			} else if (idTypeAndValue[0].contains(WEBURL_MERCHANT_ID)) {
				merchantAppended = appendIfPossible(idsBuilder, merchantAppended, idTypeAndValue,
					DEEPLINK_MERCHANT_ID, "Only one merchant id should be in a Web URL",
					"Merchant ID has non matching characters!");
			}
		}
		return idsBuilder.toString();
	}

	private boolean appendIfPossible(StringBuilder idsBuilder, boolean hasAppended,
		String[] idTypeAndValue, String deeplinkCounterPart, String alreadyAppendedMessage,
		String failedMatchMessage) throws TrendyolLinkException {
		if (hasAppended) {
			throw new TrendyolLinkException(alreadyAppendedMessage);
		}
		if (idTypeAndValue[1].matches(ID_MATCHER)) {
			idsBuilder.append(AMPERSAND);
			idsBuilder.append(deeplinkCounterPart);
			idsBuilder.append(EQUALS);
			idsBuilder.append(idTypeAndValue[1]);
			hasAppended = true;
		} else {
			throw new TrendyolLinkException(failedMatchMessage);
		}
		return hasAppended;
	}
}
