package com.hperperidis.beerbase.http;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.hperperidis.beerbase.Exceptions.RemoteServiceException;
import com.hperperidis.beerbase.model.BeerDTO;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * A HTTP helper class to make calls to remote URLs in order to consume REST-full APIs and injest/copy data from those, to our local DB.
 * Currently only has one method which uses a GET request using the Spring Framework RestTemplate for the actual HTTP request-response exchange.
 *
 * It will return a {@code List<BeerDTO>} objects.
 * TODO: This can be made more generic to fit other APIs as well, if we pass in the DTO Bean class that matches match other API data structures
 *  returned. But this is not a requirement for this implementation at present.
 *
 * @author C. Perperidis(ta6hbe@hotmail.com)
 */
@Component
public class HttpClient {

    public List<BeerDTO> fromUrl(String url) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return Optional.ofNullable(restTemplate.getForObject(url, BeerDTO[].class))
                    .map(Arrays::asList)
                    .orElseThrow(() -> new RemoteServiceException("Empty record list returned or Null", url));
        } catch (RestClientException rce) {
            throw new RemoteServiceException(rce.getMessage(), url, rce);
        }
    }
}
