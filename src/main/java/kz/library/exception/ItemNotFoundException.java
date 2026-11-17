package kz.library.exception;

import java.io.Serial;

public class ItemNotFoundException extends LibraryException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ItemNotFoundException(String message) {
        super(message);
    }
}
