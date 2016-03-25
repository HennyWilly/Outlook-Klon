package de.outlook_klon.logik.mailclient;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstrakte Basisklasse f�r alle Mailserver, �ber die Mails empfangen werden
 * k�nnen. Stellt grundlegende Funtionen zum Empfangen von Mails bereit.
 *
 * @author Hendrik Karwanni
 */
public abstract class EmpfangsServer extends MailServer {

    private static final long serialVersionUID = -6475925504329915182L;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmpfangsServer.class);

    /**
     * Ruft den protected-Konstruktor der Oberklasse auf
     *
     * @param settings Einstellungen zur Verbindung mit dem Server
     * @param serverTyp Beschreibender String zum Servertyp
     */
    protected EmpfangsServer(final ServerSettings settings, final String serverTyp) {
        super(settings, serverTyp);
    }

    /**
     * Gibt den <code>Store</code> zur�ck, der die E-Mails des Anwenders
     * enth�llt
     *
     * @param user Benutzername des Empf�ngers
     * @param passwd Passwort des Empf�ngers
     * @return <code>Store</code>-Objekt, �ber welches man auf die Mails
     * zugreifen kann
     */
    public abstract Store getMailStore(String user, String passwd) throws NoSuchProviderException;

    @Override
    public boolean pruefeLogin(final String benutzername, final String passwort) {
        boolean result = true;

        final String host = settings.getHost();
        final int port = settings.getPort();

        Store store = null;
        try {
            store = getMailStore(benutzername, passwort);
            store.connect(host, port, benutzername, passwort);
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
