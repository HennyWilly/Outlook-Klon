package de.outlook_klon.dao;

/**
 *
 * @author Hendrik Karwanni
 */
public class DAOException extends Exception {

    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
