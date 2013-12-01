package de.outlook_klon.logik.mailclient;

import javax.mail.NoSuchProviderException;
import javax.mail.Store;

/**
 * Diese Klasse stellt einen POP3-Server dar
 * 
 * @author Hendrik Karwanni
 */
public class Pop3Server extends EmpfangsServer {
	
	/**
	 * Erstellt eine neue Instanz eines Pop3-Servers mit den übergebenen Einstellungen
	 * @param settings Einstellungen zur Serververbindung
	 */
	public Pop3Server(ServerSettings settings) {
		super(settings, "POP3");
	}

	@Override
	public Store getMailStore(String user, String pw) throws NoSuchProviderException {
		// TODO Auto-generated method stub
		return null;
	}
}
