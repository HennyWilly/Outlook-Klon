package de.outlookklon.logik.mailclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMailInfo extends MailInfo {

    /**
     * Erstellt eine neue SendMailInfo-Instanz mit den übergebenen Werten.
     *
     * @param subject Betreff der Mail
     * @param text Text der Mail
     * @param contentType Typ des Mailtextes
     * @param to Primäre Empfänger der Mail
     * @param cc Sekundäre Empfänger der Mail
     * @param attachment Anhänge der Mail
     */
    public SendMailInfo(String subject, String text, String contentType,
            List<Address> to, List<Address> cc, List<String> attachment) {
        super(subject, text, contentType, to, cc, attachment);
    }

    /**
     * Erstellt eine neue SendMailInfo-Instanz mit den übergebenen Werten.
     *
     * @param subject Betreff der Mail
     * @param sender Sender der Mail
     * @param text Text der Mail
     * @param contentType Typ des Mailtextes
     * @param to Primäre Empfänger der Mail
     * @param cc Sekundäre Empfänger der Mail
     * @param attachment Anhänge der Mail
     */
    @JsonCreator
    private SendMailInfo(
            @JsonProperty("subject") String subject,
            @JsonProperty("sender") Address sender,
            @JsonProperty("text") String text,
            @JsonProperty("contentType") String contentType,
            @JsonProperty("to") List<Address> to,
            @JsonProperty("cc") List<Address> cc,
            @JsonProperty("attachment") List<String> attachment) {
        super(subject, sender, text, contentType, to, cc, attachment);
    }

    /**
     * Erstellt ein neues Message-Objekt, das gesendet werden kann.
     *
     * @param session Die Session des Servers
     * @return Ein noch nicht versendetes Message-Objekt
     * @throws MessagingException tritt auf, wenn ein Attribut nicht in das
     * Message-Objekt geschrieben werden konnte.
     */
    public Message createMessage(Session session)
            throws MessagingException {
        final Message mail = new MimeMessage(session);

        mail.setFrom(getSender());
        mail.addRecipients(Message.RecipientType.TO, addressListToArray(getTo()));
        mail.addRecipients(Message.RecipientType.CC, addressListToArray(getCc()));
        mail.setSubject(getSubject());

        final MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(getText(), getContentType());
        textPart.setDisposition(Part.INLINE);

        final MimeMultipart multiPart = new MimeMultipart();
        multiPart.addBodyPart(textPart);

        List<String> attachments = getAttachment();
        if (attachments != null) {
            try {
                for (final String strAttachment : attachments) {
                    // Fügt jeden Anhang der Mail hinzu

                    final MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(strAttachment);
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

    private static Address[] addressListToArray(List<Address> list) {
        return list.toArray(new Address[list.size()]);
    }
}
