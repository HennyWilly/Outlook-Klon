package de.outlook_klon.logik.kalendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.internet.InternetAddress;

import de.outlook_klon.logik.Benutzer;
import de.outlook_klon.logik.kontakte.Kontakt;

/**
 * Dies ist eine Datenklasse, die die Daten von einem Termin des Benutzers speichert.
 * 
 * @author Hendrik Karwanni
 */
public class Termin implements Serializable, Comparable<Termin>{
	private static final long serialVersionUID = 6997576125673406382L;
	
	private String mBetreff;
	private String mOrt;
	private Date mStart;
	private Date mEnde;
	private String mText;
	private String mBenutzerkonto;
	private String mKontakt;
	private Status mStatus;
	private InternetAddress[] mAdressen;
	
	/**
	 * Diese Aufz�hlung stellt die m�glichen Zust�nde eines Termins dar
	 */
	public static enum Status {
		/**
		 * Dem Termin wurde zugesagt
		 */
		zugesagt,
		
		/**
		 * Der Termin wurde abgelehnt
		 */
		anbelehnt   //wohl eher abgelehnt :P
	}
	
	/**
	 * Erstellt eine neue Instanz der Klasse mit den �bergebenen Werten
	 * @param betreff Betreff des Termins
	 * @param ort Ort des Termins
	 * @param start Startzeitpunkt des Termins
	 * @param ende Endzeitpunkt des Termins
	 * @param text Text des Termins
	 */
	public Termin(String betreff, String ort, Date start, Date ende, String text, String benutzer, String kontakt) {	
		setBetreff(betreff);
		setOrt(ort);
		setStartUndEnde(start, ende);
		setText(text);
		setBenutzerkonto(benutzer);
		setKontakt(kontakt);
		setStatus(Status.zugesagt);
		
		ArrayList <InternetAddress> temp = new ArrayList <InternetAddress>(2);
		for(Kontakt k: Benutzer.getInstanz().getKontakte()){
			if(k.getAnzeigename()==kontakt)
			{
				if(k.getMail1()!=null)
				{
					temp.add(k.getMail1());
				}
				if(k.getMail2()!=null)
				{
					temp.add(k.getMail2());
				}
				break;
			}
		}
		setAdressen(temp.toArray(new InternetAddress[2]));
	}

	/**
	 * Setter f�r den Betreff des Termins
	 * @param betreff Zu setzender Betreff
	 */
	public void setBetreff(String betreff) {
		this.mBetreff = betreff;
	}

	/**
	 * Getter f�r den Betreff des Termins
	 * @return Betreff
	 */
	public String getBetreff() {
		return mBetreff;
	}

	/**
	 * Setter f�r den Ort des Termins
	 * @param ort Zu setzender Ort
	 */
	public void setOrt(String ort) {
		this.mOrt = ort;
	}

	/**
	 * Getter f�r den Ort des Termins
	 * @return Ort
	 */
	public String getOrt() {
		return mOrt;
	}
	
	/**
	 * Setter f�r den Start- und Endzeitpunkt des Termins
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
	 * Getter f�r den Startzeitpunkt des Termins
	 * @return Startzeitpunkt
	 */
	public Date getStart() {
		return mStart;
	}

	/**
	 * Getter f�r den Endzeitpunkt des Termins
	 * @return Endzeitpunkt
	 */
	public Date getEnde() {
		return mEnde;
	}

	/**
	 * Setter f�r die Beschreibung des Termins
	 * @param text Zu setzende Beschreibung
	 */
	public void setText(String text) {
		this.mText = text;
	}

	/**
	 * Getter f�r die Beschreibung des Termins
	 * @return Beschreibung
	 */
	public String getText() {
		return mText;
	}
	
	/**
	 * Setter f�r das zugeordnete Benutzerkonto des Termins
	 * @param benutzer Zu setzende Benutzerkonto
	 */
	public void setBenutzerkonto(String benutzer) {
		this.mBenutzerkonto = benutzer;
	}
	
	/**
	 * Getter f�r das zugeordnete Benutzerkonto des Termins
	 * @return Benutzerkonto
	 */
	public String getBenutzerkonto() {
		return mBenutzerkonto;
	}
	
	/**
	 * Getter f�r den zugeordneten Kontakt des Termins
	 * @return Kontakt
	 */
	public String getKontakt(){
		return mKontakt;
	}
	
	/**
	 * Setter f�r den zugeordneten Kontakt des Termins
	 * @param kontakt Zu setzende Kontakt
	 */
	public void setKontakt(String kontakt){
		this.mKontakt = kontakt;
	}

	@Override
	public int compareTo(Termin o) {
		return this.mStart.compareTo(o.mStart);
	}

	/**
	 * Gibt den Status des Termins zur�ck
	 * @return Status des Termins
	 */
	public Status getStatus() {
		return mStatus;
	}

	/**
	 * Setzt den Status des Termins
	 * @param mStatus Status des Termins
	 */
	public void setStatus(Status mStatus) {
		this.mStatus = mStatus;
	}

	/**
	 * Gibt die verkn�pfen Adressen zum Termin zur�ck
	 * @return Adressen zum Termin
	 */
	public InternetAddress[] getAdressen() {
		return mAdressen;  
	}

	/**
	 * Setzt die verkn�pfen Adressen zum Termin
	 * @param mAdressen Adressen zum Termin
	 */
	public void setAdressen(InternetAddress[] mAdressen) {
		this.mAdressen = mAdressen;
	}
}
