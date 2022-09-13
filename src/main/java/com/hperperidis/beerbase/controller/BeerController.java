package com.hperperidis.beerbase.controller;

import java.util.concurrent.CompletableFuture;

import com.hperperidis.beerbase.model.BatchRequestDTO;
import com.hperperidis.beerbase.model.BeerDTO;
import com.hperperidis.beerbase.model.BeerModel;
import com.hperperidis.beerbase.service.BeerService;
import com.hperperidis.beerbase.service.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller, exposing the necessary API endpoints to:
 *  - get a beer for a given ID
 *  - get ALL beers (no pagination is explicitly required)
 *  - search all beers for a given name
 *  - add a beer
 *  - get a random beer
 *  - post with a source URL to `beers/urlUpload` to ingest data from a REST API returning JSON. e.g. https://api.punkapi.com/v2/beers ;)
 *  - post with a source URL to `beers/fileUpload to download a records file an ingest json data.
 *  - post with a MultipartFile upload request to `beers/fileUpload` to download a multipart file and ingest json data.
 *
 *  Batch opperations work on a CreateOrUpdate mode. I.e. first we check to see if we have the record being uploaded, and if we do, we just
 *  do an update. If the record does not exist, for the specific Data Source segment, we add a new record.
 *
 *  All batch opperations in the BeerService are {@code @Transactional}.
 *  All batch operations return a list of the inserted or updated records.
 *
 * Makes use of and delegates to the {@code BeerService} for handling the relevant requests.
 *
 * Always returns data in a RestFull manner, wrapped in {@code ResponseEntity}, as well as
 * as {@code RepresentationModel}.
 *
 * @author C. Perperidis(ta6hbe@hotmail.com)
 */
@RestController
@RequestMapping(value = "/beers", produces = "application/hal+json")
public class BeerController {

    private final BeerService beerService;
    private final FileService fileService;

    @Autowired
    public BeerController(BeerService beerService, FileService fileService) {
        this.beerService = beerService;
        this.fileService = fileService;
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<CollectionModel<BeerModel>>> getAllBears() {
        return beerService.getAllBeers();
    }

    @GetMapping("/id/{id}")
    public CompletableFuture<ResponseEntity<BeerModel>> getBeerById(@PathVariable long id) {
        return beerService.getById(id);
    }

    @GetMapping("/name/{name}")
    public CompletableFuture<ResponseEntity<CollectionModel<BeerModel>>> getBeerByName(@PathVariable String name) {
        return beerService.getByName(name);
    }

    @GetMapping("/random")
    public CompletableFuture<ResponseEntity<BeerModel>> getBeerRandom() {
        return beerService.getRandomBeer();
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<BeerModel>> postBeer(@RequestBody final BeerDTO beerFromRequest) {
        return beerService.add(beerFromRequest);
    }

    @PostMapping("/urlupload")
    public CompletableFuture<ResponseEntity<CollectionModel<BeerModel>>> insertOrUpdateByUrl(
            @RequestBody final BatchRequestDTO batchRequestDTO) {
        return beerService.ingestDataFromURL(batchRequestDTO.getUrl());
    }

    @PostMapping(value = "/fileupload",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<ResponseEntity<CollectionModel<BeerModel>>> insertOrUpdateByFileURL(
            @RequestBody final BatchRequestDTO batchRequestDTO) {
        return fileService.getFile(batchRequestDTO.getUrl()).thenCompose(beerService::ingestDataFromFile);
    }

    @PostMapping(value = "/multipartUpload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompletableFuture<ResponseEntity<CollectionModel<BeerModel>>> insertOrUpdateByMultipartFile(
            @RequestParam("file") MultipartFile multipartFile) {
        return fileService.getMultiPartFile(multipartFile).thenCompose(beerService::ingestDataFromFile);
    }
}
