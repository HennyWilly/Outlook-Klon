package de.outlook_klon.logik.mailclient;

import javax.mail.MessagingException;

/**
 * Abstrakte Basisklasse für alle Mailserver, über die Mails gesendet werden können.
 * Stellt grundlegende Funtionen zum Versenden von Mails bereit.
 * 
 * @author Hendrik Karwanni
 */
public abstract class SendServer extends MailServer {
	protected SendServer(ServerSettings settings) {
		super(settings);
	}

	/**
	 * Sendet eine E-Mail über den aktuellen Server.
	 * Die Implementierung des Vorgangs ist vom Serverprotokoll abhängig 
	 * @param user Benutzername des Senders
	 * @param pw Passwort des Senders
	 * @param from E-Mail-Adresse des Senders
	 * @param to Ziele der Mail
	 * @param cc CCs der Mail
	 * @param subject Betreff der Mail
	 * @param text Text der Mail
	 * @throws MessagingException Tritt auf, wenn das Senden der Mail fehlschlägt
	 */
	public abstract void sendeMail(String user, String pw, String from, String[] to, String[] cc, String subject, String text) throws MessagingException;
}
