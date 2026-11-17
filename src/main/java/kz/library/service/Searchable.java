package kz.library.service;

import kz.library.model.LibraryItem;

import java.util.List;

public interface Searchable {
    List<LibraryItem> searchByTitle(String title);
}
