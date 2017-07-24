package de.outlookklon.model.mails;

import de.outlookklon.model.mails.MailInfo;
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
import org.apache.commons.lang3.StringUtils;

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
    public static String getID(Message message) throws MessagingException {
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
                    loadRead(serverMessage);
                    break;
                case SUBJECT:
                    loadSubject(serverMessage);
                    break;
                case SENDER:
                    loadSender(serverMessage);
                    break;
                case DATE:
                    loadDate(serverMessage);
                    break;
                case TEXT:
                    loadText(serverMessage);
                    break;
                case CONTENTTYPE:
                    loadContentType(serverMessage);
                    break;
                case TO:
                    loadTo(serverMessage);
                    break;
                case CC:
                    loadCc(serverMessage);
                    break;
                case ATTACHMENT:
                    loadAttachment(serverMessage);
                    break;
                default:
                    throw new IllegalStateException("Not implemented");
            }
        }
    }

    private void loadRead(Message serverMessage) throws MessagingException {
        if (!isReadLoaded()) {
            setRead(serverMessage.isSet(Flag.SEEN));
        }
    }

    private void loadSubject(Message serverMessage) throws MessagingException {
        if (!isSubjectLoaded()) {
            setSubject(serverMessage.getSubject());
        }
    }

    private void loadSender(Message serverMessage) throws MessagingException {
        if (!isSenderLoaded()) {
            setSender(serverMessage.getFrom()[0]);
        }
    }

    private void loadDate(Message serverMessage) throws MessagingException {
        if (!isDateLoaded()) {
            setDate(serverMessage.getSentDate());
        }
    }

    private void loadText(Message serverMessage) throws MessagingException, IOException {
        if (!isTextLoaded()) {
            setText(getText(serverMessage));
        }
    }

    private void loadContentType(Message serverMessage) throws IOException, MessagingException {
        if (!isContentTypeLoaded()) {
            setContentType(getType(serverMessage));
        }
    }

    private void loadTo(Message serverMessage) throws MessagingException {
        if (!isToLoaded()) {
            Address[] messageTo = serverMessage.getRecipients(RecipientType.TO);
            if (messageTo == null) {
                messageTo = new Address[0];
            }
            setTo(Arrays.asList(messageTo));
        }
    }

    private void loadCc(Message serverMessage) throws MessagingException {
        if (!isCcLoaded()) {
            Address[] messageCC = serverMessage.getRecipients(RecipientType.CC);
            if (messageCC == null) {
                messageCC = new Address[0];
            }
            setCc(Arrays.asList(messageCC));
        }
    }

    private void loadAttachment(Message serverMessage) throws IOException, MessagingException {
        if (!isAttachmentLoaded()) {
            List<String> messageAttachment = getAttachmentFromMessage(serverMessage);
            setAttachment(messageAttachment);
        }
    }

    private List<String> getAttachmentFromMessage(Message serverMessage) throws MessagingException, IOException {
        final List<String> messageAttachment = new ArrayList<>();
        if (serverMessage.getContent() instanceof Multipart) {
            final Multipart mp = (Multipart) serverMessage.getContent();

            for (int i = 0; i < mp.getCount(); i++) {
                final BodyPart bp = mp.getBodyPart(i);
                final String filename = bp.getFileName();

                if (!StringUtils.isBlank(filename)) {
                    messageAttachment.add(filename);
                }
            }
        }
        return messageAttachment;
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
        for (MailContent content : contents) {
            if (!hasAlreadyLoadedData(content)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gibt zurück, ob der angegebene Inhaltstyp bereits in die
     * StoredMailInfo-Instanz geladen wurde.
     *
     * @param content Inhaltsart, die geprüft werden soll
     * @return {@code true}, wenn die Inhaltsart bereits geladen wurde; sonst
     * {@code false}
     */
    public boolean hasAlreadyLoadedData(MailContent content) {
        switch (content) {
            case ID:
                return true;
            case READ:
                return isReadLoaded();
            case SUBJECT:
                return isSubjectLoaded();
            case SENDER:
                return isSenderLoaded();
            case DATE:
                return isDateLoaded();
            case TEXT:
                return isTextLoaded();
            case CONTENTTYPE:
                return isContentTypeLoaded();
            case TO:
                return isToLoaded();
            case CC:
                return isCcLoaded();
            case ATTACHMENT:
                return isAttachmentLoaded();
            default:
                throw new IllegalStateException("Not implemented");
        }
    }

    private boolean isReadLoaded() {
        return isRead() != null;
    }

    private boolean isSubjectLoaded() {
        return getSubject() != null;
    }

    private boolean isSenderLoaded() {
        return getSender() != null;
    }

    private boolean isDateLoaded() {
        return date != null;
    }

    private boolean isTextLoaded() {
        return getText() != null;
    }

    private boolean isContentTypeLoaded() {
        return getContentType() != null;
    }

    private boolean isToLoaded() {
        return getTo() != null;
    }

    private boolean isCcLoaded() {
        return getCc() != null;
    }

    private boolean isAttachmentLoaded() {
        return getAttachment() != null;
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

        final Multipart mp = (Multipart) p.getContent();
        if (p.isMimeType("multipart/alternative")) {
            return getMultipartAlternativeText(mp);
        } else if (p.isMimeType("multipart/*")) {
            for (int i = 0; i < mp.getCount(); i++) {
                final String s = getText(mp.getBodyPart(i));
                if (s != null) {
                    return s;
                }
            }
        }

        return null;
    }

    private static String getMultipartAlternativeText(Multipart mp) throws MessagingException, IOException {
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
