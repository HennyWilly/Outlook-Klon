package de.outlookklon.dao;

import de.outlookklon.logik.mailclient.MailInfo;

/**
 * Diese Interface stellt Methoden zum Zugriff auf die {@link MailInfo}-Objekte
 * her, die auf einer Datenebene persistiert wurden.
 *
 * @author Hendrik Karwanni
 */
public interface MailInfoDAO {

    /**
     * Lese das {@link MailInfo}-Objekt aus der �bergebenen Datei
     *
     * @param id ID des Mail-Objekts
     * @param path Enth�llt den Pfad, in dem das Objekt liegt
     * @return Deserialisiertes {@link MailInfo}-Objekt
     * @throws de.outlookklon.dao.DAOException wenn ein Fehler beim Laden der
     * MailInfo auftritt
     */
    MailInfo loadMailInfo(String id, String path) throws DAOException;

    /**
     * Speichert die �bergebene {@link MailInfo} am �bergebenen Pfad
     *
     * @param info Zu speichernde {@link MailInfo}
     * @param path Pfad, in dem das Objekt gespeichert werden soll
     * @throws de.outlookklon.dao.DAOException wenn ein Fehler beim Speichern
     * der MailInfo auftritt
     */
    void saveMailInfo(MailInfo info, String path) throws DAOException;

    /**
     * L�scht die gespeicherte Datei der �bergebenen {@link MailInfo}
     *
     * @param id ID der zu l�schenden Nachricht
     * @param path Pfad zur zu l�schenden Datei
     * @throws de.outlookklon.dao.DAOException wenn ein Fehler beim L�schen der
     * MailInfo auftritt
     */
    void deleteMailInfo(String id, String path) throws DAOException;
}
