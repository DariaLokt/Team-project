package com.team.recommendations.model.dynamic;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;
import java.util.Objects;

@Schema(description = "Модель данных набора продуктов, рекомендованных пользователю")
public class Data {
    @Schema(
            type = "array",
            description = "Набор продуктов, рекомендованных пользователю"
    )
    Collection<DynamicProductDto> data;

    public Data(Collection<DynamicProductDto> data) {
        this.data = data;
    }

    public Collection<DynamicProductDto> getData() {
        return data;
    }

    public void setData(Collection<DynamicProductDto> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Data data1 = (Data) o;
        return Objects.equals(data, data1.data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
    }

    @Override
    public String toString() {
        return "Data{" +
                "data=" + data +
                '}';
    }
}
