package kz.library.exception;

import java.io.Serial;

public class ItemUnavailableException extends LibraryException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ItemUnavailableException(String message) {
        super(message);
    }
}
