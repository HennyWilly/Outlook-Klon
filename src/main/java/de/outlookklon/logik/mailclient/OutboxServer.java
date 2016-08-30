package de.outlookklon.logik.mailclient;

import de.outlookklon.logik.mailclient.javamail.ServiceWrapper;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstrakte Basisklasse für alle Mailserver, über die Mails gesendet werden
 * können. Stellt grundlegende Funtionen zum Versenden von Mails bereit.
 *
 * @author Hendrik Karwanni
 */
public abstract class OutboxServer extends MailServer {

    private static final long serialVersionUID = -4191787147022537178L;

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxServer.class);

    /**
     * Wird von abgeleiteten Klassen aufgerufen, um interne Attribute zu
     * initialisieren
     *
     * @param settings Einstellungen zum Verbindungsaufbau
     * @param serverType Beschreibender String zum Servertyp
     */
    protected OutboxServer(final ServerSettings settings, final String serverType) {
        super(settings, serverType);
    }

    public abstract Transport getTransport(final String user, final String passwd)
            throws NoSuchProviderException;

    /**
     * Sendet eine E-Mail über den aktuellen Server. Die Implementierung des
     * Vorgangs ist vom Serverprotokoll abhängig
     *
     * @param user Benutzername des Senders
     * @param password Passwort des Senders
     * @param mailToSend Objekt, das alle zu sendenden Daten enthält
     * @return Gibt die gesendete Mail zurück
     * @throws MessagingException Tritt auf, wenn das Senden der Mail
     * fehlschlägt oder einer der zu sendenden Anhänge nicht gefunden wurde
     */
    public Message sendMail(final String user, final String password, SendMailInfo mailToSend)
            throws MessagingException {

        final Message mail;
        try (ServiceWrapper<Transport> transportWrapper = new ServiceWrapper(getTransport(user, password))) {
            mail = mailToSend.createMessage(transportWrapper.getSession());

            Transport transport = transportWrapper.getService();
            transport.connect(settings.getHost(), settings.getPort(), user, password);
            transport.sendMessage(mail, mail.getAllRecipients());
        }

        mail.setFlag(Flags.Flag.SEEN, true);

        return mail;
    }

    @Override
    public boolean checkLogin(final String userName, final String password) {
        try (ServiceWrapper<Transport> transportWrapper = new ServiceWrapper(getTransport(userName, password))) {
            transportWrapper.getService().connect(settings.getHost(), settings.getPort(), userName, password);
        } catch (MessagingException ex) {
            LOGGER.error("Could not get transport object", ex);
            return false;
        }

        return true;
    }
}
