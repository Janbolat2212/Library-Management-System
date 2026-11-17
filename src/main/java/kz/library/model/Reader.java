package kz.library.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public final class Reader implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;
    private final String cardNumber;

    public Reader(int id, String name, String cardNumber) {
        if (id <= 0) {
            throw new IllegalArgumentException("Оқырман ID нөлден үлкен болуы керек.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Оқырманның аты бос болмауы керек.");
        }
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new IllegalArgumentException("Оқырман билеті бос болмауы керек.");
        }
        this.id = id;
        this.name = name.trim();
        this.cardNumber = cardNumber.trim();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Reader reader && id == reader.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "%d - %s (билет: %s)".formatted(id, name, cardNumber);
    }
}
