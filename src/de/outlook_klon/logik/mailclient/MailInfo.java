package de.outlook_klon.logik.mailclient;

import java.io.Serializable;
import java.util.Date;

import javax.mail.Address;

public class MailInfo implements Serializable{
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
	
	public Address[] getCc() {
		return cc;
	}
	
	public void setCc(Address[] cc) {
		this.cc = cc;
	}
	
	public Address[] getTo() {
		return to;
	}
	
	public void setTo(Address[] to) {
		this.to = to;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType.replace("text", "TEXT");
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(final String text) {
		this.text = text;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(final Date date) {
		this.date = date;
	}
	
	public Address getSender() {
		return sender;
	}
	
	public void setSender(final Address sender) {
		this.sender = sender;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(final String subject) {
		this.subject = subject;
	}
	
	public boolean isRead() {
		return read;
	}

	public void setRead(final boolean read) {
		this.read = read;
	}

	public String getID() {
		return id;
	}
	
	public void setID(final String id) {
		this.id = id;
	}

	public String[] getAttachment() {
		return attachment;
	}

	public void setAttachment(final String[] attachment) {
		this.attachment = attachment;
	}
}
