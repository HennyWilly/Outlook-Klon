package de.outlookklon.logik.calendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.outlookklon.logik.User;
import de.outlookklon.logik.contacts.Contact;
import java.util.ArrayList;
import java.util.List;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import org.joda.time.DateTime;

/**
 * Dies ist eine Datenklasse, die die Daten von einem Appointment des Benutzers
 * speichert.
 *
 * @author Hendrik Karwanni
 */
public class Appointment implements Comparable<Appointment> {

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
    private Address[] addresses;

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

        List<Address> temp = new ArrayList<>();
        for (Contact contacts : User.getInstance().getContacts()) {
            if (contacts.getDisplayname() != null && contacts.getDisplayname().equals(contact)) {
                if (contacts.getAddress1() != null) {
                    temp.add(contacts.getAddress1());
                }
                if (contacts.getAddress2() != null) {
                    temp.add(contacts.getAddress2());
                }
                break;
            }
        }
        setAddresses(temp.toArray(new Address[temp.size()]));
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
            @JsonProperty("addresses") InternetAddress[] addresses) {
        setSubject(subject);
        setLocation(location);
        setTimes(start, end);
        setText(text);
        setUser(user);
        setContact(contact);
        setState(state);
        setAddresses(addresses);
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
    public Address[] getAddresses() {
        return addresses;
    }

    /**
     * Setzt die verknüpfen Adressen zum Appointment
     *
     * @param addresses Adressen zum Appointment
     */
    public void setAddresses(Address[] addresses) {
        this.addresses = addresses;
    }

    @Override
    public int compareTo(Appointment o) {
        return this.start.compareTo(o.start);
    }
}
