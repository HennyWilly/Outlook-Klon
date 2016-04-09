package de.outlookklon.logik.mailclient;

/**
 * Die Aufz�hlung gibt die unterst�tzten Arten der Authentifizierung an einem
 * Server an
 *
 * @author Hendrik Karwanni
 */
public enum AuthentificationType {

    /**
     * Die Standard-Authentifizierungsmethode f�r Mail-Server.
     */
    NORMAL,
    /**
     * Ein verteilter Authentifizierungsdienst f�r offene und unsichere
     * Computernetze.
     */
    KERBEROS,
    /**
     * Ein Authentifizierungsdienst, vornehmlich f�r Microsoft-Produkte.
     */
    NTLM
}
