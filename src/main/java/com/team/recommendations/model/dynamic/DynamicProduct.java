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

    private String productName;
    private UUID productId;
    private String productText;

    @OneToMany(mappedBy = "product")
    private Collection<DynamicRule> rule;

    public DynamicProduct() {
    }

    public UUID getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductText() {
        return productText;
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
                ", product_name='" + productName + '\'' +
                ", product_id=" + productId +
                ", product_text='" + productText + '\'' +
                ", rule=" + rule +
                '}';
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public void setRule(Collection<DynamicRule> rule) {
        this.rule = rule;
    }
}
