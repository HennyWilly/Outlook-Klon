package de.outlook_klon.logik.kalendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

/**
 * Diese Klasse stellt die Verwaltung für die Termine des Bentzers dar
 * 
 * @author Hendrik Karwanni
 */
public class Terminkalender implements Iterable<Termin> {
	private ArrayList<Termin> mTermine;

	/**
	 * Erstellt eine neue Instanz der Terminverwaltung
	 */
	public Terminkalender() {
		mTermine = new ArrayList<Termin>();
	}

	@Override
	public Iterator<Termin> iterator() {
		return mTermine.iterator();
	}

	/**
	 * Fügt den übergebenen Termin der Verwaltung hinzu
	 * @param termin Der hinzuzufügende Termin
	 */
	public void addTermin(Termin termin) {
		mTermine.add(termin);
	}
	
	/**
	 * Löscht den übergebenen Termin aus der Verwaltung
	 * @param termin Zu löschender Termin
	 */
	public void löscheTermin(Termin termin) {
		mTermine.remove(termin);
	}
	
	public boolean ueberschneidung(Termin a){
		Date startA = a.getStart();
		Date endeA = a.getEnde();
		
		for(Termin b : this)
		{
			Date startB = b.getStart();
			Date endeB = b.getEnde();
					//IF-Abfrage des Todes
			if      (((startA.before(startB))&& (endeA.after(startB))) || ((startA.before(endeB)) && (endeA.after(endeB)))
					|| ((startB.before(startA)) && (endeB.after(startA))) || ((startB.before(endeA)) && (endeB.after(endeA))))
			
			{
				return true;
			}
		}
		return false;
	}
	
					
	
	
	public Termin getOldest(){
		Termin t = mTermine.get(0);
		
		for(Termin a : mTermine) {
			if (a.getStart().before(t.getStart()))
			{
				t=a;
			}
		}
		return t;
	}
	

	
	public int getSize(){
		return mTermine.size();
		}
	
	
	/**
	 * Entfernt alle Termine aus der Verwaltung, die am übergebenen Tag stattfinden
	 * @param tag Date-Objekt, welches den Tag enthällt, an dem alle Termine entfernt werden
	 */
	public void absagen(Date tag) {
		Date time1 = null;
		Date time2 = null;
		GregorianCalendar c = new GregorianCalendar();
		
		c.setTime(tag);
		c.set(GregorianCalendar.HOUR, 0);		//Setzt den Eintrag der Stunden auf 0
		c.set(GregorianCalendar.MINUTE, 0);		//Setzt den Eintrag der Minuten auf 0
		c.set(GregorianCalendar.SECOND, 0);		//Setzt den Eintrag der Sekunden auf 0
		
		time1 = c.getTime();					//Übergebener Tag mit der Uhrzeit 00:00:00
		c.add(GregorianCalendar.DAY_OF_YEAR, 1);
		time2 = c.getTime();					//Tag um 1 höher als time1
		
		for(int i = 0; i < mTermine.size(); i++) {
			Termin termin = mTermine.get(i);
			Date start = termin.getStart();
			
			if(start.equals(time1) || (start.after(time1) && start.before(time2))) {
				löscheTermin(termin);
				i--;
			}
		}
	}
}
