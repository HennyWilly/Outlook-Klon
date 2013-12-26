package de.outlook_klon.logik.mailclient;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

/**
 * Abstrakte Basisklasse für alle Mailserver, über die Mails gesendet werden können.
 * Stellt grundlegende Funtionen zum Versenden von Mails bereit.
 * 
 * @author Hendrik Karwanni
 */
public abstract class SendServer extends MailServer {
	private static final long serialVersionUID = -4191787147022537178L;

	protected SendServer(ServerSettings settings, String serverTyp) {
		super(settings, serverTyp);
	}

	/**
	 * Sendet eine E-Mail über den aktuellen Server.
	 * Die Implementierung des Vorgangs ist vom Serverprotokoll abhängig 
	 * @param user Benutzername des Senders
	 * @param pw Passwort des Senders
	 * @param from Anzeigename des Senders
	 * @param to Ziele der Mail
	 * @param cc CCs der Mail
	 * @param subject Betreff der Mail
	 * @param text Text der Mail
	 * @throws MessagingException Tritt auf, wenn das Senden der Mail fehlschlägt
	 */
	public abstract void sendeMail(String user, String pw, InternetAddress from, InternetAddress[] to, InternetAddress[] cc, String subject, String text, String format, File[] attachment) 
			throws MessagingException, IOException ;
	
	public boolean prüfeLogin(String benutzername, String passwort){
		return true;
		//throw new RuntimeException("Nicht implementiert");
	}
}
