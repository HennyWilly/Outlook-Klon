package de.outlookklon.application.mailclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.outlookklon.model.mails.ConnectionSecurity;
import de.outlookklon.model.mails.ServerSettings;
import java.util.Properties;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;

/**
 * Diese Klasse stellt einen Simple-Mail-Transport-Server(SMTP) dar.
 *
 * @author Hendrik Karwanni
 */
public class SmtpServer extends OutboxServer {

    private static final long serialVersionUID = -4486714062786025360L;

    /**
     * Erstellt eine neue Instanz eines SMTP-Servers mit den übergebenen
     * Einstellungen
     *
     * @param settings Einstellungen zur Serververbindung
     */
    @JsonCreator
    public SmtpServer(
            @JsonProperty("settings") ServerSettings settings) {
        super(settings, "SMTP");
    }

    @Override
    protected Properties getProperties() {
        final ConnectionSecurity security = settings.getConnectionSecurity();

        final Properties props = new Properties();
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.auth", "true");
        if (security == ConnectionSecurity.STARTTLS) {
            props.put("mail.smtp.starttls.enable", Boolean.toString(true));
        } else if (security == ConnectionSecurity.SSL_TLS) {
            props.put("mail.smtp.socketFactory.port", Integer.toString(settings.getPort()));
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", Boolean.toString(false));
        }

        return props;
    }

    @Override
    public Transport getTransport(final String user, final String password) throws NoSuchProviderException {
        final Session session = getSession(new StandardAuthenticator(user, password));
        final ConnectionSecurity security = settings.getConnectionSecurity();

        if (security == ConnectionSecurity.SSL_TLS) {
            return session.getTransport("smtps");
        }

        return session.getTransport("smtp");
    }
}
