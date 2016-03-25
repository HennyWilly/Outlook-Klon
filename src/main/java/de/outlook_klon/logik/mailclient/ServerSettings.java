package de.outlook_klon.logik.mailclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/*
 * SMTP 587
 * IMAP 220
 * POP3 110
 */
/**
 * Dies ist eine Datenklasse, die die Daten zur Verbindung mit einem Mailserver
 * speichert.
 *
 * @author Hendrik Karwanni
 */
public class ServerSettings implements Serializable {

    private static final long serialVersionUID = -2113634498937441789L;

    @JsonProperty("host")
    private String host;

    @JsonProperty("port")
    private int port;

    @JsonProperty("connectionSecurity")
    private Verbindungssicherheit connectionSecurity;

    @JsonProperty("authentificationType")
    private Authentifizierungsart authentificationType;

    /**
     * Erstellt eine neue Instanz der Klasse mit den übergebenen Werten
     *
     * @param host Hostname/Ip der Zielservers
     * @param port Zielport des Servers
     * @param connectionSecurity Art der unterstützten Verschlüsselung des
     * Servers
     * @param authentificationType Art der Authentifizierung an dem Server
     *
     * @throws NullPointerException Tritt auf, wenn <code>host</code> gleich
     * <code>null</code> ist.
     * @throws IllegalArgumentException Tritt auf, wenn <code>port</code> kein
     * korrekter Port ist.
     */
    @JsonCreator
    public ServerSettings(
            @JsonProperty("host") String host,
            @JsonProperty("port") int port,
            @JsonProperty("connectionSecurity") Verbindungssicherheit connectionSecurity,
            @JsonProperty("authentificationType") Authentifizierungsart authentificationType)
            throws NullPointerException, IllegalArgumentException {
        if (host == null) {
            throw new NullPointerException("Der Hostname darf nicht null sein");
        }
        if (port < 0 || port > 49151) {
            throw new IllegalArgumentException(
                    "Der übergebene Wert ist kein zulässiger Port");
        }

        this.host = host;
        this.port = port;
        this.connectionSecurity = connectionSecurity;
        this.authentificationType = authentificationType;
    }

    /**
     * Getter für den Hostnamen bzw. die IP des Zielservers
     *
     * @return Hostnamen bzw. IP
     */
    public String getHost() {
        return host;
    }

    /**
     * Getter für den Zielport des Servers
     *
     * @return Port
     */
    public int getPort() {
        return port;
    }

    /**
     * Getter für die Art der Verbindungssicherheit zum Server
     *
     * @return Verschlüsselungsart
     */
    public Verbindungssicherheit getConnectionSecurity() {
        return connectionSecurity;
    }

    /**
     * Getter für die Art der Authentifizierung am Server
     *
     * @return Authentifizierungsart
     */
    public Authentifizierungsart getAuthentificationType() {
        return authentificationType;
    }

    @Override
    public String toString() {
        return String.format("%s:%d", host, port);
    }
}
