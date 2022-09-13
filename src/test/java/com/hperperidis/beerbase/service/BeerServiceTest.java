package com.hperperidis.beerbase.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import com.hperperidis.beerbase.data.Beer;
import com.hperperidis.beerbase.data.BeerJpaRepository;
import com.hperperidis.beerbase.model.BeerDTO;
import com.hperperidis.beerbase.model.BeerModel;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 *
 */
@Slf4j
@SpringBootTest
class BeerServiceTest {

    @Autowired
    BeerService beerService;

    @Autowired
    FileService fileService;

    @MockBean
    BeerJpaRepository beerJpaRepository;

    @Mock
    Page<Beer> mockBeersPage;

    @Mock
    Stream<Beer> mockBeerStream;

    @BeforeEach void setUp() {

    }

    @AfterEach void tearDown() {
    }

    @Test
    @DisplayName("Test getById Success")
    void getById() throws ExecutionException, InterruptedException {
        Beer beer = Beer.builder().name("Test Beer 1").description("Test Description 1").id(1L).build();
        when(beerJpaRepository.findById(1l)).thenReturn(Optional.of(beer));

        CompletableFuture<ResponseEntity<BeerModel>> response = beerService.getById(1L);

        Assertions.assertThat(response.isDone()).isTrue();
        ResponseEntity<BeerModel> responseEntity = response.get();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.hasBody()).isTrue();
        BeerDTO beerDTO = responseEntity.getBody().getBeerDTO();
        Assertions.assertThat(beerDTO).isNotNull();
        Assertions.assertThat(beerDTO.getId()).isEqualTo(beer.getId());
        Assertions.assertThat(beerDTO.getDescription()).isEqualTo(beer.getDescription());
        Assertions.assertThat(beerDTO.getName()).isEqualTo(beer.getName());

    }

    @Test void getRandomBeer() throws ExecutionException, InterruptedException {
        Beer beer = Beer.builder().name("Test Beer 1").description("Test Description 1").id(1L).build();
        when(beerJpaRepository.findAll(Pageable.ofSize(1))).thenReturn(mockBeersPage);
        when(mockBeersPage.getTotalElements()).thenReturn(1L);
        when(mockBeersPage.stream()).thenReturn(mockBeerStream);
        when(mockBeerStream.findFirst()).thenReturn(Optional.of(beer));

        CompletableFuture<ResponseEntity<BeerModel>> response = beerService.getRandomBeer();

        Assertions.assertThat(response.isDone()).isTrue();
        ResponseEntity<BeerModel> responseEntity = response.get();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.hasBody()).isTrue();
        BeerDTO beerDTO = responseEntity.getBody().getBeerDTO();
        Assertions.assertThat(beerDTO).isNotNull();
        Assertions.assertThat(beerDTO.getId()).isEqualTo(beer.getId());
        Assertions.assertThat(beerDTO.getDescription()).isEqualTo(beer.getDescription());
        Assertions.assertThat(beerDTO.getName()).isEqualTo(beer.getName());
    }

    @Test void getByName() throws ExecutionException, InterruptedException {
        Beer beer = Beer.builder().name("Test Beer 1").description("Test Description 1").id(1L).build();
        when(beerJpaRepository.findBeerByName(anyString())).thenReturn(Collections.singletonList(beer));


        CompletableFuture<ResponseEntity<CollectionModel<BeerModel>>> response = beerService.getByName("Test Beer 1");

        Assertions.assertThat(response.isDone()).isTrue();
        ResponseEntity<CollectionModel<BeerModel>> responseEntity = response.get();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(responseEntity.hasBody()).isTrue();


        Collection<BeerModel> beerModels = responseEntity.getBody().getContent();

        Assertions.assertThat(beerModels).isNotNull();
        Assertions.assertThat(beerModels.contains(beer.toDTO()));

    }

    // TODO: Complete unit tests here
//
//    @Test void getAllBeers() {
//    }
//
//    @Test void add() {
//    }
//
//    @Test void update() {
//    }
//
//    @Test void batchUpdateOrCreateByDataSource() {
//    }
//
//    @Test void createOrUpdateFromExternalSource() {
//    }
//
//    @Test void ingestDataFromURL() {
//    }
//
//    @Test void ingestDataFromFile() {
//    }
}
