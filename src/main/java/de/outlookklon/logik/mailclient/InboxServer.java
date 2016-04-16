package de.outlookklon.logik.mailclient;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstrakte Basisklasse für alle Mailserver, über die Mails empfangen werden
 * können. Stellt grundlegende Funtionen zum Empfangen von Mails bereit.
 *
 * @author Hendrik Karwanni
 */
public abstract class InboxServer extends MailServer {

    private static final long serialVersionUID = -6475925504329915182L;

    private static final Logger LOGGER = LoggerFactory.getLogger(InboxServer.class);

    /**
     * Ruft den protected-Konstruktor der Oberklasse auf
     *
     * @param settings Einstellungen zur Verbindung mit dem Server
     * @param serverType Beschreibender String zum Servertyp
     */
    protected InboxServer(final ServerSettings settings, final String serverType) {
        super(settings, serverType);
    }

    /**
     * Gibt den <code>Store</code> zurück, der die E-Mails des Anwenders
     * enthällt
     *
     * @param user Benutzername des Empfängers
     * @param password Passwort des Empfängers
     * @return <code>Store</code>-Objekt, über welches man auf die Mails
     * zugreifen kann
     * @throws javax.mail.NoSuchProviderException wenn der Provider des Stores
     * nicht gefunden wurde
     */
    public abstract Store getMailStore(String user, String password)
            throws NoSuchProviderException;

    /**
     * Gibt an, ob die Subclasse mehrerer Ordner (nicht nur INBOX) unterstützt.
     *
     * @return {@code true}, wenn mehrere Ordner unterstützt werden; sonst
     * {@code false}.
     */
    public abstract boolean supportsMultipleFolders();

    @Override
    public boolean checkLogin(final String username, final String password) {
        boolean result = true;

        final String host = settings.getHost();
        final int port = settings.getPort();

        Store store = null;
        try {
            store = getMailStore(username, password);
            store.connect(host, port, username, password);
        } catch (MessagingException ex) {
            LOGGER.error("Error while connecting to MailStore", ex);
            result = false;
        } finally {
            if (store != null && store.isConnected()) {
                try {
                    store.close();
                } catch (MessagingException ex) {
                    LOGGER.error("Error while closing MailStore", ex);
                }
            }
        }

        return result;
    }
}
