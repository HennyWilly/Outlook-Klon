package de.outlookklon.logik.mailclient;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import lombok.NonNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Abstrakte Basisklasse für alle Mailserver. Stellt grundlegende Funktionen für
 * alle Servertypen bereit.
 *
 * @author Hendrik Karwanni
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class MailServer implements Serializable {

    private static final long serialVersionUID = -6369803776352038195L;

    /**
     * Attribut, das die nötigen Einstellungen zum Aufbau einer Verbindung zu
     * einem Mailserver enthällt
     */
    protected final ServerSettings settings;

    /**
     * Attribut, das die Stringdarstellung des Servertyps enthällt
     */
    protected final String serverType;

    /**
     * Dient zur Authentifikation mit einem Benutzernamen und Passwort
     */
    protected class StandardAuthenticator extends Authenticator {

        private final String userName;
        private final String password;

        /**
         * Erstellt eine neue Instanz des StandardAuthenticators
         *
         * @param userName Verwendeter Benutzername
         * @param password Verwendendetes Passwort
         */
        public StandardAuthenticator(final String userName, final String password) {
            super();

            this.userName = userName;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, password);
        }
    }

    /**
     * Wird von abgeleiteten Klassen aufgerufen, um interne Attribute zu
     * initialisieren
     *
     * @param settings Einstellungen zum Verbindungsaufbau
     * @param serverType Beschreibender String zum Servertyp
     */
    protected MailServer(@NonNull final ServerSettings settings, @NonNull final String serverType) {
        if (serverType.trim().isEmpty()) {
            throw new NullPointerException("Server type is empty");
        }

        this.settings = settings;
        this.serverType = serverType;
    }

    /**
     * Prüft, ob man sich mit den übergebenen Login-Daten an dem bekannten
     * Server anmelden kann.
     *
     * @param userName Anmeldename des Benutzers
     * @param password Passwort des Benutzers
     * @return true, wenn die Anmeldedaten korrekt waren; sonst false
     */
    public abstract boolean checkLogin(String userName, String password);

    /**
     * Gibt das <code>Properties</code>-Objekt zurück, das für den Zugriff über
     * ein bestimmtes Protokoll konfiguriert ist
     *
     * @return <code>Properties</code>-Objekt
     */
    protected abstract Properties getProperties();

    /**
     * Erstellt aus dem übergebenen <code>Authenticator</code> und den intern
     * erzeugten <code>Properties</code> ein <code>Session</code>-Objekt
     *
     * @param auth Zu verwendender <code>Authenticator</code>
     * @return <code>Session</code>-Objekt
     */
    protected Session getSession(Authenticator auth) {
        Properties props = getProperties();

        return Session.getInstance(props, auth);
    }

    /**
     * Gibt den beschreibenden String zum Servertyp zurück
     *
     * @return Beschreibender String zum Servertyp
     */
    public String getServerType() {
        return serverType;
    }

    /**
     * Gibt die <code>ServerSettings</code>-Instanz zur Verbindung zum Server
     * zurück
     *
     * @return <code>ServerSettings</code>-Instanz zum Server
     */
    public ServerSettings getSettings() {
        return settings;
    }

    @Override
    public String toString() {
        return String.format("%s:%d", settings.getHost(), settings.getPort());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj.getClass().equals(getClass()))) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        MailServer other = (MailServer) obj;
        return new EqualsBuilder()
                .append(getServerType(), other.getServerType())
                .append(getSettings(), other.getSettings())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getServerType())
                .append(getSettings())
                .toHashCode();
    }
}
