package de.outlook_klon.logik.mailclient;

import javax.mail.Message;

/**
 * Diese Klasse stellt ein Mailkonto dar.
 * Hierüber können Mails gesendet und empfangen werden.
 * 
 * @author Hendrik Karwanni
 */
public class MailAccount {
	private EmpfangsServer inServer;
	private SendServer outServer;
	
	private String adresse;
	private String benutzer;
	private String passwort;
	
	/**
	 * Erstellt eine neue Instanz der Klasse Mailkonto mit den übergebenen Parametern
	 * @param inServer Server-Instanz, die zum Empfangen von Mails verwendet wird
	 * @param outServer Server-Instanz, die zum Senden von Mails verwendet wird
	 * @param adresse E-Mail-Adresse, das dem Konto zugeordnet ist
	 * @param benutzer Benutzername, der zur Anmeldung verwendet werden soll
	 * @param passwort Passwort, das zur Anmeldung verwendet werden soll
	 */
	public MailAccount(EmpfangsServer inServer, SendServer outServer, String adresse, String benutzer, String passwort) {
		this.inServer = inServer;
		this.outServer = outServer;
		
		this.adresse = adresse;
		this.benutzer = benutzer;
		this.passwort = passwort;
	}
	
	@Override
	public String toString() {
		return adresse;
	}

	/**
	 * Sendet eine übergebene Mail über den dem Mailkonto zugeordneten SMTP-Server
	 * @param mail Zu sendende Mail
	 */
	public void sendeMail(Message mail) {
		outServer.sendeMail(mail);
	}
	
	public String[] getOrdnerstruktur() {
		return null;
	}
}
