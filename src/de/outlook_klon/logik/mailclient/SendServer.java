package de.outlook_klon.logik.mailclient;

import javax.mail.Message;

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
	 * @param mail Die zu sendende Nachricht
	 */
	public abstract void sendeMail(Message mail);
}
