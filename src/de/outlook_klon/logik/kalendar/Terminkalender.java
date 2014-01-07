package de.outlook_klon.logik.kalendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

/**
 * Diese Klasse stellt die Verwaltung f�r die Termine des Benutzers dar
 * 
 * @author Hendrik Karwanni
 */
public class Terminkalender implements Iterable<Termin>, Serializable {
	private static final long serialVersionUID = 3595324672069971302L;
	
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
	 * F�gt den �bergebenen Termin der Verwaltung hinzu
	 * @param termin Der hinzuzuf�gende Termin
	 */
	public void addTermin(Termin termin) {
		mTermine.add(termin);
	}
	
	/**
	 * L�scht den �bergebenen Termin aus der Verwaltung
	 * @param termin Zu l�schender Termin
	 */
	public void l�scheTermin(Termin termin) {
		mTermine.remove(termin);
	}
	
	/**
	 * Gibt zur�ck, ob sich die Termine der Verwaltung mit dem �bergebenen Termin �berschneiden
	 * @param a Zu vergleichender Termin
	 * @return true, wenn sich mindestens ein Termin �berschneidet; sonst false
	 */
	public boolean ueberschneidung(Termin a){
		Date startA = a.getStart();
		Date endeA = a.getEnde();
		
		for(Termin b : mTermine)
		{
			Date startB = b.getStart();
			Date endeB = b.getEnde();
					//IF-Abfrage des Todes
			if ((startA.before(startB) && endeA.after(startB)) || (startA.before(endeB) && endeA.after(endeB)) 
					|| (startB.before(startA) && endeB.after(startA)) || (startB.before(endeA) && endeB.after(endeA)))
			
			{
				return true;
			}
		}
		return false;
	}	
	
	/**
	 * Gibt den Termin zur�ck, der am sp�testen beginnt
	 * @return Termin-Objekt, das zeitlich am sp�testen beginnt
	 */
	public Termin getOldest() {
		if(mTermine.size() == 0)
			return null;
		
		Termin t = mTermine.get(0);
		
		for(Termin a : mTermine) {
			if (a.getStart().before(t.getStart()))
				t=a;
		}
		return t;
	}
	
	/**
	 * Gibt die Anzahl der Termine der Verwaltung zur�ck
	 * @return Anzahl der Termine
	 */
	public int getSize() {
		return mTermine.size();
	}
	
	/**
	 * Gibt alle Termine in der �bergebenen Zeitspanne zur�ck
	 * @param start Startzeit der Auswertung
	 * @param ende Endzeit der Auswertung
	 * @return Termine innerhalb des intervalls
	 */
	public Termin[] getTermine(Date start, Date ende) {
		if(ende.before(start))
			throw new IllegalArgumentException("Der Startzeitpunkt darf nicht hinter dem Endzeitpunkt liegen");
		
		ArrayList<Termin> liste = new ArrayList<Termin>();
		for(Termin termin : mTermine) {
			Date startZeit = termin.getStart();
			if(start.equals(startZeit) || (startZeit.after(start) && startZeit.before(ende)))  {
				liste.add(termin);
			}
		}
		return liste.toArray(new Termin[liste.size()]);
	}
	
	/**
	 * Gibt alle Termine des aktuellen Tages zur�ck
	 * @return Termine des aktuellen Tages
	 */
	public Termin[] getTermine() {
		Date jetzt = new Date();
		
		Date start = null;
		Date ende = null;
		GregorianCalendar c = new GregorianCalendar();
		
		c.setTime(jetzt);
		c.set(GregorianCalendar.HOUR, 0);		//Setzt den Eintrag der Stunden auf 0
		c.set(GregorianCalendar.MINUTE, 0);		//Setzt den Eintrag der Minuten auf 0
		c.set(GregorianCalendar.SECOND, 0);		//Setzt den Eintrag der Sekunden auf 0
		
		start = c.getTime();					//�bergebener Tag mit der Uhrzeit 00:00:00
		c.add(GregorianCalendar.DAY_OF_YEAR, 1);
		ende = c.getTime();					//Tag um 1 h�her als time1
		
		return getTermine(start, ende);
	}
	
	/**
	 * Entfernt alle Termine aus der Verwaltung, die am �bergebenen Tag stattfinden
	 * @param tag Date-Objekt, welches den Tag enth�llt, an dem alle Termine entfernt werden
	 */
	public void absagen(Date tag) {
		Date time1 = null;
		Date time2 = null;
		GregorianCalendar c = new GregorianCalendar();
		
		c.setTime(tag);
		c.set(GregorianCalendar.HOUR, 0);		//Setzt den Eintrag der Stunden auf 0
		c.set(GregorianCalendar.MINUTE, 0);		//Setzt den Eintrag der Minuten auf 0
		c.set(GregorianCalendar.SECOND, 0);		//Setzt den Eintrag der Sekunden auf 0
		
		time1 = c.getTime();					//�bergebener Tag mit der Uhrzeit 00:00:00
		c.add(GregorianCalendar.DAY_OF_YEAR, 1);
		time2 = c.getTime();					//Tag um 1 h�her als time1
		
		for(int i = 0; i < mTermine.size(); i++) {
			Termin termin = mTermine.get(i);
			Date start = termin.getStart();
			
			if(start.equals(time1) || (start.after(time1) && start.before(time2))) {
				l�scheTermin(termin);
				i--;
			}
		}
	}
}
