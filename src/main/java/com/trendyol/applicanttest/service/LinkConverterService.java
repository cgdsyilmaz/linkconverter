package com.trendyol.applicanttest.service;

import com.trendyol.applicanttest.dto.LinkDto;
import com.trendyol.applicanttest.entity.Link;
import com.trendyol.applicanttest.enums.LinkType;
import com.trendyol.applicanttest.enums.PageType;
import com.trendyol.applicanttest.repository.LinkConverterRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LinkConverterService {

	@NonNull
	private LinkConverterRepository linkConverterRepository;

	/**
	 * Converts WebURL to Deeplink.
	 *
	 * @param webURLDto webUrl to be converted to deeplink
	 * @return Deeplink which is converted from webUrl
	 */

	public LinkDto convertWebURLToDeeplink(LinkDto webURLDto) {
		LinkConverter webURLToDeeplinkConverter = new WebURLToDeeplinkConverter();
		String convertedLink = webURLToDeeplinkConverter.convert(webURLDto.getLink());

		saveToLinkTable(webURLDto.getLink(), convertedLink, LinkType.WebURL,
			webURLToDeeplinkConverter.extractPageType());

		return LinkDto
			.builder()
			.link(convertedLink)
			.build();
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