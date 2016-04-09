package de.outlookklon.dao;

import de.outlookklon.logik.mailclient.StoredMailInfo;

/**
 * Diese Interface stellt Methoden zum Zugriff auf die {@link StoredMailInfo}-Objekte
 * her, die auf einer Datenebene persistiert wurden.
 *
 * @author Hendrik Karwanni
 */
public interface StoredMailInfoDAO {

    /**
     * Lese das {@link StoredMailInfo}-Objekt aus der übergebenen Datei
     *
     * @param id ID des Mail-Objekts
     * @param path Enthällt den Pfad, in dem das Objekt liegt
     * @return Deserialisiertes {@link StoredMailInfo}-Objekt
     * @throws de.outlookklon.dao.DAOException wenn ein Fehler beim Laden der
 StoredMailInfo auftritt
     */
    StoredMailInfo loadMailInfo(String id, String path) throws DAOException;

    /**
     * Speichert die übergebene {@link StoredMailInfo} am übergebenen Pfad
     *
     * @param info Zu speichernde {@link StoredMailInfo}
     * @param path Pfad, in dem das Objekt gespeichert werden soll
     * @throws de.outlookklon.dao.DAOException wenn ein Fehler beim Speichern
 der StoredMailInfo auftritt
     */
    void saveMailInfo(StoredMailInfo info, String path) throws DAOException;

    /**
     * Löscht die gespeicherte Datei der übergebenen {@link StoredMailInfo}
     *
     * @param id ID der zu löschenden Nachricht
     * @param path Pfad zur zu löschenden Datei
     * @throws de.outlookklon.dao.DAOException wenn ein Fehler beim Löschen der
 StoredMailInfo auftritt
     */
    void deleteMailInfo(String id, String path) throws DAOException;
}
