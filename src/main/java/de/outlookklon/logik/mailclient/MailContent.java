package de.outlookklon.logik.mailclient;

/**
 * Diese Auflistung stellt die verschiedenen Inhaltsarten einer Mail dar.
 *
 * @author Hendrik Karwanni
 */
public enum MailContent {

    /**
     * Die ID einer Mail
     */
    ID,
    /**
     * Der Status, ob eine Mail gelesen wurde
     */
    READ,
    /**
     * Der Betreff einer Mail
     */
    SUBJECT,
    /**
     * Der/Die Absender einer Mail
     */
    SENDER,
    /**
     * Das Versendedatum einer Mail
     */
    DATE,
    /**
     * Der Text einer Mail
     */
    TEXT,
    /**
     * Der Typ des Inhalts einer Mail
     */
    CONTENTTYPE,
    /**
     * Die Hauptempfänger einer Mail
     */
    TO,
    /**
     * Die Sekundärempfänger einer Mail
     */
    CC,
    /**
     * Die Anhänge einer Mail
     */
    ATTACHMENT
}
