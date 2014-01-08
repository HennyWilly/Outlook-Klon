package de.outlook_klon.logik.mailclient;

import java.io.Serializable;

/*
 * SMTP 587
 * IMAP 220
 * POP3 110
 */

/**
 * Dies ist eine Datenklasse, die die Daten zur Verbindung mit einem Mailserver speichert.
 * 
 * @author Hendrik Karwanni
 */
public class ServerSettings implements Serializable {
	private static final long serialVersionUID = -2113634498937441789L;
	
	private String host;
	private int port;
	private Verbindungssicherheit vSicherheit;
	private Authentifizierungsart authentifizierung;

	/**
	 * Erstellt eine neue Instanz der Klasse mit den �bergebenen Werten
	 * @param host Hostname/Ip der Zielservers
	 * @param port Zielport des Servers
	 * @param vSicherheit Art der unterst�tzten Verschl�sselung des Servers
	 * @param authentifizierung Art der Authentifizierung an dem Server
	 * 
	 * @throws NullPointerException Tritt auf, wenn <code>host</code> gleich <code>null</code> ist.
	 * @throws IllegalArgumentException Tritt auf, wenn <code>port</code> kein korrekter Port ist.
	 */
	public ServerSettings(final String host, final int port, final Verbindungssicherheit vSicherheit, final Authentifizierungsart authentifizierung) 
			throws NullPointerException, IllegalArgumentException {
		if(host == null)
			throw new NullPointerException("Der Hostname darf nicht null sein");
		if(port < 0 || port  > 49151)
			throw new IllegalArgumentException("Der �bergebene Wert ist kein zul�ssiger Port");
		
		this.host = host;
		this.port = port;
		this.vSicherheit = vSicherheit;
		this.authentifizierung = authentifizierung;
	}
	
	/**
	 * Getter f�r den Hostnamen bzw. die IP des Zielservers
	 * @return Hostnamen bzw. IP
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * Getter f�r den Zielport des Servers
	 * @return Port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Getter f�r die Art der Verbindungssicherheit zum Server
	 * @return Verschl�sselungsart
	 */
	public Verbindungssicherheit getVerbingungssicherheit() {
		return vSicherheit;
	}
	
	/**
	 * Getter f�r die Art der Authentifizierung am Server
	 * @return Authentifizierungsart
	 */
	public Authentifizierungsart getAuthentifizierungsart() {
		return authentifizierung;
	}
	
	@Override
	public String toString() {
		return String.format("%s:%d", host, port);
	}
}
