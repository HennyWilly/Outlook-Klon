package de.outlook_klon.logik.mailclient;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Part;
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
public class SmtpServer extends SendServer {
	private static final long serialVersionUID = -4486714062786025360L;

	/**
	 * Erstellt eine neue Instanz eines SMTP-Servers mit den übergebenen
	 * Einstellungen
	 * 
	 * @param settings
	 *            Einstellungen zur Serververbindung
	 */
	public SmtpServer(final ServerSettings settings) {
		super(settings, "SMTP");
	}

	@Override
	protected Properties getProperties() {
		final int port = settings.getPort();
		final Verbindungssicherheit sicherheit = settings
				.getVerbingungssicherheit();
		final Properties props = System.getProperties();

		props.put("mail.smtp.debug", "true");
		props.put("mail.smtp.auth", "true");
		if (sicherheit == Verbindungssicherheit.STARTTLS) {
			props.put("mail.smtp.starttls.enable", "true");
		} else if (sicherheit == Verbindungssicherheit.SSL_TLS) {
			props.put("mail.smtp.socketFactory.port", port);
			props.put("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");
		}

		return props;
	}

	@Override
	public Message sendeMail(final String user, final String passwd,
			final InternetAddress from, final InternetAddress[] to,
			final InternetAddress[] cc, final String subject,
			final String text, final String format, final File[] attachments)
			throws MessagingException, IOException {
		final Verbindungssicherheit sicherheit = settings
				.getVerbingungssicherheit();

		final Session session = getSession(new StandardAuthenticator(user,
				passwd));

		final MimeMessage mail = new MimeMessage(session);
		mail.setFrom(from);

		for (final InternetAddress adrTo : to) {
			if(adrTo != null)
				mail.addRecipient(RecipientType.TO, adrTo);
		}

		if (cc != null && cc.length > 0) {
			for (final InternetAddress adrCC : cc) {
				if(adrCC != null)
					mail.addRecipient(RecipientType.CC, adrCC);
			}
		}

		mail.setSubject(subject);

		final MimeMultipart multiPart = new MimeMultipart();
		final MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(text, format);
		textPart.setDisposition(Part.INLINE);
		multiPart.addBodyPart(textPart);

		if (attachments != null) {
			for (final File attachment : attachments) {
				// Fügt jeden Anhang der Mail hinzu

				final MimeBodyPart attachmentPart = new MimeBodyPart();
				attachmentPart.attachFile(attachment);
				attachmentPart.setDisposition(Part.ATTACHMENT);
				multiPart.addBodyPart(attachmentPart);
			}
		}
		mail.setContent(multiPart);
		mail.setSentDate(new Date());

		Transport transport = null;
		if (sicherheit == Verbindungssicherheit.SSL_TLS) {
			transport = session.getTransport("smtps");
		} else {
			transport = session.getTransport("smtp");
		}

		transport.connect(settings.getHost(), settings.getPort(), user, passwd);
		transport.sendMessage(mail, mail.getAllRecipients());
		transport.close();
		
		mail.setFlag(Flags.Flag.SEEN, true);

		return mail;
	}

	@Override
	public boolean pruefeLogin(final String benutzername, final String passwort) {
		boolean result = true;

		final String host = settings.getHost();
		final int port = settings.getPort();
		final Verbindungssicherheit sicherheit = settings
				.getVerbingungssicherheit();

		final Session session = getSession(new StandardAuthenticator(
				benutzername, passwort));

		Transport transport = null;
		try {
			if (sicherheit == Verbindungssicherheit.SSL_TLS) {
				transport = session.getTransport("smtps");
			} else {
				transport = session.getTransport("smtp");
			}

			transport.connect(host, port, benutzername, passwort);
		} catch (MessagingException ex) {
			result = false;
		} finally {
			if (transport != null && transport.isConnected()) {
				try {
					transport.close();
				} catch (MessagingException e) {
				}
			}
		}

		return result;
	}
}
