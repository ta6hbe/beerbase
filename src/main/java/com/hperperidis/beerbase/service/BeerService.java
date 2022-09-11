package com.hperperidis.beerbase.service;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.hperperidis.beerbase.data.Beer;
import com.hperperidis.beerbase.data.BeerRepository;
import com.hperperidis.beerbase.model.BeerDTO;
import com.hperperidis.beerbase.model.BeerModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Service class implementing all necessary actions to collect data from the
 * repository sources and return id to the REST controller endpoints.
 *
 * This reads in either DTO object instances, simple Strings or long ids to
 * query the different resources for data or to add records to the local API
 * DB. Currently the DB implementation for this is based on an In Memory H2 DB
 * instance.
 *
 * @author C Perperidis(ta6hbe@hotmail.com)
 *
 */

@Service
public class BeerService {

    private BeerRepository beerRepository;

    @Autowired
    public BeerService(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    public ResponseEntity<BeerModel> getById(long id) {
        return beerRepository.findById(id)
                .map(Beer::toDTO)
                .map(BeerModel::new)
                .map( beer -> ResponseEntity.ok().body(beer) )
                .orElseGet( () -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<BeerModel> getRandomBeer() {
        long qty = beerRepository.count();
        return beerRepository.findAll(PageRequest.of(
                (int) Math.floor(Math.random()*(qty)), 1))
                .stream().findFirst()
                .map( Beer::toDTO)
                .map( BeerModel::new )
                .map( beerModel -> ResponseEntity.ok().body(beerModel) )
                .orElseGet( () -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<CollectionModel<BeerModel>> getAllBeers() {
        final List<BeerModel> collection =
                beerRepository.findAll().stream()
                        .map(Beer::toDTO)
                        .map(BeerModel::new)
                        .collect(Collectors.toList());
        if (collection.isEmpty()) {
            return ResponseEntity.notFound().build();
        } {
            final String uriString = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
            return ResponseEntity.ok().body(CollectionModel.of(collection).add(Link.of(uriString, LinkRelation.of("self"))));
        }
    }

    public ResponseEntity<CollectionModel<BeerModel>> getByName(String name) {
        final List<BeerModel> collection =
                beerRepository.findBeerByName(name).stream()
                        .map(Beer::toDTO)
                        .map(BeerModel::new)
                        .collect(Collectors.toList());

        if (collection.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            final String uriString = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
            return ResponseEntity.ok().body(CollectionModel.of(collection).add(Link.of(uriString, LinkRelation.of("self"))));
        }
    }

    public ResponseEntity<BeerModel> add(BeerDTO beerDTO) {
        Beer beer = beerDTO.toBeer();
        beerRepository.save(beer);
        final URI uri =
                MvcUriComponentsBuilder.fromController(getClass())
                        .path("/{id}")
                        .buildAndExpand(beer.getId())
                        .toUri();
        return ResponseEntity.created(uri).body(new BeerModel(beer.toDTO()));
    }


}
