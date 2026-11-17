package kz.library;

import kz.library.exception.ItemUnavailableException;
import kz.library.model.Author;
import kz.library.model.Fine;
import kz.library.model.Loan;
import kz.library.service.LibrarySystem;
import kz.library.storage.FileStorage;
import kz.library.ui.ConsoleMenu;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;

public final class Main {
    private static final Path DATA_FILE = Path.of("data", "library-data.bin");

    private Main() {
    }

    public static void main(String[] args) {
        if (args.length > 0 && "--demo".equalsIgnoreCase(args[0])) {
            runDemo();
            return;
        }

        try {
            LibrarySystem system = FileStorage.loadOrCreate(DATA_FILE);
            if (system.isEmpty()) {
                system.addSampleData();
            }
            try (Scanner scanner = new Scanner(System.in)) {
                new ConsoleMenu(system, DATA_FILE, scanner).run();
            }
        } catch (IOException exception) {
            System.err.println("Мәліметтер файлын оқу мүмкін емес: " + exception.getMessage());
            System.exit(1);
        }
    }

    private static void runDemo() {
        System.out.println("=== Кітапхана жүйесінің ООП демонстрациясы ===");
        LibrarySystem system = new LibrarySystem();
        Author author = system.registerAuthor("Мұхтар Әуезов");
        var book = system.addBook("Абай жолы", 2019, "978-601-01-0002-8", author.getId());
        var magazine = system.addMagazine("National Geographic Қазақстан", 2026, 11);
        var reader = system.registerReader("Айдана Серікқызы");

        System.out.println("\n1. Полиморфизм: каталогтағы әртүрлі материалдар");
        system.getCatalog().getItems().forEach(item -> System.out.println("- " + item.getInfo()));

        System.out.println("\n2. Атауы бойынша іздеу");
        system.getCatalog().searchByTitle("Абай").forEach(item -> System.out.println("- " + item.getInfo()));

        LocalDate issueDate = LocalDate.of(2026, 11, 1);
        LocalDate dueDate = issueDate.plusDays(14);
        Loan loan = system.issueItem(book.getId(), reader.getId(), issueDate, dueDate);
        System.out.println("\n3. Кітап берілді\n- " + loan);

        System.out.println("\n4. Бір материалды екі рет беруден қорғау");
        try {
            system.issueItem(book.getId(), reader.getId(), issueDate, dueDate);
        } catch (ItemUnavailableException exception) {
            System.out.println("- Күтілген қате: " + exception.getMessage());
        }

        Optional<Fine> fine = system.returnItem(loan.getLoanId(), dueDate.plusDays(3));
        System.out.println("\n5. Кешіктіріп қайтару");
        System.out.println("- " + fine.<String>map(Fine::toString).orElse("Айыппұл жоқ"));
        System.out.println("- Кітап қайта қолжетімді: " + book.isAvailable());
        System.out.println("- Журнал қолжетімді: " + magazine.isAvailable());

        System.out.println("\n=== Демонстрация сәтті аяқталды ===");
    }
}
