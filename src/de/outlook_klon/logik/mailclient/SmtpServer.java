package de.outlook_klon.logik.mailclient;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Diese Klasse stellt einen Simple-Mail-Transport-Server(SMTP) dar.
 * 
 * @author Hendrik Karwanni
 */
public class SmtpServer extends SendServer{
	private static final long serialVersionUID = -4486714062786025360L;

	/**
	 * Erstellt eine neue Instanz eines SMTP-Servers mit den übergebenen Einstellungen
	 * @param settings Einstellungen zur Serververbindung
	 */
	public SmtpServer(final ServerSettings settings) {
		super(settings, "SMTP");
	}

	@Override
	public void sendeMail(final String user, final String passwd, final InternetAddress from, final InternetAddress[] to, final InternetAddress[] cc, 
			final String subject, final String text, final String format, final File[] attachments) 
						throws MessagingException, IOException {     
		final Authenticator auth = new StandardAuthentificator(user, passwd);
		
		final String host = settings.getHost();
		final int port = settings.getPort();
		final Verbindungssicherheit sicherheit = settings.getVerbingungssicherheit();
		
		final Properties props = new Properties();
		//props.put("mail.smtp.user", user);
		//props.put("mail.smtp.host", host);
		//props.put("mail.smtp.port", port);
		props.put("mail.smtp.debug", "true");
		props.put("mail.smtp.auth", "true");
		
		if(sicherheit == Verbindungssicherheit.STARTTLS) {
			props.put("mail.smtp.starttls.enable","true");
		}
		else if(sicherheit == Verbindungssicherheit.SSL_TLS) {
			props.put("mail.smtp.socketFactory.port", port);
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");
		}
		
		final Session session = Session.getInstance(props, auth);
		session.setDebug(true);
        
		final MimeMessage mail = new MimeMessage(session);
		mail.setFrom(from);
		
		for(final InternetAddress adrTo : to) {
			mail.addRecipient(RecipientType.TO, adrTo);
		}
		
		if(cc.length > 0)
			for(final InternetAddress adrCC : cc) {
				mail.addRecipient(RecipientType.CC, adrCC);
			}
		
		mail.setSubject(subject);
		

		final MimeMultipart multiPart = new MimeMultipart();
		final MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(text, format);
		textPart.setDisposition(MimeBodyPart.INLINE);
		multiPart.addBodyPart(textPart);
		
		if(attachments != null) {
			for(final File attachment : attachments) {
				final MimeBodyPart attachmentPart = new MimeBodyPart();
				attachmentPart.attachFile(attachment);
				attachmentPart.setDisposition(MimeBodyPart.ATTACHMENT);
				multiPart.addBodyPart(attachmentPart);
			}			
		}
		mail.setContent(multiPart);
		
		Transport transport = null;
		if(sicherheit == Verbindungssicherheit.SSL_TLS) {
			transport = session.getTransport("smtps");
		}
		else {
			transport = session.getTransport("smtp");
		}
		
		transport.connect(host, port, user, passwd);
		transport.sendMessage(mail, mail.getAllRecipients());
		transport.close();
	}
	
	public boolean pruefeLogin(final String benutzername, final String passwort){
		boolean result = true;
		
		final Authenticator auth = new StandardAuthentificator(benutzername, passwort);
		
		final String host = settings.getHost();
		final int port = settings.getPort();
		final Verbindungssicherheit sicherheit = settings.getVerbingungssicherheit();
		
		final Properties props = new Properties();
		//props.put("mail.smtp.user", user);
		//props.put("mail.smtp.host", host);
		//props.put("mail.smtp.port", port);
		props.put("mail.smtp.debug", "true");
		props.put("mail.smtp.auth", "true");
		
		if(sicherheit == Verbindungssicherheit.STARTTLS) {
			props.put("mail.smtp.starttls.enable","true");
		}
		else if(sicherheit == Verbindungssicherheit.SSL_TLS) {
			props.put("mail.smtp.socketFactory.port", port);
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");
		}
		
		final Session session = Session.getInstance(props, auth);
		session.setDebug(true);
		
		Transport transport = null;
		try {
			if(sicherheit == Verbindungssicherheit.SSL_TLS) {
				transport = session.getTransport("smtps");
			}
			else {
				transport = session.getTransport("smtp");
			}
			
			transport.connect(host, port, benutzername, passwort);
		} catch(MessagingException ex) {
			result = false;
		} finally {
			if(transport != null && transport.isConnected()) {
				try {
					transport.close();
				} catch (MessagingException e) { }
			}
		}

		return result;
	}
}
