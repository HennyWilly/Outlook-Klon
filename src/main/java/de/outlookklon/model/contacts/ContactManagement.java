package de.outlookklon.model.contacts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.outlookklon.localization.Localization;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.NonNull;

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
    private final Map<String, Set<Contact>> mContacts;

    /**
     * Erstellt eine neue Instanz der Kontaktverwaltung
     */
    public ContactManagement() {
        this(new HashMap<String, Set<Contact>>());
        mContacts.put(DEFAULT, new HashSet<Contact>());
    }

    @JsonCreator
    private ContactManagement(
            @JsonProperty("contacts") Map<String, Set<Contact>> contacts) {
        this.mContacts = contacts;
    }

    /**
     * Fügt den übergebenen Contact der Verwaltung hinzu
     *
     * @param contact Der hinzuzufügende Contact
     */
    public void addContact(@NonNull final Contact contact) {
        final Set<Contact> contactList = mContacts.get(DEFAULT);

        contactList.add(contact);
    }

    /**
     * Fügt den übergebenen Contact der übergebenen Liste der Verwaltung hinzu
     *
     * @param contact Der hinzuzufügende Contact
     * @param list Listen, in die eingefügt werden soll
     */
    public void addToContactList(@NonNull final Contact contact, final String list) {
        ensureListNameNotEmpty(list);

        final Set<Contact> contactList = getContactList(list);
        if (!contactList.add(contact)) {
            throw new IllegalArgumentException(Localization.getString("ContactManagement_ContactAlreadyInList"));
        }

        addContact(contact);
    }

    private void ensureListNameNotEmpty(@NonNull String list) {
        if (list.trim().isEmpty()) {
            throw new NullPointerException(Localization.getString("ContactManagement_EmptyListName"));
        }
    }

    private Set<Contact> getContactList(String listName) {
        final Set<Contact> contactList = mContacts.get(listName);
        if (contactList == null) {
            throw new IllegalArgumentException(Localization.getString("ContactManagement_ListNameDoesNotExist"));
        }
        return contactList;
    }

    /**
     * Fügt die übergebene Liste der Verwaltung hinzu
     *
     * @param list Die hinzuzufügende Liste
     */
    public void addList(final String list) {
        ensureListNameNotEmpty(list);

        if (mContacts.containsKey(list)) {
            throw new IllegalArgumentException(Localization.getString("ContactManagement_ListNameAlreadyExists"));
        }

        mContacts.put(list, new HashSet<Contact>());
    }

    /**
     * Löscht den übergebenen Contact aus der Verwaltung
     *
     * @param contact Zu löschender Contact
     */
    public void deleteContact(@NonNull final Contact contact) {
        for (final Set<Contact> list : mContacts.values()) {
            list.remove(contact);
        }
    }

    /**
     * Löscht den übergebenen Contact aus der übergebenen Liste
     *
     * @param contact Zu löschender Contact
     * @param list Liste, aus der der Contact gelöscht werden soll
     */
    public void deleteContact(@NonNull final Contact contact, final String list) {
        ensureListNameNotEmpty(list);

        if (DEFAULT.equals(list)) {
            ContactManagement.this.deleteContact(contact);
        } else {
            final Set<Contact> targetList = getContactList(list);
            targetList.remove(contact);
        }
    }

    /**
     * Löscht die übergebene Liste aus der Verwaltung
     *
     * @param list Liste, die gelöscht werden soll
     */
    public void deleteList(final String list) {
        ensureListNameNotEmpty(list);

        if (DEFAULT.equals(list)) {
            throw new IllegalArgumentException(Localization.getString("ContactManagement_MustNotDeleteDefaultAddressbook"));
        }

        final Set<Contact> listArray = mContacts.remove(list);
        if (listArray == null) {
            throw new IllegalArgumentException(Localization.getString("ContactManagement_ListNameDoesNotExist"));
        }
    }

    /**
     * Benennt die Liste mit dem übergebenen alten Namen zum neuen Namen um
     *
     * @param oldName Alter Name der Liste
     * @param newName Neuer Name der Liste
     */
    public void renameList(@NonNull final String oldName, @NonNull final String newName) {
        if (oldName.trim().isEmpty() || newName.trim().isEmpty()) {
            throw new NullPointerException(Localization.getString("ContactManagement_EmptyListNames"));
        }

        if (DEFAULT.equals(oldName)) {
            throw new IllegalArgumentException(Localization.getString("ContactManagement_MustNotRenameDefaultAddressbook"));
        }

        final Set<Contact> list = mContacts.remove(oldName);
        if (list == null) {
            throw new IllegalArgumentException(Localization.getString("ContactManagement_OldListNameDoesNotExist"));
        }
        if (mContacts.get(newName) != null) {
            throw new IllegalArgumentException(Localization.getString("ContactManagement_NewListNameAlreadyExists"));
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
    public String[] getLists(@NonNull Contact contact) {
        List<String> lists = new ArrayList<>();

        for (Entry<String, Set<Contact>> set : mContacts.entrySet()) {
            String name = set.getKey();

            if (DEFAULT.equals(name)) {
                continue;
            }

            Set<Contact> content = set.getValue();
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
        ensureListNameNotEmpty(list);

        final Set<Contact> set = getContactList(list);
        return set.toArray(new Contact[set.size()]);
    }

    @Override
    public Iterator<Contact> iterator() {
        return mContacts.get(DEFAULT).iterator();
    }
}
