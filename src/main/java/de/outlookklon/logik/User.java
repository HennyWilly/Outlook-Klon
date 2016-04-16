package de.outlookklon.logik;

import de.outlookklon.dao.DAOException;
import de.outlookklon.logik.calendar.Appointment;
import de.outlookklon.logik.calendar.AppointmentCalendar;
import de.outlookklon.logik.contacts.ContactManagement;
import de.outlookklon.logik.mailclient.MailAccount;
import de.outlookklon.logik.mailclient.MailContent;
import de.outlookklon.logik.mailclient.MailInfo;
import de.outlookklon.logik.mailclient.StoredMailInfo;
import de.outlookklon.logik.mailclient.checker.MailAccountChecker;
import de.outlookklon.logik.mailclient.checker.NewMailEvent;
import de.outlookklon.logik.mailclient.checker.NewMailListener;
import de.outlookklon.serializers.Serializer;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Diese Klasse stellt den User dar. Bietet Zugriff auf die Appointment- und
 * ContactManagement. Zudem ist es m�glich, �ber die Mailkonten des Nutzers zu
 * iterieren.
 *
 * @author Hendrik Karwanni
 */
public final class User implements Iterable<MailAccountChecker> {

    private static final String DATA_FOLDER = "Mail";
    private static final String ACCOUNT_PATTERN = DATA_FOLDER + "/%s";
    private static final String ACCOUNTSETTINGS_PATTERN = ACCOUNT_PATTERN + "/settings.json";
    private static final String CONTACT_PATH = DATA_FOLDER + "/Kontakte.json";
    private static final String APPOINTMENT_PATH = DATA_FOLDER + "/Termine.json";
    private static final String SICKNOTE_PATH = DATA_FOLDER + "/Krank.txt";
    private static final String ABSENCE_PATH = DATA_FOLDER + "/Abwesend.txt";

    private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

    private static User singleton;

    private String absenceMessage;
    private String sickNote;

    private ContactManagement contacts;
    private AppointmentCalendar appointments;
    private List<MailAccountChecker> accounts;
    private boolean absent;

    /**
     * Gibt die einzige Instanz der Klasse User zur�ck. Beim ersten Aufruf wird
     * eine neue Instanz der Klasse erstellt.
     *
     * @return Einzige Instanz der Klasse
     */
    public static User getInstance() throws UserException {
        if (singleton == null) {
            try {
                singleton = new User();
            } catch (IOException ex) {
                throw new UserException("Could not create user instance", ex);
            }
        }
        return singleton;
    }

    /**
     * Erstellt eine neue Instanz der Klasse Benutzer. Liest, wenn vorhanden,
     * die gespeicherten Daten aus.
     *
     * @throws IOException
     */
    private User() throws IOException {
        setAbsent(true);

        File sickNoteFile = new File(SICKNOTE_PATH);
        try {
            sickNote = Serializer.deserializePlainText(sickNoteFile);
        } catch (IOException ex) {
            LOGGER.warn("Cold not load doctors note", ex);
            sickNote = "Ich bin krank";
        }

        File absenceMessageFile = new File(ABSENCE_PATH);
        try {
            absenceMessage = Serializer.deserializePlainText(absenceMessageFile);
        } catch (IOException ex) {
            LOGGER.warn("Cold not load absence message", ex);
            absenceMessage = "Ich bin nicht da";
        }

        try {
            contacts = Serializer.deserializeJson(new File(CONTACT_PATH), ContactManagement.class);
        } catch (IOException ex) {
            LOGGER.warn("Could not create contact manager", ex);
            contacts = new ContactManagement();
        }

        try {
            appointments = Serializer.deserializeJson(new File(APPOINTMENT_PATH), AppointmentCalendar.class);
        } catch (IOException ex) {
            LOGGER.warn("Could not create appointments manager", ex);
            appointments = new AppointmentCalendar();
        }

        accounts = new ArrayList<>();

        File file = new File(DATA_FOLDER).getAbsoluteFile();
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
            File accountFile = new File(settings).getAbsoluteFile();

            // Lade MailAccount
            MailAccount loadedAccount = Serializer.deserializeJson(accountFile, MailAccount.class);
            MailAccountChecker checker = new MailAccountChecker(loadedAccount);
            checker.addNewMessageListener(getListener());

            accounts.add(checker);
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

                MailAccount account = ((MailAccountChecker) e.getSource()).getAccount();
                StoredMailInfo info = e.getInfo();
                String path = e.getFolder();

                InternetAddress from = (InternetAddress) info.getSender();
                InternetAddress to = account.getAddress();

                String subject = info.getSubject();

                // Abfrage auf erhaltene Krankheits-Mail
                if (from.equals(to) && "Ich bin krank".equals(subject)) {
                    try {
                        cancelAppointments(account);
                    } catch (MessagingException ex) {
                        LOGGER.error("Error while canceling appointments", ex);
                    }
                }

                // Abfrage auf Abwesenheit des Benutzers
                if (!isAbsent()) {
                    sendAbsenceMail(account, path, info);
                }
            }
        };
    }

    @Override
    public Iterator<MailAccountChecker> iterator() {
        return accounts.iterator();
    }

    /**
     * Gibt die Instanz der ContactManagement zur�ck
     *
     * @return ContactManagement des Benutzers
     */
    public ContactManagement getContacts() {
        return contacts;
    }

    /**
     * Gibt die Instanz der Terminverwaltung zur�ck
     *
     * @return Terminverwaltung des Benutzers
     */
    public AppointmentCalendar getAppointments() {
        return appointments;
    }

    /**
     * F�gt die �bergebene Instanz eines MailAccounts dem User hinzu
     *
     * @param account Hinzuzuf�gender MailAccount
     * @return true, wenn der �bergebene MailAccount nicht <code>null</code>
     * ist; sonst false
     */
    public boolean addMailAccount(MailAccount account) {
        if (account == null || accounts.contains(account)) {
            return false;
        }

        boolean result = true;
        try {
            saveMailAccount(account);

            MailAccountChecker checker = new MailAccountChecker(account);
            checker.addNewMessageListener(getListener());
            accounts.add(checker);
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

        // Eigentliches L�schen
        if (!file.delete()) {
            // L�schen fehlgeschlagen
            throw new IOException("Datei \'" + file.getPath() + "\' konnte nicht gel�scht werden");
        }
    }

    /**
     * Entfernt den �bergebenen Account aus der Verwaltung
     *
     * @param account Zu l�schender Account
     * @param delete Gibt an, ob die gespeicherten Mails des MailAccounts auch
     * entfernt werden sollen
     * @return true, wenn das l�schen erfolgreich war; sonst false
     * @throws IOException Tritt auf, wenn einer der gespeicherten Dateien nicht
     * gel�scht werden konnte
     */
    public boolean removeMailAccount(MailAccount account, boolean delete) throws IOException {
        int index = accounts.indexOf(account);
        if (index != -1) {
            MailAccountChecker checker = accounts.get(index);
            checker.interrupt();

            if (accounts.remove(account)) {
                String address = account.getAddress().getAddress();
                String settings = String.format(ACCOUNTSETTINGS_PATTERN, address);
                deleteRecursive(new File(settings));

                if (delete) {
                    String path = String.format(ACCOUNT_PATTERN, address);
                    File folder = new File(path);
                    if (folder.exists()) {
                        try {
                            deleteRecursive(folder);
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
    public int getAccountCount() {
        return accounts.size();
    }

    /**
     * Speichert die Daten des Benutzers
     *
     * @throws java.io.IOException wenn das Speichern der Daten des Benutzers
     * fehlschl�gt
     */
    public void save() throws IOException {
        for (MailAccountChecker checker : accounts) {
            MailAccount acc = checker.getAccount();
            saveMailAccount(acc);
        }

        File contactPath = new File(CONTACT_PATH).getAbsoluteFile();
        File appointmentPath = new File(APPOINTMENT_PATH).getAbsoluteFile();
        File folder = contactPath.getParentFile();

        if (!folder.exists()) {
            folder.mkdirs();
        }

        Serializer.serializeStringToPlainText(new File(ABSENCE_PATH), absenceMessage);
        Serializer.serializeStringToPlainText(new File(SICKNOTE_PATH), sickNote);

        Serializer.serializeObjectToJson(contactPath, contacts);
        Serializer.serializeObjectToJson(appointmentPath, appointments);
    }

    /**
     * Speichert den �bergebenen MailAccount
     *
     * @param acc Zu speichernder MailAccount
     * @throws IOException Tritt auf, wenn der MailAccount nicht gespeichert
     * werden konnte
     */
    private void saveMailAccount(@NonNull MailAccount acc) throws IOException {
        String strAddress = acc.getAddress().getAddress();
        String strPath = String.format(ACCOUNTSETTINGS_PATTERN, strAddress);

        File path = new File(strPath).getAbsoluteFile();
        File folder = path.getParentFile();

        if (!folder.exists()) {
            // Erzeugt den geforderten Ordner und wenn n�tig auch dessen
            // Unterordner
            folder.mkdirs();
        }

        Serializer.serializeObjectToJson(path, acc);
    }

    /**
     * Gibt zur�ck, ob der User absent ist
     *
     * @return Status der Anwesenheit
     */
    public synchronized boolean isAbsent() {
        return absent;
    }

    /**
     * Setzt die Anwesenheit des Benutzers
     *
     * @param absent Zu setzender Status der Anwesenheit
     */
    public void setAbsent(boolean absent) {
        synchronized (this) {
            // TODO Hilfe, darf ich das?

            this.absent = absent;
        }
    }

    /**
     * Sendet eine Abwesenheitsmail
     *
     * @param sender MailAccount �ber den die Mail gesendet werden soll
     * @param path Pfad der zu beantwortenden Mail
     * @param info StoredMailInfo der zu beantwortenden Mail
     */
    private void sendAbsenceMail(MailAccount sender, String path, StoredMailInfo info) {
        // TODO Jetzt Abwesenheitmail senden

        try {
            sender.loadMessageData(path, info, EnumSet.of(MailContent.SENDER));

            InternetAddress target = (InternetAddress) info.getSender();

            String text = getAbsenceMessage();
            MailInfo mailToSend = new MailInfo("Abwesenheit von " + sender.getAddress().getPersonal(),
                    text, "TEXT/plain; charset=utf-8", new InternetAddress[]{target}, null, null);
            sender.sendMail(mailToSend);
        } catch (MessagingException | DAOException ex) {
            LOGGER.error("Could not send absence message", ex);
        }
    }

    private void cancelAppointments(MailAccount sender) throws MessagingException {
        Appointment[] today = appointments.getAppointments();
        for (Appointment termin : today) {
            writeCancelation(termin, sender);
        }
        appointments.cancel();
    }

    private void writeCancelation(Appointment appointment, MailAccount sender) throws MessagingException {
        String subject = "Absage: " + appointment.getSubject();
        Address[] to = appointment.getAddresses();

        if (to != null) {
            String text = getSickNote();
            MailInfo mailToSend = new MailInfo(subject,
                    text, "TEXT/plain; charset=utf-8", to, null, null);
            sender.sendMail(mailToSend);
        }
    }

    /**
     * Startet das intervallweise Abfragen der Maileing�nge aller registrierten
     * Konten.
     *
     * @return {@code true} wenn die Threads gestartet wurden, {@code false}
     * wenn nicht
     */
    public boolean startChecker() {
        boolean result = true;

        for (MailAccountChecker checker : accounts) {
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
    public boolean stopChecker() {
        boolean result = true;

        // Liste von MailAccounts, die keinen Checker haben
        List<MailAccount> aloneAccounts = new ArrayList<>();

        Iterator<MailAccountChecker> iterator = accounts.iterator();
        while (iterator.hasNext()) {
            MailAccountChecker checker = iterator.next();

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
            MailAccountChecker newChecker = new MailAccountChecker(account);
            newChecker.addNewMessageListener(getListener());
            accounts.add(newChecker);
        }

        return result;
    }

    /**
     * Gibt die Abwesenheitsmeldung des Benutzers zur�ck.
     *
     * @return Abwesenheitsmeldung des Benutzers
     */
    public String getAbsenceMessage() {
        return absenceMessage;
    }

    /**
     * Setzt die Abwesenheitsmeldung des Benutzers.
     *
     * @param absenceMessage Abwesenheitsmeldung des Benutzers
     */
    public void setAbsenceMessage(String absenceMessage) {
        if (absenceMessage == null) {
            absenceMessage = "";
        }

        this.absenceMessage = absenceMessage;
    }

    /**
     * Gibt die Krankmeldung des Benutzers zur�ck.
     *
     * @return Krankmeldung des Benutzers
     */
    public String getSickNote() {
        return sickNote;
    }

    /**
     * Setzt die Krankmeldung des Benutzers.
     *
     * @param sickNote Krankmeldung des Benutzers
     */
    public void setSickNote(String sickNote) {
        if (sickNote == null) {
            sickNote = "";
        }

        this.sickNote = sickNote;
    }
}
