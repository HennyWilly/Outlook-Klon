package de.outlookklon.logik.mailclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Properties;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Diese Klasse stellt einen Simple-Mail-Transport-Server(SMTP) dar.
 *
 * @author Hendrik Karwanni
 */
public class SmtpServer extends OutboxServer {

    private static final long serialVersionUID = -4486714062786025360L;

    private static final Logger LOGGER = LoggerFactory.getLogger(SmtpServer.class);

    /**
     * Erstellt eine neue Instanz eines SMTP-Servers mit den �bergebenen
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
        final int port = settings.getPort();
        final ConnectionSecurity sicherheit = settings.getConnectionSecurity();
        final Properties props = new Properties();

        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.auth", "true");
        if (sicherheit == ConnectionSecurity.STARTTLS) {
            props.put("mail.smtp.starttls.enable", "true");
        } else if (sicherheit == ConnectionSecurity.SSL_TLS) {
            props.put("mail.smtp.socketFactory.port", port);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
        }

        return props;
    }

    @Override
    public Message sendeMail(final String user, final String password, MailInfo mailToSend)
            throws MessagingException {
        final Session session = getSession(new StandardAuthenticator(user, password));

        Transport transport;
        final ConnectionSecurity security = settings.getConnectionSecurity();
        if (security == ConnectionSecurity.SSL_TLS) {
            transport = session.getTransport("smtps");
        } else {
            transport = session.getTransport("smtp");
        }

        final Message mail = createMessage(mailToSend, session);
        try {
            transport.connect(settings.getHost(), settings.getPort(), user, password);
            transport.sendMessage(mail, mail.getAllRecipients());
        } finally {
            transport.close();
        }

        mail.setFlag(Flags.Flag.SEEN, true);

        return mail;
    }

    @Override
    public boolean checkLogin(final String benutzername, final String passwort) {
        boolean result = true;

        final String host = settings.getHost();
        final int port = settings.getPort();
        final ConnectionSecurity security = settings.getConnectionSecurity();

        final Session session = getSession(new StandardAuthenticator(benutzername, passwort));

        Transport transport = null;
        try {
            if (security == ConnectionSecurity.SSL_TLS) {
                transport = session.getTransport("smtps");
            } else {
                transport = session.getTransport("smtp");
            }

            transport.connect(host, port, benutzername, passwort);
        } catch (MessagingException ex) {
            LOGGER.error("Could not get transport object", ex);
            result = false;
        } finally {
            if (transport != null && transport.isConnected()) {
                try {
                    transport.close();
                } catch (MessagingException ex) {
                    LOGGER.error("Could not close transport object", ex);
                }
            }
        }

        return result;
    }
}