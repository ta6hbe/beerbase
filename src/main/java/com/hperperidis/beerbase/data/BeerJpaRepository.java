package com.hperperidis.beerbase.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository interface definining all available CRUD JPA actions for the Beers Repo.
 *
 * Here we can define additional specific queries we want our Persistence API to contain.
 *
 * Here we have added the following methods.
 *
 * - findBeerByName:
 *   - Takes a "Named Parameter" {@code Param("name")} to bind to our query.
 *     As the Beer name is not constrained to be unique, this will return a {@code List<Beer>}
 *     containing all {@code Beer} records it finds, having name equal to the parameter
 *     {@code String} name.
 *
 * - count:
 *   - Returns the number of records the repository currently holds.
 *
 * - findAll(Pageable pageable):
 *   - Used to perform a paginated search, returning all records matching the page limit parameters
 *   - We use this to achieve an easy way to get a "Random" beer returned.
 *
 * @author C. Perperidis(ta6hbe@hotmail.com)
 */
@Repository
public interface BeerJpaRepository extends JpaRepository<Beer, Long> {

    List<Beer> findBeerByName(@Param("name") String name);

    long count();

    Page<Beer> findAll(Pageable pageable);

    Optional<Beer> findByExternalIdAndDataSourceAndRemotePath(
            @Param("externalId") String externalId,
            @Param("dataSource") String dataSource,
            @Param("remotePath") String remotePath);
}
