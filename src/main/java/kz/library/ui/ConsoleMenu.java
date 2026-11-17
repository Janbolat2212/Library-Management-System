package kz.library.ui;

import kz.library.model.Author;
import kz.library.model.Fine;
import kz.library.model.LibraryItem;
import kz.library.model.Loan;
import kz.library.service.LibrarySystem;
import kz.library.storage.FileStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public final class ConsoleMenu {
    private final LibrarySystem system;
    private final Path dataFile;
    private final Scanner scanner;

    public ConsoleMenu(LibrarySystem system, Path dataFile, Scanner scanner) {
        this.system = system;
        this.dataFile = dataFile;
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;
        System.out.println("\nҰлттық академиялық кітапхана - оқу жүйесі");
        while (running) {
            printMenu();
            String choice = prompt("Таңдау: ");
            try {
                switch (choice) {
                    case "1" -> showCatalog();
                    case "2" -> addAuthor();
                    case "3" -> addBook();
                    case "4" -> addMagazine();
                    case "5" -> searchByTitle();
                    case "6" -> searchByAuthor();
                    case "7" -> registerReader();
                    case "8" -> showReaders();
                    case "9" -> issueItem();
                    case "10" -> returnItem();
                    case "11" -> showLoans(system.getActiveLoans(), "Белсенді берілімдер");
                    case "12" -> showLoans(system.getOverdueLoans(LocalDate.now()), "Кешіктірілген берілімдер");
                    case "13" -> save();
                    case "0" -> {
                        save();
                        running = false;
                        System.out.println("Бағдарлама аяқталды.");
                    }
                    default -> System.out.println("Белгісіз команда. Қайта таңдаңыз.");
                }
            } catch (RuntimeException | IOException exception) {
                System.out.println("Қате: " + exception.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("""

                1 - Каталогты көру
                2 - Автор тіркеу
                3 - Кітап қосу
                4 - Журнал қосу
                5 - Атауы бойынша іздеу
                6 - Авторы бойынша іздеу
                7 - Оқырман тіркеу
                8 - Оқырмандарды көру
                9 - Материал беру
                10 - Материал қайтару
                11 - Белсенді берілімдер
                12 - Кешіктірілген берілімдер
                13 - Файлға сақтау
                0 - Сақтау және шығу
                """);
    }

    private void showCatalog() {
        showItems(system.getCatalog().getItems(), "Каталог");
    }

    private void addAuthor() {
        Author author = system.registerAuthor(prompt("Автордың аты-жөні: "));
        System.out.println("Автор тіркелді: " + author);
    }

    private void addBook() {
        showAuthors();
        var book = system.addBook(
                prompt("Кітап атауы: "),
                readInt("Шығарылған жыл: "),
                prompt("ISBN: "),
                readInt("Автор ID: "));
        System.out.println("Кітап қосылды: " + book.getInfo());
    }

    private void addMagazine() {
        var magazine = system.addMagazine(
                prompt("Журнал атауы: "),
                readInt("Шығарылған жыл: "),
                readInt("Шығарылым нөмірі: "));
        System.out.println("Журнал қосылды: " + magazine.getInfo());
    }

    private void searchByTitle() {
        showItems(system.getCatalog().searchByTitle(prompt("Атау немесе оның бөлігі: ")), "Іздеу нәтижесі");
    }

    private void searchByAuthor() {
        showItems(system.getCatalog().searchByAuthor(prompt("Автор аты немесе оның бөлігі: ")), "Іздеу нәтижесі");
    }

    private void registerReader() {
        var reader = system.registerReader(prompt("Оқырманның аты-жөні: "));
        System.out.println("Оқырман тіркелді: " + reader);
    }

    private void showReaders() {
        System.out.println("\nОқырмандар:");
        if (system.getReaders().isEmpty()) {
            System.out.println("Тізім бос.");
            return;
        }
        system.getReaders().forEach(reader -> System.out.println("- " + reader));
    }

    private void issueItem() {
        showCatalog();
        showReaders();
        int itemId = readInt("Материал ID: ");
        int readerId = readInt("Оқырман ID: ");
        LocalDate issueDate = readDate("Берілген күн", LocalDate.now());
        LocalDate dueDate = readDate("Қайтару мерзімі", issueDate.plusDays(14));
        Loan loan = system.issueItem(itemId, readerId, issueDate, dueDate);
        System.out.println("Материал берілді: " + loan);
    }

    private void returnItem() {
        showLoans(system.getActiveLoans(), "Белсенді берілімдер");
        int loanId = readInt("Loan ID: ");
        LocalDate returnDate = readDate("Қайтарылған күн", LocalDate.now());
        Optional<Fine> fine = system.returnItem(loanId, returnDate);
        System.out.println("Материал қабылданды.");
        System.out.println(fine.<String>map(value -> "Есептелді: " + value)
                .orElse("Кешігу жоқ, айыппұл есептелмеді."));
    }

    private void showAuthors() {
        System.out.println("\nАвторлар:");
        system.getAuthors().forEach(author -> System.out.println("- " + author));
    }

    private void showItems(List<LibraryItem> items, String title) {
        System.out.println("\n" + title + ":");
        if (items.isEmpty()) {
            System.out.println("Нәтиже табылмады.");
            return;
        }
        items.forEach(item -> System.out.println("- " + item.getInfo()));
    }

    private void showLoans(List<Loan> loans, String title) {
        System.out.println("\n" + title + ":");
        if (loans.isEmpty()) {
            System.out.println("Тізім бос.");
            return;
        }
        loans.forEach(loan -> System.out.println("- " + loan));
    }

    private void save() throws IOException {
        FileStorage.save(system, dataFile);
        System.out.println("Мәліметтер сақталды: " + dataFile.toAbsolutePath());
    }

    private int readInt(String message) {
        while (true) {
            try {
                return Integer.parseInt(prompt(message));
            } catch (NumberFormatException exception) {
                System.out.println("Бүтін сан енгізіңіз.");
            }
        }
    }

    private LocalDate readDate(String message, LocalDate defaultValue) {
        while (true) {
            String value = prompt("%s (YYYY-MM-DD, Enter=%s): ".formatted(message, defaultValue));
            if (value.isBlank()) {
                return defaultValue;
            }
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException exception) {
                System.out.println("Күн пішімі дұрыс емес. Мысал: 2026-11-17");
            }
        }
    }

    private String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }
}
