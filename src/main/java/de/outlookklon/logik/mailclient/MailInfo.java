package de.outlookklon.logik.mailclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.mail.Address;

/**
 * Datenklasse zum Halten von Mailinformationen
 *
 * @author Hendrik Karwanni
 */
public class MailInfo {

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("sender")
    private Address sender;

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

    public MailInfo(String subject, String text, String contentType,
            Address[] to, Address[] cc, String[] attachment) {
        setSubject(subject);
        setText(text);
        setContentType(contentType);
        setTo(to);
        setCc(cc);
        setAttachment(attachment);
    }

    @JsonCreator
    public MailInfo(
            @JsonProperty("subject") String subject,
            @JsonProperty("sender") Address sender,
            @JsonProperty("text") String text,
            @JsonProperty("contentType") String contentType,
            @JsonProperty("to") Address[] to,
            @JsonProperty("cc") Address[] cc,
            @JsonProperty("attachment") String[] attachment) {
        this(subject, text, contentType, to, cc, attachment);

        setSender(sender);
    }

    /**
     * Gibt den Betreff der Mail zurück
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
     * Gibt die Adresse des Senders der Mail zurück
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
     * Gibt den Text der Mail zurück
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
     * Gibt den Texttyp des Inhalts der Mail zurück
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
     * Gibt die Zieladressen der Mail zurück
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
     * Gibt die Copy-Adressen der Mail zurück
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
     * Gibt die Namen der Anhänge der Mail zurück
     *
     * @return Namen der Anhänge der Mail
     */
    public String[] getAttachment() {
        return attachment;
    }

    /**
     * Setzt die Namen der Anhänge der Mail
     *
     * @param attachment Namen der Anhänge der Mail
     */
    public void setAttachment(final String[] attachment) {
        this.attachment = attachment;
    }
}
