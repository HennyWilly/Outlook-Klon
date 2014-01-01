package de.outlook_klon.logik;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import de.outlook_klon.logik.kontakte.Kontaktverwaltung;
import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.kalendar.Terminkalender;

/**
 * Diese Klasse stellt den Benutzer dar.
 * Bietet Zugriff auf die Termin- und Kontaktverwaltung.
 * Zudem ist es möglich, über die Mailkonten des Nutzers zu iterieren.
 * 
 * @author Hendrik Karwanni
 */
public class Benutzer implements Iterable<MailAccount> {
	private static final String ACCOUNT_PATTERN = "Mail/%s/settings.bin";
	private static final String KONTAKT_PFAD = "Mail/Kontakte.bin";
	
	private Kontaktverwaltung kontakte;
	private Terminkalender termine;
	private ArrayList<MailAccount> konten;
	
	@SuppressWarnings("unchecked")
	private <T> T deserialisiereObjekt(File datei) {
		if(!datei.exists())
			return null;
		
		T object = null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(datei.getAbsolutePath());
			ois = new ObjectInputStream(fis);
			
			object = (T)ois.readObject();
				
		} catch (Exception e) { }
		finally {
			try {
				ois.close();
			} catch (IOException e) { }
		}
		
		return object;
	}
	
	private <T> void serialisiereObjekt(T objekt, File datei) throws IOException {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(datei.getAbsolutePath());
			oos = new ObjectOutputStream(fos);
			
			oos.writeObject(objekt);
		} 
		finally {
			if(oos != null)
				oos.close();
		}
	}
	
	/**
	 * Erstellt eine neue Instanz der Klasse Benutzer.
	 * Ließt, wenn vorhanden, die gespeicherten Daten aus.
	 */
	public Benutzer() {
		termine = new Terminkalender();
		kontakte = deserialisiereObjekt(new File(KONTAKT_PFAD));
		if(kontakte == null)
			kontakte = new Kontaktverwaltung();
		
		konten = new ArrayList<MailAccount>();
		
		File file = new File("Mail").getAbsoluteFile();
		if(!file.exists())
			file.mkdirs();
		String[] directories = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File dir, String name) {
		    return new File(dir, name).isDirectory();
		  }
		});
		
		for(String directory : directories) {
			String settings = file + "\\" + directory + "\\settings.bin";
			File datei = new File(settings).getAbsoluteFile();
			
			MailAccount geladen = deserialisiereObjekt(datei);
			if(geladen != null)
				konten.add(geladen);
		}
	}

	@Override
	public Iterator<MailAccount> iterator() {
		return konten.iterator();
	}

	/**
	 * Gibt die Instanz der Kontaktverwaltung zurück
	 * @return Kontaktverwaltung des Benutzers
	 */
	public Kontaktverwaltung getKontakte() {
		return kontakte;
	}

	/**
	 * Gibt die Instanz der Terminverwaltung zurück
	 * @return Terminverwaltung des Benutzers
	 */
	public Terminkalender getTermine() {
		return termine;
	}
	
	/**
	 * Fügt die übergebene Instanz eines MailAccounts dem Benutzer hinzu
	 * @param ma Hinzuzufügender MailAccount
	 * @return true, wenn der übergebene MailAccount nicht <code>null</code> ist; sonst false
	 */
	public boolean addMailAccount(MailAccount ma) {
		if(ma == null || konten.contains(ma))
			return false;

		boolean result = true;
		try {
			speichereMailAccount(ma);
			konten.add(ma);
		} catch (IOException e) {
			result = false;
		}
		return result;
	}
	
	/**
	 * Speichert die Daten des Benutzers
	 */
	public void speichern() throws IOException {
		for(MailAccount acc : konten) {
			speichereMailAccount(acc);
		}
		
		File kontaktPfad = new File(KONTAKT_PFAD).getAbsoluteFile();
		File ordner = kontaktPfad.getParentFile();
		
		if(!ordner.exists()) {
			ordner.mkdirs();
		}
		
		serialisiereObjekt(kontakte, kontaktPfad);
	}
	
	/**
	 * Speichert den übergebenen MailAccount
	 * @param acc Zu speichernder MailAccount
	 * @throws IOException Tritt auf, wenn der MailAccount nicht gespeichert werden konnte
	 */
	private void speichereMailAccount(MailAccount acc) throws IOException {
		String strPfad = String.format(ACCOUNT_PATTERN, acc.getAdresse().getAddress());
		
		File pfad = new File(strPfad).getAbsoluteFile();
		File ordner = pfad.getParentFile();
		
		if(!ordner.exists()) {
			ordner.mkdirs();
		}
		
		serialisiereObjekt(acc, pfad);
	}
}
