package de.outlook_klon.logik.kontakte;

import javax.mail.internet.InternetAddress;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dies ist eine Datenklasse, die die Daten von einem Kontakt des Benutzers
 * speichert.
 * 
 * @author Hendrik Karwanni
 */
public class Kontakt {

	@JsonProperty("surname")
	private String mNachname;

	@JsonProperty("forename")
	private String mVorname;

	@JsonProperty("displayname")
	private String mAnzeigename;

	@JsonProperty("nickname")
	private String mSpitzname;

	@JsonProperty("address1")
	private InternetAddress mMail1;

	@JsonProperty("address2")
	private InternetAddress mMail2;

	@JsonProperty("dutyphone")
	private String mTelDienst;

	@JsonProperty("mobilephone")
	private String mTelMobil;

	@JsonProperty("privatephone")
	private String mTelPrivat;

	/**
	 * Erstellt eine neue Instanz der Klasse mit den übergebenen Werten.
	 * 
	 * @param nachname
	 *            Nachname des Kontakts
	 * @param vorname
	 *            Vorname des Kontakts
	 * @param anzeigename
	 *            Anzeigename des Kontakts
	 * @param spitzname
	 *            Spitzname des Kontakts
	 * @param mail1
	 *            Erste E-Mail-Adresse des Kontakts
	 * @param mail2
	 *            Zweite E-Mail-Adresse des Kontakts
	 * @param telPrivat
	 *            Private Telefonnummer des Kontakts
	 * @param telDienst
	 *            Dienstliche Telefonnummer des Kontakts
	 * @param telMobil
	 *            Mobiltelefonnummer des Kontakts
	 */
	public Kontakt(
			@JsonProperty("surname") String nachname, 
			@JsonProperty("forename") String vorname, 
			@JsonProperty("displayname") String anzeigename, 
			@JsonProperty("nickname") String spitzname, 
			@JsonProperty("address1") InternetAddress mail1,
			@JsonProperty("address2") InternetAddress mail2, 
			@JsonProperty("privatephone") String telPrivat, 
			@JsonProperty("dutyphone") String telDienst, 
			@JsonProperty("mobilephone") String telMobil) {
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
	 * Setter für den Nachnamen des Kontakts
	 * 
	 * @param nachname
	 *            Zu setzender Nachname
	 */
	public void setNachname(String nachname) {
		mNachname = nachname;
	}

	/**
	 * Getter für den Nachnamen des Kontakts
	 * 
	 * @return Nachname
	 */
	@JsonIgnore
	public String getNachname() {
		return mNachname;
	}

	/**
	 * Setter für den Vornamen des Kontakts
	 * 
	 * @param vorname
	 *            Zu setzender Vorname
	 */
	public void setVorname(String vorname) {
		mVorname = vorname;
	}

	/**
	 * Getter für den Vornamen des Kontakts
	 * 
	 * @return Vorname
	 */
	@JsonIgnore
	public String getVorname() {
		return mVorname;
	}

	/**
	 * Setter für den Anzeigenamen des Kontakts
	 * 
	 * @param anzeigename
	 *            Zu setzender Anzeigename
	 */
	public void setAnzeigename(String anzeigename) {
		mAnzeigename = anzeigename;
	}

	/**
	 * Getter für den Anzeigenamen des Kontakts
	 * 
	 * @return Anzeigename
	 */
	@JsonIgnore
	public String getAnzeigename() {
		return mAnzeigename;
	}

	/**
	 * Setter für den Spitznamen des Kontakts
	 * 
	 * @param spitzname
	 *            Zu setzender Spitzname
	 */
	public void setSpitzname(String spitzname) {
		mSpitzname = spitzname;
	}

	/**
	 * Getter für den Spitznamen des Kontakts
	 * 
	 * @return Spitzname
	 */
	@JsonIgnore
	public String getSpitzname() {
		return mSpitzname;
	}

	/**
	 * Setter für die erste E-Mail-Adresse des Kontakts
	 * 
	 * @param mail1
	 *            Zu setzende E-Mail-Adresse
	 */
	public void setMail1(InternetAddress mail1) {
		this.mMail1 = mail1;
	}

	/**
	 * Getter für die erste E-Mail-Adresse des Kontakts
	 * 
	 * @return Erste E-Mail-Adresse
	 */
	@JsonIgnore
	public InternetAddress getMail1() {
		return mMail1;
	}

	/**
	 * Setter für die zweite E-Mail-Adresse des Kontaks
	 * 
	 * @param mail2
	 *            Zu setzende E-Mail-Adresse
	 */
	public void setMail2(InternetAddress mail2) {
		this.mMail2 = mail2;
	}

	/**
	 * Getter für die zweite E-Mail-Adresse des Kontakts
	 * 
	 * @return Zweite E-Mail-Adresse
	 */
	@JsonIgnore
	public InternetAddress getMail2() {
		return mMail2;
	}

	/**
	 * Setter für die private Telefonnummer des Kontakts
	 * 
	 * @param telPrivat
	 *            Zu setzende Telefonnummer
	 */
	public void setTelPrivat(String telPrivat) {
		this.mTelPrivat = telPrivat;
	}

	/**
	 * Getter für die private Telefonnummer des Kontakts
	 * 
	 * @return Private Telefonnummer
	 */
	@JsonIgnore
	public String getTelPrivat() {
		return mTelPrivat;
	}

	/**
	 * Setter für die dienstliche Telefonnummer des Kontakts
	 * 
	 * @param telDienst
	 *            Zu setzende Telefonnummer
	 */
	public void setTelDienst(String telDienst) {
		this.mTelDienst = telDienst;
	}

	/**
	 * Getter für die dienstliche Telefonnummer des Kontakts
	 * 
	 * @return Dienstliche Telefonnummer
	 */
	@JsonIgnore
	public String getTelDienst() {
		return mTelDienst;
	}

	/**
	 * Setter für die Mobiltelefonnummer des Kontakts
	 * 
	 * @param telMobil
	 *            Zu setzende Telefonnummer
	 */
	public void setTelMobil(String telMobil) {
		this.mTelMobil = telMobil;
	}

	/**
	 * Getter für die Mobiltelefonnummer des Kontakts
	 * 
	 * @return Mobiltelefonnummer
	 */
	@JsonIgnore
	public String getTelMobil() {
		return mTelMobil;
	}
}
