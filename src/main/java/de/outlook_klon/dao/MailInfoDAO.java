package de.outlook_klon.dao;

import de.outlook_klon.logik.mailclient.MailInfo;

/**
 * Diese Interface stellt Methoden zum Zugriff auf die {@link MailInfo}-Objekte
 * her, die auf einer Datenebene persistiert wurden.
 *
 * @author Hendrik Karwanni
 */
public interface MailInfoDAO {

    /**
     * Lese das {@link MailInfo}-Objekt aus der übergebenen Datei
     *
     * @param id ID des Mail-Objekts
     * @param path Enthällt den Pfad, in dem das Objekt liegt
     * @return Deserialisiertes {@link MailInfo}-Objekt
     * @throws de.outlook_klon.dao.DAOException wenn ein Fehler beim Laden der
     * MailInfo auftritt
     */
    MailInfo loadMailInfo(String id, String path) throws DAOException;

    /**
     * Speichert die übergebene {@link MailInfo} am übergebenen Pfad
     *
     * @param info Zu speichernde {@link MailInfo}
     * @param path Pfad, in dem das Objekt gespeichert werden soll
     * @throws de.outlook_klon.dao.DAOException wenn ein Fehler beim Speichern
     * der MailInfo auftritt
     */
    void saveMailInfo(MailInfo info, String path) throws DAOException;

    /**
     * Löscht die gespeicherte Datei der übergebenen {@link MailInfo}
     *
     * @param id ID der zu löschenden Nachricht
     * @param path Pfad zur zu löschenden Datei
     * @throws de.outlook_klon.dao.DAOException wenn ein Fehler beim Löschen der
     * MailInfo auftritt
     */
    void deleteMailInfo(String id, String path) throws DAOException;
}
