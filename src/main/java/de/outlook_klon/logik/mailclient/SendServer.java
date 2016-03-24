package de.outlook_klon.logik.mailclient;

import java.io.File;
import java.io.IOException;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * Abstrakte Basisklasse für alle Mailserver, über die Mails gesendet werden
 * können. Stellt grundlegende Funtionen zum Versenden von Mails bereit.
 * 
 * @author Hendrik Karwanni
 */
public abstract class SendServer extends MailServer {
	private static final long serialVersionUID = -4191787147022537178L;

	/**
	 * Wird von abgeleiteten Klassen aufgerufen, um interne Attribute zu
	 * initialisieren
	 * 
	 * @param settings
	 *            Einstellungen zum Verbindungsaufbau
	 * @param serverTyp
	 *            Beschreibender String zum Servertyp
	 */
	protected SendServer(final ServerSettings settings, final String serverTyp) {
		super(settings, serverTyp);
	}

	/**
	 * Sendet eine E-Mail über den aktuellen Server. Die Implementierung des
	 * Vorgangs ist vom Serverprotokoll abhängig
	 * 
	 * @param user
	 *            Benutzername des Senders
	 * @param passwd
	 *            Passwort des Senders
	 * @param from
	 *            Anzeigename des Senders
	 * @param to
	 *            Ziele der Mail
	 * @param cc
	 *            CCs der Mail
	 * @param subject
	 *            Betreff der Mail
	 * @param text
	 *            Text der Mail
	 * @return Gibt die gesendete Mail zurück
	 * @throws MessagingException
	 *             Tritt auf, wenn das Senden der Mail fehlschlägt
	 * @throws IOException
	 *             Tritt auf, wenn einer der zu sendenden Anhänge nicht gefunden
	 *             wurde
	 */
	public abstract Message sendeMail(String user, String passwd, Address from, Address[] to,
			Address[] cc, String subject, String text, String format, File[] attachment)
			throws MessagingException;
}
