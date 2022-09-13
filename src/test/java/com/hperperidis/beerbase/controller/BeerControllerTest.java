package com.hperperidis.beerbase.controller;

import java.util.concurrent.CompletableFuture;

import com.hperperidis.beerbase.model.BeerDTO;
import com.hperperidis.beerbase.model.BeerModel;
import com.hperperidis.beerbase.service.BeerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BeerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    BeerService beerService;

    BeerDTO result;

    @BeforeEach void setUp() {
        result = BeerDTO.builder().id(1L).name("Test Name 1").description("Test Description 1").build();

    }

    @AfterEach void tearDown() {
    }

    @Test void getBeerById() throws Exception {

        MockHttpServletRequestBuilder request = get("/beers/id/1")
                .accept("application/hal+json");

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.beerDTO.identifier").exists())
                .andExpect(jsonPath("$.beerDTO.identifier").value("1"))
                .andExpect(jsonPath("$.beerDTO.name").exists())
                .andExpect(jsonPath("$.beerDTO.name").value("Harry's home made lagger"))
                .andExpect(jsonPath("$.beerDTO.description").exists())
                .andExpect(jsonPath("$.beerDTO.description").value("Home brewed and distilled to perfection, best served ice cold in a frosen pint. 4.5% Alcohol."));

    }

    @Test void getBeerByName() throws Exception {
        MockHttpServletRequestBuilder request = get("/beers/name/House pale IPA")
                .accept("application/hal+json");

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$._embedded.beerModels[0].beerDTO.identifier").exists())
                .andExpect(jsonPath("$._embedded.beerModels[0].beerDTO.identifier").value("2"))
                .andExpect(jsonPath("$._embedded.beerModels[0].beerDTO.name").exists())
                .andExpect(jsonPath("$._embedded.beerModels[0].beerDTO.name").value("House pale IPA"))
                .andExpect(jsonPath("$._embedded.beerModels[0].beerDTO.description").exists())
                .andExpect(jsonPath("$._embedded.beerModels[0].beerDTO.description").value("A pale IPA you cannot miss., 4.5% Alcohol."));

    }

    //TODO: Add more tests for the remaining endpoints here.
}
