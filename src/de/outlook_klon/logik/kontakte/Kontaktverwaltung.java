package de.outlook_klon.logik.kontakte;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Diese Klasse stellt die Verwaltung für die Kontakte des Benutzers dar
 * 
 * @author Hendrik Karwanni
 */
public class Kontaktverwaltung implements Serializable {
	private static final long serialVersionUID = -5634887633796780397L;

	private HashMap<String, HashSet<Kontakt>> mKontakte;
	
	public static final String DEFAULT = "Adressbuch";
	
	/**
	 * Erstellt eine neue Instanz der Kontaktverwaltung
	 */
	public Kontaktverwaltung() {
		mKontakte = new HashMap<String, HashSet<Kontakt>>();
		mKontakte.put(DEFAULT, new HashSet<Kontakt>());
	}
	
	/**
	 * Fügt den übergebenen Kontakt der Verwaltung hinzu
	 * @param kontakt Der hinzuzufügende Kontakt
	 */
	public void addKontakt(Kontakt kontakt) {
		HashSet<Kontakt> kontaktliste = mKontakte.get(DEFAULT);
		
		kontaktliste.add(kontakt);
	}
	
	/**
	 * Fügt den übergebenen Kontakt der übergebenen Liste der Verwaltung hinzu
	 * @param kontakt Der hinzuzufügende Kontakt
	 * @param liste Listen, in die eingefügt werden soll
	 */
	public void addKontaktZuListe(Kontakt kontakt, String liste) {
		if(liste == null || liste.trim().isEmpty())
			throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
		if(kontakt == null)
			throw new NullPointerException("Instanz des Kontakts wurde nicht initialisiert");
		
		HashSet<Kontakt> kontaktliste = mKontakte.get(liste);
		if(kontaktliste == null) 
			throw new NullPointerException("Der Listenname existiert nicht");
		if(!kontaktliste.add(kontakt))
			throw new IllegalArgumentException("Die Liste enthällt den Kontakt bereits");
		
		addKontakt(kontakt);
	}
	
	/**
	 * Fügt die übergebene Liste der Verwaltung hinzu
	 * @param kontakt Die hinzuzufügende Liste
	 */
	public void addListe(String liste) {
		if(liste == null || liste.trim().isEmpty())
			throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
		
		if(mKontakte.containsKey(liste))
			throw new IllegalArgumentException("Der Listenname ist bereits vorhanden!");
		
		mKontakte.put(liste, new HashSet<Kontakt>());
	}
	
	/**
	 * Löscht den übergebenen Kontakt aus der Verwaltung
	 * @param kontakt Zu löschender Kontakt
	 */
	public void löscheKontakt(Kontakt kontakt) {
		if(kontakt == null)
			throw new NullPointerException("Instanz des Kontakts wurde nicht initialisiert");
		
		Collection<HashSet<Kontakt>> sammlung = mKontakte.values();
		for(HashSet<Kontakt> liste : sammlung) {
			liste.remove(kontakt);
		}
	}
	
	/**
	 * Löscht den übergebenen Kontakt aus der übergebenen Liste
	 * @param kontakt Zu löschender Kontakt
	 * @param liste Liste, aus der der Kontakt gelöscht werden soll
	 */
	public void löscheKontakt(Kontakt kontakt, String liste) {		
		if(kontakt == null)
			throw new NullPointerException("Instanz des Kontakts wurde nicht initialisiert");
		if(liste == null || liste.trim().isEmpty())
			throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
		
		if(DEFAULT.equals(liste))
			löscheKontakt(kontakt);
		else {
			HashSet<Kontakt> zielListe = mKontakte.get(liste);

			if(zielListe == null) 
				throw new NullPointerException("Der Listenname existiert nicht");
			
			zielListe.remove(kontakt);
		}
	}

	/**
	 * Löscht die übergebene Liste aus der Verwaltung
	 * @param liste Liste, die gelöscht werden soll
	 */
	public void löscheListe(String liste) {
		if(liste == null || liste.trim().isEmpty())
			throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
		if(DEFAULT.equals(liste))
			throw new IllegalArgumentException("Das Standardadressbuch darf nicht entfernt werden");
		
		HashSet<Kontakt> listenArray = mKontakte.remove(liste);

		if(listenArray == null) 
			throw new NullPointerException("Der Listenname existiert nicht");
	}
	
	/**
	 * Benennt die Liste mit dem übergebenen alten Namen zum neuen Namen um
	 * @param alt Alter Name der Liste
	 * @param neu Neuer Name der Liste
	 */
	public void renameListe(String alt, String neu) {
		if(alt == null || alt.trim().isEmpty() || neu == null || neu.trim().isEmpty()) 
			throw new NullPointerException("Die Listennamen dürfen nicht leer sein!");
		
		if(DEFAULT.equals(alt))
			throw new IllegalArgumentException("Das Standardadressbuch darf nicht umbenannt werden");
		
		HashSet<Kontakt> liste = mKontakte.remove(alt);
		if(liste == null)
			throw new NullPointerException("Der alte Listenname existiert nicht");
		if(mKontakte.get(neu) != null)
			throw new IllegalArgumentException("Der neue Listenname existiert bereits");
		
		mKontakte.put(neu, liste);
	}
	
	/**
	 * Gibt die Namen aller Kontaktlisten der Verwaltung zurück
	 * @return Namen aller Kontaktlisten
	 */
	public String[] getListen() {	
		Set<String> listen = mKontakte.keySet();
		return listen.toArray(new String[mKontakte.size()]);
	}
	
	/**
	 * Gibt die Kontakte der übergebenen Liste zurück
	 * @param liste Name der Liste, von der die Kontakte zurückgegeben werden sollen
	 * @return Kontakte der übergebenen Liste
	 */
	public Kontakt[] getKontakte(String liste) {
		if(liste == null || liste.trim().isEmpty())
			throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
		
		HashSet<Kontakt> set = mKontakte.get(liste);
		if(set == null) 
			throw new NullPointerException("Der Listenname existiert nicht");
		
		return set.toArray(new Kontakt[set.size()]);
	}
}
