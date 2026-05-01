package org.aussiebox.starexpress.exception;

public class MissingJsonFieldException extends RuntimeException {
    public MissingJsonFieldException(String message) {
        super(message);
    }
}
