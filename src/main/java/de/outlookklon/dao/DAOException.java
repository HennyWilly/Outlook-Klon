package de.outlookklon.dao;

import lombok.NonNull;

/**
 * Ausnahme für Data Access Objekte
 *
 * @author Hendrik Karwanni
 */
public class DAOException extends Exception {

    /**
     * Erstellt eine neue Ausnahme mit der übergebenen detailierten Nachricht.
     *
     * @param message Die detailierte Nachricht der Ausnamhe
     */
    public DAOException(@NonNull String message) {
        super(message);
    }

    /**
     * Erstellt eine neue Ausnahme mit der übergebenen detailierten Nachricht
     * und Ursache.
     *
     * @param message Die detailierte Nachricht der Ausnamhe
     * @param cause Die Ursache der Ausnahme
     */
    public DAOException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
