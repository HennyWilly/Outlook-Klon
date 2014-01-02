package de.outlook_klon.logik.kalendar;

import java.util.Date;

/**
 * Dies ist eine Datenklasse, die die Daten von einem Termin des Benutzers speichert.
 * 
 * @author Hendrik Karwanni
 */
public class Termin {
	private String mBetreff;
	private String mOrt;
	private Date mStart;
	private Date mEnde;
	private String mText;
	private String kontaktMail;
	
	/**
	 * Erstellt eine neue Instanz der Klasse mit den übergebenen Werten
	 * @param betreff Betreff des Termins
	 * @param ort Ort des Termins
	 * @param start Startzeitpunkt des Termins
	 * @param ende Endzeitpunkt des Termins
	 * @param text Text des Termins
	 */
	public Termin(String betreff, String ort, Date start, Date ende, String text, String kontakt) {	
		setBetreff(betreff);
		setOrt(ort);
		setStartUndEnde(start, ende);
		setText(text);
		setkontaktMail(kontakt);
	}

	/**
	 * Setter für den Betreff des Termins
	 * @param betreff Zu setzender Betreff
	 */
	public void setBetreff(String betreff) {
		this.mBetreff = betreff;
	}

	/**
	 * Getter für den Betreff des Termins
	 * @return Betreff
	 */
	public String getBetreff() {
		return mBetreff;
	}

	/**
	 * Setter für den Ort des Termins
	 * @param ort Zu setzender Ort
	 */
	public void setOrt(String ort) {
		this.mOrt = ort;
	}

	/**
	 * Getter für den Ort des Termins
	 * @return Ort
	 */
	public String getOrt() {
		return mOrt;
	}
	
	/**
	 * Setter für den Start- und Endzeitpunkt des Termins
	 * @param start Zu setzender Startzeitpunkt
	 * @param ende Zu setzender Endzeitpunkt
	 */
	public void setStartUndEnde(Date start, Date ende) {
		if(start.after(ende))
			throw new RuntimeException("Der Startzeitpunkt darf nicht hinter dem Endzeitpunkt liegen");
		mStart = new Date(start.getTime());
		mEnde = new Date(ende.getTime());
	}
	
	/**
	 * Getter für den Startzeitpunkt des Termins
	 * @return Startzeitpunkt
	 */
	public Date getStart() {
		return mStart;
	}

	/**
	 * Getter für den Endzeitpunkt des Termins
	 * @return Endzeitpunkt
	 */
	public Date getEnde() {
		return mEnde;
	}

	/**
	 * Setter für die Beschreibung des Termins
	 * @param text Zu setzende Beschreibung
	 */
	public void setText(String text) {
		this.mText = text;
	}

	/**
	 * Getter für die Beschreibung des Termins
	 * @return Beschreibung
	 */
	public String getText() {
		return mText;
	}
	
	public void setkontaktMail(String txt) {
		this.kontaktMail = txt;
	}
	
	public String getkontaktMail() {
		return kontaktMail;
	}
}
