package de.outlook_klon.logik.mailclient;

import javax.mail.Message;

/**
 * Diese Klasse stellt einen Simple-Mail-Transport-Server(SMTP) dar.
 * 
 * @author Hendrik Karwanni
 */
public class SmtpServer extends SendServer{

	/**
	 * Erstellt eine neue Instanz eines SMTP-Servers mit den übergebenen Einstellungen
	 * @param settings Einstellungen zur Serververbindung
	 */
	public SmtpServer(ServerSettings settings) {
		super(settings);
	}

	@Override
	public void sendeMail(Message mail) {
		
	}
}
