package de.outlookklon.logik;

import de.outlookklon.dao.StoredMailInfoDAO;
import de.outlookklon.logik.calendar.AppointmentCalendar;
import de.outlookklon.logik.contacts.ContactManagement;
import de.outlookklon.logik.mailclient.MailAccount;
import de.outlookklon.logik.mailclient.checker.MailAccountChecker;
import de.outlookklon.serializers.Serializer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Diese Klasse stellt den User dar. Bietet Zugriff auf die Appointment- und
 * ContactManagement. Zudem ist es möglich, über die Mailkonten des Nutzers zu
 * iterieren.
 *
 * @author Hendrik Karwanni
 */
@Component
@Scope(value = "singleton")
public final class User implements Iterable<MailAccountChecker>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

    @Autowired
    private Serializer serializer;

    @Autowired
    private File dataFolder;

    @Autowired
    private File contactFile;

    @Autowired
    private File appointmentFile;

    @Autowired
    private String accountFolderPattern;

    @Autowired
    private String accountSettingsFilePattern;

    @Autowired
    private ContactManagement contacts;

    @Autowired
    private AppointmentCalendar appointments;

    @Autowired
    private StoredMailInfoDAO mailInfoDAO;

    private List<MailAccountChecker> accounts;

    /**
     * Erstellt eine neue Instanz der Klasse Benutzer. Liest, wenn vorhanden,
     * die gespeicherten Daten aus.
     *
     * @throws IOException
     */
    private User() throws IOException {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        accounts = new ArrayList<>();

        // Filter, der nur Pfade von direkten Unterordnern zurückgibt
        String[] directories = dataFolder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });

        for (String directory : directories) {
            final String settings = String.format(accountSettingsFilePattern, directory);
            File accountFile = new File(settings).getAbsoluteFile();

            try {
                // Lade MailAccount
                MailAccount loadedAccount = serializer.deserializeJson(accountFile, MailAccount.class);
                loadedAccount.setStoredMailInfoDAO(mailInfoDAO);
                accounts.add(new MailAccountChecker(loadedAccount));
            } catch (FileNotFoundException ex) {
                LOGGER.warn("No account configuration file found", ex);
            }
        }
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
                String address = account.getAddress();
                String settings = String.format(accountSettingsFilePattern, address);
                deleteRecursive(new File(settings));

                String path = String.format(accountFolderPattern, address);
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

        serializer.serializeObjectToJson(contactFile, contacts);
        serializer.serializeObjectToJson(appointmentFile, appointments);
    }

    /**
     * Speichert den übergebenen MailAccount
     *
     * @param acc Zu speichernder MailAccount
     * @throws IOException Tritt auf, wenn der MailAccount nicht gespeichert
     * werden konnte
     */
    private void saveMailAccount(@NonNull MailAccount acc) throws IOException {
        String strAddress = acc.getAddress();
        String strPath = String.format(accountSettingsFilePattern, strAddress);

        File path = new File(strPath).getAbsoluteFile();
        File folder = path.getParentFile();

        if (!folder.exists()) {
            // Erzeugt den geforderten Ordner und wenn nötig auch dessen
            // Unterordner
            folder.mkdirs();
        }

        serializer.serializeObjectToJson(path, acc);
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
