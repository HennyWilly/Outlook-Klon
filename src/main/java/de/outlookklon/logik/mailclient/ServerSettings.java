package de.outlookklon.logik.mailclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.NonNull;

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

    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;

    @JsonProperty("host")
    private String host;

    @JsonProperty("port")
    private int port;

    @JsonProperty("connectionSecurity")
    private ConnectionSecurity connectionSecurity;

    @JsonProperty("authentificationType")
    private AuthentificationType authentificationType;

    /**
     * Erstellt eine neue Instanz der Klasse mit den �bergebenen Werten
     *
     * @param host Hostname/Ip der Zielservers
     * @param port Zielport des Servers
     * @param connectionSecurity Art der unterst�tzten Verschl�sselung des
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
            @JsonProperty("host") @NonNull String host,
            @JsonProperty("port") int port,
            @JsonProperty("connectionSecurity") ConnectionSecurity connectionSecurity,
            @JsonProperty("authentificationType") AuthentificationType authentificationType) {
        if (port < MIN_PORT || port > MAX_PORT) {
            throw new IllegalArgumentException(
                    "Der �bergebene Wert ist kein zul�ssiger Port");
        }

        this.host = host;
        this.port = port;
        this.connectionSecurity = connectionSecurity;
        this.authentificationType = authentificationType;
    }

    /**
     * Getter f�r den Hostnamen bzw. die IP des Zielservers
     *
     * @return Hostnamen bzw. IP
     */
    public String getHost() {
        return host;
    }

    /**
     * Getter f�r den Zielport des Servers
     *
     * @return Port
     */
    public int getPort() {
        return port;
    }

    /**
     * Getter f�r die Art der ConnectionSecurity zum Server
     *
     * @return Verschl�sselungsart
     */
    public ConnectionSecurity getConnectionSecurity() {
        return connectionSecurity;
    }

    /**
     * Getter f�r die Art der Authentifizierung am Server
     *
     * @return AuthentificationType
     */
    public AuthentificationType getAuthentificationType() {
        return authentificationType;
    }

    @Override
    public String toString() {
        return String.format("%s:%d", host, port);
    }
}
