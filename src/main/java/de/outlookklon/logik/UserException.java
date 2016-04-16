package de.outlookklon.logik;

import lombok.NonNull;

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
    public UserException(@NonNull String message) {
        super(message);
    }

    /**
     * Erstellt eine neue UserException.
     *
     * @param message Nachricht der Exception
     * @param cause Ursprung der Auﬂnahme
     */
    public UserException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
