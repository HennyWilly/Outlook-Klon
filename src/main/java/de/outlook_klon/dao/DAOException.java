package de.outlook_klon.dao;

/**
 * Ausnahme f�r Data Access Objekte
 *
 * @author Hendrik Karwanni
 */
public class DAOException extends Exception {

    /**
     * Erstellt eine neue Ausnahme mit der �bergebenen detailierten Nachricht.
     *
     * @param message Die detailierte Nachricht der Ausnamhe
     */
    public DAOException(String message) {
        super(message);
    }

    /**
     * Erstellt eine neue Ausnahme mit der �bergebenen detailierten Nachricht
     * und Ursache.
     *
     * @param message Die detailierte Nachricht der Ausnamhe
     * @param cause Die Ursache der Ausnahme
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
