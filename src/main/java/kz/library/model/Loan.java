package kz.library.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

public final class Loan implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int loanId;
    private final LibraryItem item;
    private final Reader reader;
    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private Fine fine;

    public Loan(int loanId, LibraryItem item, Reader reader, LocalDate issueDate, LocalDate dueDate) {
        if (loanId <= 0) {
            throw new IllegalArgumentException("Loan ID нөлден үлкен болуы керек.");
        }
        this.item = Objects.requireNonNull(item, "Материал көрсетілуі керек.");
        this.reader = Objects.requireNonNull(reader, "Оқырман көрсетілуі керек.");
        this.issueDate = Objects.requireNonNull(issueDate, "Берілген күн көрсетілуі керек.");
        this.dueDate = Objects.requireNonNull(dueDate, "Қайтару мерзімі көрсетілуі керек.");
        if (dueDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("Қайтару мерзімі берілген күннен бұрын болмауы керек.");
        }
        this.loanId = loanId;
    }

    public int getLoanId() {
        return loanId;
    }

    public LibraryItem getItem() {
        return item;
    }

    public Reader getReader() {
        return reader;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public Optional<Fine> getFine() {
        return Optional.ofNullable(fine);
    }

    public boolean isActive() {
        return returnDate == null;
    }

    public boolean isOverdue() {
        return isOverdue(LocalDate.now());
    }

    public boolean isOverdue(LocalDate date) {
        Objects.requireNonNull(date, "Тексеру күні көрсетілуі керек.");
        LocalDate effectiveDate = returnDate == null ? date : returnDate;
        return effectiveDate.isAfter(dueDate);
    }

    public long calculateDaysLate(LocalDate date) {
        Objects.requireNonNull(date, "Тексеру күні көрсетілуі керек.");
        LocalDate effectiveDate = returnDate == null ? date : returnDate;
        return effectiveDate.isAfter(dueDate) ? ChronoUnit.DAYS.between(dueDate, effectiveDate) : 0;
    }

    public Optional<Fine> close(LocalDate date, double dailyFineRate) {
        Objects.requireNonNull(date, "Қайтару күні көрсетілуі керек.");
        if (!isActive()) {
            throw new IllegalStateException("Бұл беру жазбасы бұрын жабылған: " + loanId);
        }
        if (date.isBefore(issueDate)) {
            throw new IllegalArgumentException("Қайтару күні берілген күннен бұрын болмауы керек.");
        }
        this.returnDate = date;
        long daysLate = calculateDaysLate(date);
        if (daysLate > 0) {
            this.fine = Fine.calculate(daysLate, dailyFineRate);
        }
        item.returnToLibrary();
        return getFine();
    }

    @Override
    public String toString() {
        String state = isActive() ? "белсенді" : "қайтарылды: " + returnDate;
        return "Loan {id=%d, материал='%s', оқырман='%s', берілді=%s, мерзімі=%s, күйі=%s%s}"
                .formatted(loanId, item.getTitle(), reader.getName(), issueDate, dueDate, state,
                        fine == null ? "" : ", " + fine);
    }
}
