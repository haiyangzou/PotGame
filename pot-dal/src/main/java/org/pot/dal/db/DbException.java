package org.pot.dal.db;

public class DbException extends RuntimeException {
    public DbException(Throwable cause) {
        super(cause);
    }

    public DbException(String message) {
        super(message);
    }

    public DbException(String message, Throwable cause) {
        super(message, cause);
    }
}
