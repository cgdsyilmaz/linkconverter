package com.trendyol.applicanttest.converter;

import static com.trendyol.applicanttest.util.LinkConverterUtil.*;

import com.trendyol.applicanttest.exception.TrendyolLinkDuplicateProductDetailsException;
import com.trendyol.applicanttest.model.LinkType;
import com.trendyol.applicanttest.model.PageType;
import com.trendyol.applicanttest.exception.TrendyolLinkException;
import com.trendyol.applicanttest.model.ProductDetailsField;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

@Getter
public abstract class LinkConverter {

	protected PageType pageType;
	/**
	 * Converts link from webURL to deeplink and vice versa using the rules that has been specified
	 * in the assignment.
	 *
	 * @param originalLink Link to be converted to its counterpart.
	 * @return Converted link from the original link.
	 */
	public String convert(String originalLink, LinkType type) {
		boolean isOriginalLinkDeeplink = type == LinkType.Deeplink;

		StringBuilder convertedLinkBuilder = new StringBuilder().append(isOriginalLinkDeeplink ? WEBURL_START : DEEPLINK_START);

		String requestedLinkStart = isOriginalLinkDeeplink ? DEEPLINK_START : WEBURL_START;
		if (!originalLink.startsWith(requestedLinkStart)) {
			return convertedLinkBuilder.toString();
		}
		boolean containsProductPageClassifier = isOriginalLinkDeeplink ? originalLink.contains(DEEPLINK_PRODUCT_PAGE) : originalLink.contains(WEBURL_PRODUCT_PAGE);
		boolean containsSearchPageClassifier = isOriginalLinkDeeplink ? originalLink.contains(DEEPLINK_SEARCH_PAGE) : originalLink.contains(WEBURL_SEARCH_PAGE);

		if (containsProductPageClassifier && containsSearchPageClassifier) {
			throw new TrendyolLinkException("Both Product and Search Page classifiers can not be contained!");
		}

		if (containsProductPageClassifier) {
			convertedLinkBuilder.append(convertProductPage(originalLink));
		} else if (containsSearchPageClassifier){
			convertedLinkBuilder.append(convertSearchPage(originalLink));
		} else {
			return convertHomePage();
		}

		return convertedLinkBuilder.toString();
	}

	/**
	 * Extracts link type of the original link.
	 *
	 * @return Extracted link type from the original link.
	 */

	public abstract LinkType getLinkType();

	/**
	 * Returns respective link's home page
	 *
	 * @return Respective link's home page
	 */
	public abstract String convertHomePage();

	/**
	 * Returns product page conversion of the original link
	 * @param originalLink Link to build the product page of its counterpart
	 * @return Converted product page of the originalLink
	 */
	abstract String convertProductPage(String originalLink);

	/**
	 * Returns search page conversion of the original link
	 * @param originalLink Link to build the search page of its counterpart
	 * @return Converted search page of the originalLink
	 */
	abstract String convertSearchPage(String originalLink);

	/**
	 * Returns and accepts only one content campaign and merchant id.
	 *
	 * @param productDetails Includes content campaign and merchant types and id values
	 * @return WebURL equivalent of content campaign and merchant ids or id has non matching
	 * characters
	 */
	protected String getAvailableProductDetails(String productDetails, ProductDetailsField campaignFieldType, ProductDetailsField merchantFieldType) {
		String[] idsList = productDetails.split("&");

		List<Pair<String, String>> productDetailsPairs = Arrays.stream(idsList)
			.filter(id -> !id.isEmpty())
			.map(id -> {
				String[] idEntry = id.split(EQUALS);
				return Pair.of(idEntry[0], idEntry.length == 2 ? idEntry[1] : "");
			})
			.collect(Collectors.toList());

		StringBuilder detailsBuilder = new StringBuilder();

		boolean campaignAlreadyAppended = false;
		boolean merchantAlreadyAppended = false;

		for (Pair<String, String> pair : productDetailsPairs) {
			if (pair.getLeft().equals(campaignFieldType.getFieldName())) {
				if (campaignAlreadyAppended) {
					throw new TrendyolLinkDuplicateProductDetailsException(campaignFieldType.getFieldName());
				}
				campaignAlreadyAppended = appendIfPossible(detailsBuilder, pair, campaignFieldType.getCounterPart().getFieldName());
			} else if (pair.getLeft().equals(merchantFieldType.getFieldName())) {
				if (merchantAlreadyAppended) {
					throw new TrendyolLinkDuplicateProductDetailsException(merchantFieldType.getFieldName());
				}
				merchantAlreadyAppended = appendIfPossible(detailsBuilder, pair, merchantFieldType.getCounterPart().getFieldName());
			}
		}

		return StringUtils.chop(detailsBuilder.toString());
	}

	/**
	 * Return respective params counterpart in other link type
	 * @param idsBuilder Link builder
	 * @param productDetailsPair Id type and value pair
	 * @param counterPart its counterpart
	 * @return Always True if there is no exception
	 * to the result link or id is failed to meet the criteria
	 */
	protected boolean appendIfPossible(StringBuilder idsBuilder, Pair<String, String> productDetailsPair, String counterPart) {
		if (productDetailsPair.getRight().isEmpty()) {
			throw new TrendyolLinkException("Missing ID Value!");
		}
		if (!productDetailsPair.getRight().matches(ID_MATCHER)) {
			throw new TrendyolLinkException(String.format("%s has non matching characters!", productDetailsPair.getLeft()));
		}

		idsBuilder.append(counterPart)
			.append(EQUALS)
			.append(productDetailsPair.getRight())
			.append(AMPERSAND);

		return true;
	}
}