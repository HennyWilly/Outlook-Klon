package de.outlook_klon.logik.mailclient;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * Diese Klasse stellt einen POP3-Server dar
 * 
 * @author Hendrik Karwanni
 */
public class Pop3Server extends EmpfangsServer {
	private static final long serialVersionUID = 926746044207884587L;

	/**
	 * Erstellt eine neue Instanz eines Pop3-Servers mit den übergebenen Einstellungen
	 * @param settings Einstellungen zur Serververbindung
	 */
	public Pop3Server(ServerSettings settings) {
		super(settings, "POP3");
	}

	@Override
	public Store getMailStore(String user, String pw) throws NoSuchProviderException {
		Authenticator auth = new StandardAuthentificator(user, pw);
		
		Properties props = System.getProperties();
		props.put("mail.pop3.host", settings.getHost());
		props.put("mail.pop3.port", settings.getPort());
		props.put("mail.pop3.auth", true);

		if(settings.getVerbingungssicherheit() == Verbindungssicherheit.SSL_TLS) {
			props.put("mail.pop3.ssl.enable", true);
		}
		
		Session session = Session.getInstance(props, auth);
		session.setDebug(true);
		
		Store store = null;
		if(settings.getVerbingungssicherheit() == Verbindungssicherheit.SSL_TLS)
			store = session.getStore("pop3s");
		else
			store = session.getStore("pop3");
		
		return store;
	}
}
