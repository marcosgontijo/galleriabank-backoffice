package com.webnowbr.siscoat.db.dao;

/**
 * This class represents an exception that occurred due to a access to the data
 * access object layer.
 * @author Walter Wong
 */
public class DAOException extends RuntimeException {
    /** Serial id. */
    private static final long serialVersionUID = 1L;

    /**
     * Construtor default.
     */
    public DAOException() {
        super();
    }

    /**
     * Construtor.
     * @param message - mensagem de erro.
     */
    public DAOException(final String message) {
        super(message);
    }

    /**
     * Construtor.
     * @param cause - Throwable
     */
    public DAOException(final Throwable cause) {
        super(cause);
    }

    /**
     * Construtor.
     * @param message - mensagem.
     * @param cause - throwable.
     */
    public DAOException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
