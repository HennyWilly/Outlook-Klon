package de.outlook_klon.logik.mailclient;

import java.io.File;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * Abstrakte Basisklasse f�r alle Mailserver, �ber die Mails gesendet werden
 * k�nnen. Stellt grundlegende Funtionen zum Versenden von Mails bereit.
 *
 * @author Hendrik Karwanni
 */
public abstract class OutboxServer extends MailServer {

    private static final long serialVersionUID = -4191787147022537178L;

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

    /**
     * Sendet eine E-Mail �ber den aktuellen Server. Die Implementierung des
     * Vorgangs ist vom Serverprotokoll abh�ngig
     *
     * @param user Benutzername des Senders
     * @param password Passwort des Senders
     * @param from Anzeigename des Senders
     * @param to Ziele der Mail
     * @param cc CCs der Mail
     * @param subject Betreff der Mail
     * @param text Text der Mail
     * @param format Format der Mail
     * @param attachment Anh�nge der Mail
     * @return Gibt die gesendete Mail zur�ck
     * @throws MessagingException Tritt auf, wenn das Senden der Mail
     * fehlschl�gt oder einer der zu sendenden Anh�nge nicht gefunden wurde
     */
    public abstract Message sendeMail(String user, String password, Address from, Address[] to,
            Address[] cc, String subject, String text, String format, File[] attachment)
            throws MessagingException;
}
