package de.outlook_klon.logik.mailclient;

/**
 * Die Aufzählung gibt die unterstützten Arten der Authentifizierung an einem
 * Server an
 *
 * @author Hendrik Karwanni
 */
public enum Authentifizierungsart {

    /**
     * Die Standard-Authentifizierungsmethode für Mail-Server.
     */
    NORMAL,
    /**
     * Ein verteilter Authentifizierungsdienst für offene und unsichere
     * Computernetze.
     */
    KERBEROS,
    /**
     * Ein Authentifizierungsdienst, vornehmlich für Microsoft-Produkte.
     */
    NTLM
}
