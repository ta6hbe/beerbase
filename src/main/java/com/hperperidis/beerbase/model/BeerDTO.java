package com.hperperidis.beerbase.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hperperidis.beerbase.data.Beer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object model used by the local API to read in or send out
 * data from / to the clients via HTTP request/response.
 *
 * This is the JSON representation definition of our Beer API.
 *
 * The API will read (Deserialize) all incoming JSON data to a {@code BeerDTO} object,
 * recognising all incoming ID aliases of:
 *  - id
 *  - identifier
 *  - beer_identifier
 *
 *  When the DTO will be serialised, it will only be to return data from OUR API, which
 *  only defines the beer id: as **identifier**.
 *
 * Also decouples our Data Transfer - Request/Response data from the Data Access layer
 * representations.
 *
 *@author C. Pereridis(ta6hbe@hotmail.com)
 *
 */

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BeerDTO {

    @JsonAlias({ "id", "beer_identifier", "identifier" })
    @JsonProperty("identifier")
    private Long id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    public Beer toBeer() {
        return Beer.builder()
                .id(this.getId())
                .name(this.getName())
                .description(this.getDescription())
                .build();
    }
}
