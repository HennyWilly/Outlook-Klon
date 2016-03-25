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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Diese Klasse stellt die Verwaltung für die Kontakte des Benutzers dar
 *
 * @author Hendrik Karwanni
 */
public class Kontaktverwaltung implements Iterable<Kontakt> {

    public static final String DEFAULT = "Adressbuch";

    @JsonProperty("contacts")
    private final Map<String, HashSet<Kontakt>> mKontakte;

    /**
     * Erstellt eine neue Instanz der Kontaktverwaltung
     */
    public Kontaktverwaltung() {
        mKontakte = new HashMap<String, HashSet<Kontakt>>();
        mKontakte.put(DEFAULT, new HashSet<Kontakt>());
    }

    @JsonCreator
    private Kontaktverwaltung(@JsonProperty("contacts") Map<String, HashSet<Kontakt>> contacts) {
        this.mKontakte = contacts;
    }

    /**
     * Fügt den übergebenen Kontakt der Verwaltung hinzu
     *
     * @param kontakt Der hinzuzufügende Kontakt
     */
    public void addKontakt(final Kontakt kontakt) {
        if (kontakt == null) {
            throw new NullPointerException("Instanz des Kontakts wurde nicht initialisiert");
        }

        final HashSet<Kontakt> kontaktliste = mKontakte.get(DEFAULT);

        kontaktliste.add(kontakt);
    }

    /**
     * Fügt den übergebenen Kontakt der übergebenen Liste der Verwaltung hinzu
     *
     * @param kontakt Der hinzuzufügende Kontakt
     * @param liste Listen, in die eingefügt werden soll
     */
    public void addKontaktZuListe(final Kontakt kontakt, final String liste) {
        if (liste == null || liste.trim().isEmpty()) {
            throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
        }
        if (kontakt == null) {
            throw new NullPointerException("Instanz des Kontakts wurde nicht initialisiert");
        }

        final HashSet<Kontakt> kontaktliste = mKontakte.get(liste);
        if (kontaktliste == null) {
            throw new IllegalArgumentException("Der Listenname existiert nicht");
        }
        if (!kontaktliste.add(kontakt)) {
            throw new IllegalArgumentException("Die Liste enthält den Kontakt bereits");
        }

        addKontakt(kontakt);
    }

    /**
     * Fügt die übergebene Liste der Verwaltung hinzu
     *
     * @param liste Die hinzuzufügende Liste
     */
    public void addListe(final String liste) {
        if (liste == null || liste.trim().isEmpty()) {
            throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
        }

        if (mKontakte.containsKey(liste)) {
            throw new IllegalArgumentException("Der Listenname ist bereits vorhanden!");
        }

        mKontakte.put(liste, new HashSet<Kontakt>());
    }

    /**
     * Löscht den übergebenen Kontakt aus der Verwaltung
     *
     * @param kontakt Zu löschender Kontakt
     */
    public void löscheKontakt(final Kontakt kontakt) {
        if (kontakt == null) {
            throw new NullPointerException("Instanz des Kontakts wurde nicht initialisiert");
        }

        final Collection<HashSet<Kontakt>> sammlung = mKontakte.values();
        for (final HashSet<Kontakt> liste : sammlung) {
            liste.remove(kontakt);
        }
    }

    /**
     * Löscht den übergebenen Kontakt aus der übergebenen Liste
     *
     * @param kontakt Zu löschender Kontakt
     * @param liste Liste, aus der der Kontakt gelöscht werden soll
     */
    public void löscheKontakt(final Kontakt kontakt, final String liste) {
        if (kontakt == null) {
            throw new NullPointerException("Instanz des Kontakts wurde nicht initialisiert");
        }
        if (liste == null || liste.trim().isEmpty()) {
            throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
        }

        if (DEFAULT.equals(liste)) {
            löscheKontakt(kontakt);
        } else {
            final HashSet<Kontakt> zielListe = mKontakte.get(liste);

            if (zielListe == null) {
                throw new IllegalArgumentException("Der Listenname existiert nicht");
            }

            zielListe.remove(kontakt);
        }
    }

    /**
     * Löscht die übergebene Liste aus der Verwaltung
     *
     * @param liste Liste, die gelöscht werden soll
     */
    public void löscheListe(final String liste) {
        if (liste == null || liste.trim().isEmpty()) {
            throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
        }
        if (DEFAULT.equals(liste)) {
            throw new IllegalArgumentException("Das Standardadressbuch darf nicht entfernt werden");
        }

        final HashSet<Kontakt> listenArray = mKontakte.remove(liste);

        if (listenArray == null) {
            throw new NullPointerException("Der Listenname existiert nicht");
        }
    }

    /**
     * Benennt die Liste mit dem übergebenen alten Namen zum neuen Namen um
     *
     * @param alt Alter Name der Liste
     * @param neu Neuer Name der Liste
     */
    public void renameListe(final String alt, final String neu) {
        if (alt == null || alt.trim().isEmpty() || neu == null || neu.trim().isEmpty()) {
            throw new NullPointerException("Die Listennamen dürfen nicht leer sein!");
        }

        if (DEFAULT.equals(alt)) {
            throw new IllegalArgumentException("Das Standardadressbuch darf nicht umbenannt werden");
        }

        final HashSet<Kontakt> liste = mKontakte.remove(alt);
        if (liste == null) {
            throw new IllegalArgumentException("Der alte Listenname existiert nicht");
        }
        if (mKontakte.get(neu) != null) {
            throw new IllegalArgumentException("Der neue Listenname existiert bereits");
        }

        mKontakte.put(neu, liste);
    }

    /**
     * Gibt die Namen aller Kontaktlisten der Verwaltung zurück
     *
     * @return Namen aller Kontaktlisten
     */
    @JsonIgnore
    public String[] getListen() {
        final Set<String> listen = mKontakte.keySet();

        String[] arryListen = listen.toArray(new String[mKontakte.size()]);
        Arrays.sort(arryListen);

        return arryListen;
    }

    /**
     * Gibt die Namen aller Kontaktlisten zurück, in denen der übergebene
     * Kontakt eingetragen ist
     *
     * @param kontakt Kontakt, zu dem die Listen bestimmt werden sollen
     * @return String-Array, welches die Listennamen enthällt
     */
    public String[] getListen(Kontakt kontakt) {
        ArrayList<String> listen = new ArrayList<String>();

        for (Entry<String, HashSet<Kontakt>> set : mKontakte.entrySet()) {
            String name = set.getKey();
            HashSet<Kontakt> inhalt = set.getValue();

            if (DEFAULT.equals(name)) {
                continue;
            }

            if (inhalt.contains(kontakt)) {
                listen.add(name);
            }
        }

        String[] arryListen = listen.toArray(new String[listen.size()]);
        Arrays.sort(arryListen);

        return arryListen;
    }

    /**
     * Gibt die Kontakte der übergebenen Liste zurück
     *
     * @param liste Name der Liste, von der die Kontakte zurückgegeben werden
     * sollen
     * @return Kontakte der übergebenen Liste
     */
    public Kontakt[] getKontakte(final String liste) {
        if (liste == null || liste.trim().isEmpty()) {
            throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
        }

        final HashSet<Kontakt> set = mKontakte.get(liste);
        if (set == null) {
            throw new IllegalArgumentException("Der Listenname existiert nicht");
        }

        return set.toArray(new Kontakt[set.size()]);
    }

    @Override
    public Iterator<Kontakt> iterator() {
        return mKontakte.get(DEFAULT).iterator();
    }
}
