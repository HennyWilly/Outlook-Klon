package de.outlook_klon.logik.mailclient;

import java.io.Serializable;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Abstrakte Basisklasse für alle Mailserver.
 * Stellt grundlegende Funktionen für alle Servertypen bereit. 
 * 
 * @author Hendrik Karwanni
 */
public abstract class MailServer implements Serializable{
	private static final long serialVersionUID = -6369803776352038195L;

	/**
	 * Dient zur Authentifikation mit einem Benutzernamen und Passwort
	 */
	protected class StandardAuthentificator extends Authenticator {
		private String benutzername;
		private String passwort;
		
		/**
		 * Erstellt eine neue Instanz des StandardAuthentificators
		 * @param benutzername Verwendeter Benutzername
		 * @param passwort Verwendendetes Passwort
		 */
		public StandardAuthentificator(String benutzername, String passwort) {
			this.benutzername = benutzername;
			this.passwort = passwort;
		}
		
		@Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(benutzername, passwort);
        }
	}
	
	/**
	 * Attribut, das die nötigen Einstellungen zum Aufbau einer Verbindung zu einem Mailserver enthällt
	 */
	protected ServerSettings settings;
	
	/**
	 * Attribut, das die Stringdarstellung des Servertyps enthällt
	 */
	protected String serverTyp;
	
	/**
	 * Wird von abgeleiteten Klassen aufgerufen, um interne Attribute zu initialisieren
	 * @param settings Einstellungen zum Verbindungsaufbau
	 * @param serverTyp Beschreibender String zum Servertyp
	 */
	protected MailServer(ServerSettings settings, String serverTyp) {
		if(settings == null)
			throw new NullPointerException("Servereinstellungen wurden nicht instanziiert");
		
		this.settings = settings;
		this.serverTyp = serverTyp;
	}
	
	/**
	 * Prüft, ob man sich mit den übergebenen Login-Daten an dem bekannten Server anmelden kann. 
	 * @param benutzername Anmeldename des Benutzers
	 * @param passwort Passwort des Benutzers
	 * @return true, wenn die Anmeldedaten korrekt waren; sonst false
	 */
	public abstract boolean prüfeLogin(String benutzername, String passwort);
	
	/**
	 * Gibt den beschreibenden String zum Servertyp zurück
	 * @return Beschreibender String zum Servertyp
	 */
	public String getServerTyp() {
		return serverTyp;
	}
	
	/**
	 * Gibt die ServerSettings-Instanz zur Verbindung zum Server zurück
	 * @return ServerSettings-Instanz zum Server
	 */
	public ServerSettings getSettings() {
		return settings;
	}
	
	@Override
	public String toString() {
		return String.format("%s:%d", settings.getHost(), settings.getPort());
	}
}
