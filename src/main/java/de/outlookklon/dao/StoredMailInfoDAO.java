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
     * Lese das {@link StoredMailInfo}-Objekt aus der �bergebenen Datei
     *
     * @param id ID des Mail-Objekts
     * @param path Enth�llt den Pfad, in dem das Objekt liegt
     * @return Deserialisiertes {@link StoredMailInfo}-Objekt
     * @throws de.outlookklon.dao.DAOException wenn ein Fehler beim Laden der
 StoredMailInfo auftritt
     */
    StoredMailInfo loadStoredMailInfo(String id, String path) throws DAOException;

    /**
     * Speichert die �bergebene {@link StoredMailInfo} am �bergebenen Pfad
     *
     * @param info Zu speichernde {@link StoredMailInfo}
     * @param path Pfad, in dem das Objekt gespeichert werden soll
     * @throws de.outlookklon.dao.DAOException wenn ein Fehler beim Speichern
 der StoredMailInfo auftritt
     */
    void saveStoredMailInfo(StoredMailInfo info, String path) throws DAOException;

    /**
     * L�scht die gespeicherte Datei der �bergebenen {@link StoredMailInfo}
     *
     * @param id ID der zu l�schenden Nachricht
     * @param path Pfad zur zu l�schenden Datei
     * @throws de.outlookklon.dao.DAOException wenn ein Fehler beim L�schen der
 StoredMailInfo auftritt
     */
    void deleteStoredMailInfo(String id, String path) throws DAOException;
}
