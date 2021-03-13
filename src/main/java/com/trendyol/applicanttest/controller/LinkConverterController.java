package com.trendyol.applicanttest.controller;

import com.trendyol.applicanttest.service.LinkConverterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LinkConverterController {

    @Autowired
    private LinkConverterService linkConverterService;
}
