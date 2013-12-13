package de.outlook_klon.logik.kontakte;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Diese Klasse stellt die Verwaltung f�r die Kontakte des Bentzers dar
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
	 * F�gt den �bergebenen Kontakt der Verwaltung hinzu
	 * @param kontakt Der hinzuzuf�gende Kontakt
	 */
	public void addKontakt(Kontakt kontakt) {
		ArrayList<Kontakt> keineListe = mKontakte.get("");
		
		if(!keineListe.contains(kontakt)) {
			keineListe.add(kontakt);
		}
	}
	
	/**
	 * F�gt den �bergebenen Kontakt der �bergebenen Liste der Verwaltung hinzu
	 * @param kontakt Der hinzuzuf�gende Kontakt
	 * @param liste Listen, in die eingef�gt werden soll
	 */
	public void addKontakt(Kontakt kontakt, String liste) {
		ArrayList<Kontakt> kontaktliste = mKontakte.get(liste);
		
		if(!kontaktliste.contains(kontakt)) {
			kontaktliste.add(kontakt);
		}
	}
	
	/**
	 * F�gt die �bergebene Liste der Verwaltung hinzu
	 * @param kontakt Die hinzuzuf�gende Liste
	 */
	public void addListe(String liste) {
		if(!mKontakte.containsKey(liste)) {
			mKontakte.put(liste, new ArrayList<Kontakt>());
		}
	}
	
	/**
	 * L�scht den �bergebenen Kontakt aus der Verwaltung
	 * @param kontakt Zu l�schender Kontakt
	 */
	public void l�scheKontakt(Kontakt kontakt) {
		Collection<ArrayList<Kontakt>> sammlung = mKontakte.values();
		
		for(ArrayList<Kontakt> liste : sammlung) {
			liste.remove(kontakt);
		}
	}
	
	/**
	 * L�scht den �bergebenen Kontakt aus der �bergebenen Liste
	 * @param kontakt Zu l�schender Kontakt
	 * @param liste Liste, aus der der Kontakt gel�scht werden soll
	 */
	public void l�scheKontakt(Kontakt kontakt, String liste) {
		ArrayList<Kontakt> zielListe = mKontakte.get(liste);
		zielListe.remove(kontakt);
	}
	
	/**
	 * Gibt die Namen aller Kontaktlisten der Verwaltung zur�ck
	 * @return Namen aller Kontaktlisten
	 */
	public String[] getListen() {	
		return mKontakte.keySet().toArray(new String[0]);
	}
	
	/**
	 * Gibt die Kontakte der �bergebenen Liste zur�ck
	 * @param liste Name der Liste, von der die Kontakte zur�ckgegeben werden sollen
	 * @return Kontakte der �bergebenen Liste
	 */
	public Kontakt[] getKontakte(String liste) {
		return mKontakte.get(liste).toArray(new Kontakt[0]);
	}
}
