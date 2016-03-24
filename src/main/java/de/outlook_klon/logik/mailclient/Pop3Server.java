package de.outlook_klon.logik.mailclient;

import java.util.Properties;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Diese Klasse stellt einen POP3-Server dar
 * 
 * @author Hendrik Karwanni
 */
public class Pop3Server extends EmpfangsServer {
	private static final long serialVersionUID = 926746044207884587L;

	/**
	 * Erstellt eine neue Instanz eines Pop3-Servers mit den �bergebenen
	 * Einstellungen
	 * 
	 * @param settings
	 *            Einstellungen zur Serververbindung
	 */
	@JsonCreator
	public Pop3Server(@JsonProperty("settings") ServerSettings settings) {
		super(settings, "POP3");
	}

	@Override
	protected Properties getProperties() {
		final Properties props = new Properties();

		props.put("mail.pop3.host", settings.getHost());
		props.put("mail.pop3.port", settings.getPort());
		props.put("mail.pop3.auth", true);

		if (settings.getConnectionSecurity() == Verbindungssicherheit.SSL_TLS) {
			props.put("mail.pop3.ssl.enable", true);
		}

		return props;
	}

	@Override
	public Store getMailStore(final String user, final String passwd) throws NoSuchProviderException {
		final Session session = getSession(new StandardAuthenticator(user, passwd));

		Store store = null;
		if (settings.getConnectionSecurity() == Verbindungssicherheit.SSL_TLS)
			store = session.getStore("pop3s");
		else
			store = session.getStore("pop3");

		return store;
	}
}
