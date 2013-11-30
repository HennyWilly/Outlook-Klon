package de.outlook_klon.logik;

import java.util.ArrayList;
import java.util.Iterator;

import de.outlook_klon.logik.kontakte.Kontaktverwaltung;
import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.kalendar.Terminkalendar;

/**
 * Diese Klasse stellt den Benutzer dar.
 * Bietet Zugriff auf die Termin- und Kontaktverwaltung.
 * Zudem ist es möglich, über die Mailkonten des Nutzers zu iterieren.
 * 
 * @author Hendrik Karwanni
 */
public class Benutzer implements Iterable<MailAccount> {
	private Kontaktverwaltung kontakte;
	private Terminkalendar termine;
	private ArrayList<MailAccount> konten;
	
	public Benutzer() {
		kontakte = new Kontaktverwaltung();
		termine = new Terminkalendar();
		
		konten = new ArrayList<MailAccount>();
	}

	@Override
	public Iterator<MailAccount> iterator() {
		return konten.iterator();
	}

	public Kontaktverwaltung getKontakte() {
		return kontakte;
	}
	
	public Terminkalendar getTermine() {
		return termine;
	}
	
	public boolean addMailAccount(MailAccount ma) {
		if(ma == null)
			return false;
		
		konten.add(ma);
		return true;
	}
}
