package de.outlookklon.logik.mailclient;

import java.io.IOException;
import java.util.Date;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Abstrakte Basisklasse f�r alle Mailserver, �ber die Mails gesendet werden
 * k�nnen. Stellt grundlegende Funtionen zum Versenden von Mails bereit.
 *
 * @author Hendrik Karwanni
 */
public abstract class OutboxServer extends MailServer {

    private static final long serialVersionUID = -4191787147022537178L;

    /**
     * Wird von abgeleiteten Klassen aufgerufen, um interne Attribute zu
     * initialisieren
     *
     * @param settings Einstellungen zum Verbindungsaufbau
     * @param serverType Beschreibender String zum Servertyp
     */
    protected OutboxServer(final ServerSettings settings, final String serverType) {
        super(settings, serverType);
    }

    /**
     * Sendet eine E-Mail �ber den aktuellen Server. Die Implementierung des
     * Vorgangs ist vom Serverprotokoll abh�ngig
     *
     * @param user Benutzername des Senders
     * @param password Passwort des Senders
     * @param mailToSend Objekt, das alle zu sendenden Daten enth�lt
     * @return Gibt die gesendete Mail zur�ck
     * @throws MessagingException Tritt auf, wenn das Senden der Mail
     * fehlschl�gt oder einer der zu sendenden Anh�nge nicht gefunden wurde
     */
    public abstract Message sendeMail(String user, String password, MailInfo mailToSend)
            throws MessagingException;

    /**
     * Erstellt ein neues Message-Objekt, das gesendet werden kann.
     *
     * @param mailToSend Zu sendende Daten
     * @param session Die Session des Servers
     * @return Ein noch nicht versendetes Message-Objekt
     * @throws MessagingException tritt auf, wenn ein Attribut nicht in das
     * Message-Objekt geschrieben werden konnte.
     */
    protected Message createMessage(MailInfo mailToSend, Session session)
            throws MessagingException {
        final Message mail = new MimeMessage(session);

        mail.setFrom(mailToSend.getSender());
        mail.addRecipients(Message.RecipientType.TO, mailToSend.getTo());
        mail.addRecipients(Message.RecipientType.CC, mailToSend.getCc());
        mail.setSubject(mailToSend.getSubject());

        final MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(mailToSend.getText(), mailToSend.getContentType());
        textPart.setDisposition(Part.INLINE);

        final MimeMultipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(textPart);

        String[] attachments = mailToSend.getAttachment();
        if (attachments != null) {
            try {
                for (final String attachment : attachments) {
                    // F�gt jeden Anhang der Mail hinzu

                    final MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(attachment);
                    attachmentPart.setDisposition(Part.ATTACHMENT);
                    multiPart.addBodyPart(attachmentPart);
                }
            } catch (IOException ex) {
                throw new MessagingException("Could not access attachment", ex);
            }
        }

        mail.setContent(multiPart);
        mail.setSentDate(new Date());

        return mail;
    }
}
