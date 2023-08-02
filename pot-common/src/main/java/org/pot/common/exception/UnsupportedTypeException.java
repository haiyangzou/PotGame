package org.pot.common.exception;

public class UnsupportedTypeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnsupportedTypeException(final Class clazz) {
        super(customMessage(clazz.getName()));
    }

    public UnsupportedTypeException(final Class clazz, Throwable cause) {
        super(customMessage(clazz.getName()), cause);
    }

    private static String customMessage(final String message) {
        return "Unsupported Type(" + message + ")";
    }
}
