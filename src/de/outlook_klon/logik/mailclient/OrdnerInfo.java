package de.outlook_klon.logik.mailclient;

/**
 * Datenklasse zum Halten von abgefragten Informationen von Ordnern
 * @author Hendrik Karwanni
 */
public class OrdnerInfo {
	private String name;
	private String pfad;
	private int anzahlUngelesen;

	/**
	 * Ertsellt eine neue Instanz der Klasse OrdnerInfo mit den übergebenen Werten
	 * @param name Name des Ordners
	 * @param pfad Pfad innerhalb des MailStores
	 * @param anzahlUngelesen Anzahl ungelesener Nachrichten
	 */
	public OrdnerInfo(String name, String pfad, int anzahlUngelesen) {
		setName(name);
		setPfad(pfad);
		setAnzahlUngelesen(anzahlUngelesen);
	}

	/**
	 * Gibt den Namen des Ordners zurück
	 * @return Name des Ordners
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setzt den Namen des Ordners
	 * @param name Name des Ordners
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gibt den Pfad des Ordners innerhalb des MailStores zurück
	 * @return Pfad des Ordners
	 */
	public String getPfad() {
		return pfad;
	}

	/**
	 * Setzt den Pfad des Ordners innerhalb des MailStores
	 * @param pfad Pfad des Ordners
	 */
	public void setPfad(String pfad) {
		this.pfad = pfad;
	}

	/**
	 * Gibt die Anzahl an ungelesenen Mails innerhalb des Ordners zurück
	 * @return Anzahl an ungelesenen Mails
	 */
	public int getAnzahlUngelesen() {
		return anzahlUngelesen;
	}

	/**
	 * Setzt die Anzahl an ungelesenen Mails innerhalb des Ordners
	 * @param anzahlUngelesen Anzahl an ungelesenen Mails
	 */
	public void setAnzahlUngelesen(int anzahlUngelesen) {
		this.anzahlUngelesen = anzahlUngelesen;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public boolean equals(Object other) {
		if(other == null || !(other instanceof OrdnerInfo))
			return false;
		if(this == other)
			return true;
		
		OrdnerInfo ordner = (OrdnerInfo) other;
		
		return this.pfad.equals(ordner.pfad);
	}
}
