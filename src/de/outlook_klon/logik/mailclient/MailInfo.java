package de.outlook_klon.logik.mailclient;

import java.util.Date;

public class MailInfo {
	private String id;
	private String subject;
	private String sender;
	private Date date;
	
	public MailInfo(String id, String subject, String sender, Date date) {
		this.id = id;
		this.subject = subject;
		this.sender = sender;
		this.date = date;
	}
	
	public String getID() {
		return id;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public String getSender() {
		return sender;
	}
	
	public Date getDate() {
		return date;
	}
}
