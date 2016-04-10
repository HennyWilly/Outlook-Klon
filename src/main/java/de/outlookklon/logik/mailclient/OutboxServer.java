package de.outlookklon.logik.mailclient;

import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * Abstrakte Basisklasse für alle Mailserver, über die Mails gesendet werden
 * können. Stellt grundlegende Funtionen zum Versenden von Mails bereit.
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
    public abstract Message sendeMail(String user, String password, MailInfo mailToSend)
            throws MessagingException;
}
