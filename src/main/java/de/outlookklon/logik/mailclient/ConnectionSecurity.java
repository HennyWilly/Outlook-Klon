package de.outlookklon.logik.mailclient;

/**
 * Diese Aufz�hlung gibt die verschiedenen Arten der verschl�sselten
 * Kommunikation an
 *
 * @author Hendrik Karwanni
 */
public enum ConnectionSecurity {
    /**
     * Verwende keine Verschl�sselung
     */
    NONE,
    /**
     * Verwende SSL/TLS als Verschl�sselung
     */
    SSL_TLS,
    /**
     * Verwende STARTTLS als Verschl�sselung
     */
    STARTTLS
}