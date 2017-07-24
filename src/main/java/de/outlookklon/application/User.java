package de.outlookklon.application;

import de.outlookklon.application.mailclient.MailAccount;
import de.outlookklon.application.mailclient.checker.MailAccountChecker;
import de.outlookklon.dao.StoredMailInfoDAO;
import de.outlookklon.dao.impl.StoredMailInfoDAOFilePersistence;
import de.outlookklon.model.calendar.AppointmentCalendar;
import de.outlookklon.model.contacts.ContactManagement;
import de.outlookklon.serializers.Serializer;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.NonNull;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Diese Klasse stellt den User dar. Bietet Zugriff auf die Appointment- und
 * ContactManagement. Zudem ist es möglich, über die Mailkonten des Nutzers zu
 * iterieren.
 *
 * @author Hendrik Karwanni
 */
public final class User implements Iterable<MailAccountChecker> {

    private static final String DATA_FOLDER = FilenameUtils.concat(System.getProperty("user.home"), ".outlookklon");
    private static final String ACCOUNT_PATTERN = FilenameUtils.concat(DATA_FOLDER, "%s");
    private static final String ACCOUNTSETTINGS_PATTERN = FilenameUtils.concat(ACCOUNT_PATTERN, "settings.json");
    private static final String CONTACT_PATH = FilenameUtils.concat(DATA_FOLDER, "Kontakte.json");
    private static final String APPOINTMENT_PATH = FilenameUtils.concat(DATA_FOLDER, "Termine.json");

    private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

    private static User singleton;

    private ContactManagement contacts;
    private AppointmentCalendar appointments;
    private List<MailAccountChecker> accounts;

    /**
     * Erstellt eine neue Instanz der Klasse Benutzer. Liest, wenn vorhanden,
     * die gespeicherten Daten aus.
     *
     * @throws IOException
     */
    private User() throws IOException {
        File dataFolder = new File(DATA_FOLDER).getAbsoluteFile();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        try {
            contacts = Serializer.deserializeJson(new File(CONTACT_PATH), ContactManagement.class);
        } catch (IOException ex) {
            LOGGER.warn("Could not load contacts", ex);
            contacts = new ContactManagement();
        }

        try {
            appointments = Serializer.deserializeJson(new File(APPOINTMENT_PATH), AppointmentCalendar.class);
        } catch (IOException ex) {
            LOGGER.warn("Could not load appointments", ex);
            appointments = new AppointmentCalendar();
        }

        accounts = new ArrayList<>();

        // Filter, der nur Pfade von direkten Unterordnern zurückgibt
        String[] directories = dataFolder.list(new FilenameFilter() {
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
            setMailInfoDAO(loadedAccount);

            accounts.add(new MailAccountChecker(loadedAccount));
        }
    }

    /**
     * Gibt die einzige Instanz der Klasse User zurück. Beim ersten Aufruf wird
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
     * Setzt das MailInfo-DataAccessObject des übergebenen MailAccounts.
     *
     * @param account MailAccount zu dem das MailInfoDAO gesetzt wird.
     * @throws IOException Tritt auf, wenn das DAO nicht erstellt werden konnte
     */
    public void setMailInfoDAO(MailAccount account) throws IOException {
        if (account.getMailInfoDAO() != null) {
            return;
        }

        final String accountFolder = String.format(ACCOUNT_PATTERN, account.getAddress().getAddress());
        StoredMailInfoDAO mailInfoDAO = new StoredMailInfoDAOFilePersistence(new File(accountFolder));
        account.setMailInfoDAO(mailInfoDAO);
    }

    @Override
    public Iterator<MailAccountChecker> iterator() {
        return accounts.iterator();
    }

    /**
     * Gibt die Instanz der ContactManagement zurück
     *
     * @return ContactManagement des Benutzers
     */
    public ContactManagement getContacts() {
        return contacts;
    }

    /**
     * Gibt die Instanz der Terminverwaltung zurück
     *
     * @return Terminverwaltung des Benutzers
     */
    public AppointmentCalendar getAppointments() {
        return appointments;
    }

    /**
     * Fügt die übergebene Instanz eines MailAccounts dem User hinzu
     *
     * @param account Hinzuzufügender MailAccount
     * @return true, wenn der übergebene MailAccount nicht <code>null</code>
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
            accounts.add(checker);
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
     * @param file Verweis auf den zu löschenden Eintrag im Dateisystem
     * @throws IOException Tritt auf, wenn eine der Dateien nicht gelöscht
     * werden konnte
     */
    private void deleteRecursive(File file) throws IOException {
        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File subfile : files) {
                // Rekursiver Aufruf auf Dateien/Unterordner
                deleteRecursive(subfile);
            }
        }

        // Eigentliches Löschen
        if (!file.delete()) {
            // Löschen fehlgeschlagen
            throw new IOException("Datei \'" + file.getPath() + "\' konnte nicht gelöscht werden");
        }
    }

    /**
     * Entfernt den übergebenen Account aus der Verwaltung
     *
     * @param account Zu löschender Account
     * @return true, wenn das löschen erfolgreich war; sonst false
     * @throws IOException Tritt auf, wenn einer der gespeicherten Dateien nicht
     * gelöscht werden konnte
     */
    public boolean removeMailAccount(MailAccount account) throws IOException {
        int index = accounts.indexOf(account);
        if (index != -1) {
            MailAccountChecker checker = accounts.get(index);
            checker.interrupt();

            if (accounts.remove(account)) {
                String address = account.getAddress().getAddress();
                String settings = String.format(ACCOUNTSETTINGS_PATTERN, address);
                deleteRecursive(new File(settings));

                String path = String.format(ACCOUNT_PATTERN, address);
                try {
                    deleteRecursive(new File(path));
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
     * Gibt die Anzahl an MailAccounts zurück
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
     * fehlschlägt
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

        Serializer.serializeObjectToJson(contactPath, contacts);
        Serializer.serializeObjectToJson(appointmentPath, appointments);
    }

    /**
     * Speichert den übergebenen MailAccount
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
            // Erzeugt den geforderten Ordner und wenn nötig auch dessen
            // Unterordner
            folder.mkdirs();
        }

        Serializer.serializeObjectToJson(path, acc);
    }

    /**
     * Startet das intervallweise Abfragen der Maileingänge aller registrierten
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
     * Stoppt das intervallweise Abfragen der Maileingänge aller registrierten
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
            // Erstellt einen neuen Checker für die MailAccounts
            MailAccountChecker newChecker = new MailAccountChecker(account);
            accounts.add(newChecker);
        }

        return result;
    }
}
