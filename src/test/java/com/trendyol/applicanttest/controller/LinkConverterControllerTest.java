package com.trendyol.applicanttest.controller;

import com.trendyol.applicanttest.service.LinkConverterService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class LinkConverterControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private LinkConverterService linkConverterService;
}
