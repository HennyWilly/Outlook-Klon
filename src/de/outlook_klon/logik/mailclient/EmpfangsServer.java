package de.outlook_klon.logik.mailclient;

/**
 * Abstrakte Basisklasse für alle Mailserver, über die Mails empfangen werden können.
 * Stellt grundlegende Funtionen zum Empfangen von Mails bereit.
 * 
 * @author Hendrik Karwanni
 */
public abstract class EmpfangsServer extends MailServer {
	protected EmpfangsServer(ServerSettings settings) {
		super(settings);
	}
}
