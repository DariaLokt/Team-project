package com.team.recommendations.model.recommendations;

import java.util.Objects;
import java.util.UUID;

public class Recommendation {
    private final String name;
    private final UUID id;
    private final String text;

    public Recommendation(String name, UUID id, String text) {
        this.name = name;
        this.id = id;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Recommendation that = (Recommendation) o;
        return Objects.equals(name, that.name) && Objects.equals(id, that.id) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, text);
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", text='" + text + '\'' +
                '}';
    }
}
