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
	 * Verwende SSL als Verschlüsselung
	 */
	SSL,
	
	SSL_OPTIONAL,
	
	/**
	 * Verwende TLS als Verschlüsselung
	 */
	TLS,
	
	TLS_OPTIONAL
}
