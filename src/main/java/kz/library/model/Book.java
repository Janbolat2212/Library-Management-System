package kz.library.model;

import java.io.Serial;
import java.util.Objects;

public final class Book extends LibraryItem {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String isbn;
    private final Author author;

    public Book(int id, String title, int year, String isbn, Author author) {
        super(id, title, year);
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN бос болмауы керек.");
        }
        this.isbn = isbn.trim();
        this.author = Objects.requireNonNull(author, "Автор көрсетілуі керек.");
    }

    public String getIsbn() {
        return isbn;
    }

    public Author getAuthor() {
        return author;
    }

    @Override
    public String getInfo() {
        return "Кітап {id=%d, атауы='%s', автор='%s', жыл=%d, ISBN='%s', күйі=%s}"
                .formatted(getId(), getTitle(), author.getName(), getYear(), isbn, availabilityText());
    }
}
