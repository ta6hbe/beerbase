package com.hperperidis.beerbase.service;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.hperperidis.beerbase.data.Beer;
import com.hperperidis.beerbase.data.BeerJpaRepository;
import com.hperperidis.beerbase.data.DataSourceType;
import com.hperperidis.beerbase.http.HttpClient;
import com.hperperidis.beerbase.model.BeerDTO;
import com.hperperidis.beerbase.model.BeerModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final BeerJpaRepository beerJpaRepository;
    private final HttpClient httpClient;

    /**
     * Service class handling the data exchange from the database.
     * Uses a JPA Repository as the DAO layer to exchange data with the underlying DB layer.
     *
     * @param beerJpaRepository - {@code BeerJpaRepository} The JPA Repository implementor to provide the DAO layer over JPA.
     */
    @Autowired
    public BeerService(BeerJpaRepository beerJpaRepository, HttpClient httpClient) {
        this.beerJpaRepository = beerJpaRepository;
        this.httpClient = httpClient;
    }

    public ResponseEntity<BeerModel> getById(long id) {
        return beerJpaRepository.findById(id)
                .map(Beer::toDTO)
                .map(BeerModel::new)
                .map( beer -> ResponseEntity.ok().body(beer) )
                .orElseGet( () -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<BeerModel> getRandomBeer() {
        long qty = beerJpaRepository.count();
        return beerJpaRepository.findAll(PageRequest.of(
                (int) Math.floor(Math.random()*(qty)), 1))
                .stream().findFirst()
                .map( Beer::toDTO)
                .map( BeerModel::new )
                .map( beerModel -> ResponseEntity.ok().body(beerModel) )
                .orElseGet( () -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<CollectionModel<BeerModel>> getAllBeers() {
        final List<BeerModel> collection =
                beerJpaRepository.findAll().stream()
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
                beerJpaRepository.findBeerByName(name).stream()
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
        beerJpaRepository.save(beer);
        final URI uri =
                MvcUriComponentsBuilder.fromController(getClass())
                        .path("/{id}")
                        .buildAndExpand(beer.getId())
                        .toUri();
        return ResponseEntity.created(uri).body(new BeerModel(beer.toDTO()));
    }

    public ResponseEntity<BeerModel> update(BeerDTO beerDTO) {
        return beerJpaRepository.findById(beerDTO.getId()).map(beer -> beer.toBuilder()
                .description(beerDTO.getDescription()).name(beerDTO.getName()).build())
                .map(beerJpaRepository::save)
                .map(Beer::toDTO)
                .map(BeerModel::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<CollectionModel<BeerModel>> batchUpdateOrCreateByDataSource(
            List<BeerDTO> records, DataSourceType type, String dataSourceUrl) {

        if(records.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        List<BeerModel>updatedOrCreated = records.stream()
                .map(beerDTO -> createOrUpdateFromExternalSource(beerDTO, type, dataSourceUrl))
                .map(Beer::toDTO).map(BeerModel::new).collect(Collectors.toList());

        if (updatedOrCreated.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(CollectionModel.of(updatedOrCreated));
    }

    @Transactional
    protected Beer createOrUpdateFromExternalSource(BeerDTO beerDTO, DataSourceType type, String dataSourceUrl) {
        Optional<Beer> existingBeer =
                beerJpaRepository.findByExternalIdAndDataSourceAndRemotePath(Long.toString(beerDTO.getId()), type.label, dataSourceUrl);

        if (existingBeer.isPresent()) {
            return existingBeer.map(beer -> beerJpaRepository.save(
                    beer.toBuilder()
                            .name(beerDTO.getName())
                            .description(beerDTO.getDescription())
                            .build())).get();
        } else {
            return beerJpaRepository.save(
                    Beer.builder()
                            .name(beerDTO.getName())
                            .description(beerDTO.getDescription())
                            .dataSource(type.label)
                            .remotePath(dataSourceUrl)
                            .externalId(Long.toString(beerDTO.getId())).build());
        }
    }

    @Transactional
    public ResponseEntity<CollectionModel<BeerModel>> injestDataFromURL(String url){

        return batchUpdateOrCreateByDataSource(httpClient.fromUrl(url), DataSourceType.URL, url);

    }
}
