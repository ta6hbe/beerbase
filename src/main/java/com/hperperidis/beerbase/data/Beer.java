package com.hperperidis.beerbase.data;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.hperperidis.beerbase.model.BeerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Data Access model representing "OUR" REST API Beer Entity.
 * Works with the {@code BeerRepository}, and represents its entity.
 *
 * Contains additional fields to identify the data source and external ids of the
 * the ingested data records, since this is intended to hold records from multiple
 * data sources. We can later add additional queries to our JPA Repository to filter data based on
 * these fields.
 *
 * @author C. Perperidis(ta6hbe@hotmail.com)
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@Setter
@Entity
public class Beer {

    @Id @GeneratedValue
    private Long id;

    private String dataSource;

    private String externalId;

    private String name;

    private String description;

    @Override
    public boolean equals(Object object) {
        if (! (object instanceof Beer)) {
            return false;
        }

        if (this == object) {
            return true;
        }

        Beer beer = (Beer) object;
        return Objects.equals(this.name, beer.name)
                && Objects.equals(this.id, beer.id)
                && Objects.equals(this.description, beer.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.description);
    }

    public BeerDTO toDTO() {
        return BeerDTO.builder()
                .id(this.getId())
                .name(this.getName())
                .description(this.getDescription())
                .build();
    }

}
