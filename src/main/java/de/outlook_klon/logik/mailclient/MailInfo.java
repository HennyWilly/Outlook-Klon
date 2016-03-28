package de.outlook_klon.logik.mailclient;

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
public class MailInfo implements Comparable<MailInfo> {

    @JsonProperty("id")
    private String id;

    @JsonProperty("read")
    private boolean read;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("sender")
    private Address sender;

    @JsonProperty("date")
    private Date date;

    @JsonProperty("text")
    private String text;

    @JsonProperty("contentType")
    private String contentType;

    @JsonProperty("to")
    private Address[] to;

    @JsonProperty("cc")
    private Address[] cc;

    @JsonProperty("attachment")
    private String[] attachment;

    /**
     * Erstellt eine neue Instanz der Klasse mit der �bergebenen ID
     *
     * @param id ID der Mail
     */
    public MailInfo(String id) {
        setID(id);
    }

    @JsonCreator
    private MailInfo(
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
        setID(id);
        setRead(read);
        setSubject(subject);
        setSender(sender);
        setDate(date);
        setText(text);
        setContentType(contentType);
        setTo(to);
        setCc(cc);
        setAttachment(attachment);
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
                    if (subject == null) {
                        setSubject(serverMessage.getSubject());
                    }
                    break;
                case SENDER:
                    if (sender == null) {
                        setSender(serverMessage.getFrom()[0]);
                    }
                    break;
                case DATE:
                    if (date == null) {
                        setDate(serverMessage.getSentDate());
                    }
                    break;
                case TEXT:
                    if (text == null) {
                        setText(getText(serverMessage));
                    }
                    break;
                case CONTENTTYPE:
                    if (contentType == null) {
                        setContentType(getType(serverMessage));
                    }
                    break;
                case TO:
                    if (to == null) {
                        Address[] messageTo = serverMessage.getRecipients(RecipientType.TO);
                        if (messageTo == null) {
                            messageTo = new Address[0];
                        }
                        setTo(messageTo);
                    }
                    break;
                case CC:
                    if (cc == null) {
                        Address[] messageCC = serverMessage.getRecipients(RecipientType.CC);
                        if (messageCC == null) {
                            messageCC = new Address[0];
                        }
                        setCc(messageCC);
                    }
                    break;
                case ATTACHMENT:
                    if (attachment == null) {
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
     * MailInfo-Instanz geladen wurden.
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
                    if (subject == null) {
                        return false;
                    }
                    break;
                case SENDER:
                    if (sender == null) {
                        return false;
                    }
                    break;
                case DATE:
                    if (date == null) {
                        return false;
                    }
                    break;
                case TEXT:
                    if (text == null) {
                        return false;
                    }
                    break;
                case CONTENTTYPE:
                    if (contentType == null) {
                        return false;
                    }
                    break;
                case TO:
                    if (to == null) {
                        return false;
                    }
                    break;
                case CC:
                    if (cc == null) {
                        return false;
                    }
                    break;
                case ATTACHMENT:
                    if (attachment == null) {
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
     * Gibt den Betreff der Mail zur�ck
     *
     * @return Betreff der Mail
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Setzt den Betreff der Mail
     *
     * @param subject Betreff der Mail
     */
    public void setSubject(final String subject) {
        this.subject = subject;
    }

    /**
     * Gibt die Adresse des Senders der Mail zur�ck
     *
     * @return Adresse des Senders
     */
    public Address getSender() {
        return sender;
    }

    /**
     * Setzt die Adresse des Senders der Mail
     *
     * @param sender Adresse des Senders
     */
    public void setSender(final Address sender) {
        this.sender = sender;
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

    /**
     * Gibt den Text der Mail zur�ck
     *
     * @return Text der Mail
     */
    public String getText() {
        return text;
    }

    /**
     * Setzt den Text der Mail
     *
     * @param text Text der Mail
     */
    public void setText(final String text) {
        this.text = text;
    }

    /**
     * Gibt den Texttyp des Inhalts der Mail zur�ck
     *
     * @return Texttyp des Inhalts der Mail
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Setzt den Texttyp des Inhalts der Mail
     *
     * @param contentType Texttyp des Inhalts der Mail
     */
    public void setContentType(String contentType) {
        if (contentType != null) {
            this.contentType = contentType.replace("text", "TEXT");
        } else {
            this.contentType = null;
        }
    }

    /**
     * Gibt die Zieladressen der Mail zur�ck
     *
     * @return Zieladressen der Mail
     */
    public Address[] getTo() {
        return to;
    }

    /**
     * Setzt die Zieladressen der Mail
     *
     * @param to Zieladressen der Mail
     */
    public void setTo(Address[] to) {
        this.to = to;
    }

    /**
     * Gibt die Copy-Adressen der Mail zur�ck
     *
     * @return Copy-Adressen der Mail
     */
    public Address[] getCc() {
        return cc;
    }

    /**
     * Setzt die Copy-Adressen der Mail
     *
     * @param cc
     */
    public void setCc(Address[] cc) {
        this.cc = cc;
    }

    /**
     * Gibt die Namen der Anh�nge der Mail zur�ck
     *
     * @return Namen der Anh�nge der Mail
     */
    public String[] getAttachment() {
        return attachment;
    }

    /**
     * Setzt die Namen der Anh�nge der Mail
     *
     * @param attachment Namen der Anh�nge der Mail
     */
    public void setAttachment(final String[] attachment) {
        this.attachment = attachment;
    }

    @Override
    public int compareTo(MailInfo o) {
        return date.compareTo(o.date);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && !(obj instanceof MailInfo)) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        MailInfo info = (MailInfo) obj;
        return id.equals(info.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
