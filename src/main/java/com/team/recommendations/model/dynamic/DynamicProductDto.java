package com.team.recommendations.model.dynamic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@Schema(description = "Модель данных продукта с динамическими правилами")
public class DynamicProductDto {
    @Schema(
            type = "uuid",
            description = "ID сущности продукта с динамическими правилами"
    )
    @JsonProperty(access = Access.READ_ONLY)
    private UUID id;

    @Schema(
            type = "string",
            description = "Наименование продукта с динамическими правилами"
    )
    private String productName;
    @Schema(
            type = "uuid",
            description = "ID продукта с динамическими правилами"
    )
    private UUID productId;
    @Schema(
            type = "string",
            description = "Описание продукта с динамическими правилами"
    )
    private String productText;

    @Schema(
            description = "Динамические правила продукта"
    )
    private Collection<DynamicRuleDto> rule;

    public DynamicProductDto() {
    }

    public DynamicProductDto(UUID id, String productName, UUID productId, String productText, Collection<DynamicRuleDto> rule) {
        this.id = id;
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.rule = rule;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public Collection<DynamicRuleDto> getRule() {
        return rule;
    }

    public void setRule(Collection<DynamicRuleDto> rule) {
        this.rule = rule;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DynamicProductDto that = (DynamicProductDto) o;
        return Objects.equals(id, that.id) && Objects.equals(productName, that.productName) && Objects.equals(productId, that.productId) && Objects.equals(productText, that.productText) && Objects.equals(rule, that.rule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productName, productId, productText, rule);
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
}
