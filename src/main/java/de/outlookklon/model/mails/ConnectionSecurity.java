package de.outlookklon.model.mails;

/**
 * Diese Aufzählung gibt die verschiedenen Arten der verschlüsselten
 * Kommunikation an
 *
 * @author Hendrik Karwanni
 */
public enum ConnectionSecurity {
    /**
     * Verwende keine Verschlüsselung
     */
    NONE,
    /**
     * Verwende SSL/TLS als Verschlüsselung
     */
    SSL_TLS,
    /**
     * Verwende STARTTLS als Verschlüsselung
     */
    STARTTLS
}
