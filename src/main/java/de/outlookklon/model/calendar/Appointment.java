package de.outlookklon.model.calendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.outlookklon.application.User;
import de.outlookklon.model.contacts.Contact;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.mail.Address;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.time.DateTime;

/**
 * Dies ist eine Datenklasse, die die Daten von einem Appointment des Benutzers
 * speichert.
 *
 * @author Hendrik Karwanni
 */
public class Appointment implements Comparable<Appointment> {

    /**
     * Diese Aufzählung stellt die möglichen Zustände eines Termins dar
     */
    public enum AppointmentState {
        /**
         * Dem Termin wurde zugesagt
         */
        PROMISED,
        /**
         * Der Termin wurde abgelehnt
         */
        REJECTED
    }

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("location")
    private String location;

    @JsonProperty("start")
    private DateTime start;

    @JsonProperty("end")
    private DateTime end;

    @JsonProperty("text")
    private String text;

    @JsonProperty("user")
    private String user;

    @JsonProperty("contact")
    private String contact;

    @JsonProperty("state")
    private AppointmentState status;

    @JsonProperty("addresses")
    private List<Address> addresses;

    /**
     * Erstellt eine neue Instanz der Klasse mit den übergebenen Werten
     *
     * @param subject Betreff des Termins
     * @param location Ort des Termins
     * @param start Startzeitpunkt des Termins
     * @param end Endzeitpunkt des Termins
     * @param text Text des Termins
     * @param user Name des Benutzers
     * @param contact Name des Kontakts
     */
    public Appointment(String subject, String location, DateTime start, DateTime end, String text, String user, String contact) {
        setSubject(subject);
        setLocation(location);
        setTimes(start, end);
        setText(text);
        setUser(user);
        setContact(contact);
        setState(AppointmentState.PROMISED);

        initAddresses();
    }

    /**
     * Erstellt eine neue Instanz der Klasse mit den übergebenen Werten
     *
     * @param subject Betreff des Termins
     * @param location Ort des Termins
     * @param start Startzeitpunkt des Termins
     * @param end Endzeitpunkt des Termins
     * @param text Text des Termins
     * @param user Name des Benutzers
     * @param contact Name des Kontakts
     * @param state Status des Termins
     * @param addresses Verknüpfte Adressen des Termins
     */
    @JsonCreator
    public Appointment(
            @JsonProperty("subject") String subject,
            @JsonProperty("location") String location,
            @JsonProperty("start") DateTime start,
            @JsonProperty("end") DateTime end,
            @JsonProperty("text") String text,
            @JsonProperty("user") String user,
            @JsonProperty("contact") String contact,
            @JsonProperty("state") AppointmentState state,
            @JsonProperty("addresses") Collection<? extends Address> addresses) {
        setSubject(subject);
        setLocation(location);
        setTimes(start, end);
        setText(text);
        setUser(user);
        setContact(contact);
        setState(state);
        setAddresses(addresses);
    }

    private void initAddresses() {

        List<Address> temp = new ArrayList<>();
        for (Contact contactInstance : User.getInstance().getContacts()) {
            if (Objects.equals(contactInstance.getDisplayname(), contact)) {
                CollectionUtils.addIgnoreNull(temp, contactInstance.getAddress1());
                CollectionUtils.addIgnoreNull(temp, contactInstance.getAddress2());
                break;
            }
        }
        setAddresses(temp);
    }

    /**
     * Setter für den Betreff des Termins
     *
     * @param subject Zu setzender Betreff
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Getter für den Betreff des Termins
     *
     * @return Betreff
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Setter für den Ort des Termins
     *
     * @param location Zu setzender Ort
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Getter für den Ort des Termins
     *
     * @return Ort
     */
    public String getLocation() {
        return location;
    }

    /**
     * Setter für den Start- und Endzeitpunkt des Termins
     *
     * @param start Zu setzender Startzeitpunkt
     * @param end Zu setzender Endzeitpunkt
     */
    public void setTimes(DateTime start, DateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Der Startzeitpunkt darf nicht hinter dem Endzeitpunkt liegen");
        }
        this.start = start;
        this.end = end;
    }

    /**
     * Getter für den Startzeitpunkt des Termins
     *
     * @return Startzeitpunkt
     */
    public DateTime getStart() {
        return start;
    }

    /**
     * Getter für den Endzeitpunkt des Termins
     *
     * @return Endzeitpunkt
     */
    public DateTime getEnd() {
        return end;
    }

    /**
     * Setter für die Beschreibung des Termins
     *
     * @param text Zu setzende Beschreibung
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Getter für die Beschreibung des Termins
     *
     * @return Beschreibung
     */
    public String getText() {
        return text;
    }

    /**
     * Setter für das zugeordnete Benutzerkonto des Termins
     *
     * @param user Zu setzende Benutzerkonto
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Getter für das zugeordnete Benutzerkonto des Termins
     *
     * @return Benutzerkonto
     */
    public String getUser() {
        return user;
    }

    /**
     * Setter für den zugeordneten Contact des Termins
     *
     * @param contact Zu setzende Contact
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Getter für den zugeordneten Contact des Termins
     *
     * @return Contact
     */
    public String getContact() {
        return contact;
    }

    /**
     * Setzt den Status des Termins
     *
     * @param state Status des Termins
     */
    public void setState(AppointmentState state) {
        this.status = state;
    }

    /**
     * Gibt den Status des Termins zurück
     *
     * @return Status des Termins
     */
    public AppointmentState getState() {
        return status;
    }

    /**
     * Gibt die verknüpfen Adressen zum Appointment zurück
     *
     * @return Adressen zum Appointment
     */
    public List<Address> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }

    /**
     * Setzt die verknüpfen Adressen zum Appointment
     *
     * @param addresses Adressen zum Appointment
     */
    public void setAddresses(Collection<? extends Address> addresses) {
        this.addresses = new ArrayList<>(addresses);
    }

    @Override
    public int compareTo(Appointment other) {
        return getStart().compareTo(other.getStart());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        Appointment other = (Appointment) obj;
        return new EqualsBuilder()
                .append(getSubject(), other.getSubject())
                .append(getLocation(), other.getLocation())
                .append(getStart(), other.getStart())
                .append(getEnd(), other.getEnd())
                .append(getText(), other.getText())
                .append(getUser(), other.getUser())
                .append(getContact(), other.getContact())
                .append(getState(), other.getState())
                .append(getAddresses(), other.getAddresses())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getSubject())
                .append(getLocation())
                .append(getStart())
                .append(getEnd())
                .append(getText())
                .append(getUser())
                .append(getContact())
                .append(getState())
                .append(getAddresses())
                .toHashCode();
    }
}
