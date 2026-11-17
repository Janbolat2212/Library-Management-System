package kz.library.service;

import kz.library.exception.DuplicateEntityException;
import kz.library.exception.ItemNotFoundException;
import kz.library.model.Author;
import kz.library.model.Book;
import kz.library.model.Fine;
import kz.library.model.Librarian;
import kz.library.model.LibraryItem;
import kz.library.model.Loan;
import kz.library.model.Magazine;
import kz.library.model.Reader;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class LibrarySystem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final double DEFAULT_DAILY_FINE_RATE = 100.0;

    private final Catalog catalog = new Catalog();
    private final ArrayList<Author> authors = new ArrayList<>();
    private final ArrayList<Reader> readers = new ArrayList<>();
    private final ArrayList<Loan> loans = new ArrayList<>();
    private final Librarian librarian;
    private double dailyFineRate;
    private int nextAuthorId = 1;
    private int nextItemId = 1;
    private int nextReaderId = 1;
    private int nextLoanId = 1;

    public LibrarySystem() {
        this(new Librarian(1, "Бас кітапханашы"), DEFAULT_DAILY_FINE_RATE);
    }

    public LibrarySystem(Librarian librarian, double dailyFineRate) {
        this.librarian = librarian;
        setDailyFineRate(dailyFineRate);
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public List<Author> getAuthors() {
        return Collections.unmodifiableList(authors);
    }

    public List<Reader> getReaders() {
        return Collections.unmodifiableList(readers);
    }

    public List<Loan> getLoans() {
        return Collections.unmodifiableList(loans);
    }

    public Librarian getLibrarian() {
        return librarian;
    }

    public double getDailyFineRate() {
        return dailyFineRate;
    }

    public void setDailyFineRate(double dailyFineRate) {
        if (dailyFineRate < 0) {
            throw new IllegalArgumentException("Күндік айыппұл мөлшері теріс болмауы керек.");
        }
        this.dailyFineRate = dailyFineRate;
    }

    public Author registerAuthor(String name) {
        String cleanName = requireText(name, "Автордың аты бос болмауы керек.");
        boolean duplicate = authors.stream().anyMatch(author -> author.getName().equalsIgnoreCase(cleanName));
        if (duplicate) {
            throw new DuplicateEntityException("Бұл автор бұрыннан тіркелген: " + cleanName);
        }
        Author author = new Author(nextAuthorId, cleanName);
        authors.add(author);
        nextAuthorId++;
        return author;
    }

    public Book addBook(String title, int year, String isbn, int authorId) {
        String cleanIsbn = requireText(isbn, "ISBN бос болмауы керек.");
        if (catalog.getItems().stream()
                .filter(Book.class::isInstance)
                .map(Book.class::cast)
                .anyMatch(book -> book.getIsbn().equalsIgnoreCase(cleanIsbn))) {
            throw new DuplicateEntityException("Бұл ISBN бұрыннан бар: " + cleanIsbn);
        }
        Book book = new Book(nextItemId, title, year, cleanIsbn, findAuthor(authorId));
        catalog.addItem(book);
        nextItemId++;
        return book;
    }

    public Magazine addMagazine(String title, int year, int issueNumber) {
        Magazine magazine = new Magazine(nextItemId, title, year, issueNumber);
        catalog.addItem(magazine);
        nextItemId++;
        return magazine;
    }

    public Reader registerReader(String name) {
        int id = nextReaderId;
        Reader reader = new Reader(id, name, "CARD-%04d".formatted(id));
        readers.add(reader);
        nextReaderId++;
        return reader;
    }

    public Loan issueItem(int itemId, int readerId, LocalDate issueDate, LocalDate dueDate) {
        LibraryItem item = catalog.findById(itemId);
        Reader reader = findReader(readerId);
        Loan loan = librarian.issueItem(nextLoanId, item, reader, issueDate, dueDate);
        loans.add(loan);
        nextLoanId++;
        return loan;
    }

    public Optional<Fine> returnItem(int loanId, LocalDate returnDate) {
        Loan loan = findLoan(loanId);
        return librarian.returnItem(loan, returnDate, dailyFineRate);
    }

    public List<Loan> getActiveLoans() {
        return loans.stream().filter(Loan::isActive).toList();
    }

    public List<Loan> getOverdueLoans(LocalDate date) {
        return loans.stream()
                .filter(Loan::isActive)
                .filter(loan -> loan.isOverdue(date))
                .toList();
    }

    public Author findAuthor(int id) {
        return authors.stream()
                .filter(author -> author.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Автор табылмады. ID: " + id));
    }

    public Reader findReader(int id) {
        return readers.stream()
                .filter(reader -> reader.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Оқырман табылмады. ID: " + id));
    }

    public Loan findLoan(int id) {
        return loans.stream()
                .filter(loan -> loan.getLoanId() == id)
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Loan жазбасы табылмады. ID: " + id));
    }

    public boolean isEmpty() {
        return catalog.getItems().isEmpty() && authors.isEmpty() && readers.isEmpty() && loans.isEmpty();
    }

    public void addSampleData() {
        if (!isEmpty()) {
            return;
        }
        Author abai = registerAuthor("Абай Құнанбайұлы");
        Author auezov = registerAuthor("Мұхтар Әуезов");
        addBook("Қара сөздер", 2020, "978-601-01-0001-1", abai.getId());
        addBook("Абай жолы", 2019, "978-601-01-0002-8", auezov.getId());
        addMagazine("National Geographic Қазақстан", 2026, 11);
        registerReader("Айдана Серікқызы");
        registerReader("Нұрлан Әлиұлы");
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
