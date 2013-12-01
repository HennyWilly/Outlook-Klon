package de.outlook_klon.logik.mailclient;

import javax.mail.NoSuchProviderException;
import javax.mail.Store;

/**
 * Abstrakte Basisklasse f�r alle Mailserver, �ber die Mails empfangen werden k�nnen.
 * Stellt grundlegende Funtionen zum Empfangen von Mails bereit.
 * 
 * @author Hendrik Karwanni
 */
public abstract class EmpfangsServer extends MailServer {
	protected EmpfangsServer(ServerSettings settings, String serverTyp) {
		super(settings, serverTyp);
	}

	/**
	 * Gibt den Store zur�ck, der die E-Mails des Anwenders enth�llt
	 * @param user Benutzername des Empf�ngers
	 * @param pw Passwort des Empf�ngers
	 * @return Store-Objekt, �ber welches man auf die Mails zugreifen kann
	 */
	public abstract Store getMailStore(String user, String pw) throws NoSuchProviderException;
}
