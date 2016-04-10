package de.outlookklon.logik;

/**
 * Eine Auﬂnahme, die beim Initialisieren der user-Instanz auftreten kann.
 *
 * @author Hendrik Karwanni
 */
public class UserException extends RuntimeException {

    /**
     * Erstellt eine neue UserException.
     *
     * @param message Nachricht der Exception
     */
    public UserException(String message) {
        super(message);
    }

    /**
     * Erstellt eine neue UserException.
     *
     * @param message Nachricht der Exception
     * @param cause Ursprung der Auﬂnahme
     */
    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}
