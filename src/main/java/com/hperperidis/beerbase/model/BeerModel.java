package com.hperperidis.beerbase.model;

import com.hperperidis.beerbase.controller.BeerController;
import com.hperperidis.beerbase.data.Beer;
import lombok.Getter;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Spring RepresentationModel - EntityModel Wrapper for our BeerDTO.
 *
 * This adds spring HATEOAS REST structure to our responses, by adding
 * Representational State Links to the underlying DB returned data.
 *
 * Useful, as it adds RESTFull links to the HTTP URIs that represent the
 * returned data. (Hypermedia As The Engine Of Application State) - HATEOAS
 *
 * @author C. Perperidis(ta6hbe@hotmail.com)
 *
 */
@Getter
public class BeerModel extends RepresentationModel<EntityModel<Beer>> {

    private final BeerDTO beerDTO;
    public BeerModel(final BeerDTO beerDTO) {
        this.beerDTO = beerDTO;
        final long id = beerDTO.getId();
        add(linkTo(BeerController.class).withRel("beers"));
        add(linkTo(methodOn(BeerController.class).getBeerById(id)).withSelfRel());
    }
}
