package de.outlookklon.logik.mailclient;

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
     * @param mailToSend Objekt, das alle zu sendenden Daten enth�lt
     * @return Gibt die gesendete Mail zur�ck
     * @throws MessagingException Tritt auf, wenn das Senden der Mail
     * fehlschl�gt oder einer der zu sendenden Anh�nge nicht gefunden wurde
     */
    public abstract Message sendMail(String user, String password, MailInfo mailToSend)
            throws MessagingException;
}
