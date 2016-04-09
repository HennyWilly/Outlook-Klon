package de.outlookklon.logik.mailclient;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

/**
 * Abstrakte Basisklasse f�r alle Mailserver. Stellt grundlegende Funktionen f�r
 * alle Servertypen bereit.
 *
 * @author Hendrik Karwanni
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class MailServer implements Serializable {

    private static final long serialVersionUID = -6369803776352038195L;

    /**
     * Attribut, das die n�tigen Einstellungen zum Aufbau einer Verbindung zu
     * einem Mailserver enth�llt
     */
    protected final ServerSettings settings;

    /**
     * Attribut, das die Stringdarstellung des Servertyps enth�llt
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
    protected MailServer(final ServerSettings settings, final String serverType) {
        if (settings == null) {
            throw new NullPointerException("Servereinstellungen wurden nicht instanziiert");
        }

        this.settings = settings;
        this.serverType = serverType;
    }

    /**
     * Pr�ft, ob man sich mit den �bergebenen Login-Daten an dem bekannten
     * Server anmelden kann.
     *
     * @param userName Anmeldename des Benutzers
     * @param password Passwort des Benutzers
     * @return true, wenn die Anmeldedaten korrekt waren; sonst false
     */
    public abstract boolean checkLogin(String userName, String password);

    /**
     * Gibt das <code>Properties</code>-Objekt zur�ck, das f�r den Zugriff �ber
     * ein bestimmtes Protokoll konfiguriert ist
     *
     * @return <code>Properties</code>-Objekt
     */
    protected abstract Properties getProperties();

    /**
     * Erstellt aus dem �bergebenen <code>Authenticator</code> und den intern
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
     * Gibt den beschreibenden String zum Servertyp zur�ck
     *
     * @return Beschreibender String zum Servertyp
     */
    public String getServerType() {
        return serverType;
    }

    /**
     * Gibt die <code>ServerSettings</code>-Instanz zur Verbindung zum Server
     * zur�ck
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
}
