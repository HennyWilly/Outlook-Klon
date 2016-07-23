package de.outlookklon.logik.contacts;

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
 * Diese Klasse stellt die Verwaltung f�r die Kontakte des Benutzers dar
 *
 * @author Hendrik Karwanni
 */
public class ContactManagement implements Iterable<Contact> {

    /**
     * Standardm��iges Adressbuch der ContactManagement
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
     * F�gt den �bergebenen Contact der Verwaltung hinzu
     *
     * @param contact Der hinzuzuf�gende Contact
     */
    public void addContact(@NonNull final Contact contact) {
        final Set<Contact> contactList = mContacts.get(DEFAULT);

        contactList.add(contact);
    }

    /**
     * F�gt den �bergebenen Contact der �bergebenen Liste der Verwaltung hinzu
     *
     * @param contact Der hinzuzuf�gende Contact
     * @param list Listen, in die eingef�gt werden soll
     */
    public void addToContactList(@NonNull final Contact contact, @NonNull final String list) {
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
     * F�gt die �bergebene Liste der Verwaltung hinzu
     *
     * @param list Die hinzuzuf�gende Liste
     */
    public void addList(@NonNull final String list) {
        ensureListNameNotEmpty(list);

        if (mContacts.containsKey(list)) {
            throw new IllegalArgumentException(Localization.getString("ContactManagement_ListNameAlreadyExists"));
        }

        mContacts.put(list, new HashSet<Contact>());
    }

    /**
     * L�scht den �bergebenen Contact aus der Verwaltung
     *
     * @param contact Zu l�schender Contact
     */
    public void deleteContact(@NonNull final Contact contact) {
        for (final Set<Contact> list : mContacts.values()) {
            list.remove(contact);
        }
    }

    /**
     * L�scht den �bergebenen Contact aus der �bergebenen Liste
     *
     * @param contact Zu l�schender Contact
     * @param list Liste, aus der der Contact gel�scht werden soll
     */
    public void deleteContact(@NonNull final Contact contact, @NonNull final String list) {
        ensureListNameNotEmpty(list);

        if (DEFAULT.equals(list)) {
            ContactManagement.this.deleteContact(contact);
        } else {
            final Set<Contact> targetList = getContactList(list);
            targetList.remove(contact);
        }
    }

    /**
     * L�scht die �bergebene Liste aus der Verwaltung
     *
     * @param list Liste, die gel�scht werden soll
     */
    public void deleteList(@NonNull final String list) {
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
     * Benennt die Liste mit dem �bergebenen alten Namen zum neuen Namen um
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
     * Gibt die Namen aller Kontaktlisten der Verwaltung zur�ck
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
     * Gibt die Namen aller Kontaktlisten zur�ck, in denen der �bergebene
     * Contact eingetragen ist
     *
     * @param contact Contact, zu dem die Listen bestimmt werden sollen
     * @return String-Array, welches die Listennamen enth�llt
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
     * Gibt die Kontakte der �bergebenen Liste zur�ck
     *
     * @param list Name der Liste, von der die Kontakte zur�ckgegeben werden
     * sollen
     * @return Kontakte der �bergebenen Liste
     */
    public Contact[] getContacts(@NonNull final String list) {
        ensureListNameNotEmpty(list);

        final Set<Contact> set = getContactList(list);
        return set.toArray(new Contact[set.size()]);
    }

    @Override
    public Iterator<Contact> iterator() {
        return mContacts.get(DEFAULT).iterator();
    }
}
