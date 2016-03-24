package de.outlook_klon.logik.mailclient;

import java.io.Serializable;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstrakte Basisklasse für alle Mailserver. Stellt grundlegende Funktionen für
 * alle Servertypen bereit.
 * 
 * @author Hendrik Karwanni
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class MailServer implements Serializable {
	private static final long serialVersionUID = -6369803776352038195L;

	/**
	 * Attribut, das die nötigen Einstellungen zum Aufbau einer Verbindung zu
	 * einem Mailserver enthällt
	 */
	protected ServerSettings settings;

	/**
	 * Attribut, das die Stringdarstellung des Servertyps enthällt
	 */
	protected String serverType;

	/**
	 * Dient zur Authentifikation mit einem Benutzernamen und Passwort
	 */
	protected class StandardAuthenticator extends Authenticator {
		private final String benutzername;
		private final String passwort;

		/**
		 * Erstellt eine neue Instanz des StandardAuthenticators
		 * 
		 * @param benutzername
		 *            Verwendeter Benutzername
		 * @param passwort
		 *            Verwendendetes Passwort
		 */
		public StandardAuthenticator(final String benutzername, final String passwort) {
			super();

			this.benutzername = benutzername;
			this.passwort = passwort;
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(benutzername, passwort);
		}
	}

	/**
	 * Wird von abgeleiteten Klassen aufgerufen, um interne Attribute zu
	 * initialisieren
	 * 
	 * @param settings
	 *            Einstellungen zum Verbindungsaufbau
	 * @param serverTyp
	 *            Beschreibender String zum Servertyp
	 */
	protected MailServer(final ServerSettings settings, final String serverTyp) {
		if (settings == null)
			throw new NullPointerException("Servereinstellungen wurden nicht instanziiert");

		this.settings = settings;
		this.serverType = serverTyp;
	}

	/**
	 * Prüft, ob man sich mit den übergebenen Login-Daten an dem bekannten
	 * Server anmelden kann.
	 * 
	 * @param benutzername
	 *            Anmeldename des Benutzers
	 * @param passwort
	 *            Passwort des Benutzers
	 * @return true, wenn die Anmeldedaten korrekt waren; sonst false
	 */
	public abstract boolean pruefeLogin(String benutzername, String passwort);

	/**
	 * Gibt das <code>Properties</code>-Objekt zurück, das für den Zugriff über
	 * ein bestimmtes Protokoll konfiguriert ist
	 * 
	 * @return <code>Properties</code>-Objekt
	 */
	protected abstract Properties getProperties();

	/**
	 * Erstellt aus dem übergebenen <code>Authenticator</code> und den intern
	 * erzeugten <code>Properties</code> ein <code>Session</code>-Objekt
	 * 
	 * @param auth
	 *            Zu verwendender <code>Authenticator</code>
	 * @return <code>Session</code>-Objekt
	 */
	protected Session getSession(Authenticator auth) {
		Properties props = getProperties();

		Session session = Session.getInstance(props, auth);
		//session.setDebug(true);

		return session;
	}

	/**
	 * Gibt den beschreibenden String zum Servertyp zurück
	 * 
	 * @return Beschreibender String zum Servertyp
	 */
	public String getServerType() {
		return serverType;
	}

	/**
	 * Gibt die <code>ServerSettings</code>-Instanz zur Verbindung zum Server
	 * zurück
	 * 
	 * @return <code>ServerSettings</code>-Instanz zum Server
	 */
	public ServerSettings getSettings() {
		return settings;
	}

	@Override
	public String toString() {
		return String.format("%s:%d", settings.getHost(), settings.getPort());
	}
}
