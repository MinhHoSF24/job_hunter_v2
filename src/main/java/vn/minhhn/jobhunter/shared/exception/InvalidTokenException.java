package vn.minhhn.jobhunter.shared.exception;

import java.io.Serial;

public class InvalidTokenException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidTokenException(String message) {
        super(message);
    }
}
