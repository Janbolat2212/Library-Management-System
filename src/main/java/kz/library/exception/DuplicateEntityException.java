package kz.library.exception;

import java.io.Serial;

public class DuplicateEntityException extends LibraryException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateEntityException(String message) {
        super(message);
    }
}
