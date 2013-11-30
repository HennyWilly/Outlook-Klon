package de.outlook_klon.logik.mailclient;

/**
 * Diese Aufzählung gibt die verschiedenen Arten der verschlüsselten Kommunikation an
 * 
 * @author Hendrik Karwanni
 */
public enum Verbindungssicherheit {
	/**
	 * Verwende keine Verschlüsselung
	 */
	NONE,
	
	/**
	 * Verwende SSL/TLS als Verschlüsselung
	 */
	SSL_TLS,
	
	/**
	 * Verwende STARTTLS als Verschlüsselung
	 */
	STARTTLS
}
