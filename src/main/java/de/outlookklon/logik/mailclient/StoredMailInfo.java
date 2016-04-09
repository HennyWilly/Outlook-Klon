package de.outlookklon.logik.mailclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import java.util.ArrayList;
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

/**
 * Datenklasse zum Halten von abgefragten Informationen von Mails
 *
 * @author Hendrik Karwanni
 */
public class StoredMailInfo extends MailInfo implements Comparable<StoredMailInfo> {

    @JsonProperty("id")
    private String id;

    @JsonProperty("read")
    private boolean read;

    @JsonProperty("date")
    private Date date;

    /**
     * Erstellt eine neue Instanz der Klasse mit der �bergebenen ID
     *
     * @param id ID der Mail
     */
    public StoredMailInfo(String id) {
        super(null, null, null, null, null, null, null);

        setID(id);
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
            @JsonProperty("to") Address[] to,
            @JsonProperty("cc") Address[] cc,
            @JsonProperty("attachment") String[] attachment) {
        super(subject, sender, text, contentType, to, cc, attachment);

        setID(id);
        setRead(read);
        setDate(date);
    }

    /**
     * L�dt den Inhalt der �bergebenen Mail in die Klasse.
     *
     * @param serverMessage Mail-Object, das abgefragt wird
     * @param contents Inhaltsarten, die gespeichert werden sollen
     * @throws MessagingException wenn ein Fehler beim Lesen des Mail-Objekts
     * auftritt
     * @throws IOException wenn ein Fehler beim Lesen des Mail-Objekts auf der
     * Datenebene auftritt
     */
    public void loadData(Message serverMessage, Set<MailContent> contents)
            throws MessagingException, IOException {
        if (serverMessage == null) {
            throw new NullPointerException("serverMessage is null");
        }
        if (contents == null) {
            throw new NullPointerException("contents is null");
        }

        for (MailContent setContentType : contents) {
            switch (setContentType) {
                case ID:
                    if (getID() == null) {
                        throw new IllegalStateException("ID not set");
                    }
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
                        setTo(messageTo);
                    }
                    break;
                case CC:
                    if (getCc() == null) {
                        Address[] messageCC = serverMessage.getRecipients(RecipientType.CC);
                        if (messageCC == null) {
                            messageCC = new Address[0];
                        }
                        setCc(messageCC);
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

                        setAttachment(messageAttachment.toArray(new String[messageAttachment.size()]));
                    }
                    break;
                default:
                    throw new IllegalStateException("Not implemented");
            }
        }
    }

    /**
     * Gibt zur�ck, ob die angegebenen Inhaltstypen bereits in die
     * StoredMailInfo-Instanz geladen wurden.
     *
     * @param contents Inhaltsarten, die gepr�ft werden sollen
     * @return {@code true}, wenn alle Inhaltsarten bereits geladen wurden;
     * sonst {@code false}
     */
    public boolean hasAlreadyLoadedData(Set<MailContent> contents) {
        for (MailContent setContentType : contents) {
            switch (setContentType) {
                case ID:
                case READ:
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
     * Durchsucht den �bergebenen <code>Part</code> nach dem Text der E-Mail
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
     * Durchsucht den �bergebenen <code>Part</code> nach dem ContentType der
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
     * Gibt die ID der Mail zur�ck
     *
     * @return (Eindeutige) ID
     */
    public String getID() {
        return id;
    }

    private void setID(String id) {
        if (id == null) {
            throw new NullPointerException("id is null");
        }

        this.id = id;
    }

    /**
     * Gibt zur�ck, ob die Mail gelesen wurde
     *
     * @return true, wenn die Mail gelesen wurde; sonst false
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Setzt, ob die Mail gelesen wurde
     *
     * @param read Lesestatus der Mail
     */
    public void setRead(final boolean read) {
        this.read = read;
    }

    /**
     * Gibt das Datum zur�ck, an dem die Mail gesendet wurde
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
        if (obj != null && !(obj instanceof StoredMailInfo)) {
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