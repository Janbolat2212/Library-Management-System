package kz.library.storage;

import kz.library.service.LibrarySystem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileStorage {
    private FileStorage() {
    }

    public static LibrarySystem loadOrCreate(Path file) throws IOException {
        if (!Files.exists(file)) {
            return new LibrarySystem();
        }
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(file))) {
            Object value = input.readObject();
            if (!(value instanceof LibrarySystem system)) {
                throw new IOException("Сақталған файл форматы дұрыс емес: " + file);
            }
            return system;
        } catch (ClassNotFoundException exception) {
            throw new IOException("Сақталған кластарды оқу мүмкін емес.", exception);
        }
    }

    public static void save(LibrarySystem system, Path file) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(file))) {
            output.writeObject(system);
        }
    }
}
