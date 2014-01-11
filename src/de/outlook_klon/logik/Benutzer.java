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
import java.util.Vector;

import javax.mail.FolderNotFoundException;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import de.outlook_klon.logik.kalendar.Termin;
import de.outlook_klon.logik.kalendar.Terminkalender;
import de.outlook_klon.logik.kontakte.Kontaktverwaltung;
import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.mailclient.MailInfo;

/**
 * Diese Klasse stellt den Benutzer dar. Bietet Zugriff auf die Termin- und
 * Kontaktverwaltung. Zudem ist es m�glich, �ber die Mailkonten des Nutzers zu
 * iterieren.
 * 
 * @author Hendrik Karwanni
 */
public final class Benutzer implements Iterable<Benutzer.MailChecker> {
	private static final String DATEN_ORDNER = "Mail";
	private static final String ACCOUNT_PATTERN = DATEN_ORDNER + "/%s";
	private static final String ACCOUNTSETTINGS_PATTERN = ACCOUNT_PATTERN
			+ "/settings.bin";
	private static final String KONTAKT_PFAD = DATEN_ORDNER + "/Kontakte.bin";
	private static final String TERMIN_PFAD = DATEN_ORDNER + "/Termine.bin";

	private static Benutzer singleton;

	private Kontaktverwaltung kontakte;
	private Terminkalender termine;
	private ArrayList<MailChecker> konten;
	private boolean anwesend;

	/**
	 * Diese Klasse dient zum automatischen, intervallweisen Abfragen des
	 * Posteingangs eines MailAccounts.
	 */
	public class MailChecker extends Thread {
		private static final String FOLDER = "INBOX";
		private MailAccount account;
		private HashSet<MailInfo> mails;
		private Vector<NewMailListener> listenerVector;

		/**
		 * Erzeugt ein neues MailChecker-Objekt f�r den �bergebenen Account
		 * 
		 * @param account
		 *            MailAccount, der abgeh�rt werden soll
		 */
		public MailChecker(MailAccount account) {
			this.account = account;
			this.mails = new HashSet<MailInfo>();
			this.listenerVector = new Vector<NewMailListener>();
		}

		/**
		 * Registriert einen neuen NewMailListener f�r Events innerhalb der
		 * Klasse
		 * 
		 * @param mcl
		 *            Neuer NewMailListener
		 */
		public void addNewMessageListener(NewMailListener mcl) {
			if (mcl == null)
				throw new NullPointerException(
						"Der hinzuzuf�gende Listener muss initialisiert sein.");

			synchronized (listenerVector) {
				listenerVector.add(mcl);
			}
		}

		/**
		 * Feuert ein neues NewMessageEvent f�r die �bergebene MailInfo an alle
		 * registrierten Listener
		 * 
		 * @param info
		 *            MailInfo-Objekt, aus dem das Event erzeugt wird
		 */
		private void fireNewMessageEvent(MailInfo info) {
			NewMailEvent ev = new NewMailEvent(this, FOLDER, info);

			for (NewMailListener listener : listenerVector) {
				listener.newMessage(ev);
			}
		}

		/**
		 * Gibt den internen MailAccount zur�ck
		 * 
		 * @return Interner MailAccount
		 */
		public MailAccount getAccount() {
			return account;
		}

		@Override
		public void run() {
			synchronized (mails) {
				// Erstmaliges Abfragen des Posteingangs

				MailInfo[] mailInfos = null;
				try {
					mailInfos = account.getMessages(FOLDER);
				} catch (FolderNotFoundException e) {
					//Ignorieren, da INBOX immer vorhanden sein sollte!
				}
				
				for (MailInfo info : mailInfos) {
					mails.add(info);
					if(!info.isRead()) {
						fireNewMessageEvent(info);
					}
				}
			}

			while (true) {
				try {
					Thread.sleep(60000); // Pausiere f�r eine Minute
					MailInfo[] mailTmp = account.getMessages(FOLDER);
					
					// HashSet, da oft darin gesucht wird (s.u.)
					HashSet<MailInfo> tmpSet = new HashSet<MailInfo>(); 
					for (MailInfo info : mailTmp) {
						tmpSet.add(info); // F�lle mit abgefragten MailInfos
					}

					// Pr�fe, ob eine bekannte MailInfo weggefallen ist
					Iterator<MailInfo> iterator = mails.iterator();
					while (iterator.hasNext()) {
						MailInfo current = iterator.next();

						if (!tmpSet.contains(current)) {
							// Entferne weggefallene MailInfo aus dem Speicher
							iterator.remove();
						}
					}

					synchronized (mails) {
						for (MailInfo info : mailTmp) {
							if (mails.add(info)) {
								// Wird eine neue MailInfo hinzugef�gt, werden
								// die Listener benachrichtigt
								fireNewMessageEvent(info);
							}
						}
					}
				} catch (InterruptedException e) {
					// Bricht die Ausf�hrung ab
					break;
				} catch (FolderNotFoundException e) {
					//Ignorieren, da INBOX immer vorhanden sein sollte!
				}
			}
		}

		/**
		 * Wenn der Thread aktiv und der gesuchte Ordner "INBOX" ist, werden die
		 * MailInfos aus dem Speicher des MailCheckers zur�ckgegeben; sonst wird
		 * die getMessages-Methode des internen MailAccount-Objekts aufgerufen
		 * 
		 * @param pfad
		 *            Zu durchsuchender Ordnerpfad
		 * @return MailInfos des gesuchten Ordners
		 * @throws FolderNotFoundException
		 *             Tritt auf, wenn ein Ordner nicht gefunden werden konnte
		 */
		public MailInfo[] getMessages(String pfad) throws FolderNotFoundException {
			MailInfo[] array = null;

			boolean threadOK = this.isAlive() && !this.isInterrupted();
			if (threadOK && pfad.toLowerCase().equals(FOLDER.toLowerCase())) {
				synchronized (mails) {
					array = mails.toArray(new MailInfo[mails.size()]);
				}
			} else {
				array = account.getMessages(pfad);
			}

			return array;
		}

		/**
		 * Entfernt die �bergebenen MailInfos aus dem Speicher
		 * 
		 * @param infos
		 *            Zu entfernende MailInfos
		 */
		public void removeMailInfos(MailInfo[] infos) {
			synchronized (mails) {
				for (MailInfo info : infos) {
					mails.remove(info);
				}
			}
		}

		@Override
		public String toString() {
			return account.toString();
		}
		
		@Override
		public boolean equals(Object other) {
			if(this == other)
				return true;
			if(other == null)
				return false;
			
			if(other instanceof MailChecker) {
				MailChecker checker = ((MailChecker)other);
				return this.getAccount().equals(checker.getAccount());
			}
			if(other instanceof MailAccount) {
				MailAccount account = (MailAccount) other;
				return this.getAccount().equals(account);
			}
			
			return false;
		}
	}

	/**
	 * Gibt die einzige Instanz der Klasse Benutzer zur�ck. Beim ersten Aufruf
	 * wird eine neue Instanz der Klasse erstellt.
	 * 
	 * @return Einzige Instanz der Klasse
	 */
	public static Benutzer getInstanz() {
		if (singleton == null)
			singleton = new Benutzer();
		return singleton;
	}

	/**
	 * Deserialisiert das Objekt, welches in der �bergebenen Datei gespeichert
	 * wurde
	 * 
	 * @param datei
	 *            Verweis auf die Datei, in der das zu deserialisierende Objekt
	 *            gespeichert wurde
	 * @return Das deserialisierte Objekt, oder <code>null</code>, falls das
	 *         Objekt nicht deserialisiert werden konnte
	 */
	@SuppressWarnings("unchecked")
	private static <T> T deserialisiereObjekt(final File datei) {
		if (!datei.exists())
			return null;

		T object = null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(datei.getAbsolutePath());
			ois = new ObjectInputStream(fis);

			object = (T) ois.readObject();
		} catch (Exception e) {

		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
				}
			}
		}

		return object;
	}

	/**
	 * Serialisiert das �bergebene Objekt in die �bergebene Datei
	 * 
	 * @param objekt
	 *            Objekt, das serialisiert werden soll
	 * @param datei
	 *            Verweis auf die Datei, in der das zu serialisierende Objekt
	 *            gespeichert werden soll
	 * @throws IOException
	 *             Tritt auf, wenn das Objekt nicht serialisiert werden konnte
	 */
	private static <T> void serialisiereObjekt(T objekt, File datei)
			throws IOException {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(datei.getAbsolutePath());
			oos = new ObjectOutputStream(fos);

			oos.writeObject(objekt);
		} finally {
			if (oos != null)
				oos.close();
		}
	}

	/**
	 * Erstellt eine neue Instanz der Klasse Benutzer. Liest, wenn vorhanden,
	 * die gespeicherten Daten aus.
	 */
	private Benutzer() {
		setAnwesend(true);

		termine = deserialisiereObjekt(new File(TERMIN_PFAD));
		if (termine == null)
			termine = new Terminkalender();

		kontakte = deserialisiereObjekt(new File(KONTAKT_PFAD));
		if (kontakte == null)
			kontakte = new Kontaktverwaltung();

		konten = new ArrayList<MailChecker>();

		File file = new File(DATEN_ORDNER).getAbsoluteFile();
		if (!file.exists())
			file.mkdirs();

		// Filter, der nur Pfade von direkten Unterordnern zur�ckgibt
		String[] directories = file.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		});

		for (String directory : directories) {
			final String settings = String.format(ACCOUNTSETTINGS_PATTERN,
					directory);
			File datei = new File(settings).getAbsoluteFile();

			MailAccount geladen = deserialisiereObjekt(datei);
			if (geladen != null) {
				MailChecker checker = new MailChecker(geladen);
				checker.addNewMessageListener(getListener());

				konten.add(checker);
			}
		}
	}

	/**
	 * Gibt eine neue Instanz der Implementierung des NewMailListeners zur�ck
	 * 
	 * @return Neue Instanz der Implementierung des NewMailListeners
	 */
	private NewMailListener getListener() {
		return new NewMailListener() {
			@Override
			public void newMessage(NewMailEvent e) {
				// TODO Teste mich hart

				MailAccount account = ((MailChecker)e.getSource()).getAccount();
				MailInfo info = e.getInfo();
				String pfad = e.getFolder();

				InternetAddress from = (InternetAddress) info.getSender();
				InternetAddress empfaenger = account.getAdresse();

				String subject = info.getSubject();

				// Abfrage auf erhaltene Krankheits-Mail
				if (from.equals(empfaenger) && subject.equals("Ich bin krank")) {
					termineAbsagen(account);
				}

				// Abfrage auf Abwesenheit des Benutzers
				if (!isAnwesend()) {
					sendeAbwesenheitsMail(account, pfad, info);
				}
			}
		};
	}

	@Override
	public Iterator<MailChecker> iterator() {
		return konten.iterator();
	}

	/**
	 * Gibt die Instanz der Kontaktverwaltung zur�ck
	 * 
	 * @return Kontaktverwaltung des Benutzers
	 */
	public Kontaktverwaltung getKontakte() {
		return kontakte;
	}

	/**
	 * Gibt die Instanz der Terminverwaltung zur�ck
	 * 
	 * @return Terminverwaltung des Benutzers
	 */
	public Terminkalender getTermine() {
		return termine;
	}

	/**
	 * F�gt die �bergebene Instanz eines MailAccounts dem Benutzer hinzu
	 * 
	 * @param account
	 *            Hinzuzuf�gender MailAccount
	 * @return true, wenn der �bergebene MailAccount nicht <code>null</code>
	 *         ist; sonst false
	 */
	public boolean addMailAccount(MailAccount account) {
		if (account == null || konten.contains(account))
			return false;

		boolean result = true;
		try {
			speichereMailAccount(account);

			MailChecker checker = new MailChecker(account);
			checker.addNewMessageListener(getListener());
			konten.add(checker);
		} catch (IOException e) {
			result = false;
		}
		return result;
	}

	/**
	 * L�scht rekursiv die Datei oder den Ordner mit allen Unterordnern und
	 * Dateien
	 * 
	 * @param file
	 *            Verweis auf den zu l�schenden Eintrag im Dateisystem
	 * @throws IOException
	 *             Tritt auf, wenn eine der Dateien nicht gel�scht werden konnte
	 */
	private void deleteRecursive(File file) throws IOException {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File subfile : files) {
				// Rekursiver Aufruf auf Dateien/Unterordner
				deleteRecursive(subfile);
			}
		}

		if (!file.delete()) { // Eigentliches L�schen
			// L�schen fehlgeschlagen
			throw new IOException("Datei \'" + file.getPath()
					+ "\' konnte nicht gel�scht werden");
		}
	}

	/**
	 * Entfernt den �bergebenen Account aus der Verwaltung
	 * 
	 * @param account
	 *            Zu l�schender Account
	 * @return true, wenn das l�schen erfolgreich war; sonst false
	 * @throws IOException
	 *             Tritt auf, wenn einer der gespeicherten Dateien nicht
	 *             gel�scht werden konnte
	 */
	public boolean entferneMailAccount(MailAccount account) throws IOException {
		if (konten.remove(account)) {
			String pfad = String.format(ACCOUNT_PATTERN, account.getAdresse()
					.getAddress());
			File ordner = new File(pfad);
			if (ordner.exists()) {
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
	 * 
	 * @return Anzahl der MailAccounts
	 */
	public int getAnzahlKonten() {
		return konten.size();
	}

	/**
	 * Speichert die Daten des Benutzers
	 */
	public void speichern() throws IOException {
		for (MailChecker checker : konten) {
			MailAccount acc = checker.getAccount();
			speichereMailAccount(acc);
		}

		File kontaktPfad = new File(KONTAKT_PFAD).getAbsoluteFile();
		File terminPfad = new File(TERMIN_PFAD).getAbsoluteFile();
		File ordner = kontaktPfad.getParentFile();

		if (!ordner.exists()) {
			ordner.mkdirs();
		}

		serialisiereObjekt(kontakte, kontaktPfad);
		serialisiereObjekt(termine, terminPfad);
	}

	/**
	 * Speichert den �bergebenen MailAccount
	 * 
	 * @param acc
	 *            Zu speichernder MailAccount
	 * @throws IOException
	 *             Tritt auf, wenn der MailAccount nicht gespeichert werden
	 *             konnte
	 */
	private void speichereMailAccount(MailAccount acc) throws IOException {
		String strPfad = String.format(ACCOUNTSETTINGS_PATTERN, acc
				.getAdresse().getAddress());

		File pfad = new File(strPfad).getAbsoluteFile();
		File ordner = pfad.getParentFile();

		if (!ordner.exists()) {
			// Erzeugt den geforderten Ordner und wenn n�tig auch dessen
			// Unterordner
			ordner.mkdirs();
		}

		serialisiereObjekt(acc, pfad);
	}

	/**
	 * Gibt zur�ck, ob der Benutzer anwesend ist
	 * 
	 * @return Status der Anwesenheit
	 */
	public synchronized boolean isAnwesend() {
		return anwesend;
	}

	/**
	 * Setzt die Anwesenheit des Benutzers
	 * 
	 * @param anwesend
	 *            Zu setzender Status der Anwesenheit
	 */
	public void setAnwesend(boolean anwesend) {
		synchronized (this) {
			// TODO Hilfe, darf ich das?

			this.anwesend = anwesend;
		}
	}

	/**
	 * Sendet eine Abwesenheitsmail
	 * 
	 * @param sender
	 *            MailAccount �ber den die Mail gesendet werden soll
	 * @param pfad
	 *            Pfad der zu beantwortenden Mail
	 * @param info
	 *            MailInfo der zu beantwortenden Mail
	 */
	private void sendeAbwesenheitsMail(MailAccount sender, String pfad,
			MailInfo info) {
		// TODO Jetzt Abwesenheitmail senden

		try {
			sender.getWholeMessage(pfad, info);

			InternetAddress ziel = (InternetAddress) info.getSender();

			sender.sendeMail(new InternetAddress[] { ziel }, null,
					"Abwesenheit von " + sender.getAdresse().getPersonal(),
					"Ich bin nicht da", "TEXT/plain; charset=utf-8", null);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void termineAbsagen(MailAccount sender) {
		Termin[] heute = termine.getTermine();
		for (Termin termin : heute) {
			try {
				schreibeAbsage(termin, sender);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void schreibeAbsage(Termin termin, MailAccount sender)
			throws MessagingException {
		String betreff = "Absage: " + termin.getBetreff();
		InternetAddress[] ziele = termin.getAdressen();

		// TODO Krankheitsmeldung einf�gen
		String text = "";

		sender.sendeMail(ziele, null, betreff, text,
				"TEXT/plain; charset=utf-8", null);
	}

	public boolean starteChecker() {
		boolean result = true;

		for (MailChecker checker : konten) {
			try {
				checker.start();
			} catch (IllegalThreadStateException ex) {
				result = false;
			}
		}

		return result;
	}

	public boolean stoppeChecker() {
		boolean result = true;

		for (MailChecker checker : konten) {
			try {
				checker.interrupt();
			} catch (SecurityException ex) {
				result = false;
			}
		}

		return result;
	}
}
