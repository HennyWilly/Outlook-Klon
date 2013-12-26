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
	public SmtpServer(ServerSettings settings) {
		super(settings, "SMTP");
	}

	@Override
	public void sendeMail(String user, String pw, InternetAddress from, InternetAddress[] to, InternetAddress[] cc, String subject, String text, String format, File[] attachments) 
						throws MessagingException, IOException {     
		Authenticator auth = new StandardAuthentificator(user, pw);
		
		String host = settings.getHost();
		int port = settings.getPort();
		Verbindungssicherheit sicherheit = settings.getVerbingungssicherheit();
		
		Properties props = new Properties();
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
		
        Session session = Session.getInstance(props, auth);
		session.setDebug(true);
        
		MimeMessage mail = new MimeMessage(session);
		mail.setFrom(from);
		
		for(InternetAddress adrTo : to) {
			mail.addRecipient(RecipientType.TO, adrTo);
		}
		
		if(cc.length > 0)
			for(InternetAddress adrCC : cc) {
				mail.addRecipient(RecipientType.CC, adrCC);
			}
		
		mail.setSubject(subject);
		

		MimeMultipart multiPart = new MimeMultipart();
		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(text, format);
		textPart.setDisposition(MimeBodyPart.INLINE);
		multiPart.addBodyPart(textPart);
		
		if(attachments != null) {
			for(File attachment : attachments) {
				MimeBodyPart attachmentPart = new MimeBodyPart();
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
		
		transport.connect(host, port, user, pw);
		transport.sendMessage(mail, mail.getAllRecipients());
		transport.close();
	}
}
