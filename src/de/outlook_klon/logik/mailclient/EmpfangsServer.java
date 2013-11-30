package de.outlook_klon.logik.mailclient;

import javax.mail.NoSuchProviderException;
import javax.mail.Store;

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

	/**
	 * Gibt den Store zurück, der die E-Mails des Anwenders enthällt
	 * @param user Benutzername des Empfängers
	 * @param pw Passwort des Empfängers
	 * @return Store-Objekt, über welches man auf die Mails zugreifen kann
	 */
	public abstract Store getMailStore(String user, String pw) throws NoSuchProviderException;
}
