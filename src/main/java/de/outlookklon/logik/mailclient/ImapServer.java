package de.outlookklon.logik.mailclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Properties;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * Diese Klasse stellt einen IMAP-Server dar.
 *
 * @author Hendrik Karwanni
 */
public class ImapServer extends InboxServer {

    private static final long serialVersionUID = 3401491699856582843L;

    /**
     * Erstellt eine neue Instanz eines IMAP-Servers mit den übergebenen
     * Einstellungen
     *
     * @param settings Einstellungen zur Serververbindung
     */
    @JsonCreator
    public ImapServer(
            @JsonProperty("settings") ServerSettings settings) {
        super(settings, "IMAP");
    }

    @Override
    protected Properties getProperties() {
        final Properties props = new Properties();

        props.put("mail.imap.host", settings.getHost());
        props.put("mail.imap.port", Integer.toString(settings.getPort()));
        props.put("mail.imap.auth", Boolean.toString(true));

        if (settings.getConnectionSecurity() == ConnectionSecurity.SSL_TLS) {
            props.put("mail.imap.ssl.enable", Boolean.toString(true));
        }

        return props;
    }

    @Override
    public boolean supportsMultipleFolders() {
        return true;
    }

    @Override
    public Store getMailStore(final String user, final String passwd) throws NoSuchProviderException {
        final Session session = getSession(new StandardAuthenticator(user, passwd));

        Store store;
        if (settings.getConnectionSecurity() == ConnectionSecurity.SSL_TLS) {
            store = session.getStore("imaps");
        } else {
            store = session.getStore("imap");
        }

        return store;
    }
}
