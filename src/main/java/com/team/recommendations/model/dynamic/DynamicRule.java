package com.team.recommendations.model.dynamic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "rule")
public class DynamicRule {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String query;
    private Collection<String> arguments;
    private Boolean negate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id")
    private DynamicProduct product;

    public DynamicRule() {
    }

    public String getQuery() {
        return query;
    }

    public Collection<String> getArguments() {
        return arguments;
    }

    public Boolean getNegate() {
        return negate;
    }

    public UUID getId() {
        return id;
    }

    public DynamicProduct getProduct() {
        return product;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DynamicRule that = (DynamicRule) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DynamicRule{" +
                "query='" + query + '\'' +
                ", arguments=" + arguments +
                ", negate=" + negate +
                '}';
    }

    public void setProduct(DynamicProduct product) {
        this.product = product;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setArguments(Collection<String> arguments) {
        this.arguments = arguments;
    }

    public void setNegate(Boolean negate) {
        this.negate = negate;
    }
}
