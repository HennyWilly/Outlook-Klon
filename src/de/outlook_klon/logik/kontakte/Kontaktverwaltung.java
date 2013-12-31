package de.outlook_klon.logik.kontakte;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Diese Klasse stellt die Verwaltung für die Kontakte des Bentzers dar
 * 
 * @author Hendrik Karwanni
 */
public class Kontaktverwaltung {
	private HashMap<String, ArrayList<Kontakt>> mKontakte;
	
	private static final String DEFAULT = "Adressbuch";
	
	/**
	 * Erstellt eine neue Instanz der Kontaktverwaltung
	 */
	public Kontaktverwaltung() {
		mKontakte = new HashMap<String, ArrayList<Kontakt>>();
		mKontakte.put(DEFAULT, new ArrayList<Kontakt>());
	}
	
	/**
	 * Fügt den übergebenen Kontakt der Verwaltung hinzu
	 * @param kontakt Der hinzuzufügende Kontakt
	 */
	public void addKontakt(Kontakt kontakt) {
		addKontakt(kontakt, DEFAULT);
	}
	
	/**
	 * Fügt den übergebenen Kontakt der übergebenen Liste der Verwaltung hinzu
	 * @param kontakt Der hinzuzufügende Kontakt
	 * @param liste Listen, in die eingefügt werden soll
	 */
	public void addKontakt(Kontakt kontakt, String liste) {
		if(liste == null || liste.trim().isEmpty())
			throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
		if(kontakt == null)
			throw new NullPointerException("Instanz des Kontakts wurde nicht initialisiert");
		
		ArrayList<Kontakt> kontaktliste = mKontakte.get(liste);
		
		if(kontaktliste == null) 
			throw new NullPointerException("Der Listenname existiert nicht");
		if(kontaktliste.contains(kontakt))
			throw new IllegalArgumentException("Die Liste enthällt den Kontakt bereits");
		
		kontaktliste.add(kontakt);
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
		
		mKontakte.put(liste, new ArrayList<Kontakt>());
	}
	
	/**
	 * Löscht den übergebenen Kontakt aus der Verwaltung
	 * @param kontakt Zu löschender Kontakt
	 */
	public void löscheKontakt(Kontakt kontakt) {
		if(kontakt == null)
			throw new NullPointerException("Instanz des Kontakts wurde nicht initialisiert");
		
		Collection<ArrayList<Kontakt>> sammlung = mKontakte.values();
		for(ArrayList<Kontakt> liste : sammlung) {
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
		
		ArrayList<Kontakt> zielListe = mKontakte.get(liste);

		if(zielListe == null) 
			throw new NullPointerException("Der Listenname existiert nicht");
		
		zielListe.remove(kontakt);
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
		
		ArrayList<Kontakt> listenArray = mKontakte.remove(liste);

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
		
		ArrayList<Kontakt> liste = mKontakte.remove(alt);
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
		Set<String> arrayListe = mKontakte.keySet();
		return arrayListe.toArray(new String[mKontakte.size()]);
	}
	
	
	/**
	 * Gibt die Kontakte der übergebenen Liste zurück
	 * @param liste Name der Liste, von der die Kontakte zurückgegeben werden sollen
	 * @return Kontakte der übergebenen Liste
	 */
	public Kontakt[] getKontakte(String liste) {
		if(liste == null || liste.trim().isEmpty())
			throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
		
		ArrayList<Kontakt> arrayliste = mKontakte.get(liste);
		if(arrayliste == null) 
			throw new NullPointerException("Der Listenname existiert nicht");
		
		return arrayliste.toArray(new Kontakt[arrayliste.size()]);
	}
}
