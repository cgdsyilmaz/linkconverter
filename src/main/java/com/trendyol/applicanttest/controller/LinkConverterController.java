package com.trendyol.applicanttest.controller;

import com.trendyol.applicanttest.model.dto.LinkDto;
import com.trendyol.applicanttest.service.LinkConverterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class LinkConverterController {

    @Autowired
    private LinkConverterService linkConverterService;

    @PostMapping("/to_deeplink")
    public ResponseEntity<LinkDto> toDeepLink(@RequestBody LinkDto linkDto) {
        LinkDto convertedLink = linkConverterService.convertWebURLToDeeplink(linkDto);

        return new ResponseEntity<>(convertedLink, HttpStatus.OK);
    }

    @PostMapping("/to_web_url")
    public ResponseEntity<LinkDto> toWebUrl(@RequestBody LinkDto linkDto) {
        LinkDto convertedLink = linkConverterService.convertDeeplinktoWebURL(linkDto);

        return new ResponseEntity<>(convertedLink, HttpStatus.OK);
    }
}
