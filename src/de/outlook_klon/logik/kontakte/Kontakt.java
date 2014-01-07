package de.outlook_klon.logik.kontakte;

import java.io.Serializable;
import javax.mail.internet.InternetAddress;

/**
 * Dies ist eine Datenklasse, die die Daten von einem Kontakt des Benutzers speichert.
 * 
 * @author Hendrik Karwanni
 */
public class Kontakt implements Serializable {
	private static final long serialVersionUID = -4417684942862339869L;
	
	private String mNachname;
	private String mVorname;
	private String mAnzeigename;
	private String mSpitzname;
	private InternetAddress mMail1;
	private InternetAddress mMail2;
	private String mTelDienst;
	private String mTelMobil;
	private String mTelPrivat;
	private String mSprache;
	
	/**
	 * Erstellt eine neue Instanz der Klasse mit den �bergebenen Werten.
	 * @param nachname Nachname des Kontakts
	 * @param vorname Vorname des Kontakts
	 * @param anzeigename Anzeigename des Kontakts
	 * @param spitzname Spitzname des Kontakts
	 * @param mail1 Erste E-Mail-Adresse des Kontakts
	 * @param mail2 Zweite E-Mail-Adresse des Kontakts
	 * @param telPrivat Private Telefonnummer des Kontakts
	 * @param telDienst Dienstliche Telefonnummer des Kontakts
	 * @param telMobil Mobiltelefonnummer des Kontakts
	 */
	public Kontakt(String nachname, String vorname, String anzeigename, String spitzname, 
			InternetAddress mail1, InternetAddress mail2, String telPrivat, String telDienst, String telMobil) {
		setNachname(nachname);
		setVorname(vorname);
		setAnzeigename(anzeigename);
		setSpitzname(spitzname);
		setMail1(mail1);
		setMail2(mail2);
		setTelDienst(telDienst);
		setTelMobil(telMobil);
		setTelPrivat(telPrivat);
	}

	@Override
	public String toString() {
		return mAnzeigename;
	}

	/**
	 * Setter f�r den Nachnamen des Kontakts
	 * @param nachname Zu setzender Nachname
	 */
	public void setNachname(String nachname) {
		mNachname = nachname;
	}
	
	/**
	 * Getter f�r den Nachnamen des Kontakts
	 * @return Nachname
	 */
	public String getNachname() {
		return mNachname;
	}

	/**
	 * Setter f�r den Vornamen des Kontakts
	 * @param vorname Zu setzender Vorname
	 */
	public void setVorname(String vorname) {
		mVorname = vorname;
	}
	
	/**
	 * Getter f�r den Vornamen des Kontakts
	 * @return Vorname
	 */
	public String getVorname() {
		return mVorname;
	}

	/**
	 * Setter f�r den Anzeigenamen des Kontakts
	 * @param anzeigename Zu setzender Anzeigename
	 */
	public void setAnzeigename(String anzeigename) {
		mAnzeigename = anzeigename;
	}
	
	/**
	 * Getter f�r den Anzeigenamen des Kontakts
	 * @return Anzeigename
	 */
	public String getAnzeigename() {
		return mAnzeigename;
	}

	/**
	 * Setter f�r den Spitznamen des Kontakts
	 * @param spitzname Zu setzender Spitzname
	 */
	public void setSpitzname(String spitzname) {
		mSpitzname = spitzname;
	}
	
	/**
	 * Getter f�r den Spitznamen des Kontakts
	 * @return Spitzname
	 */
	public String getSpitzname() {
		return mSpitzname;
	}

	/**
	 * Setter f�r die erste E-Mail-Adresse des Kontakts
	 * @param mail1 Zu setzende E-Mail-Adresse
	 */
	public void setMail1(InternetAddress mail1) {
		this.mMail1 = mail1;
	}

	/**
	 * Getter f�r die erste E-Mail-Adresse des Kontakts
	 * @return Erste E-Mail-Adresse
	 */
	public InternetAddress getMail1() {
		return mMail1;
	}
	
	/**
	 * Setter f�r die zweite E-Mail-Adresse des Kontaks
	 * @param mail2 Zu setzende E-Mail-Adresse
	 */
	public void setMail2(InternetAddress mail2) {
		this.mMail2 = mail2;
	}

	/**
	 * Getter f�r die zweite E-Mail-Adresse des Kontakts
	 * @return Zweite E-Mail-Adresse
	 */
	public InternetAddress getMail2() {
		return mMail2;
	}

	/**
	 * Setter f�r die private Telefonnummer des Kontakts
	 * @param telPrivat Zu setzende Telefonnummer
	 */
	public void setTelPrivat(String telPrivat) {
		this.mTelPrivat = telPrivat;
	}

	/**
	 * Getter f�r die private Telefonnummer des Kontakts
	 * @return Private Telefonnummer
	 */
	public String getTelPrivat() {
		return mTelPrivat;
	}

	/**
	 * Setter f�r die dienstliche Telefonnummer des Kontakts
	 * @param telDienst Zu setzende Telefonnummer
	 */
	public void setTelDienst(String telDienst) {
		this.mTelDienst = telDienst;
	}

	/**
	 * Getter f�r die dienstliche Telefonnummer des Kontakts
	 * @return Dienstliche Telefonnummer
	 */
	public String getTelDienst() {
		return mTelDienst;
	}
	
	/**
	 * Setter f�r die Mobiltelefonnummer des Kontakts
	 * @param telMobil Zu setzende Telefonnummer
	 */
	public void setTelMobil(String telMobil) {
		this.mTelMobil = telMobil;
	}

	/**
	 * Getter f�r die Mobiltelefonnummer des Kontakts
	 * @return Mobiltelefonnummer
	 */
	public String getTelMobil() {
		return mTelMobil;
	}
	
	/**
	 *
	 * Setter f�r die bevorzugte Sprache des Kontakts
	 * @param mSprache Zu setzende Sprache
	 */
	public void setSprache(String mSprache) {
		this.mSprache = mSprache;
	}

	/**
	 * Getter f�r die bevorzugte Sprache des Kontakts
	 * @return Sprache
	 */
	public String getSprache() {
		return mSprache;
	}
}
