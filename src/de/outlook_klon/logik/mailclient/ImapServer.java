package de.outlook_klon.logik.mailclient;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * Diese Klasse stellt einen IMAP-Server dar.
 * 
 * @author Hendrik Karwanni
 */
public class ImapServer extends EmpfangsServer {
	private static final long serialVersionUID = 3401491699856582843L;

	/**
	 * Erstellt eine neue Instanz eines IMAP-Servers mit den �bergebenen Einstellungen
	 * @param settings Einstellungen zur Serververbindung
	 */
	public ImapServer(ServerSettings settings) {
		super(settings, "IMAP");
	}

	@Override
	public Store getMailStore(String user, String pw) throws NoSuchProviderException {
		Authenticator auth = new StandardAuthentificator(user, pw);
		
		Properties props = System.getProperties();
		props.put("mail.imap.host", settings.getHost());
		props.put("mail.imap.port", settings.getPort());
		props.put("mail.imap.auth", true);

		if(settings.getVerbingungssicherheit() == Verbindungssicherheit.SSL_TLS) {
			props.put("mail.imap.ssl.enable", true);
		}
		
		Session session = Session.getInstance(props, auth);
		session.setDebug(true);
		
		Store store = null;
		if(settings.getVerbingungssicherheit() == Verbindungssicherheit.SSL_TLS)
			store = session.getStore("imaps");
		else
			store = session.getStore("imap");
		
		return store;
	}
}
