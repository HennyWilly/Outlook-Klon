package de.outlook_klon.logik.mailclient;

import java.util.Properties;

import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Diese Klasse stellt einen IMAP-Server dar.
 * 
 * @author Hendrik Karwanni
 */
public class ImapServer extends EmpfangsServer {
	private static final long serialVersionUID = 3401491699856582843L;

	/**
	 * Erstellt eine neue Instanz eines IMAP-Servers mit den �bergebenen
	 * Einstellungen
	 * 
	 * @param settings
	 *            Einstellungen zur Serververbindung
	 */
	@JsonCreator
	public ImapServer(@JsonProperty("settings") ServerSettings settings) {
		super(settings, "IMAP");
	}

	@Override
	protected Properties getProperties() {
		final Properties props = new Properties();

		props.put("mail.imap.host", settings.getHost());
		props.put("mail.imap.port", settings.getPort());
		props.put("mail.imap.auth", true);

		if (settings.getConnectionSecurity() == Verbindungssicherheit.SSL_TLS) {
			props.put("mail.imap.ssl.enable", true);
		}

		return props;
	}

	@Override
	public Store getMailStore(final String user, final String passwd) throws NoSuchProviderException {
		final Session session = getSession(new StandardAuthenticator(user, passwd));

		Store store = null;
		if (settings.getConnectionSecurity() == Verbindungssicherheit.SSL_TLS)
			store = session.getStore("imaps");
		else
			store = session.getStore("imap");

		return store;
	}
}
