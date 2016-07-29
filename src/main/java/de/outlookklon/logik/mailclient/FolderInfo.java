package de.outlookklon.logik.mailclient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Datenklasse zum Halten von abgefragten Informationen von Ordnern
 *
 * @author Hendrik Karwanni
 */
public class FolderInfo {

    private String name;
    private String path;
    private int numberUnread;

    /**
     * Ertsellt eine neue Instanz der Klasse OrdnerInfo mit den übergebenen
     * Werten
     *
     * @param name Name des Ordners
     * @param path Pfad innerhalb des MailStores
     * @param numberUnread Anzahl ungelesener Nachrichten
     */
    public FolderInfo(String name, String path, int numberUnread) {
        setName(name);
        setPath(path);
        setNumberUnread(numberUnread);
    }

    /**
     * Gibt den Namen des Ordners zurück
     *
     * @return Name des Ordners
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Namen des Ordners
     *
     * @param name Name des Ordners
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gibt den Pfad des Ordners innerhalb des MailStores zurück
     *
     * @return Pfad des Ordners
     */
    public String getPath() {
        return path;
    }

    /**
     * Setzt den Pfad des Ordners innerhalb des MailStores
     *
     * @param path Pfad des Ordners
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Gibt die Anzahl an ungelesenen Mails innerhalb des Ordners zurück
     *
     * @return Anzahl an ungelesenen Mails
     */
    public int getNumberUnread() {
        return numberUnread;
    }

    /**
     * Setzt die Anzahl an ungelesenen Mails innerhalb des Ordners
     *
     * @param numberUnread Anzahl an ungelesenen Mails
     */
    public void setNumberUnread(int numberUnread) {
        this.numberUnread = numberUnread;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof FolderInfo)) {
            return false;
        }
        if (this == other) {
            return true;
        }

        FolderInfo folder = (FolderInfo) other;
        return new EqualsBuilder()
                .append(getName(), folder.getName())
                .append(getPath(), folder.getPath())
                .append(getNumberUnread(), folder.getNumberUnread())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getName())
                .append(getPath())
                .append(getNumberUnread())
                .toHashCode();
    }
}
