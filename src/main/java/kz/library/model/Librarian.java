package kz.library.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public final class Librarian implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;

    public Librarian(int id, String name) {
        if (id <= 0) {
            throw new IllegalArgumentException("Кітапханашы ID нөлден үлкен болуы керек.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Кітапханашының аты бос болмауы керек.");
        }
        this.id = id;
        this.name = name.trim();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Loan issueItem(int loanId, LibraryItem item, Reader reader,
                          LocalDate issueDate, LocalDate dueDate) {
        Objects.requireNonNull(item, "Материал көрсетілуі керек.");
        item.borrow();
        try {
            return new Loan(loanId, item, reader, issueDate, dueDate);
        } catch (RuntimeException exception) {
            item.returnToLibrary();
            throw exception;
        }
    }

    public Optional<Fine> returnItem(Loan loan, LocalDate returnDate, double dailyFineRate) {
        return Objects.requireNonNull(loan, "Loan көрсетілуі керек.")
                .close(returnDate, dailyFineRate);
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
