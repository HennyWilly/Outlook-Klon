package de.outlook_klon.logik.kalendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import de.outlook_klon.logik.kalendar.Termin.Status;

/**
 * Diese Klasse stellt die Verwaltung für die Termine des Benutzers dar
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
	 * Fügt den übergebenen Termin der Verwaltung hinzu
	 * 
	 * @param termin
	 *            Der hinzuzufügende Termin
	 */
	public void addTermin(Termin termin) {
		mTermine.add(termin);
	}

	/**
	 * Löscht den übergebenen Termin aus der Verwaltung
	 * 
	 * @param termin
	 *            Zu löschender Termin
	 */
	public void löscheTermin(Termin termin) {
		mTermine.remove(termin);
	}

	/**
	 * Gibt zurück, ob sich die Termine der Verwaltung mit dem übergebenen
	 * Termin überschneiden
	 * 
	 * @param a
	 *            Zu vergleichender Termin
	 * @return true, wenn sich mindestens ein Termin überschneidet; sonst false
	 */
	public boolean ueberschneidung(Termin a) {
		Date startA = a.getStart();
		Date endeA = a.getEnde();

		for (Termin b : mTermine) {
			Date startB = b.getStart();
			Date endeB = b.getEnde();
			// IF-Abfrage des Todes
			if ((startA.before(startB) && endeA.after(startB))
					|| (startA.before(endeB) && endeA.after(endeB))
					|| (startB.before(startA) && endeB.after(startA))
					|| (startB.before(endeA) && endeB.after(endeA)))

			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Gibt den Termin zurück, der am ehesten beginnt
	 * 
	 * @return Termin-Objekt, das zeitlich am ehesten beginnt
	 */
	public Termin getOldest() {
		if (mTermine.size() == 0)
			return null;

		Termin t = mTermine.get(0);

		for (Termin a : mTermine) {
			if (a.getStart().before(t.getStart()))
				t = a;
		}
		return t;
	}

	/**
	 * Gibt die Anzahl der Termine der Verwaltung zurück
	 * 
	 * @return Anzahl der Termine
	 */
	public int getSize() {
		return mTermine.size();
	}

	/**
	 * Gibt alle Termine in der übergebenen Zeitspanne zurück
	 * 
	 * @param start
	 *            Startzeit der Auswertung
	 * @param ende
	 *            Endzeit der Auswertung
	 * @return Termine innerhalb des intervalls
	 */
	public Termin[] getTermine(Date start, Date ende) {
		if (ende.before(start))
			throw new IllegalArgumentException(
					"Der Startzeitpunkt darf nicht hinter dem Endzeitpunkt liegen");

		ArrayList<Termin> liste = new ArrayList<Termin>();
		for (Termin termin : mTermine) {
			Date startZeit = termin.getStart();
			if (termin.getStatus() != Status.abgelehnt
					&& start.equals(startZeit)
					|| (startZeit.after(start) && startZeit.before(ende))) {
				liste.add(termin);
			}
		}
		return liste.toArray(new Termin[liste.size()]);
	}

	/**
	 * Gibt alle Termine des aktuellen Tages zurück
	 * 
	 * @return Termine des aktuellen Tages
	 */
	public Termin[] getTermine() {
		Date jetzt = new Date();

		Date start = null;
		Date ende = null;
		GregorianCalendar c = new GregorianCalendar();

		c.setTime(jetzt);
		c.set(Calendar.HOUR, 0); // Setzt den Eintrag der Stunden auf 0
		c.set(Calendar.MINUTE, 0); // Setzt den Eintrag der Minuten auf 0
		c.set(Calendar.SECOND, 0); // Setzt den Eintrag der Sekunden auf 0

		start = c.getTime(); // Übergebener Tag mit der Uhrzeit 00:00:00
		c.add(Calendar.DAY_OF_YEAR, 1);
		ende = c.getTime(); // Tag um 1 höher als time1

		return getTermine(start, ende);
	}

	/**
	 * Entfernt alle Termine aus der Verwaltung, die am aktuellen Tag
	 * stattfinden
	 */
	public void absagen() {
		for(Termin t: getTermine()){
			t.setStatus(Status.abgelehnt);
		}
	}
}
