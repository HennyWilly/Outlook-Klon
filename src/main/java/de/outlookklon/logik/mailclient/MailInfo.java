package de.outlookklon.logik.mailclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.mail.Address;

/**
 * Datenklasse zum Halten von Mailinformationen
 *
 * @author Hendrik Karwanni
 */
public abstract class MailInfo {

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("sender")
    private Address sender;

    @JsonProperty("text")
    private String text;

    @JsonProperty("contentType")
    private String contentType;

    @JsonProperty("to")
    private List<Address> to;

    @JsonProperty("cc")
    private List<Address> cc;

    @JsonProperty("attachment")
    private List<String> attachment;

    /**
     * Erstellt eine neue MailInfo-Instanz mit den übergebenen Werten.
     *
     * @param subject Betreff der Mail
     * @param text Text der Mail
     * @param contentType Typ des Mailtextes
     * @param to Primäre Empfänger der Mail
     * @param cc Sekundäre Empfänger der Mail
     * @param attachment Anhänge der Mail
     */
    protected MailInfo(String subject, String text, String contentType,
            List<Address> to, List<Address> cc, List<String> attachment) {
        setSubject(subject);
        setText(text);
        setContentType(contentType);
        setTo(to);
        setCc(cc);
        setAttachment(attachment);
    }

    /**
     * Erstellt eine neue MailInfo-Instanz mit den übergebenen Werten.
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
    protected MailInfo(
            @JsonProperty("subject") String subject,
            @JsonProperty("sender") Address sender,
            @JsonProperty("text") String text,
            @JsonProperty("contentType") String contentType,
            @JsonProperty("to") List<Address> to,
            @JsonProperty("cc") List<Address> cc,
            @JsonProperty("attachment") List<String> attachment) {
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
    public List<Address> getTo() {
        return to != null ? Collections.unmodifiableList(to) : null;
    }

    /**
     * Setzt die Zieladressen der Mail
     *
     * @param to Zieladressen der Mail
     */
    public void setTo(List<Address> to) {
        this.to = to != null ? new ArrayList<>(to) : null;
    }

    /**
     * Gibt die Copy-Adressen der Mail zurück
     *
     * @return Copy-Adressen der Mail
     */
    public List<Address> getCc() {
        return cc != null ? Collections.unmodifiableList(cc) : null;
    }

    /**
     * Setzt die Copy-Adressen der Mail
     *
     * @param cc
     */
    public void setCc(List<Address> cc) {
        this.cc = cc != null ? new ArrayList<>(cc) : null;
    }

    /**
     * Gibt die Namen der Anhänge der Mail zurück
     *
     * @return Namen der Anhänge der Mail
     */
    public List<String> getAttachment() {
        return attachment != null ? Collections.unmodifiableList(attachment) : null;
    }

    /**
     * Setzt die Namen der Anhänge der Mail
     *
     * @param attachment Namen der Anhänge der Mail
     */
    public void setAttachment(List<String> attachment) {
        this.attachment = attachment != null ? new ArrayList<>(attachment) : null;
    }
}
