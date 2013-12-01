package de.outlook_klon.logik.mailclient;

/**
 * Abstrakte Basisklasse für alle Mailserver.
 * Stellt grundlegende Funktionen für alle Servertypen bereit. 
 * 
 * @author Hendrik Karwanni
 */
public abstract class MailServer {
	/**
	 * Attribut, das die nötigen Einstellungen zum Aufbau einer Verbindung zu einem Mailserver enthällt
	 */
	protected ServerSettings settings;
	
	protected String serverTyp;
	
	/**
	 * Wird von abgeleiteten Klassen aufgerufen, um interne Attribute zu initialisieren
	 * @param settings Einstellungen zum Verbindungsaufbau
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
	public boolean prüfeLogin(String benutzername, String passwort) {
		throw new RuntimeException("Nicht implementiert");
	}
	
	public String getServerTyp() {
		return serverTyp;
	}
	
	public ServerSettings getSettings() {
		return settings;
	}
	
	@Override
	public String toString() {
		return String.format("%s:%d", settings.getHost(), settings.getPort());
	}
}
