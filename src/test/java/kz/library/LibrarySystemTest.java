package kz.library;

import kz.library.exception.ItemUnavailableException;
import kz.library.model.Fine;
import kz.library.model.Loan;
import kz.library.service.LibrarySystem;
import kz.library.storage.FileStorage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public final class LibrarySystemTest {
    private int passed;

    public static void main(String[] args) throws Exception {
        LibrarySystemTest tests = new LibrarySystemTest();
        tests.runAll();
    }

    private void runAll() throws Exception {
        testCatalogSearchAndPolymorphism();
        testIssueAndReturnWithFine();
        testUnavailableItemProtection();
        testPersistence();
        System.out.println("Барлық тест сәтті өтті: " + passed);
    }

    private void testCatalogSearchAndPolymorphism() {
        LibrarySystem system = createSystem();
        assertEquals(2, system.getCatalog().getItems().size(), "Каталог өлшемі");
        assertEquals(1, system.getCatalog().searchByTitle("абай").size(), "Атау бойынша іздеу");
        assertEquals(1, system.getCatalog().searchByAuthor("ӘУЕЗОВ").size(), "Автор бойынша іздеу");
        assertTrue(system.getCatalog().getItems().stream().allMatch(item -> !item.getInfo().isBlank()),
                "Полиморфты getInfo нәтижесі");
        passed++;
    }

    private void testIssueAndReturnWithFine() {
        LibrarySystem system = createSystem();
        LocalDate issueDate = LocalDate.of(2026, 11, 1);
        LocalDate dueDate = LocalDate.of(2026, 11, 15);
        Loan loan = system.issueItem(1, 1, issueDate, dueDate);

        assertTrue(!loan.getItem().isAvailable(), "Берілген материал қолжетімсіз болуы керек");
        assertEquals(1, system.getActiveLoans().size(), "Белсенді Loan саны");

        Fine fine = system.returnItem(loan.getLoanId(), dueDate.plusDays(3)).orElseThrow();
        assertEquals(3L, fine.getDaysLate(), "Кешіккен күн саны");
        assertEquals(300.0, fine.getAmount(), "Айыппұл сомасы");
        assertTrue(loan.getItem().isAvailable(), "Қайтарылған материал қолжетімді болуы керек");
        assertEquals(0, system.getActiveLoans().size(), "Қайтарудан кейін белсенді Loan саны");
        passed++;
    }

    private void testUnavailableItemProtection() {
        LibrarySystem system = createSystem();
        LocalDate issueDate = LocalDate.of(2026, 11, 1);
        system.issueItem(1, 1, issueDate, issueDate.plusDays(14));
        assertThrows(ItemUnavailableException.class,
                () -> system.issueItem(1, 1, issueDate, issueDate.plusDays(14)),
                "Бір материалды екі рет беру қорғалуы керек");
        passed++;
    }

    private void testPersistence() throws Exception {
        LibrarySystem original = createSystem();
        original.issueItem(1, 1, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 15));
        Path file = Files.createTempFile("library-system-test-", ".bin");
        try {
            FileStorage.save(original, file);
            LibrarySystem restored = FileStorage.loadOrCreate(file);
            assertEquals(2, restored.getCatalog().getItems().size(), "Сақталған каталог");
            assertEquals(1, restored.getActiveLoans().size(), "Сақталған белсенді Loan");
            assertTrue(!restored.getCatalog().findById(1).isAvailable(), "Сақталған материал күйі");
        } finally {
            Files.deleteIfExists(file);
        }
        passed++;
    }

    private LibrarySystem createSystem() {
        LibrarySystem system = new LibrarySystem();
        var author = system.registerAuthor("Мұхтар Әуезов");
        system.addBook("Абай жолы", 2019, "978-601-01-0002-8", author.getId());
        system.addMagazine("Ғылым және өмір", 2026, 11);
        system.registerReader("Айдана Серікқызы");
        return system;
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError("%s: күтілді=%s, нақты=%s".formatted(message, expected, actual));
        }
    }

    private static void assertThrows(Class<? extends Throwable> expected, Runnable action, String message) {
        try {
            action.run();
        } catch (Throwable throwable) {
            if (expected.isInstance(throwable)) {
                return;
            }
            throw new AssertionError(message + ": басқа қате алынды " + throwable, throwable);
        }
        throw new AssertionError(message + ": қате тасталмады");
    }
}
