package de.outlook_klon.logik.mailclient;

import java.io.Serializable;

/**
 * Abstrakte Basisklasse f�r alle Mailserver.
 * Stellt grundlegende Funktionen f�r alle Servertypen bereit. 
 * 
 * @author Hendrik Karwanni
 */
public abstract class MailServer implements Serializable{
	private static final long serialVersionUID = -6369803776352038195L;

	/**
	 * Attribut, das die n�tigen Einstellungen zum Aufbau einer Verbindung zu einem Mailserver enth�llt
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
	 * Pr�ft, ob man sich mit den �bergebenen Login-Daten an dem bekannten Server anmelden kann. 
	 * @param benutzername Anmeldename des Benutzers
	 * @param passwort Passwort des Benutzers
	 * @return true, wenn die Anmeldedaten korrekt waren; sonst false
	 */
	public abstract boolean pr�feLogin(String benutzername, String passwort);
	
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
