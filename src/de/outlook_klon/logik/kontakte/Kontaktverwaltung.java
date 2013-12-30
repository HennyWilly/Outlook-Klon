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
	
	/**
	 * Erstellt eine neue Instanz der Kontaktverwaltung
	 */
	public Kontaktverwaltung() {
		mKontakte = new HashMap<String, ArrayList<Kontakt>>();
		mKontakte.put("", new ArrayList<Kontakt>());
	}
	
	/**
	 * Fügt den übergebenen Kontakt der Verwaltung hinzu
	 * @param kontakt Der hinzuzufügende Kontakt
	 */
	public void addKontakt(Kontakt kontakt) {
		ArrayList<Kontakt> keineListe = mKontakte.get("");
		
		if(!keineListe.contains(kontakt)) {
			keineListe.add(kontakt);
		}
	}
	
	/**
	 * Fügt den übergebenen Kontakt der übergebenen Liste der Verwaltung hinzu
	 * @param kontakt Der hinzuzufügende Kontakt
	 * @param liste Listen, in die eingefügt werden soll
	 */
	public void addKontakt(Kontakt kontakt, String liste) {
		ArrayList<Kontakt> kontaktliste = mKontakte.get(liste);
		
		if(!kontaktliste.contains(kontakt)) {
			kontaktliste.add(kontakt);
		}
	}
	
	/**
	 * Fügt die übergebene Liste der Verwaltung hinzu
	 * @param kontakt Die hinzuzufügende Liste
	 */
	public void addListe(String liste) {
		if(!mKontakte.containsKey(liste)) {
			mKontakte.put(liste, new ArrayList<Kontakt>());
		}
	}
	
	/**
	 * Löscht den übergebenen Kontakt aus der Verwaltung
	 * @param kontakt Zu löschender Kontakt
	 */
	public void löscheKontakt(Kontakt kontakt) {
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
		ArrayList<Kontakt> zielListe = mKontakte.get(liste);
		zielListe.remove(kontakt);
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
		ArrayList<Kontakt> arrayliste = mKontakte.get(liste);
		return arrayliste.toArray(new Kontakt[arrayliste.size()]);
	}
}
