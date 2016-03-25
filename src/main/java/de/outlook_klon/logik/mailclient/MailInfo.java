package de.outlook_klon.logik.mailclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Flags.Flag;
import javax.mail.Message.RecipientType;

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

	public void loadData(Message serverMessage, Set<MailContent> contents) throws MessagingException, IOException {
		if (serverMessage == null) {
			throw new NullPointerException("serverMessage is null");
		}
		if (contents == null) {
			throw new NullPointerException("contents is null");
		}

		for (MailContent contentType : contents) {
			switch (contentType) {
			case ID:
				if(getID() == null) {
					throw new IllegalStateException("ID not set");
				}
				break;
			case READ:
				setRead(serverMessage.isSet(Flag.SEEN));
				break;
			case SUBJECT:
				if (getSubject() == null)
					setSubject(serverMessage.getSubject());
				break;
			case SENDER:
				if (getSender() == null)
					setSender(serverMessage.getFrom()[0]);
				break;
			case DATE:
				if (getDate() == null)
					setDate(serverMessage.getSentDate());
				break;
			case TEXT:
				if (getText() == null)
					setText(getText(serverMessage));
				break;
			case CONTENTTYPE:
				if (getContentType() == null)
					setContentType(getType(serverMessage));
				break;
			case TO:
				if (getTo() == null) {
					Address[] to = serverMessage.getRecipients(RecipientType.TO);
					if (to == null)
						to = new Address[0];
					setTo(to);
				}
				break;
			case CC:
				if (getCc() == null) {
					Address[] cc = serverMessage.getRecipients(RecipientType.CC);
					if (cc == null)
						cc = new Address[0];
					setCc(cc);
				}
				break;
			case ATTACHMENT:
				if (getAttachment() == null) {
					final ArrayList<String> attachment = new ArrayList<String>();
					if (serverMessage.getContent() instanceof Multipart) {
						final Multipart mp = (Multipart) serverMessage.getContent();

						for (int i = 0; i < mp.getCount(); i++) {
							final BodyPart bp = mp.getBodyPart(i);
							final String filename = bp.getFileName();

							if (filename != null && !filename.isEmpty())
								attachment.add(bp.getFileName());
						}
					}

					setAttachment(attachment.toArray(new String[attachment.size()]));
				}
				break;
			default:
				throw new IllegalStateException("Not implemented");
			}
		}
	}

	/**
	 * Durchsucht den übergebenen <code>Part</code> nach dem Text der E-Mail
	 * 
	 * @param p
	 *            <code>Part</code>-Objekt, indem der Text gesucht werden soll
	 * @return Text der E-Mail
	 */
	private String getText(final Part p) throws MessagingException, IOException {
		if (p.isMimeType("text/*")) {
			return (String) p.getContent();
		}

		if (p.isMimeType("multipart/alternative")) {
			final Multipart mp = (Multipart) p.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
				final Part bp = mp.getBodyPart(i);
				if (bp.isMimeType("text/plain")) {
					if (text == null)
						text = getText(bp);
					continue;
				} else if (bp.isMimeType("text/html")) {
					final String s = getText(bp);
					if (s != null)
						return s;
				} else
					return getText(bp);
			}
			return text;
		} else if (p.isMimeType("multipart/*")) {
			final Multipart mp = (Multipart) p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				final String s = getText(mp.getBodyPart(i));
				if (s != null)
					return s;
			}
		}

		return null;
	}

	/**
	 * Durchsucht den übergebenen <code>Part</code> nach dem ContentType der
	 * E-Mail
	 * 
	 * @param p
	 *            <code>Part</code>-Objekt, indem der Text gesucht werden soll
	 * @return ContentType der E-Mail
	 */
	private String getType(final Part p) throws IOException, MessagingException {
		if (p.isMimeType("text/*"))
			return p.getContentType();

		final Object content = p.getContent();
		if (content instanceof Multipart) {
			final Multipart mp = (Multipart) content;
			for (int i = 0; i < mp.getCount(); i++) {
				final BodyPart bp = mp.getBodyPart(i);
				if (bp.getDisposition() == Part.ATTACHMENT)
					continue;

				return getType(bp);
			}
		}
		return "text/plain";
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
		if(id == null)
			throw new NullPointerException("id is null");
		
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
