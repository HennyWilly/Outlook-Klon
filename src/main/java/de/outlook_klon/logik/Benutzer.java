package de.outlook_klon.logik;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import de.outlook_klon.dao.DAOException;
import de.outlook_klon.logik.kalendar.Termin;
import de.outlook_klon.logik.kalendar.Terminkalender;
import de.outlook_klon.logik.kontakte.Kontaktverwaltung;
import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.mailclient.MailInfo;
import de.outlook_klon.serializers.Serializer;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.mail.Address;
import javax.mail.FolderNotFoundException;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final String ACCOUNTSETTINGS_PATTERN = ACCOUNT_PATTERN + "/settings.json";
    private static final String KONTAKT_PFAD = DATEN_ORDNER + "/Kontakte.json";
    private static final String TERMIN_PFAD = DATEN_ORDNER + "/Termine.json";
    private static final String KRANK_PFAD = DATEN_ORDNER + "/Krank.txt";
    private static final String ABWESEND_PFAD = DATEN_ORDNER + "/Abwesend.txt";

    private static final Logger LOGGER = LoggerFactory.getLogger(Benutzer.class);

    private static Benutzer singleton;

    /**
     * Gibt die einzige Instanz der Klasse Benutzer zur�ck. Beim ersten Aufruf
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

    private String abwesenheitsmeldung;
    private String krankmeldung;

    private Kontaktverwaltung kontakte;
    private Terminkalender termine;
    private List<MailChecker> konten;
    private boolean anwesend;

    /**
     * Diese Klasse dient zum automatischen, intervallweisen Abfragen des
     * Posteingangs eines MailAccounts.
     */
    public class MailChecker extends Thread {

        private static final String FOLDER = "INBOX";
        private final MailAccount account;
        private final Set<MailInfo> mails;
        private final List<NewMailListener> listenerList;

        /**
         * Erzeugt ein neues MailChecker-Objekt f�r den �bergebenen Account
         *
         * @param account MailAccount, der abgeh�rt werden soll
         */
        public MailChecker(MailAccount account) {
            this.account = account;
            this.mails = new HashSet<>();
            this.listenerList = new ArrayList<>();
        }

        /**
         * Registriert einen neuen NewMailListener f�r Events innerhalb der
         * Klasse
         *
         * @param mcl Neuer NewMailListener
         */
        public void addNewMessageListener(NewMailListener mcl) {
            if (mcl == null) {
                throw new NullPointerException("Der hinzuzuf�gende Listener muss initialisiert sein.");
            }

            synchronized (listenerList) {
                listenerList.add(mcl);
            }
        }

        /**
         * Feuert ein neues NewMessageEvent f�r die �bergebene MailInfo an alle
         * registrierten Listener
         *
         * @param info MailInfo-Objekt, aus dem das Event erzeugt wird
         */
        private void fireNewMessageEvent(MailInfo info) {
            NewMailEvent ev = new NewMailEvent(this, FOLDER, info);

            synchronized (listenerList) {
                for (NewMailListener listener : listenerList) {
                    listener.newMessage(ev);
                }
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
                    // Ignorieren, da INBOX immer vorhanden sein sollte!
                } catch (MessagingException | DAOException ex) {
                    LOGGER.error("Error while getting messages", ex);
                }

                if (mailInfos != null) {
                    for (MailInfo info : mailInfos) {
                        mails.add(info);
                        if (!info.isRead()) {
                            fireNewMessageEvent(info);
                        }
                    }
                }
            }

            while (true) {
                try {
                    Thread.sleep(60000); // Pausiere f�r eine Minute
                    MailInfo[] mailTmp = account.getMessages(FOLDER);

                    // HashSet, da oft darin gesucht wird (s.u.)
                    Set<MailInfo> tmpSet = new HashSet<>();

                    // F�lle mit abgefragten MailInfos
                    tmpSet.addAll(Arrays.asList(mailTmp));

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
                    // Ignorieren, da INBOX immer vorhanden sein sollte!
                } catch (MessagingException | DAOException ex) {
                    LOGGER.error("While getting messages", ex);
                }
            }
        }

        /**
         * Wenn der Thread aktiv und der gesuchte Ordner "INBOX" ist, werden die
         * MailInfos aus dem Speicher des MailCheckers zur�ckgegeben; sonst wird
         * die getMessages-Methode des internen MailAccount-Objekts aufgerufen
         *
         * @param pfad Zu durchsuchender Ordnerpfad
         * @return MailInfos des gesuchten Ordners
         * @throws javax.mail.MessagingException wenn die Nachrichten nicht
         * abgerufen werden konnten
         */
        public MailInfo[] getMessages(String pfad) throws MessagingException, DAOException {
            boolean threadOK = this.isAlive() && !this.isInterrupted();

            MailInfo[] array;
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
         * @param infos Zu entfernende MailInfos
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
            if (this == other) {
                return true;
            }
            if (other == null) {
                return false;
            }

            if (other instanceof MailChecker) {
                MailChecker checker = ((MailChecker) other);
                return this.getAccount().equals(checker.getAccount());
            }
            if (other instanceof MailAccount) {
                MailAccount mailAccount = (MailAccount) other;
                return this.getAccount().equals(mailAccount);
            }

            return false;
        }
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

        konten = new ArrayList<>();

        File file = new File(DATEN_ORDNER).getAbsoluteFile();
        if (!file.exists()) {
            file.mkdirs();
        }

        // Filter, der nur Pfade von direkten Unterordnern zur�ckgibt
        String[] directories = file.list(new FilenameFilter() {
            @Override
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
     * Gibt eine neue Instanz der Implementierung des NewMailListeners zur�ck
     *
     * @return Neue Instanz der Implementierung des NewMailListeners
     */
    private NewMailListener getListener() {
        return new NewMailListener() {
            @Override
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
     * @param account Hinzuzuf�gender MailAccount
     * @return true, wenn der �bergebene MailAccount nicht <code>null</code>
     * ist; sonst false
     */
    public boolean addMailAccount(MailAccount account) {
        if (account == null || konten.contains(account)) {
            return false;
        }

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
     * L�scht rekursiv die Datei oder den Ordner mit allen Unterordnern und
     * Dateien
     *
     * @param file Verweis auf den zu l�schenden Eintrag im Dateisystem
     * @throws IOException Tritt auf, wenn eine der Dateien nicht gel�scht
     * werden konnte
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
            throw new IOException("Datei \'" + file.getPath() + "\' konnte nicht gel�scht werden");
        }
    }

    /**
     * Entfernt den �bergebenen Account aus der Verwaltung
     *
     * @param account Zu l�schender Account
     * @param loeschen Gibt an, ob die gespeicherten Mails des MailAccounts auch
     * entfernt werden sollen
     * @return true, wenn das l�schen erfolgreich war; sonst false
     * @throws IOException Tritt auf, wenn einer der gespeicherten Dateien nicht
     * gel�scht werden konnte
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
     * Gibt die Anzahl an MailAccounts zur�ck
     *
     * @return Anzahl der MailAccounts
     */
    public int getAnzahlKonten() {
        return konten.size();
    }

    /**
     * Speichert die Daten des Benutzers
     *
     * @throws java.io.IOException wenn das Speichern der Daten des Benutzers
     * fehlschl�gt
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
     * Speichert den �bergebenen MailAccount
     *
     * @param acc Zu speichernder MailAccount
     * @throws IOException Tritt auf, wenn der MailAccount nicht gespeichert
     * werden konnte
     */
    private void speichereMailAccount(MailAccount acc) throws IOException {
        String strAdresse = acc.getAddress().getAddress();
        String strPfad = String.format(ACCOUNTSETTINGS_PATTERN, strAdresse);

        File pfad = new File(strPfad).getAbsoluteFile();
        File ordner = pfad.getParentFile();

        if (!ordner.exists()) {
            // Erzeugt den geforderten Ordner und wenn n�tig auch dessen
            // Unterordner
            ordner.mkdirs();
        }

        Serializer.serializeObjectToJson(pfad, acc);
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
     * @param anwesend Zu setzender Status der Anwesenheit
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
     * @param sender MailAccount �ber den die Mail gesendet werden soll
     * @param pfad Pfad der zu beantwortenden Mail
     * @param info MailInfo der zu beantwortenden Mail
     */
    private void sendeAbwesenheitsMail(MailAccount sender, String pfad, MailInfo info) {
        // TODO Jetzt Abwesenheitmail senden

        try {
            sender.getWholeMessage(pfad, info);

            InternetAddress ziel = (InternetAddress) info.getSender();

            String text = getAbwesenheitsmeldung();
            sender.sendeMail(new InternetAddress[]{ziel}, null,
                    "Abwesenheit von " + sender.getAddress().getPersonal(), text, "TEXT/plain; charset=utf-8", null);
        } catch (MessagingException | DAOException ex) {
            LOGGER.error("Could not send absence message", ex);
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

    /**
     * Startet das intervallweise Abfragen der Maileing�nge aller registrierten
     * Konten.
     *
     * @return {@code true} wenn die Threads gestartet wurden, {@code false}
     * wenn nicht
     */
    public boolean starteChecker() {
        boolean result = true;

        for (MailChecker checker : konten) {
            try {
                checker.start();
            } catch (IllegalThreadStateException ex) {
                LOGGER.warn("Could not start MailChecker-Thread", ex);
                result = false;
            }
        }

        return result;
    }

    /**
     * Stoppt das intervallweise Abfragen der Maileing�nge aller registrierten
     * Konten.
     *
     * @return {@code true} wenn die Threads gestoppt wurden, {@code false} wenn
     * nicht
     */
    public boolean stoppeChecker() {
        boolean result = true;

        // Liste von MailAccounts, die keinen Checker haben
        List<MailAccount> aloneAccounts = new ArrayList<>();

        Iterator<MailChecker> iterator = konten.iterator();
        while (iterator.hasNext()) {
            MailChecker checker = iterator.next();

            try {
                checker.interrupt();
            } catch (SecurityException ex) {
                LOGGER.warn("Could not interrupt MailChecker-Thread", ex);
                result = false;
            }

            aloneAccounts.add(checker.getAccount());
            iterator.remove();
        }

        for (MailAccount account : aloneAccounts) {
            // Erstellt einen neuen Checker f�r die MailAccounts
            MailChecker neu = new MailChecker(account);
            neu.addNewMessageListener(getListener());
            konten.add(neu);
        }

        return result;
    }

    /**
     * Gibt die Abwesenheitsmeldung des Benutzers zur�ck.
     *
     * @return Abwesenheitsmeldung des Benutzers
     */
    public String getAbwesenheitsmeldung() {
        return abwesenheitsmeldung;
    }

    /**
     * Setzt die Abwesenheitsmeldung des Benutzers.
     *
     * @param abwesenheitsmeldung Abwesenheitsmeldung des Benutzers
     */
    public void setAbwesenheitsmeldung(String abwesenheitsmeldung) {
        if (abwesenheitsmeldung == null) {
            abwesenheitsmeldung = "";
        }

        this.abwesenheitsmeldung = abwesenheitsmeldung;
    }

    /**
     * Gibt die Krankmeldung des Benutzers zur�ck.
     *
     * @return Krankmeldung des Benutzers
     */
    public String getKrankmeldung() {
        return krankmeldung;
    }

    /**
     * Setzt die Krankmeldung des Benutzers.
     *
     * @param krankmeldung Krankmeldung des Benutzers
     */
    public void setKrankmeldung(String krankmeldung) {
        if (krankmeldung == null) {
            krankmeldung = "";
        }

        this.krankmeldung = krankmeldung;
    }
}
