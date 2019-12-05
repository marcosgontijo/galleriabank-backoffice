package com.webnowbr.siscoat.db.dao;

/**
 * This exception is thrown whenever there is a database connection error (e.g. DB not active).
 * @author Walter Wong
 */
public class DBConnectionException extends RuntimeException {

    /** serial id. */
    private static final long serialVersionUID = 1L;
    /** Mensagem de erro. */
    private String message;

    /**
     * Construtor default.
     */
    public DBConnectionException() {
        super();
    }

    /**
     * Construtor.
     * @param pMessage - mensagem de erro.
     */
    public DBConnectionException(final String pMessage) {
        super();
        this.message = pMessage;
    }

    /**
     * Construtor.
     * @param cause - throwable.
     */
    public DBConnectionException(final Throwable cause) {
        super(cause);
    }

    /**
     * Retorna a mensagem de erro.
     * @return String - mensagem.
     */
    @Override
    public final String getMessage() {
        return message;
    }

    /**
     * Define a mensagem de erro.
     * @param pMessage - mensagem
     */
    public final void setMessage(final String pMessage) {
        this.message = pMessage;
    }

}
