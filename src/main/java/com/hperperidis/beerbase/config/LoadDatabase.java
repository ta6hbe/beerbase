package com.hperperidis.beerbase.config;

import com.hperperidis.beerbase.data.Beer;
import com.hperperidis.beerbase.data.BeerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {
    private static final Logger logger = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(BeerRepository repository) {
        return args -> {
            logger.info("Beer repository initialised!");

            repository.save(Beer.builder()
                                    .name("Harry's home made lagger")
                                    .description("Home brewed and distilled to perfection, best served ice cold in a frosen pint. 4.5% Alcohol.")
                                    .build());


        };
    }
}
