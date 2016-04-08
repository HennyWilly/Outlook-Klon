package de.outlook_klon.logik.kontakte;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Diese Klasse stellt die Verwaltung für die Kontakte des Benutzers dar
 *
 * @author Hendrik Karwanni
 */
public class ContactManagement implements Iterable<Contact> {

    /**
     * Standardmäßiges Adressbuch der ContactManagement
     */
    public static final String DEFAULT = "Adressbuch";

    @JsonProperty("contacts")
    private final Map<String, HashSet<Contact>> mContacts;

    /**
     * Erstellt eine neue Instanz der Kontaktverwaltung
     */
    public ContactManagement() {
        mContacts = new HashMap<>();
        mContacts.put(DEFAULT, new HashSet<Contact>());
    }

    @JsonCreator
    private ContactManagement(
            @JsonProperty("contacts") Map<String, HashSet<Contact>> contacts) {
        this.mContacts = contacts;
    }

    /**
     * Fügt den übergebenen Contact der Verwaltung hinzu
     *
     * @param contact Der hinzuzufügende Contact
     */
    public void addContact(final Contact contact) {
        if (contact == null) {
            throw new NullPointerException("Instanz des Kontakts wurde nicht initialisiert");
        }

        final HashSet<Contact> contactList = mContacts.get(DEFAULT);

        contactList.add(contact);
    }

    /**
     * Fügt den übergebenen Contact der übergebenen Liste der Verwaltung hinzu
     *
     * @param contact Der hinzuzufügende Contact
     * @param list Listen, in die eingefügt werden soll
     */
    public void addToContactList(final Contact contact, final String list) {
        if (list == null || list.trim().isEmpty()) {
            throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
        }
        if (contact == null) {
            throw new NullPointerException("Instanz des Kontakts wurde nicht initialisiert");
        }

        final HashSet<Contact> contactList = mContacts.get(list);
        if (contactList == null) {
            throw new IllegalArgumentException("Der Listenname existiert nicht");
        }
        if (!contactList.add(contact)) {
            throw new IllegalArgumentException("Die Liste enthält den Kontakt bereits");
        }

        addContact(contact);
    }

    /**
     * Fügt die übergebene Liste der Verwaltung hinzu
     *
     * @param list Die hinzuzufügende Liste
     */
    public void addList(final String list) {
        if (list == null || list.trim().isEmpty()) {
            throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
        }

        if (mContacts.containsKey(list)) {
            throw new IllegalArgumentException("Der Listenname ist bereits vorhanden!");
        }

        mContacts.put(list, new HashSet<Contact>());
    }

    /**
     * Löscht den übergebenen Contact aus der Verwaltung
     *
     * @param contact Zu löschender Contact
     */
    public void deleteContact(final Contact contact) {
        if (contact == null) {
            throw new NullPointerException("Instanz des Kontakts wurde nicht initialisiert");
        }

        final Collection<HashSet<Contact>> collection = mContacts.values();
        for (final HashSet<Contact> list : collection) {
            list.remove(contact);
        }
    }

    /**
     * Löscht den übergebenen Contact aus der übergebenen Liste
     *
     * @param contact Zu löschender Contact
     * @param list Liste, aus der der Contact gelöscht werden soll
     */
    public void deleteContact(final Contact contact, final String list) {
        if (contact == null) {
            throw new NullPointerException("Instanz des Kontakts wurde nicht initialisiert");
        }
        if (list == null || list.trim().isEmpty()) {
            throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
        }

        if (DEFAULT.equals(list)) {
            ContactManagement.this.deleteContact(contact);
        } else {
            final HashSet<Contact> targetList = mContacts.get(list);

            if (targetList == null) {
                throw new IllegalArgumentException("Der Listenname existiert nicht");
            }

            targetList.remove(contact);
        }
    }

    /**
     * Löscht die übergebene Liste aus der Verwaltung
     *
     * @param list Liste, die gelöscht werden soll
     */
    public void deleteList(final String list) {
        if (list == null || list.trim().isEmpty()) {
            throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
        }
        if (DEFAULT.equals(list)) {
            throw new IllegalArgumentException("Das Standardadressbuch darf nicht entfernt werden");
        }

        final HashSet<Contact> listArray = mContacts.remove(list);

        if (listArray == null) {
            throw new NullPointerException("Der Listenname existiert nicht");
        }
    }

    /**
     * Benennt die Liste mit dem übergebenen alten Namen zum neuen Namen um
     *
     * @param oldName Alter Name der Liste
     * @param newName Neuer Name der Liste
     */
    public void renameList(final String oldName, final String newName) {
        if (oldName == null || oldName.trim().isEmpty() || newName == null || newName.trim().isEmpty()) {
            throw new NullPointerException("Die Listennamen dürfen nicht leer sein!");
        }

        if (DEFAULT.equals(oldName)) {
            throw new IllegalArgumentException("Das Standardadressbuch darf nicht umbenannt werden");
        }

        final HashSet<Contact> list = mContacts.remove(oldName);
        if (list == null) {
            throw new IllegalArgumentException("Der alte Listenname existiert nicht");
        }
        if (mContacts.get(newName) != null) {
            throw new IllegalArgumentException("Der neue Listenname existiert bereits");
        }

        mContacts.put(newName, list);
    }

    /**
     * Gibt die Namen aller Kontaktlisten der Verwaltung zurück
     *
     * @return Namen aller Kontaktlisten
     */
    @JsonIgnore
    public String[] getLists() {
        final Set<String> lists = mContacts.keySet();

        String[] arryLists = lists.toArray(new String[mContacts.size()]);
        Arrays.sort(arryLists);

        return arryLists;
    }

    /**
     * Gibt die Namen aller Kontaktlisten zurück, in denen der übergebene
     * Contact eingetragen ist
     *
     * @param contact Contact, zu dem die Listen bestimmt werden sollen
     * @return String-Array, welches die Listennamen enthällt
     */
    public String[] getLists(Contact contact) {
        List<String> lists = new ArrayList<>();

        for (Entry<String, HashSet<Contact>> set : mContacts.entrySet()) {
            String name = set.getKey();
            HashSet<Contact> content = set.getValue();

            if (DEFAULT.equals(name)) {
                continue;
            }

            if (content.contains(contact)) {
                lists.add(name);
            }
        }

        String[] arryLists = lists.toArray(new String[lists.size()]);
        Arrays.sort(arryLists);

        return arryLists;
    }

    /**
     * Gibt die Kontakte der übergebenen Liste zurück
     *
     * @param list Name der Liste, von der die Kontakte zurückgegeben werden
     * sollen
     * @return Kontakte der übergebenen Liste
     */
    public Contact[] getContacts(final String list) {
        if (list == null || list.trim().isEmpty()) {
            throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
        }

        final HashSet<Contact> set = mContacts.get(list);
        if (set == null) {
            throw new IllegalArgumentException("Der Listenname existiert nicht");
        }

        return set.toArray(new Contact[set.size()]);
    }

    @Override
    public Iterator<Contact> iterator() {
        return mContacts.get(DEFAULT).iterator();
    }
}
