package com.team.recommendations.model.dynamic;

import java.util.Objects;
import java.util.UUID;
//@Table
public class DynamicProduct {
    private final UUID id;
    private final String product_name;
    private final UUID product_id;
    private final String product_text;
    private final DynamicRule rule;

    public DynamicProduct(String product_name, UUID product_id, String product_text, DynamicRule rule) {
        this.id = UUID.randomUUID();
        this.product_name = product_name;
        this.product_id = product_id;
        this.product_text = product_text;
        this.rule = rule;
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

    public DynamicRule getRule() {
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
}
