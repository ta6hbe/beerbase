package com.hperperidis.beerbase.controller;

import com.hperperidis.beerbase.model.BeerDTO;
import com.hperperidis.beerbase.model.BeerModel;
import com.hperperidis.beerbase.service.BeerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller, exposing the necessary API endpoints to:
 *  - get a beer for a given ID
 *  - get ALL beers (no pagination is explicitly required)
 *  - search all beers for a given name
 *  - add a beer
 *  - get a random beer
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

    private BeerService beerService;

    @Autowired
    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<BeerModel>> getAllBears() {
        return beerService.getAllBeers();
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<BeerModel> getBeerById(@PathVariable long id) {
        return beerService.getById(id);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CollectionModel<BeerModel>> getBeerByName(@PathVariable String name) {
        return beerService.getByName(name);
    }

    @GetMapping("/random")
    public ResponseEntity<BeerModel> getBeerRandom() {
        return beerService.getRandomBeer();
    }

    @PostMapping
    public ResponseEntity<BeerModel> postBeer(@RequestBody final BeerDTO beerFromRequest) {
        return beerService.add(beerFromRequest);
    }
}
