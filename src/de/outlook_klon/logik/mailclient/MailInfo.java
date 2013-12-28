package de.outlook_klon.logik.mailclient;

import java.util.Date;

import javax.mail.Address;

public class MailInfo {
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
		this.contentType = contentType;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Address getSender() {
		return sender;
	}
	
	public void setSender(Address sender) {
		this.sender = sender;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public String getID() {
		return id;
	}
	
	public void setID(String id) {
		this.id = id;
	}

	public String[] getAttachment() {
		return attachment;
	}

	public void setAttachment(String[] attachment) {
		this.attachment = attachment;
	}
}
