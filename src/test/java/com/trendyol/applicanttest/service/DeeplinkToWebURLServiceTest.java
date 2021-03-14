package com.trendyol.applicanttest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.trendyol.applicanttest.model.dto.LinkDto;
import com.trendyol.applicanttest.model.entity.Link;
import com.trendyol.applicanttest.model.LinkType;
import com.trendyol.applicanttest.model.PageType;
import com.trendyol.applicanttest.repository.LinkConverterRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DeeplinkToWebURLServiceTest {

	@Autowired
	private LinkConverterRepository linkConverterRepository;

	/**
	 * Checks if database writes work correctly or not
	 */
	@Test
	void checkIfSavedCorrectly() {
		List<Link> links = new ArrayList<>();
		links.add(Link.builder()
			.originalLink("ty://?Page=Product&ContentId=1925865&CampaignId=439892&MerchantId=105064")
			.convertedLink("https://www.trendyol.com/brand/name-p-1925865?boutiqueId=439892&merchantId=105064")
			.linkType(LinkType.Deeplink)
			.pageType(PageType.ProductDetailPage)
			.build());

		links.add(Link.builder()
			.originalLink(
				"ty://?Page=Product&ContentId=1925865")
			.convertedLink(
				"https://www.trendyol.com/brand/name-p-1925865")
			.linkType(LinkType.Deeplink)
			.pageType(PageType.ProductDetailPage)
			.build());

		links.add(Link.builder()
			.originalLink(
				"ty://?Page=Product&ContentId=1925865&CampaignId=439892")
			.convertedLink("https://www.trendyol.com/brand/name-p-1925865?boutiqueId=439892")
			.linkType(LinkType.Deeplink)
			.pageType(PageType.ProductDetailPage)
			.build());

		links.add(Link.builder()
			.originalLink(
				"ty://?Page=Product&ContentId=1925865&MerchantId=105064")
			.convertedLink("https://www.trendyol.com/brand/name-p-1925865?merchantId=105064")
			.linkType(LinkType.Deeplink)
			.pageType(PageType.ProductDetailPage)
			.build());

		links.add(Link.builder()
			.originalLink("ty://?Page=Search&Query=elbise")
			.convertedLink("https://www.trendyol.com/sr?q=elbise")
			.linkType(LinkType.Deeplink)
			.pageType(PageType.SearchPage)
			.build());

		links.add(Link.builder()
			.originalLink("ty://?Page=Search&Query=%C3%BCt%C3%BC")
			.convertedLink("https://www.trendyol.com/sr?q=%C3%BCt%C3%BC")
			.linkType(LinkType.Deeplink)
			.pageType(PageType.SearchPage)
			.build());

		links.add(Link.builder()
			.originalLink("ty://?Page=Favorites")
			.convertedLink("https://www.trendyol.com")
			.linkType(LinkType.Deeplink)
			.pageType(PageType.OtherPage)
			.build());

		links.add(Link.builder()
			.originalLink("ty://?Page=Orders")
			.convertedLink("https://www.trendyol.com")
			.linkType(LinkType.Deeplink)
			.pageType(PageType.OtherPage)
			.build());

		linkConverterRepository.saveAll(links);
		List<Link> linkList = linkConverterRepository.findAll();

		linkList.forEach(link -> {
			assertEquals(link.getOriginalLink(), link.getOriginalLink());
			assertEquals(link.getConvertedLink(), link.getConvertedLink());
			assertEquals(link.getLinkType(), link.getLinkType());
			assertEquals(link.getPageType(), link.getPageType());
		});
		assertEquals(linkConverterRepository.count(), links.size());
	}

	/**
	 * Includes "Product" text with content Id Should be classified as Product Detail Page.
	 */
	@Test
	void checkForProductDetailsPageWithOnlyContentId() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(
				buildLinkDto("ty://?Page=Product&ContentId=1925865"))
			.getLink();

		assertEquals("https://www.trendyol.com/brand/name-p-1925865", convertedLink);
	}

	/**
	 * Includes "Product" text but no content Id Should be classified as Other page.
	 */
	@Test
	void checkForProductDetailsPageWithoutContentId() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(
				buildLinkDto("ty://?Page=Product"))
			.getLink();

		assertEquals("https://www.trendyol.com", convertedLink);
	}

	/**
	 * Includes "Product" text, content Id and campaign Id Should be classified as Product Detail Page.
	 */
	@Test
	void checkForProductsDetailsPageWithCampaignId() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(buildLinkDto(
				"ty://?Page=Product&ContentId=1925865&CampaignId=439892"))
			.getLink();

		assertEquals("https://www.trendyol.com/brand/name-p-1925865?boutiqueId=439892", convertedLink);
	}


	/**
	 * Includes "Product" text, content Id and merchant Id Should be classified as Product Detail Page.
	 */
	@Test
	void checkForProductsDetailsPageWithMerchantId() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(buildLinkDto(
				"ty://?Page=Product&ContentId=1925865&MerchantId=105064"))
			.getLink();

		assertEquals("https://www.trendyol.com/brand/name-p-1925865?merchantId=105064", convertedLink);
	}

	/**
	 * Includes "Product" text, content Id, campaign Id and merchant Id Should be classified as Product
	 * Detail Page.
	 */
	@Test
	void checkForProductsDetailsPageWithCampaignAndMerchantIds() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(buildLinkDto(
				"ty://?Page=Product&ContentId=1925865&CampaignId=439892&MerchantId=105064"))
			.getLink();

		assertEquals("https://www.trendyol.com/brand/name-p-1925865?boutiqueId=439892&merchantId=105064",
			convertedLink);
	}

	/**
	 * Includes "Product" text, content Id, campaign Id and merchant Id values are empty Should be classified as Product
	 * Detail Page.
	 */
	@Test
	void checkForProductsDetailsPageWithEmptyContentCampaignAndMerchantIds() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(buildLinkDto(
				"ty://?Page=Product&ContentId=&CampaignId=&MerchantId="))
			.getLink();

		assertEquals("https://www.trendyol.com",
			convertedLink);
	}

	/**
	 * Includes "Product" text, multiple content Id, campaign Id and merchant Id Should be classified as Other Page
	 */
	@Test
	void checkForProductsDetailsPageWithMultipleContentCampaignAndMerchantIds() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(buildLinkDto(
				"ty://?Page=Product&ContentId=1925864&ContentId=1925865&CampaignId=439892&MerchantId=105064"))
			.getLink();

		assertEquals("https://www.trendyol.com",
			convertedLink);
	}

	/**
	 * Includes "Product" text, content Id, multiple campaign Id and merchant Id Should be classified as Other Page
	 */
	@Test
	void checkForProductsDetailsPageWithMultipleCampaignAndMerchantIds() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(buildLinkDto(
				"ty://?Page=Product&ContentId=1925865&CampaignId=439891&CampaignId=439892&MerchantId=105064"))
			.getLink();

		assertEquals("https://www.trendyol.com",
			convertedLink);
	}

	/**
	 * Includes "Product" text, content Id,  campaign Id and multiple merchant Id Should be classified as Other Page
	 */
	@Test
	void checkForProductsDetailsPageWithCampaignAndMultipleMerchantIds() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(buildLinkDto(
				"ty://?Page=Product&ContentId=1925865&CampaignId=439891&MerchantId=105063&MerchantId=105064"))
			.getLink();

		assertEquals("https://www.trendyol.com",
			convertedLink);
	}

	/**
	 * Includes "Search" text and only non turkish characters in query
	 */
	@Test
	void checkForSearchPageWithNonTurkishCharacterQuery() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(buildLinkDto(
				"ty://?Page=Search&Query=elbise"))
			.getLink();

		assertEquals("https://www.trendyol.com/sr?q=elbise",
			convertedLink);
	}

	/**
	 * Includes "Search" text and converted turkish characters in query
	 */
	@Test
	void checkForSearchPageWithConvertedTurkishCharacterQuery() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(buildLinkDto(
				"ty://?Page=Search&Query=%C3%BCt%C3%BC"))
			.getLink();

		assertEquals("https://www.trendyol.com/sr?q=%C3%BCt%C3%BC",
			convertedLink);
	}

	/**
	 * Includes "Search" text and turkish characters in query
	 */
	@Test
	void checkForSearchPageWithTurkishCharacterQuery() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(buildLinkDto(
				"ty://?Page=Search&Query=çağdaş"))
			.getLink();

		assertEquals("https://www.trendyol.com",
			convertedLink);
	}

	/**
	 * Includes "Search" text with no query
	 */
	@Test
	void checkForSearchPageWithoutQuery() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(buildLinkDto(
				"ty://?Page=Search"))
			.getLink();

		assertEquals("https://www.trendyol.com",
			convertedLink);
	}

	/**
	 * Includes "Search" text with empty query
	 */
	@Test
	void checkForSearchPageWithEmptyQuery() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(buildLinkDto(
				"ty://?Page=Search&Query="))
			.getLink();

		assertEquals("https://www.trendyol.com",
			convertedLink);
	}

	/**
	 * Includes "Search", "Query" and "Product" text
	 * Should be an Other Page
	 */
	@Test
	void checkForProductAndSearchPage() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(buildLinkDto(
				"ty://?Page=Search&Query=elbise&Page=Product"))
			.getLink();

		assertEquals("https://www.trendyol.com",
			convertedLink);
	}

	/**
	 * Other page hesabim/favoriler
	 */
	@Test
	void checkForAccountFavorites() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(buildLinkDto(
				"ty://?Page=Favorites"))
			.getLink();

		assertEquals("https://www.trendyol.com",
			convertedLink);
	}

	/**
	 * Other page hesabim/siparislerim
	 */
	@Test
	void checkForAccountOrders() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertDeeplinktoWebURL(buildLinkDto(
				"ty://?Page=Orders"))
			.getLink();

		assertEquals("https://www.trendyol.com",
			convertedLink);
	}

	@AfterEach
	void tearDown() {
		linkConverterRepository.deleteAll();
	}

	private LinkDto buildLinkDto(String originalLink) {
		return LinkDto.builder()
			.link(originalLink)
			.build();
	}
}
