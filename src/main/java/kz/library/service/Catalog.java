package kz.library.service;

import kz.library.exception.DuplicateEntityException;
import kz.library.exception.ItemNotFoundException;
import kz.library.model.Book;
import kz.library.model.LibraryItem;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class Catalog implements Searchable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final ArrayList<LibraryItem> items = new ArrayList<>();

    public void addItem(LibraryItem item) {
        Objects.requireNonNull(item, "Материал көрсетілуі керек.");
        if (findByIdOrNull(item.getId()) != null) {
            throw new DuplicateEntityException("Материал ID бұрыннан бар: " + item.getId());
        }
        items.add(item);
    }

    public List<LibraryItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public List<LibraryItem> searchByTitle(String title) {
        String query = normalize(title);
        return items.stream()
                .filter(item -> normalize(item.getTitle()).contains(query))
                .toList();
    }

    public List<LibraryItem> searchByAuthor(String authorName) {
        String query = normalize(authorName);
        return items.stream()
                .filter(Book.class::isInstance)
                .map(Book.class::cast)
                .filter(book -> normalize(book.getAuthor().getName()).contains(query))
                .map(LibraryItem.class::cast)
                .toList();
    }

    public LibraryItem findById(int id) {
        LibraryItem item = findByIdOrNull(id);
        if (item == null) {
            throw new ItemNotFoundException("Материал табылмады. ID: " + id);
        }
        return item;
    }

    private LibraryItem findByIdOrNull(int id) {
        return items.stream().filter(item -> item.getId() == id).findFirst().orElse(null);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Іздеу мәтіні бос болмауы керек.");
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
