package de.outlook_klon.logik.mailclient;

import java.util.Date;

import javax.mail.Address;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Datenklasse zum Halten von abgefragten Informationen von Mails
 * 
 * @author Hendrik Karwanni
 */
public class MailInfo implements Comparable<MailInfo> {

	@JsonProperty("id")
	private String id;

	@JsonProperty("read")
	private boolean read;

	@JsonProperty("subject")
	private String subject;

	@JsonProperty("sender")
	private Address sender;

	@JsonProperty("date")
	private Date date;

	@JsonProperty("text")
	private String text;

	@JsonProperty("contentType")
	private String contentType;

	@JsonProperty("to")
	private Address[] to;

	@JsonProperty("cc")
	private Address[] cc;

	@JsonProperty("attachment")
	private String[] attachment;

	/**
	 * Erstellt eine neue Instanz der Klasse mit der übergebenen ID
	 * 
	 * @param id
	 *            ID der Mail
	 */
	public MailInfo(String id) {
		if (id == null || id.trim().isEmpty())
			throw new NullPointerException("Die ID darf niemals leer sein");
		this.id = id;
	}

	@JsonCreator
	private MailInfo(@JsonProperty("id") String id, @JsonProperty("read") boolean read,
			@JsonProperty("subject") String subject, @JsonProperty("sender") Address sender,
			@JsonProperty("date") Date date, @JsonProperty("text") String text,
			@JsonProperty("contentType") String contentType, @JsonProperty("to") Address[] to,
			@JsonProperty("cc") Address[] cc, @JsonProperty("attachment") String[] attachment) {
		setID(id);
		setRead(read);
		setSubject(subject);
		setSender(sender);
		setDate(date);
		setText(text);
		setContentType(contentType);
		setTo(to);
		setCc(cc);
		setAttachment(attachment);
	}

	/**
	 * Gibt die ID der Mail zurück
	 * 
	 * @return (Eindeutige) ID
	 */
	public String getID() {
		return id;
	}

	private void setID(String id) {
		this.id = id;
	}

	/**
	 * Gibt zurück, ob die Mail gelesen wurde
	 * 
	 * @return true, wenn die Mail gelesen wurde; sonst false
	 */
	public boolean isRead() {
		return read;
	}

	/**
	 * Setzt, ob die Mail gelesen wurde
	 * 
	 * @param read
	 *            Lesestatus der Mail
	 */
	public void setRead(final boolean read) {
		this.read = read;
	}

	/**
	 * Gibt den Betreff der Mail zurück
	 * 
	 * @return Betreff der Mail
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Setzt den Betreff der Mail
	 * 
	 * @param subject
	 *            Betreff der Mail
	 */
	public void setSubject(final String subject) {
		this.subject = subject;
	}

	/**
	 * Gibt die Adresse des Senders der Mail zurück
	 * 
	 * @return Adresse des Senders
	 */
	public Address getSender() {
		return sender;
	}

	/**
	 * Setzt die Adresse des Senders der Mail
	 * 
	 * @param sender
	 *            Adresse des Senders
	 */
	public void setSender(final Address sender) {
		this.sender = sender;
	}

	/**
	 * Gibt das Datum zurück, an dem die Mail gesendet wurde
	 * 
	 * @return Versandtdatum der Mail
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Setzt das Datum, an dem die Mail gesendet wurde
	 * 
	 * @param date
	 *            Versandtdatum der Mail
	 */
	public void setDate(final Date date) {
		this.date = date;
	}

	/**
	 * Gibt den Text der Mail zurück
	 * 
	 * @return Text der Mail
	 */
	public String getText() {
		return text;
	}

	/**
	 * Setzt den Text der Mail
	 * 
	 * @param text
	 *            Text der Mail
	 */
	public void setText(final String text) {
		this.text = text;
	}

	/**
	 * Gibt den Texttyp des Inhalts der Mail zurück
	 * 
	 * @return Texttyp des Inhalts der Mail
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Setzt den Texttyp des Inhalts der Mail
	 * 
	 * @param contentType
	 *            Texttyp des Inhalts der Mail
	 */
	public void setContentType(String contentType) {
		if (contentType != null) {
			this.contentType = contentType.replace("text", "TEXT");
		} else {
			this.contentType = null;
		}
	}

	/**
	 * Gibt die Zieladressen der Mail zurück
	 * 
	 * @return Zieladressen der Mail
	 */
	public Address[] getTo() {
		return to;
	}

	/**
	 * Setzt die Zieladressen der Mail
	 * 
	 * @param to
	 *            Zieladressen der Mail
	 */
	public void setTo(Address[] to) {
		this.to = to;
	}

	/**
	 * Gibt die Copy-Adressen der Mail zurück
	 * 
	 * @return Copy-Adressen der Mail
	 */
	public Address[] getCc() {
		return cc;
	}

	/**
	 * Setzt die Copy-Adressen der Mail
	 * 
	 * @param cc
	 */
	public void setCc(Address[] cc) {
		this.cc = cc;
	}

	/**
	 * Gibt die Namen der Anhänge der Mail zurück
	 * 
	 * @return Namen der Anhänge der Mail
	 */
	public String[] getAttachment() {
		return attachment;
	}

	/**
	 * Setzt die Namen der Anhänge der Mail
	 * 
	 * @param attachment
	 *            Namen der Anhänge der Mail
	 */
	public void setAttachment(final String[] attachment) {
		this.attachment = attachment;
	}

	@Override
	public int compareTo(MailInfo o) {
		return date.compareTo(o.date);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && !(obj instanceof MailInfo))
			return false;
		if (this == obj)
			return true;

		MailInfo info = (MailInfo) obj;
		return id.equals(info.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
