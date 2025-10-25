package com.team.recommendations.model.dynamic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "product")
public class DynamicProduct {
    @JsonProperty(access = Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String product_name;
    private UUID product_id;
    private String product_text;

    @OneToMany(mappedBy = "product")
    private Collection<DynamicRule> rule;

    public DynamicProduct() {
    }

    public UUID getId() {
        return id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public UUID getProduct_id() {
        return product_id;
    }

    public String getProduct_text() {
        return product_text;
    }

    public Collection<DynamicRule> getRule() {
        return rule;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DynamicProduct that = (DynamicProduct) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DynamicProduct{" +
                "id=" + id +
                ", product_name='" + product_name + '\'' +
                ", product_id=" + product_id +
                ", product_text='" + product_text + '\'' +
                ", rule=" + rule +
                '}';
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setProduct_id(UUID product_id) {
        this.product_id = product_id;
    }

    public void setProduct_text(String product_text) {
        this.product_text = product_text;
    }

    public void setRule(Collection<DynamicRule> rule) {
        this.rule = rule;
    }
}
