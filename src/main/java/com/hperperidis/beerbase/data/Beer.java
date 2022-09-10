package com.hperperidis.beerbase.data;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@Setter

@Entity
public class Beer {

    @Id @GeneratedValue
    private Long id;

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

}
