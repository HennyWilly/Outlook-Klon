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
     * Die Hauptempf�nger einer Mail
     */
    TO,
    /**
     * Die Sekund�rempf�nger einer Mail
     */
    CC,
    /**
     * Die Anh�nge einer Mail
     */
    ATTACHMENT
}
