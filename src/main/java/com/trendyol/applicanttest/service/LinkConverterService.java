package com.trendyol.applicanttest.service;

import com.trendyol.applicanttest.converter.DeeplinkToWebURLConverter;
import com.trendyol.applicanttest.converter.LinkConverter;
import com.trendyol.applicanttest.converter.WebURLToDeeplinkConverter;
import com.trendyol.applicanttest.exception.TrendyolLinkException;
import com.trendyol.applicanttest.model.dto.LinkDto;
import com.trendyol.applicanttest.model.entity.Link;
import com.trendyol.applicanttest.model.LinkType;
import com.trendyol.applicanttest.model.PageType;
import com.trendyol.applicanttest.repository.LinkConverterRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class LinkConverterService {

	@NonNull
	private final LinkConverterRepository linkConverterRepository;

	/**
	 * Converts WebURL to Deeplink.
	 *
	 * @param webURLDto webUrl to be converted to deeplink
	 * @return Deeplink which is converted from webUrl
	 */

	public LinkDto convertWebURLToDeeplink(LinkDto webURLDto) {
		return convertLink(webURLDto, new WebURLToDeeplinkConverter());
	}

	/**
	 * Converts Deeplink to WebURL.
	 *
	 * @param deeplinkDto Deeplink to be converted to WebURL
	 * @return WebURL which is converted from deeplink
	 */

	public LinkDto convertDeeplinktoWebURL(LinkDto deeplinkDto) {
		return convertLink(deeplinkDto, new DeeplinkToWebURLConverter());
	}

	/**
	 * Converts requested link to its counterpart.
	 *
	 * @param linkDto requested link to be converted to its counterpart
	 * @param linkConverter converter to be used
	 * @return Response link which is converted from given link
	 */
	private LinkDto convertLink(LinkDto linkDto, LinkConverter linkConverter) {
		try {
			String convertedLink = linkConverter.convert(linkDto.getLink(), linkConverter.getLinkType());

			saveToLinkTable(linkDto.getLink(), convertedLink, linkConverter.getLinkType(), linkConverter.getPageType());

			return LinkDto
				.builder()
				.link(convertedLink)
				.build();

		} catch (TrendyolLinkException e) {
			log.warn(String.format("Failed to convert link: %s", linkDto.getLink()), e);
			return LinkDto
				.builder()
				.link(linkConverter.convertHomePage())
				.build();
		}
	}

	/**
	 * Saves the link object to the database
	 *
	 * @param originalLink original link to be saved to the database
	 * @param convertedLink converted link to be saved to the database
	 * @param linkType original link's type
	 * @param pageType original link's page type
	 */
	private void saveToLinkTable(String originalLink, String convertedLink, LinkType linkType,
		PageType pageType) {

		Link link = Link.builder()
			.originalLink(originalLink)
			.convertedLink(convertedLink)
			.linkType(linkType)
			.pageType(pageType)
			.build();

		linkConverterRepository.save(link);
	}
}