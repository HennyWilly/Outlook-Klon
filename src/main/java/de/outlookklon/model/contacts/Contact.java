package de.outlookklon.model.contacts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Dies ist eine Datenklasse, die die Daten von einem Contact des Benutzers
 * speichert.
 *
 * @author Hendrik Karwanni
 */
public class Contact {

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("forename")
    private String forename;

    @JsonProperty("displayname")
    private String displayname;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("address1")
    private Address address1;

    @JsonProperty("address2")
    private Address address2;

    @JsonProperty("dutyphone")
    private String dutyphone;

    @JsonProperty("mobilephone")
    private String mobilephone;

    @JsonProperty("privatephone")
    private String privatephone;

    /**
     * Erstellt eine neue Instanz der Klasse mit den übergebenen Werten.
     *
     * @param surname Nachname des Kontakts
     * @param forename Vorname des Kontakts
     * @param displayname Anzeigename des Kontakts
     * @param nickname Spitzname des Kontakts
     * @param address1 Erste E-Mail-Adresse des Kontakts
     * @param address2 Zweite E-Mail-Adresse des Kontakts
     * @param privatephone Private Telefonnummer des Kontakts
     * @param dutyphone Dienstliche Telefonnummer des Kontakts
     * @param mobilephone Mobiltelefonnummer des Kontakts
     */
    @JsonCreator
    public Contact(
            @JsonProperty("surname") String surname,
            @JsonProperty("forename") String forename,
            @JsonProperty("displayname") String displayname,
            @JsonProperty("nickname") String nickname,
            @JsonProperty("address1") Address address1,
            @JsonProperty("address2") Address address2,
            @JsonProperty("privatephone") String privatephone,
            @JsonProperty("dutyphone") String dutyphone,
            @JsonProperty("mobilephone") String mobilephone) {
        setSurname(surname);
        setForename(forename);
        setDisplayname(displayname);
        setNickname(nickname);
        setAddress1(address1);
        setAddress2(address2);
        setDutyphone(dutyphone);
        setMobilephone(mobilephone);
        setPrivatephone(privatephone);
    }

    @Override
    public String toString() {
        return displayname;
    }

    /**
     * Setter für den Nachnamen des Kontakts
     *
     * @param surname Zu setzender Nachname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Getter für den Nachnamen des Kontakts
     *
     * @return Nachname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Setter für den Vornamen des Kontakts
     *
     * @param forename Zu setzender Vorname
     */
    public void setForename(String forename) {
        this.forename = forename;
    }

    /**
     * Getter für den Vornamen des Kontakts
     *
     * @return Vorname
     */
    public String getForename() {
        return forename;
    }

    /**
     * Setter für den Anzeigenamen des Kontakts
     *
     * @param displayname Zu setzender Anzeigename
     */
    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    /**
     * Getter für den Anzeigenamen des Kontakts
     *
     * @return Anzeigename
     */
    public String getDisplayname() {
        return displayname;
    }

    /**
     * Setter für den Spitznamen des Kontakts
     *
     * @param nickname Zu setzender Spitzname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Getter für den Spitznamen des Kontakts
     *
     * @return Spitzname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Setter für die erste E-Mail-Adresse des Kontakts
     *
     * @param address1 Zu setzende E-Mail-Adresse
     */
    public void setAddress1(Address address1) {
        this.address1 = address1;
    }

    /**
     * Getter für die erste E-Mail-Adresse des Kontakts
     *
     * @return Erste E-Mail-Adresse
     */
    public Address getAddress1() {
        return address1;
    }

    /**
     * Setter für die zweite E-Mail-Adresse des Kontaks
     *
     * @param address2 Zu setzende E-Mail-Adresse
     */
    public void setAddress2(Address address2) {
        this.address2 = address2;
    }

    /**
     * Getter für die zweite E-Mail-Adresse des Kontakts
     *
     * @return Zweite E-Mail-Adresse
     */
    public Address getAddress2() {
        return address2;
    }

    /**
     * Setter für die private Telefonnummer des Kontakts
     *
     * @param privatephone Zu setzende Telefonnummer
     */
    public void setPrivatephone(String privatephone) {
        this.privatephone = privatephone;
    }

    /**
     * Getter für die private Telefonnummer des Kontakts
     *
     * @return Private Telefonnummer
     */
    public String getPrivatephone() {
        return privatephone;
    }

    /**
     * Setter für die dienstliche Telefonnummer des Kontakts
     *
     * @param dutyphone Zu setzende Telefonnummer
     */
    public void setDutyphone(String dutyphone) {
        this.dutyphone = dutyphone;
    }

    /**
     * Getter für die dienstliche Telefonnummer des Kontakts
     *
     * @return Dienstliche Telefonnummer
     */
    public String getDutyphone() {
        return dutyphone;
    }

    /**
     * Setter für die Mobiltelefonnummer des Kontakts
     *
     * @param mobilephone Zu setzende Telefonnummer
     */
    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    /**
     * Getter für die Mobiltelefonnummer des Kontakts
     *
     * @return Mobiltelefonnummer
     */
    public String getMobilephone() {
        return mobilephone;
    }

    /**
     * Getter für die erste E-Mail-Adresse des Kontakts als Unicode-String
     *
     * @return Erste E-Mail-Adresse als Unicode-String
     */
    @JsonIgnore
    public String getAddress1AsString() {
        return getAddressAsString(address1);
    }

    /**
     * Getter für die zweite E-Mail-Adresse des Kontakts als Unicode-String
     *
     * @return Zweite E-Mail-Adresse als Unicode-String
     */
    @JsonIgnore
    public String getAddress2AsString() {
        return getAddressAsString(address2);
    }

    private static String getAddressAsString(Address address) {
        if (address == null) {
            return "";
        }

        if (InternetAddress.class.isInstance(address)) {
            return ((InternetAddress) address).toUnicodeString();
        }

        return address.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        Contact other = (Contact) obj;
        return new EqualsBuilder()
                .append(getAddress1(), other.getAddress1())
                .append(getAddress2(), other.getAddress2())
                .append(getDisplayname(), other.getDisplayname())
                .append(getDutyphone(), other.getDutyphone())
                .append(getForename(), other.getForename())
                .append(getMobilephone(), other.getMobilephone())
                .append(getNickname(), other.getNickname())
                .append(getPrivatephone(), other.getPrivatephone())
                .append(getSurname(), other.getSurname())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(surname)
                .append(this.forename)
                .append(this.displayname)
                .append(this.nickname)
                .append(this.address1)
                .append(this.address2)
                .append(this.dutyphone)
                .append(this.mobilephone)
                .append(this.privatephone)
                .toHashCode();
    }
}
