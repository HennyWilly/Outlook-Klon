package de.outlook_klon.logik.mailclient;

/**
 * Diese Klasse stellt einen IMAP-Server dar.
 * 
 * @author Hendrik Karwanni
 */
public class ImapServer extends EmpfangsServer {
	
	/**
	 * Erstellt eine neue Instanz eines IMAP-Servers mit den übergebenen Einstellungen
	 * @param settings Einstellungen zur Serververbindung
	 */
	public ImapServer(ServerSettings settings) {
		super(settings);
	}
}
