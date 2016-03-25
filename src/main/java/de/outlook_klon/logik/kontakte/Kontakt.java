package de.outlook_klon.logik.kontakte;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;

/**
 * Dies ist eine Datenklasse, die die Daten von einem Kontakt des Benutzers
 * speichert.
 *
 * @author Hendrik Karwanni
 */
public class Kontakt {

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
     * Erstellt eine neue Instanz der Klasse mit den �bergebenen Werten.
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
    public Kontakt(
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
     * Setter f�r den Nachnamen des Kontakts
     *
     * @param surname Zu setzender Nachname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Getter f�r den Nachnamen des Kontakts
     *
     * @return Nachname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Setter f�r den Vornamen des Kontakts
     *
     * @param forename Zu setzender Vorname
     */
    public void setForename(String forename) {
        this.forename = forename;
    }

    /**
     * Getter f�r den Vornamen des Kontakts
     *
     * @return Vorname
     */
    public String getForename() {
        return forename;
    }

    /**
     * Setter f�r den Anzeigenamen des Kontakts
     *
     * @param displayname Zu setzender Anzeigename
     */
    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    /**
     * Getter f�r den Anzeigenamen des Kontakts
     *
     * @return Anzeigename
     */
    public String getDisplayname() {
        return displayname;
    }

    /**
     * Setter f�r den Spitznamen des Kontakts
     *
     * @param nickname Zu setzender Spitzname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Getter f�r den Spitznamen des Kontakts
     *
     * @return Spitzname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Setter f�r die erste E-Mail-Adresse des Kontakts
     *
     * @param address1 Zu setzende E-Mail-Adresse
     */
    public void setAddress1(Address address1) {
        this.address1 = address1;
    }

    /**
     * Getter f�r die erste E-Mail-Adresse des Kontakts
     *
     * @return Erste E-Mail-Adresse
     */
    public Address getAddress1() {
        return address1;
    }

    /**
     * Setter f�r die zweite E-Mail-Adresse des Kontaks
     *
     * @param address2 Zu setzende E-Mail-Adresse
     */
    public void setAddress2(Address address2) {
        this.address2 = address2;
    }

    /**
     * Getter f�r die zweite E-Mail-Adresse des Kontakts
     *
     * @return Zweite E-Mail-Adresse
     */
    public Address getAddress2() {
        return address2;
    }

    /**
     * Setter f�r die private Telefonnummer des Kontakts
     *
     * @param privatephone Zu setzende Telefonnummer
     */
    public void setPrivatephone(String privatephone) {
        this.privatephone = privatephone;
    }

    /**
     * Getter f�r die private Telefonnummer des Kontakts
     *
     * @return Private Telefonnummer
     */
    public String getPrivatephone() {
        return privatephone;
    }

    /**
     * Setter f�r die dienstliche Telefonnummer des Kontakts
     *
     * @param dutyphone Zu setzende Telefonnummer
     */
    public void setDutyphone(String dutyphone) {
        this.dutyphone = dutyphone;
    }

    /**
     * Getter f�r die dienstliche Telefonnummer des Kontakts
     *
     * @return Dienstliche Telefonnummer
     */
    public String getDutyphone() {
        return dutyphone;
    }

    /**
     * Setter f�r die Mobiltelefonnummer des Kontakts
     *
     * @param mobilephone Zu setzende Telefonnummer
     */
    public void setMobilephone(String mobilephone) {
        this.mobilephone = mobilephone;
    }

    /**
     * Getter f�r die Mobiltelefonnummer des Kontakts
     *
     * @return Mobiltelefonnummer
     */
    public String getMobilephone() {
        return mobilephone;
    }

    /**
     * Getter f�r die erste E-Mail-Adresse des Kontakts als Unicode-String
     *
     * @return Erste E-Mail-Adresse als Unicode-String
     */
    @JsonIgnore
    public String getAddress1AsString() {
        if (address1 == null) {
            return "";
        }

        if (InternetAddress.class.isInstance(address1)) {
            return ((InternetAddress) address1).toUnicodeString();
        }

        return address1.toString();
    }

    /**
     * Getter f�r die zweite E-Mail-Adresse des Kontakts als Unicode-String
     *
     * @return Zweite E-Mail-Adresse als Unicode-String
     */
    @JsonIgnore
    public String getAddress2AsString() {
        if (address2 == null) {
            return "";
        }

        if (InternetAddress.class.isInstance(address2)) {
            return ((InternetAddress) address2).toUnicodeString();
        }

        return address2.toString();
    }
}
