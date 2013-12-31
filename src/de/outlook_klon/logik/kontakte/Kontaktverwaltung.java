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
	
	private static final String DEFAULT = "Adressbuch";
	
	/**
	 * Erstellt eine neue Instanz der Kontaktverwaltung
	 */
	public Kontaktverwaltung() {
		mKontakte = new HashMap<String, ArrayList<Kontakt>>();
		mKontakte.put(DEFAULT, new ArrayList<Kontakt>());
	}
	
	/**
	 * F�gt den �bergebenen Kontakt der Verwaltung hinzu
	 * @param kontakt Der hinzuzuf�gende Kontakt
	 */
	public void addKontakt(Kontakt kontakt) {
		addKontakt(kontakt, DEFAULT);
	}
	
	/**
	 * F�gt den �bergebenen Kontakt der �bergebenen Liste der Verwaltung hinzu
	 * @param kontakt Der hinzuzuf�gende Kontakt
	 * @param liste Listen, in die eingef�gt werden soll
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
			throw new IllegalArgumentException("Die Liste enth�llt den Kontakt bereits");
		
		kontaktliste.add(kontakt);
	}
	
	/**
	 * F�gt die �bergebene Liste der Verwaltung hinzu
	 * @param kontakt Die hinzuzuf�gende Liste
	 */
	public void addListe(String liste) {
		if(liste == null || liste.trim().isEmpty())
			throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
		
		if(mKontakte.containsKey(liste))
			throw new IllegalArgumentException("Der Listenname ist bereits vorhanden!");
		
		mKontakte.put(liste, new ArrayList<Kontakt>());
	}
	
	/**
	 * L�scht den �bergebenen Kontakt aus der Verwaltung
	 * @param kontakt Zu l�schender Kontakt
	 */
	public void l�scheKontakt(Kontakt kontakt) {
		if(kontakt == null)
			throw new NullPointerException("Instanz des Kontakts wurde nicht initialisiert");
		
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
	 * L�scht die �bergebene Liste aus der Verwaltung
	 * @param liste Liste, die gel�scht werden soll
	 */
	public void l�scheListe(String liste) {
		if(liste == null || liste.trim().isEmpty())
			throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
		if(DEFAULT.equals(liste))
			throw new IllegalArgumentException("Das Standardadressbuch darf nicht entfernt werden");
		
		ArrayList<Kontakt> listenArray = mKontakte.remove(liste);

		if(listenArray == null) 
			throw new NullPointerException("Der Listenname existiert nicht");
	}
	
	/**
	 * Benennt die Liste mit dem �bergebenen alten Namen zum neuen Namen um
	 * @param alt Alter Name der Liste
	 * @param neu Neuer Name der Liste
	 */
	public void renameListe(String alt, String neu) {
		if(alt == null || alt.trim().isEmpty() || neu == null || neu.trim().isEmpty()) 
			throw new NullPointerException("Die Listennamen d�rfen nicht leer sein!");
		
		ArrayList<Kontakt> liste = mKontakte.remove(alt);
		if(liste == null)
			throw new NullPointerException("Der alte Listenname existiert nicht");
		if(mKontakte.get(neu) != null)
			throw new IllegalArgumentException("Der neue Listenname existiert bereits");
		
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
		if(liste == null || liste.trim().isEmpty())
			throw new NullPointerException("Der Name der Liste darf nicht leer sein.");
		
		ArrayList<Kontakt> arrayliste = mKontakte.get(liste);
		if(arrayliste == null) 
			throw new NullPointerException("Der Listenname existiert nicht");
		
		return arrayliste.toArray(new Kontakt[arrayliste.size()]);
	}
}
