package com.hperperidis.beerbase.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Trivial POJO, defining the JSON request message structure for a data import based on a URL. Either a file or a direct ingestion.
 *
 * @author C. Perperidis(ta6hbe@hotmail.com)
 */

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchRequestDTO {

    @JsonProperty
    private String url;

}
