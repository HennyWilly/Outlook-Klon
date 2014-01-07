package de.outlook_klon.logik;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import de.outlook_klon.logik.kalendar.Terminkalender;
import de.outlook_klon.logik.kontakte.Kontaktverwaltung;
import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.mailclient.MailInfo;

/**
 * Diese Klasse stellt den Benutzer dar.
 * Bietet Zugriff auf die Termin- und Kontaktverwaltung.
 * Zudem ist es m�glich, �ber die Mailkonten des Nutzers zu iterieren.
 * 
 * @author Hendrik Karwanni
 */
public final class Benutzer implements Iterable<MailAccount> {
	private static final String DATEN_ORDNER = "Mail";
	private static final String ACCOUNT_PATTERN = DATEN_ORDNER + "/%s";
	private static final String ACCOUNTSETTINGS_PATTERN = ACCOUNT_PATTERN + "/settings.bin";
	private static final String KONTAKT_PFAD = DATEN_ORDNER + "/Kontakte.bin";
	private static final String TERMIN_PFAD = DATEN_ORDNER + "/Termine.bin";
	
	private static Benutzer singleton;
	
	private Kontaktverwaltung kontakte;
	private Terminkalender termine;
	private ArrayList<MailAccount> konten;
	private boolean anwesend;
	
	private class MailChecker implements Runnable {
		private static final String FOLDER = "INBOX";
		private MailAccount account;
		private HashSet<MailInfo> mails;
		
		public MailChecker(MailAccount account) {
			this.account = account;
		}
		
		@Override
		public void run() {
			MailInfo[] mailInfos = account.getMessages(FOLDER);
			mails = new HashSet<MailInfo>();
			for(MailInfo info : mailInfos) {
				mails.add(info);
			}
			
			while(!isAnwesend()) {
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) { break; }
				MailInfo[] mailTmp = account.getMessages(FOLDER);
				for(MailInfo info : mailTmp) {
					if(mails.add(info) && !isAnwesend()) {
						sendeAbwesenheitsMail(account, FOLDER, info);
					}
				}
			}
		}
		
	}
	
	/**
	 * Gibt die einzige Instanz der Klasse Benutzer zur�ck.
	 * Beim ersten Aufruf wird eine neue Instanz der Klasse erstellt.
	 * @return Einzige Instanz der Klasse
	 */
	public static Benutzer getInstanz() {
		if(singleton == null)
			singleton = new Benutzer();
		return singleton;
	}
	
	/**
	 * Deserialisiert das Objekt, welches in der �bergebenen Datei gespeichert wurde
	 * @param datei Verweis auf die Datei, in der das zu deserialisierende Objekt gespeichert wurde
	 * @return Das deserialisierte Objekt, oder <code>null</code>, falls das Objekt nicht deserialisiert werden konnte
	 */
	@SuppressWarnings("unchecked")
	private static <T> T deserialisiereObjekt(final File datei) {
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
			if(ois != null) {
				try {
					ois.close();
				} catch (IOException e) { }
			}
		}
		
		return object;
	}
	
	/**
	 * Serialisiert das �bergebene Objekt in die �bergebene Datei
	 * @param objekt Objekt, das serialisiert werden soll
	 * @param datei Verweis auf die Datei, in der das zu serialisierende Objekt gespeichert werden soll
	 * @throws IOException Tritt auf, wenn das Objekt nicht serialisiert werden konnte
	 */
	private static <T> void serialisiereObjekt(T objekt, File datei) throws IOException {
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
	 * Liest, wenn vorhanden, die gespeicherten Daten aus.
	 */
	private Benutzer() {
		setAnwesend(true);
		
		termine = deserialisiereObjekt(new File(TERMIN_PFAD));
		if(termine == null)
			termine = new Terminkalender();
		
		kontakte = deserialisiereObjekt(new File(KONTAKT_PFAD));
		if(kontakte == null)
			kontakte = new Kontaktverwaltung();
		
		konten = new ArrayList<MailAccount>();
		
		File file = new File(DATEN_ORDNER).getAbsoluteFile();
		if(!file.exists())
			file.mkdirs();
		String[] directories = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File dir, String name) {
		    return new File(dir, name).isDirectory();
		  }
		});
		
		for(String directory : directories) {
			final String settings = String.format(ACCOUNTSETTINGS_PATTERN, directory);
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
	 * Gibt die Instanz der Kontaktverwaltung zur�ck
	 * @return Kontaktverwaltung des Benutzers
	 */
	public Kontaktverwaltung getKontakte() {
		return kontakte;
	}

	/**
	 * Gibt die Instanz der Terminverwaltung zur�ck
	 * @return Terminverwaltung des Benutzers
	 */
	public Terminkalender getTermine() {
		return termine;
	}
	
	/**
	 * F�gt die �bergebene Instanz eines MailAccounts dem Benutzer hinzu
	 * @param account Hinzuzuf�gender MailAccount
	 * @return true, wenn der �bergebene MailAccount nicht <code>null</code> ist; sonst false
	 */
	public boolean addMailAccount(MailAccount account) {
		if(account == null || konten.contains(account))
			return false;

		boolean result = true;
		try {
			speichereMailAccount(account);
			konten.add(account);
		} catch (IOException e) {
			result = false;
		}
		return result;
	}
	
	/**
	 * L�scht rekursiv die Datei oder den Ordner mit allen Unterordnern und Dateien
	 * @param file Verweis auf den zu l�schenden Eintrag im Dateisystem
	 * @throws IOException Tritt auf, wenn eine der Dateien nicht gel�scht werden konnte
	 */
	private void deleteRecursive(File file) throws IOException {
		if(file.isDirectory()) {
			File[] files = file.listFiles();
			for(File subfile : files)
				deleteRecursive(subfile);
		}
		
		if(!file.delete())
			throw new IOException("Datei \'" + file.getPath() + "\' konnte nicht gel�scht werden");
	}
	
	/**
	 * Entfernt den �bergebenen Account aus der Verwaltung
	 * @param account Zu l�schender Account
	 * @return true, wenn das l�schen erfolgreich war; sonst false
	 * @throws IOException Tritt auf, wenn einer der gespeicherten Dateien nicht gel�scht werden konnte
	 */
	public boolean entferneMailAccount(MailAccount account) throws IOException {
		if(konten.remove(account)) {
			String pfad = String.format(ACCOUNT_PATTERN, account.getAdresse().getAddress());
			File ordner = new File(pfad);
			if(ordner.exists()) {
				try {
					deleteRecursive(ordner);
					return true;
				} catch (IOException e) {
					addMailAccount(account);
					throw e;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Gibt die Anzahl an MailAccounts zur�ck
	 * @return Anzahl der MailAccounts
	 */
	public int getAnzahlKonten() {
		return konten.size();
	}
	
	/**
	 * Speichert die Daten des Benutzers
	 */
	public void speichern() throws IOException {
		for(MailAccount acc : konten) {
			speichereMailAccount(acc);
		}
		
		File kontaktPfad = new File(KONTAKT_PFAD).getAbsoluteFile();
		File terminPfad = new File(TERMIN_PFAD).getAbsoluteFile();
		File ordner = kontaktPfad.getParentFile();
		
		if(!ordner.exists()) {
			ordner.mkdirs();
		}
		
		serialisiereObjekt(kontakte, kontaktPfad);
		serialisiereObjekt(termine, terminPfad);
	}
	
	/**
	 * Speichert den �bergebenen MailAccount
	 * @param acc Zu speichernder MailAccount
	 * @throws IOException Tritt auf, wenn der MailAccount nicht gespeichert werden konnte
	 */
	private void speichereMailAccount(MailAccount acc) throws IOException {
		String strPfad = String.format(ACCOUNTSETTINGS_PATTERN, acc.getAdresse().getAddress());
		
		File pfad = new File(strPfad).getAbsoluteFile();
		File ordner = pfad.getParentFile();
		
		if(!ordner.exists()) {
			ordner.mkdirs();
		}
		
		serialisiereObjekt(acc, pfad);
	}

	/**
	 * Gibt zur�ck, ob der Benutzer anwesend ist
	 * @return Status der Anwesenheit
	 */
	public synchronized boolean isAnwesend() {
		return anwesend;
	}

	/**
	 * Setzt die Anwesenheit des Benutzers
	 * @param anwesend Zu setzender Status der Anwesenheit
	 */
	public void setAnwesend(boolean anwesend) {
		this.anwesend = anwesend;
		
		//TODO Hier muss was passieren!!!
		if(!anwesend) {
			for(MailAccount account : konten) {
				MailChecker checker = new MailChecker(account);
				Thread checkerThread = new Thread(checker);
				checkerThread.start();
			}
		}
	}
	
	private void sendeAbwesenheitsMail(MailAccount sender, String pfad, MailInfo info) {
		//TODO Jetzt Abwesenheitmail senden

		try {
			sender.getWholeMessage(pfad, info);
			
			InternetAddress ziel = (InternetAddress) info.getSender();
			
			sender.sendeMail(new InternetAddress[] {ziel}, null, "Abwesenheit von " + sender.getAdresse().getPersonal(), 
					"Ich bin nicht da", "TEXT/plain; charset=utf-8", null);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
