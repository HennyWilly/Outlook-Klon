package de.outlookklon.logik.mailclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import lombok.NonNull;

/**
 * Datenklasse zum Halten von abgefragten Informationen von Mails
 *
 * @author Hendrik Karwanni
 */
public class StoredMailInfo extends MailInfo implements Comparable<StoredMailInfo> {

    private static final String MESSAGE_ID_HEADER_NAME = "Message-Id";

    @JsonProperty("id")
    private String id;

    @JsonProperty("read")
    private Boolean read;

    @JsonProperty("date")
    private Date date;

    /**
     * Erstellt eine neue Instanz der Klasse mit der übergebenen ID
     *
     * @param id ID der Mail
     */
    public StoredMailInfo(String id) {
        super(null, null, null, null, null, null, null);

        setID(id);
    }

    /**
     * Erstellt eine neue Instanz der Klasse mit der übergebenen Servernachricht
     *
     * @param message Mail-Object von JavaMail
     * @throws javax.mail.MessagingException Tritt auf, wenn die ID nicht
     * abgefragt werden konnte.
     */
    public StoredMailInfo(Message message) throws MessagingException {
        this(getID(message));
    }

    @JsonCreator
    private StoredMailInfo(
            @JsonProperty("id") String id,
            @JsonProperty("read") boolean read,
            @JsonProperty("subject") String subject,
            @JsonProperty("sender") Address sender,
            @JsonProperty("date") Date date,
            @JsonProperty("text") String text,
            @JsonProperty("contentType") String contentType,
            @JsonProperty("to") List<Address> to,
            @JsonProperty("cc") List<Address> cc,
            @JsonProperty("attachment") List<String> attachment) {
        super(subject, sender, text, contentType, to, cc, attachment);

        setID(id);
        setRead(read);
        setDate(date);
    }

    /**
     * Gibt die ID zur übergebenen Mail zurück
     *
     * @param message Mail, für die die ID bestimmt werden soll
     * @return ID der Mail, oder <code>null</code>, wenn nicht gefunden
     */
    private static String getID(Message message) throws MessagingException {
        String[] tmpID = message.getHeader(MESSAGE_ID_HEADER_NAME);
        if (tmpID != null && tmpID.length > 0) {
            return tmpID[0];
        }

        String id = null;
        if (message instanceof MimeMessage) {
            MimeMessage mime = (MimeMessage) message;

            id = mime.getMessageID();
            if (id == null) {
                id = mime.getContentID();
            }
        }

        return id;
    }

    /**
     * Lädt den Inhalt der übergebenen Mail in die Klasse.
     *
     * @param serverMessage Mail-Object, das abgefragt wird
     * @param contents Inhaltsarten, die gespeichert werden sollen
     * @throws MessagingException wenn ein Fehler beim Lesen des Mail-Objekts
     * auftritt
     * @throws IOException wenn ein Fehler beim Lesen des Mail-Objekts auf der
     * Datenebene auftritt
     */
    public void loadData(@NonNull Message serverMessage, @NonNull Set<MailContent> contents)
            throws MessagingException, IOException {
        if (!getID(serverMessage).equals(getID())) {
            // TODO Localize me!!!
            throw new IllegalArgumentException("IDs don't match");
        }

        for (MailContent setContentType : contents) {
            switch (setContentType) {
                case ID:
                    // Haben wir schon ;-)
                    break;
                case READ:
                    setRead(serverMessage.isSet(Flag.SEEN));
                    break;
                case SUBJECT:
                    if (getSubject() == null) {
                        setSubject(serverMessage.getSubject());
                    }
                    break;
                case SENDER:
                    if (getSender() == null) {
                        setSender(serverMessage.getFrom()[0]);
                    }
                    break;
                case DATE:
                    if (date == null) {
                        setDate(serverMessage.getSentDate());
                    }
                    break;
                case TEXT:
                    if (getText() == null) {
                        setText(getText(serverMessage));
                    }
                    break;
                case CONTENTTYPE:
                    if (getContentType() == null) {
                        setContentType(getType(serverMessage));
                    }
                    break;
                case TO:
                    if (getTo() == null) {
                        Address[] messageTo = serverMessage.getRecipients(RecipientType.TO);
                        if (messageTo == null) {
                            messageTo = new Address[0];
                        }
                        setTo(Arrays.asList(messageTo));
                    }
                    break;
                case CC:
                    if (getCc() == null) {
                        Address[] messageCC = serverMessage.getRecipients(RecipientType.CC);
                        if (messageCC == null) {
                            messageCC = new Address[0];
                        }
                        setCc(Arrays.asList(messageCC));
                    }
                    break;
                case ATTACHMENT:
                    if (getAttachment() == null) {
                        final List<String> messageAttachment = new ArrayList<>();
                        if (serverMessage.getContent() instanceof Multipart) {
                            final Multipart mp = (Multipart) serverMessage.getContent();

                            for (int i = 0; i < mp.getCount(); i++) {
                                final BodyPart bp = mp.getBodyPart(i);
                                final String filename = bp.getFileName();

                                if (filename != null && !filename.isEmpty()) {
                                    messageAttachment.add(bp.getFileName());
                                }
                            }
                        }

                        setAttachment(messageAttachment);
                    }
                    break;
                default:
                    throw new IllegalStateException("Not implemented");
            }
        }
    }

    /**
     * Gibt zurück, ob die angegebenen Inhaltstypen bereits in die
     * StoredMailInfo-Instanz geladen wurden.
     *
     * @param contents Inhaltsarten, die geprüft werden sollen
     * @return {@code true}, wenn alle Inhaltsarten bereits geladen wurden;
     * sonst {@code false}
     */
    public boolean hasAlreadyLoadedData(Set<MailContent> contents) {
        for (MailContent setContentType : contents) {
            switch (setContentType) {
                case ID:
                    break;
                case READ:
                    if (isRead() == null) {
                        return false;
                    }
                    break;
                case SUBJECT:
                    if (getSubject() == null) {
                        return false;
                    }
                    break;
                case SENDER:
                    if (getSender() == null) {
                        return false;
                    }
                    break;
                case DATE:
                    if (date == null) {
                        return false;
                    }
                    break;
                case TEXT:
                    if (getText() == null) {
                        return false;
                    }
                    break;
                case CONTENTTYPE:
                    if (getContentType() == null) {
                        return false;
                    }
                    break;
                case TO:
                    if (getTo() == null) {
                        return false;
                    }
                    break;
                case CC:
                    if (getCc() == null) {
                        return false;
                    }
                    break;
                case ATTACHMENT:
                    if (getAttachment() == null) {
                        return false;
                    }
                    break;
                default:
                    throw new IllegalStateException("Not implemented");
            }
        }

        return true;
    }

    /**
     * Durchsucht den übergebenen <code>Part</code> nach dem Text der E-Mail
     *
     * @param p <code>Part</code>-Objekt, indem der Text gesucht werden soll
     * @return Text der E-Mail
     */
    private static String getText(final Part p) throws MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            return (String) p.getContent();
        }

        if (p.isMimeType("multipart/alternative")) {
            final Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                final Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null) {
                        text = getText(bp);
                    }
                } else if (bp.isMimeType("text/html")) {
                    final String s = getText(bp);
                    if (s != null) {
                        return s;
                    }
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            final Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                final String s = getText(mp.getBodyPart(i));
                if (s != null) {
                    return s;
                }
            }
        }

        return null;
    }

    /**
     * Durchsucht den übergebenen <code>Part</code> nach dem ContentType der
     * E-Mail
     *
     * @param p <code>Part</code>-Objekt, indem der Text gesucht werden soll
     * @return ContentType der E-Mail
     */
    private static String getType(final Part p) throws IOException, MessagingException {
        if (p.isMimeType("text/*")) {
            return p.getContentType();
        }

        final Object content = p.getContent();
        if (content instanceof Multipart) {
            final Multipart mp = (Multipart) content;
            for (int i = 0; i < mp.getCount(); i++) {
                final BodyPart bp = mp.getBodyPart(i);
                String disposition = bp.getDisposition();
                if (disposition != null && disposition.equalsIgnoreCase(Part.ATTACHMENT)) {
                    continue;
                }

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

    private void setID(@NonNull String id) {
        this.id = id;
    }

    /**
     * Gibt zurück, ob die Mail gelesen wurde
     *
     * @return true, wenn die Mail gelesen wurde; sonst false
     */
    public Boolean isRead() {
        return read;
    }

    /**
     * Setzt, ob die Mail gelesen wurde
     *
     * @param read Lesestatus der Mail
     */
    public void setRead(final Boolean read) {
        this.read = read;
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
     * @param date Versandtdatum der Mail
     */
    public void setDate(final Date date) {
        this.date = date;
    }

    @Override
    public int compareTo(StoredMailInfo o) {
        return date.compareTo(o.date);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof StoredMailInfo)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        StoredMailInfo info = (StoredMailInfo) obj;
        return id.equals(info.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
