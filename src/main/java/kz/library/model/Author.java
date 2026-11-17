package kz.library.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public final class Author implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;

    public Author(int id, String name) {
        if (id <= 0) {
            throw new IllegalArgumentException("Автор ID нөлден үлкен болуы керек.");
        }
        this.id = id;
        this.name = requireText(name, "Автордың аты бос болмауы керек.");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Author author && id == author.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
