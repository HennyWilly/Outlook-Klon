package de.outlook_klon.logik;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.mail.Address;
import javax.mail.FolderNotFoundException;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import de.outlook_klon.logik.kalendar.Termin;
import de.outlook_klon.logik.kalendar.Terminkalender;
import de.outlook_klon.logik.kontakte.Kontaktverwaltung;
import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.mailclient.MailInfo;
import de.outlook_klon.serializers.Serializer;

/**
 * Diese Klasse stellt den Benutzer dar. Bietet Zugriff auf die Termin- und
 * Kontaktverwaltung. Zudem ist es möglich, über die Mailkonten des Nutzers zu
 * iterieren.
 * 
 * @author Hendrik Karwanni
 */
public final class Benutzer implements Iterable<Benutzer.MailChecker> {
	private static final String DATEN_ORDNER = "Mail";
	private static final String ACCOUNT_PATTERN = DATEN_ORDNER + "/%s";
	private static final String ACCOUNTSETTINGS_PATTERN = ACCOUNT_PATTERN + "/settings.json";
	private static final String KONTAKT_PFAD = DATEN_ORDNER + "/Kontakte.json";
	private static final String TERMIN_PFAD = DATEN_ORDNER + "/Termine.json";
	private static final String KRANK_PFAD = DATEN_ORDNER + "/Krank.txt";
	private static final String ABWESEND_PFAD = DATEN_ORDNER + "/Abwesend.txt";

	private static final Logger LOGGER = LoggerFactory.getLogger(Benutzer.class);

	private String abwesenheitsmeldung;
	private String krankmeldung;

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
		 * Erzeugt ein neues MailChecker-Objekt für den übergebenen Account
		 * 
		 * @param account
		 *            MailAccount, der abgehört werden soll
		 */
		public MailChecker(MailAccount account) {
			this.account = account;
			this.mails = new HashSet<MailInfo>();
			this.listenerVector = new Vector<NewMailListener>();
		}

		/**
		 * Registriert einen neuen NewMailListener für Events innerhalb der
		 * Klasse
		 * 
		 * @param mcl
		 *            Neuer NewMailListener
		 */
		public void addNewMessageListener(NewMailListener mcl) {
			if (mcl == null)
				throw new NullPointerException("Der hinzuzufügende Listener muss initialisiert sein.");

			synchronized (listenerVector) {
				listenerVector.add(mcl);
			}
		}

		/**
		 * Feuert ein neues NewMessageEvent für die übergebene MailInfo an alle
		 * registrierten Listener
		 * 
		 * @param info
		 *            MailInfo-Objekt, aus dem das Event erzeugt wird
		 */
		private void fireNewMessageEvent(MailInfo info) {
			NewMailEvent ev = new NewMailEvent(this, FOLDER, info);

			synchronized (listenerVector) {
				for (NewMailListener listener : listenerVector) {
					listener.newMessage(ev);
				}
			}
		}

		/**
		 * Gibt den internen MailAccount zurück
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
					// Ignorieren, da INBOX immer vorhanden sein sollte!
				} catch (MessagingException ex) {
					LOGGER.error("While getting messages", ex);
				}

				for (MailInfo info : mailInfos) {
					mails.add(info);
					if (!info.isRead()) {
						fireNewMessageEvent(info);
					}
				}
			}

			while (true) {
				try {
					Thread.sleep(60000); // Pausiere für eine Minute
					MailInfo[] mailTmp = account.getMessages(FOLDER);

					// HashSet, da oft darin gesucht wird (s.u.)
					HashSet<MailInfo> tmpSet = new HashSet<MailInfo>();
					for (MailInfo info : mailTmp) {
						tmpSet.add(info); // Fülle mit abgefragten MailInfos
					}

					// Prüfe, ob eine bekannte MailInfo weggefallen ist
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
								// Wird eine neue MailInfo hinzugefügt, werden
								// die Listener benachrichtigt
								fireNewMessageEvent(info);
							}
						}
					}
				} catch (InterruptedException e) {
					// Bricht die Ausführung ab
					break;
				} catch (FolderNotFoundException e) {
					// Ignorieren, da INBOX immer vorhanden sein sollte!
				} catch (MessagingException ex) {
					LOGGER.error("While getting messages", ex);
				}
			}
		}

		/**
		 * Wenn der Thread aktiv und der gesuchte Ordner "INBOX" ist, werden die
		 * MailInfos aus dem Speicher des MailCheckers zurückgegeben; sonst wird
		 * die getMessages-Methode des internen MailAccount-Objekts aufgerufen
		 * 
		 * @param pfad
		 *            Zu durchsuchender Ordnerpfad
		 * @return MailInfos des gesuchten Ordners
		 */
		public MailInfo[] getMessages(String pfad) throws MessagingException {
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
		 * Entfernt die übergebenen MailInfos aus dem Speicher
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
			if (this == other)
				return true;
			if (other == null)
				return false;

			if (other instanceof MailChecker) {
				MailChecker checker = ((MailChecker) other);
				return this.getAccount().equals(checker.getAccount());
			}
			if (other instanceof MailAccount) {
				MailAccount account = (MailAccount) other;
				return this.getAccount().equals(account);
			}

			return false;
		}
	}

	/**
	 * Gibt die einzige Instanz der Klasse Benutzer zurück. Beim ersten Aufruf
	 * wird eine neue Instanz der Klasse erstellt.
	 * 
	 * @return Einzige Instanz der Klasse
	 */
	public static Benutzer getInstanz() {
		if (singleton == null) {
			try {
				singleton = new Benutzer();
			} catch (IOException ex) {
				LOGGER.error("Could not create user instance", ex);
			}
		}
		return singleton;
	}

	/**
	 * Erstellt eine neue Instanz der Klasse Benutzer. Liest, wenn vorhanden,
	 * die gespeicherten Daten aus.
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	private Benutzer() throws IOException {
		setAnwesend(true);

		File doctorsNote = new File(KRANK_PFAD);
		try {
			krankmeldung = Serializer.deserializePlainText(doctorsNote);
		} catch (IOException ex) {
			LOGGER.warn("Cold not load doctors note", ex);
			krankmeldung = "Ich bin krank";
		}

		File absenceMessage = new File(ABWESEND_PFAD);
		try {
			abwesenheitsmeldung = Serializer.deserializePlainText(absenceMessage);
		} catch (IOException ex) {
			LOGGER.warn("Cold not load absence message", ex);
			abwesenheitsmeldung = "Ich bin nicht da";
		}

		try {
			kontakte = Serializer.deserializeJson(new File(KONTAKT_PFAD), Kontaktverwaltung.class);
		} catch (IOException ex) {
			LOGGER.warn("Could not create contact manager", ex);
			kontakte = new Kontaktverwaltung();
		}

		try {
			termine = Serializer.deserializeJson(new File(TERMIN_PFAD), Terminkalender.class);
		} catch (IOException ex) {
			LOGGER.warn("Could not create appointments manager", ex);
			termine = new Terminkalender();
		}

		konten = new ArrayList<MailChecker>();

		File file = new File(DATEN_ORDNER).getAbsoluteFile();
		if (!file.exists())
			file.mkdirs();

		// Filter, der nur Pfade von direkten Unterordnern zurückgibt
		String[] directories = file.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		});

		for (String directory : directories) {
			final String settings = String.format(ACCOUNTSETTINGS_PATTERN, directory);
			File datei = new File(settings).getAbsoluteFile();

			// Lade MailAccount
			MailAccount geladen = Serializer.deserializeJson(datei, MailAccount.class);
			MailChecker checker = new MailChecker(geladen);
			checker.addNewMessageListener(getListener());

			konten.add(checker);
		}
	}

	/**
	 * Gibt eine neue Instanz der Implementierung des NewMailListeners zurück
	 * 
	 * @return Neue Instanz der Implementierung des NewMailListeners
	 */
	private NewMailListener getListener() {
		return new NewMailListener() {
			public void newMessage(NewMailEvent e) {
				// TODO Teste mich hart

				MailAccount account = ((MailChecker) e.getSource()).getAccount();
				MailInfo info = e.getInfo();
				String pfad = e.getFolder();

				InternetAddress from = (InternetAddress) info.getSender();
				InternetAddress empfaenger = account.getAddress();

				String subject = info.getSubject();

				// Abfrage auf erhaltene Krankheits-Mail
				if (from.equals(empfaenger) && subject.equals("Ich bin krank")) {
					try {
						termineAbsagen(account);
					} catch (MessagingException ex) {
						LOGGER.error("Error while canceling appointments", ex);
					}
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
	 * Gibt die Instanz der Kontaktverwaltung zurück
	 * 
	 * @return Kontaktverwaltung des Benutzers
	 */
	public Kontaktverwaltung getKontakte() {
		return kontakte;
	}

	/**
	 * Gibt die Instanz der Terminverwaltung zurück
	 * 
	 * @return Terminverwaltung des Benutzers
	 */
	public Terminkalender getTermine() {
		return termine;
	}

	/**
	 * Fügt die übergebene Instanz eines MailAccounts dem Benutzer hinzu
	 * 
	 * @param account
	 *            Hinzuzufügender MailAccount
	 * @return true, wenn der übergebene MailAccount nicht <code>null</code>
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
		} catch (IOException ex) {
			LOGGER.error("Error while adding account", ex);
			result = false;
		}
		return result;
	}

	/**
	 * Löscht rekursiv die Datei oder den Ordner mit allen Unterordnern und
	 * Dateien
	 * 
	 * @param file
	 *            Verweis auf den zu löschenden Eintrag im Dateisystem
	 * @throws IOException
	 *             Tritt auf, wenn eine der Dateien nicht gelöscht werden konnte
	 */
	private void deleteRecursive(File file) throws IOException {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File subfile : files) {
				// Rekursiver Aufruf auf Dateien/Unterordner
				deleteRecursive(subfile);
			}
		}

		if (!file.delete()) { // Eigentliches Löschen
			// Löschen fehlgeschlagen
			throw new IOException("Datei \'" + file.getPath() + "\' konnte nicht gelöscht werden");
		}
	}

	/**
	 * Entfernt den übergebenen Account aus der Verwaltung
	 * 
	 * @param account
	 *            Zu löschender Account
	 * @param loeschen
	 *            Gibt an, ob die gespeicherten Mails des MailAccounts auch
	 *            entfernt werden sollen
	 * @return true, wenn das löschen erfolgreich war; sonst false
	 * @throws IOException
	 *             Tritt auf, wenn einer der gespeicherten Dateien nicht
	 *             gelöscht werden konnte
	 */
	public boolean entferneMailAccount(MailAccount account, boolean loeschen) throws IOException {
		int index = konten.indexOf(account);
		if (index != -1) {
			MailChecker checker = konten.get(index);
			checker.interrupt();

			if (konten.remove(account)) {
				String adresse = account.getAddress().getAddress();
				String settings = String.format(ACCOUNTSETTINGS_PATTERN, adresse);
				deleteRecursive(new File(settings));

				if (loeschen) {
					String pfad = String.format(ACCOUNT_PATTERN, adresse);
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
			}
		}

		return false;
	}

	/**
	 * Gibt die Anzahl an MailAccounts zurück
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

		Serializer.serializeStringToPlainText(new File(ABWESEND_PFAD), abwesenheitsmeldung);
		Serializer.serializeStringToPlainText(new File(KRANK_PFAD), krankmeldung);

		Serializer.serializeObjectToJson(kontaktPfad, kontakte);
		Serializer.serializeObjectToJson(terminPfad, termine);
	}

	/**
	 * Speichert den übergebenen MailAccount
	 * 
	 * @param acc
	 *            Zu speichernder MailAccount
	 * @throws IOException
	 *             Tritt auf, wenn der MailAccount nicht gespeichert werden
	 *             konnte
	 */
	private void speichereMailAccount(MailAccount acc) throws IOException {
		String strAdresse = acc.getAddress().getAddress();
		String strPfad = String.format(ACCOUNTSETTINGS_PATTERN, strAdresse);

		File pfad = new File(strPfad).getAbsoluteFile();
		File ordner = pfad.getParentFile();

		if (!ordner.exists()) {
			// Erzeugt den geforderten Ordner und wenn nötig auch dessen
			// Unterordner
			ordner.mkdirs();
		}

		Serializer.serializeObjectToJson(pfad, acc);
	}

	/**
	 * Gibt zurück, ob der Benutzer anwesend ist
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
	 *            MailAccount über den die Mail gesendet werden soll
	 * @param pfad
	 *            Pfad der zu beantwortenden Mail
	 * @param info
	 *            MailInfo der zu beantwortenden Mail
	 */
	private void sendeAbwesenheitsMail(MailAccount sender, String pfad, MailInfo info) {
		// TODO Jetzt Abwesenheitmail senden

		try {
			sender.getWholeMessage(pfad, info);

			InternetAddress ziel = (InternetAddress) info.getSender();

			String text = getAbwesenheitsmeldung();
			sender.sendeMail(new InternetAddress[] { ziel }, null,
					"Abwesenheit von " + sender.getAddress().getPersonal(), text, "TEXT/plain; charset=utf-8", null);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void termineAbsagen(MailAccount sender) throws MessagingException {
		Termin[] heute = termine.getTermine();
		for (Termin termin : heute) {
			schreibeAbsage(termin, sender);
		}
		termine.absagen();
	}

	private void schreibeAbsage(Termin termin, MailAccount sender) throws MessagingException {
		String betreff = "Absage: " + termin.getSubject();
		Address[] ziele = termin.getAddresses();

		if (ziele != null) {
			String text = getKrankmeldung();

			sender.sendeMail(ziele, null, betreff, text, "TEXT/plain; charset=utf-8", null);
		}
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

		// Liste von MailAccounts, die keinen Checker haben
		ArrayList<MailAccount> aloneAccounts = new ArrayList<MailAccount>();

		Iterator<MailChecker> iterator = konten.iterator();
		while (iterator.hasNext()) {
			MailChecker checker = iterator.next();

			try {
				checker.interrupt();
			} catch (SecurityException ex) {
				result = false;
			}

			aloneAccounts.add(checker.getAccount());
			iterator.remove();
		}

		for (MailAccount account : aloneAccounts) {
			// Erstellt einen neuen Checker für die MailAccounts
			MailChecker neu = new MailChecker(account);
			neu.addNewMessageListener(getListener());
			konten.add(neu);
		}

		return result;
	}

	public String getAbwesenheitsmeldung() {
		return abwesenheitsmeldung;
	}

	public void setAbwesenheitsmeldung(String abwesenheitsmeldung) {
		this.abwesenheitsmeldung = abwesenheitsmeldung;
	}

	public String getKrankmeldung() {
		return krankmeldung;
	}

	public void setKrankmeldung(String krankmeldung) {
		this.krankmeldung = krankmeldung;
	}
}
