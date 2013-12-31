package de.outlook_klon.logik.kontakte;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

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
	 * L�scht die �bergebene Liste aus der Verwaltung
	 * @param liste Liste, die gel�scht werden soll
	 */
	public void l�scheListe(String liste) {
		mKontakte.remove(liste);
	}
	
	/**
	 * Benennt die Liste mit dem �bergebenen alten Namen zum neuen Namen um
	 * @param alt Alter Name der Liste
	 * @param neu Neuer Name der Liste
	 */
	public void renameListe(String alt, String neu) {
		ArrayList<Kontakt> liste = mKontakte.remove(alt);
		if(liste != null) 
			mKontakte.put(neu, liste);
	}
	
	/**
	 * Gibt die Namen aller Kontaktlisten der Verwaltung zur�ck
	 * @return Namen aller Kontaktlisten
	 */
	public String[] getListen() {	
		Set<String> arrayListe = mKontakte.keySet();
		return arrayListe.toArray(new String[mKontakte.size()]);
	}
	
	/**
	 * Gibt die Kontakte der �bergebenen Liste zur�ck
	 * @param liste Name der Liste, von der die Kontakte zur�ckgegeben werden sollen
	 * @return Kontakte der �bergebenen Liste
	 */
	public Kontakt[] getKontakte(String liste) {
		if(liste == null)
			return null;
		
		ArrayList<Kontakt> arrayliste = mKontakte.get(liste);
		return arrayliste.toArray(new Kontakt[arrayliste.size()]);
	}
}
