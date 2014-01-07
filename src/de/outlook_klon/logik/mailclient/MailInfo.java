package de.outlook_klon.logik.mailclient;

import java.io.Serializable;
import java.util.Date;

import javax.mail.Address;

/**
 * Datenklasse zum Halten von abgefragten Informationen von Mails
 * @author Hendrik Karwanni
 */
public class MailInfo implements Serializable, Comparable<MailInfo> {
	private static final long serialVersionUID = 7484178938043380415L;
	
	private String id;
	private boolean read;
	private String subject;
	private Address sender;
	private Date date;
	private String text;
	private String contentType;
	private Address[] to;
	private Address[] cc;
	private String[] attachment;

	/**
	 * Gibt die ID der Mail zurück
	 * @return (Eindeutige) ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Setzt die ID der Mail
	 * @param id (Eindeutige) ID der Mail
	 */
	public void setID(final String id) {
		this.id = id;
	}
	
	/**
	 * Gibt zurück, ob die Mail gelesen wurde
	 * @return true, wenn die Mail gelesen wurde; sonst false
	 */
	public boolean isRead() {
		return read;
	}

	/**
	 * Setzt, ob die Mail gelesen wurde
	 * @param read Lesestatus der Mail
	 */
	public void setRead(final boolean read) {
		this.read = read;
	}
	
	/**
	 * Gibt den Betreff der Mail zurück
	 * @return Betreff der Mail
	 */
	public String getSubject() {
		return subject;
	}
	
	/**
	 * Setzt den Betreff der Mail
	 * @param subject Betreff der Mail
	 */
	public void setSubject(final String subject) {
		this.subject = subject;
	}
	
	/**
	 * Gibt die Adresse des Senders der Mail zurück
	 * @return Adresse des Senders
	 */
	public Address getSender() {
		return sender;
	}
	
	/**
	 * Setzt die Adresse des Senders der Mail
	 * @param sender Adresse des Senders
	 */
	public void setSender(final Address sender) {
		this.sender = sender;
	}
	
	/**
	 * Gibt das Datum zurück, an dem die Mail gesendet wurde
	 * @return Versandtdatum der Mail
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * Setzt das Datum, an dem die Mail gesendet wurde
	 * @param date Versandtdatum der Mail
	 */
	public void setDate(final Date date) {
		this.date = date;
	}
	
	/**
	 * Gibt den Text der Mail zurück
	 * @return Text der Mail
	 */
	public String getText() {
		return text;
	}
	 
	/**
	 * Setzt den Text der Mail
	 * @param text Text der Mail
	 */
	public void setText(final String text) {
		this.text = text;
	}
	
	/**
	 * Gibt den Texttyp des Inhalts der Mail zurück
	 * @return Texttyp des Inhalts der Mail
	 */
	public String getContentType() {
		return contentType;
	}
	
	/**
	 * Setzt den Texttyp des Inhalts der Mail
	 * @param contentType Texttyp des Inhalts der Mail
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType.replace("text", "TEXT");
	}
	
	/**
	 * Gibt die Zieladressen der Mail zurück
	 * @return Zieladressen der Mail
	 */
	public Address[] getTo() {
		return to;
	}
	
	/**
	 * Setzt die Zieladressen der Mail
	 * @param to Zieladressen der Mail
	 */
	public void setTo(Address[] to) {
		this.to = to;
	}
	
	/**
	 * Gibt die Copy-Adressen der Mail zurück
	 * @return Copy-Adressen der Mail
	 */
	public Address[] getCc() {
		return cc;
	}
	
	/**
	 * Setzt die Copy-Adressen der Mail
	 * @param cc
	 */
	public void setCc(Address[] cc) {
		this.cc = cc;
	}

	/**
	 * Gibt die Namen der Anhänge der Mail zurück
	 * @return Namen der Anhänge der Mail
	 */
	public String[] getAttachment() {
		return attachment;
	}

	/**
	 * Setzt die Namen der Anhänge der Mail
	 * @param attachment Namen der Anhänge der Mail
	 */
	public void setAttachment(final String[] attachment) {
		this.attachment = attachment;
	}

	@Override
	public int compareTo(MailInfo o) {
		return date.compareTo(o.date);
	}
}
