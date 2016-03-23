package de.outlook_klon.logik.kalendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.InternetAddress;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.outlook_klon.logik.Benutzer;
import de.outlook_klon.logik.kontakte.Kontakt;

/**
 * Dies ist eine Datenklasse, die die Daten von einem Termin des Benutzers
 * speichert.
 * 
 * @author Hendrik Karwanni
 */
public class Termin implements Comparable<Termin> {

	@JsonProperty("subject")
	private String mBetreff;

	@JsonProperty("location")
	private String mOrt;

	@JsonProperty("start")
	private Date mStart;

	@JsonProperty("end")
	private Date mEnde;

	@JsonProperty("text")
	private String mText;

	@JsonProperty("user")
	private String mBenutzerkonto;

	@JsonProperty("contact")
	private String mKontakt;

	@JsonProperty("state")
	private Status mStatus;

	@JsonProperty("addresses")
	private InternetAddress[] mAdressen;

	/**
	 * Diese Aufzählung stellt die möglichen Zustände eines Termins dar
	 */
	public static enum Status {
		/**
		 * Dem Termin wurde zugesagt
		 */
		zugesagt,

		/**
		 * Der Termin wurde abgelehnt
		 */
		abgelehnt
	}

	/**
	 * Erstellt eine neue Instanz der Klasse mit den übergebenen Werten
	 * 
	 * @param betreff
	 *            Betreff des Termins
	 * @param ort
	 *            Ort des Termins
	 * @param start
	 *            Startzeitpunkt des Termins
	 * @param ende
	 *            Endzeitpunkt des Termins
	 * @param text
	 *            Text des Termins
	 */
	public Termin(String betreff, String ort, Date start, Date ende, String text, String benutzer, String kontakt) {
		setBetreff(betreff);
		setOrt(ort);
		setStartUndEnde(start, ende);
		setText(text);
		setBenutzerkonto(benutzer);
		setKontakt(kontakt);
		setStatus(Status.zugesagt);

		List<InternetAddress> temp = new ArrayList<InternetAddress>(2);
		for (Kontakt k : Benutzer.getInstanz().getKontakte()) {
			if (k.getAnzeigename() == kontakt) {
				if (k.getMail1() != null) {
					temp.add(k.getMail1());
				}
				if (k.getMail2() != null) {
					temp.add(k.getMail2());
				}
				break;
			}
		}
		setAdressen(temp.toArray(new InternetAddress[2]));
	}
	
	@JsonCreator
	public Termin(
			@JsonProperty("subject") String betreff, 
			@JsonProperty("location") String ort, 
			@JsonProperty("start") Date start, 
			@JsonProperty("end") Date ende, 
			@JsonProperty("text") String text, 
			@JsonProperty("user") String benutzer, 
			@JsonProperty("contact") String kontakt,
			@JsonProperty("state") Status status,
			@JsonProperty("addresses") InternetAddress[] adressen) {
		setBetreff(betreff);
		setOrt(ort);
		setStartUndEnde(start, ende);
		setText(text);
		setBenutzerkonto(benutzer);
		setKontakt(kontakt);
		setStatus(status);
		setAdressen(adressen);
	}

	/**
	 * Setter für den Betreff des Termins
	 * 
	 * @param betreff
	 *            Zu setzender Betreff
	 */
	public void setBetreff(String betreff) {
		this.mBetreff = betreff;
	}

	/**
	 * Getter für den Betreff des Termins
	 * 
	 * @return Betreff
	 */
	@JsonIgnore
	public String getBetreff() {
		return mBetreff;
	}

	/**
	 * Setter für den Ort des Termins
	 * 
	 * @param ort
	 *            Zu setzender Ort
	 */
	public void setOrt(String ort) {
		this.mOrt = ort;
	}

	/**
	 * Getter für den Ort des Termins
	 * 
	 * @return Ort
	 */
	@JsonIgnore
	public String getOrt() {
		return mOrt;
	}

	/**
	 * Setter für den Start- und Endzeitpunkt des Termins
	 * 
	 * @param start
	 *            Zu setzender Startzeitpunkt
	 * @param ende
	 *            Zu setzender Endzeitpunkt
	 */
	public void setStartUndEnde(Date start, Date ende) {
		if (start.after(ende))
			throw new RuntimeException("Der Startzeitpunkt darf nicht hinter dem Endzeitpunkt liegen");
		mStart = new Date(start.getTime());
		mEnde = new Date(ende.getTime());
	}

	/**
	 * Getter für den Startzeitpunkt des Termins
	 * 
	 * @return Startzeitpunkt
	 */
	@JsonIgnore
	public Date getStart() {
		return mStart;
	}

	/**
	 * Getter für den Endzeitpunkt des Termins
	 * 
	 * @return Endzeitpunkt
	 */
	@JsonIgnore
	public Date getEnde() {
		return mEnde;
	}

	/**
	 * Setter für die Beschreibung des Termins
	 * 
	 * @param text
	 *            Zu setzende Beschreibung
	 */
	public void setText(String text) {
		this.mText = text;
	}

	/**
	 * Getter für die Beschreibung des Termins
	 * 
	 * @return Beschreibung
	 */
	@JsonIgnore
	public String getText() {
		return mText;
	}

	/**
	 * Setter für das zugeordnete Benutzerkonto des Termins
	 * 
	 * @param benutzer
	 *            Zu setzende Benutzerkonto
	 */
	public void setBenutzerkonto(String benutzer) {
		this.mBenutzerkonto = benutzer;
	}

	/**
	 * Getter für das zugeordnete Benutzerkonto des Termins
	 * 
	 * @return Benutzerkonto
	 */
	@JsonIgnore
	public String getBenutzerkonto() {
		return mBenutzerkonto;
	}

	/**
	 * Getter für den zugeordneten Kontakt des Termins
	 * 
	 * @return Kontakt
	 */
	@JsonIgnore
	public String getKontakt() {
		return mKontakt;
	}

	/**
	 * Setter für den zugeordneten Kontakt des Termins
	 * 
	 * @param kontakt
	 *            Zu setzende Kontakt
	 */
	public void setKontakt(String kontakt) {
		this.mKontakt = kontakt;
	}

	@Override
	public int compareTo(Termin o) {
		return this.mStart.compareTo(o.mStart);
	}

	/**
	 * Gibt den Status des Termins zurück
	 * 
	 * @return Status des Termins
	 */
	@JsonIgnore
	public Status getStatus() {
		return mStatus;
	}

	/**
	 * Setzt den Status des Termins
	 * 
	 * @param mStatus
	 *            Status des Termins
	 */
	public void setStatus(Status mStatus) {
		this.mStatus = mStatus;
	}

	/**
	 * Gibt die verknüpfen Adressen zum Termin zurück
	 * 
	 * @return Adressen zum Termin
	 */
	@JsonIgnore
	public InternetAddress[] getAdressen() {
		return mAdressen;
	}

	/**
	 * Setzt die verknüpfen Adressen zum Termin
	 * 
	 * @param mAdressen
	 *            Adressen zum Termin
	 */
	public void setAdressen(InternetAddress[] mAdressen) {
		this.mAdressen = mAdressen;
	}
}