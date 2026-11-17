package kz.library.model;

import kz.library.exception.ItemUnavailableException;

import java.io.Serial;
import java.io.Serializable;
import java.time.Year;
import java.util.Objects;

public abstract class LibraryItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String title;
    private final int year;
    private boolean available;

    protected LibraryItem(int id, String title, int year) {
        if (id <= 0) {
            throw new IllegalArgumentException("Материал ID нөлден үлкен болуы керек.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Материал атауы бос болмауы керек.");
        }
        if (year < 1000 || year > Year.now().getValue() + 1) {
            throw new IllegalArgumentException("Шығарылған жыл дұрыс емес: " + year);
        }
        this.id = id;
        this.title = title.trim();
        this.year = year;
        this.available = true;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public boolean isAvailable() {
        return available;
    }

    public void borrow() {
        if (!available) {
            throw new ItemUnavailableException("Материал қазір қолжетімсіз: " + title);
        }
        available = false;
    }

    public void returnToLibrary() {
        if (available) {
            throw new IllegalStateException("Материал кітапханада бар: " + title);
        }
        available = true;
    }

    public abstract String getInfo();

    protected String availabilityText() {
        return available ? "қолжетімді" : "берілген";
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof LibraryItem item && id == item.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getInfo();
    }
}
