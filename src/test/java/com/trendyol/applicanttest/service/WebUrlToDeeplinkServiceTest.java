package com.trendyol.applicanttest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.trendyol.applicanttest.dto.LinkDto;
import com.trendyol.applicanttest.entity.Link;
import com.trendyol.applicanttest.enums.LinkType;
import com.trendyol.applicanttest.enums.PageType;
import com.trendyol.applicanttest.repository.LinkConverterRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebUrlToDeeplinkServiceTest {

	@Autowired
	private LinkConverterRepository linkConverterRepository;

	/**
	 * Includes "-p-" text and content Id Should be classified as Product Detail Page.
	 */
	@Test
	void checkIfSavedCorrectly() {
		List<Link> links = new ArrayList<>();
		links.add(Link.builder()
			.originalLink("https://www.trendyol.com/casio/erkek-kol-saati-p-1925865")
			.convertedLink("ty://?Page=Product&ContentId=1925865")
			.linkType(LinkType.WebURL)
			.pageType(PageType.ProductDetailPage)
			.build());

		links.add(Link.builder()
			.originalLink(
				"https://www.trendyol.com/casio/saat-p-1925865?boutiqueId=439892&merchantId=105064")
			.convertedLink(
				"ty://?Page=Product&ContentId=1925865&CampaignId=439892&MerchantId=105064")
			.linkType(LinkType.WebURL)
			.pageType(PageType.ProductDetailPage)
			.build());

		links.add(Link.builder()
			.originalLink(
				"https://www.trendyol.com/casio/erkek-kol-saati-p-1925865?boutiqueId=439892")
			.convertedLink("ty://?Page=Product&ContentId=1925865&CampaignId=439892")
			.linkType(LinkType.WebURL)
			.pageType(PageType.ProductDetailPage)
			.build());

		links.add(Link.builder()
			.originalLink(
				"https://www.trendyol.com/casio/erkek-kol-saati-p-1925865?merchantId=105064")
			.convertedLink("ty://?Page=Product&ContentId=1925865&MerchantId=105064")
			.linkType(LinkType.WebURL)
			.pageType(PageType.ProductDetailPage)
			.build());

		links.add(Link.builder()
			.originalLink("https://www.trendyol.com/sr?q=elbise")
			.convertedLink("ty://?Page=Search&Query=elbise")
			.linkType(LinkType.WebURL)
			.pageType(PageType.SearchPage)
			.build());

		links.add(Link.builder()
			.originalLink("https://www.trendyol.com/sr?q=%C3%BCt%C3%BC")
			.convertedLink("ty://?Page=Search&Query=%C3%BCt%C3%BC")
			.linkType(LinkType.WebURL)
			.pageType(PageType.SearchPage)
			.build());

		links.add(Link.builder()
			.originalLink("https://www.trendyol.com/Hesabim/Favoriler")
			.convertedLink("ty://?Page=Home")
			.linkType(LinkType.WebURL)
			.pageType(PageType.OtherPage)
			.build());

		links.add(Link.builder()
			.originalLink("https://www.trendyol.com/Hesabim/#/Siparislerim")
			.convertedLink("ty://?Page=Home")
			.linkType(LinkType.WebURL)
			.pageType(PageType.OtherPage)
			.build());

		linkConverterRepository.saveAll(links);
		List<Link> linkList = linkConverterRepository.findAll();

		for (int i = 0; i < linkList.size(); i++) {
			assertEquals(links.get(i).getOriginalLink(), linkList.get(i).getOriginalLink());
			assertEquals(links.get(i).getConvertedLink(), linkList.get(i).getConvertedLink());
			assertEquals(links.get(i).getLinkType(), linkList.get(i).getLinkType());
			assertEquals(links.get(i).getPageType(), linkList.get(i).getPageType());
		}
		assertEquals(linkConverterRepository.count(), links.size());
	}

	/**
	 * Includes "-p-" text with content Id Should be classified as Product Detail Page.
	 */
	@Test
	void checkForProductDetailsPageWithOnlyContentId() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertWebURLToDeeplink(
				buildLinkDto("https://www.trendyol.com/casio/erkek-kol-saati-p-1925865"))
			.getLink();

		assertEquals(convertedLink, "ty://?Page=Product&ContentId=1925865");
	}

	/**
	 * Includes "-p-" text but no content Id Should be classified as Other page.
	 */
	@Test
	void checkForProductDetailsPageWithoutContentId() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertWebURLToDeeplink(
				buildLinkDto("https://www.trendyol.com/casio/erkek-kol-saati-p-"))
			.getLink();

		assertEquals(convertedLink, "ty://?Page=Home");
	}

	/**
	 * Includes "-p-" text, content Id and boutique Id Should be classified as Product Detail Page.
	 */
	@Test
	void checkForProductsDetailsPageWithBoutiqueId() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertWebURLToDeeplink(buildLinkDto(
				"https://www.trendyol.com/casio/erkek-kol-saati-p-1925865?boutiqueId=439892"))
			.getLink();

		assertEquals(convertedLink, "ty://?Page=Product&ContentId=1925865&CampaignId=439892");
	}

	/**
	 * Includes "-p-" text, content Id and merchant Id Should be classified as Product Detail Page.
	 */
	@Test
	void checkForProductsDetailsPageWithMerchantId() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertWebURLToDeeplink(buildLinkDto(
				"https://www.trendyol.com/casio/erkek-kol-saati-p-1925865?merchantId=105064"))
			.getLink();

		assertEquals(convertedLink, "ty://?Page=Product&ContentId=1925865&MerchantId=105064");
	}

	/**
	 * Includes "-p-" text, content Id, boutique Id and merchant Id Should be classified as Product
	 * Detail Page.
	 */
	@Test
	void checkForProductsDetailsPageWithBoutiqueAndMerchantIds() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertWebURLToDeeplink(buildLinkDto(
				"https://www.trendyol.com/casio/saat-p-1925865?boutiqueId=439892&merchantId=105064"))
			.getLink();

		assertEquals(convertedLink,
			"ty://?Page=Product&ContentId=1925865&CampaignId=439892&MerchantId=105064");
	}

	/**
	 * Includes "-p-" text, content Id, multiple boutique Id and merchant Id Should be classified as Other Page
	 */
	@Test
	void checkForProductsDetailsPageWithMultipleBoutiqueAndMerchantIds() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertWebURLToDeeplink(buildLinkDto(
				"https://www.trendyol.com/casio/saat-p-1925865?boutiqueId=439892&boutiqueId=439891&merchantId=105064"))
			.getLink();

		assertEquals(convertedLink,
			"ty://?Page=Home");
	}

	/**
	 * Includes "-p-" text, content Id,  boutique Id and multiple merchant Id Should be classified as Other Page
	 */
	@Test
	void checkForProductsDetailsPageWithBoutiqueAndMultipleMerchantIds() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertWebURLToDeeplink(buildLinkDto(
				"https://www.trendyol.com/casio/saat-p-1925865?boutiqueId=439892&merchantId=105063&merchantId=105064"))
			.getLink();

		assertEquals(convertedLink,
			"ty://?Page=Home");
	}

	/**
	 * Includes "/sr" text and only non turkish characters in query
	 */
	@Test
	void checkForSearchPageWithNonTurkishCharacterQuery() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertWebURLToDeeplink(buildLinkDto(
				"https://www.trendyol.com/sr?q=elbise"))
			.getLink();

		assertEquals(convertedLink,
			"ty://?Page=Search&Query=elbise");
	}

	/**
	 * Includes "/sr" text and converted turkish characters in query
	 */
	@Test
	void checkForSearchPageWithConvertedTurkishCharacterQuery() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertWebURLToDeeplink(buildLinkDto(
				"https://www.trendyol.com/sr?q=%C3%BCt%C3%BC"))
			.getLink();

		assertEquals(convertedLink,
			"ty://?Page=Search&Query=%C3%BCt%C3%BC");
	}

	/**
	 * Includes "/sr" text and turkish characters in query
	 */
	@Test
	void checkForSearchPageWithTurkishCharacterQuery() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertWebURLToDeeplink(buildLinkDto(
				"https://www.trendyol.com/sr?q=çağdaş"))
			.getLink();

		assertEquals(convertedLink,
			"ty://?Page=Home");
	}

	/**
	 * Includes "/sr" text with no query
	 */
	@Test
	void checkForSearchPageWithoutQuery() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertWebURLToDeeplink(buildLinkDto(
				"https://www.trendyol.com/sr"))
			.getLink();

		assertEquals(convertedLink,
			"ty://?Page=Home");
	}

	/**
	 * Includes "/sr" text with empty query
	 */
	@Test
	void checkForSearchPageWithEmptyQuery() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertWebURLToDeeplink(buildLinkDto(
				"https://www.trendyol.com/sr?q="))
			.getLink();

		assertEquals(convertedLink,
			"ty://?Page=Home");
	}

	/**
	 * Includes "/sr", "q=" and "-p" text
	 * Should be an Other Page
	 */
	@Test
	void checkForProductAndSearchPage() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertWebURLToDeeplink(buildLinkDto(
				"https://www.trendyol.com/casio/sr?q=erkek-kol-saati-p-1925865"))
			.getLink();

		assertEquals(convertedLink,
			"ty://?Page=Home");
	}

	/**
	 * Other page hesabim/favoriler
	 */
	@Test
	void checkForAccountFavorites() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertWebURLToDeeplink(buildLinkDto(
				"https://www.trendyol.com/Hesabim/Favoriler"))
			.getLink();

		assertEquals(convertedLink,
			"ty://?Page=Home");
	}

	/**
	 * Other page hesabim/siparislerim
	 */
	@Test
	void checkForAccountOrders() {
		LinkConverterService linkConverterService = new LinkConverterService(linkConverterRepository);

		String convertedLink = linkConverterService
			.convertWebURLToDeeplink(buildLinkDto(
				"https://www.trendyol.com/Hesabim/#/Siparislerim"))
			.getLink();

		assertEquals(convertedLink,
			"ty://?Page=Home");
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
