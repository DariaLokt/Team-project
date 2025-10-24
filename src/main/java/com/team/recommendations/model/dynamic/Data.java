package com.team.recommendations.model.dynamic;

import java.util.Collection;
import java.util.Objects;

public class Data {
    Collection<DynamicProduct> data;

    public Data(Collection<DynamicProduct> data) {
        this.data = data;
    }

    public Collection<DynamicProduct> getData() {
        return data;
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
