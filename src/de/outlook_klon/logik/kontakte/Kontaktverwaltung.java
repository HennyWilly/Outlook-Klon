package de.outlook_klon.logik.kontakte;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Diese Klasse stellt die Verwaltung für die Kontakte des Bentzers dar
 * 
 * @author Hendrik Karwanni
 */
public class Kontaktverwaltung implements Iterable<Kontakt> {
	private ArrayList<Kontakt> mKontakte;
	
	/**
	 * Erstellt eine neue Instanz der Kontaktverwaltung
	 */
	public Kontaktverwaltung() {
		mKontakte = new ArrayList<Kontakt>();
	}

	@Override
	public Iterator<Kontakt> iterator() {
		return mKontakte.iterator();
	}
	
	/**
	 * Fügt den übergebenen Kontakt der Verwaltung hinzu
	 * @param kontakt Der hinzuzufügende Kontakt
	 */
	public void addKontakt(Kontakt kontakt) {
		mKontakte.add(kontakt);
	}
	
	/**
	 * Löscht den übergebenen Kontakt aus der Verwaltung
	 * @param kontakt Zu löschender Kontakt
	 */
	public void löscheKontakt(Kontakt kontakt) {
		mKontakte.remove(kontakt);
	}
}
