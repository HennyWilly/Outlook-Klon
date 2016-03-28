package de.outlook_klon.logik.mailclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.mail.imap.IMAPFolder;
import de.outlook_klon.dao.DAOException;
import de.outlook_klon.dao.MailInfoDAO;
import de.outlook_klon.dao.impl.MailInfoDAOFilePersistence;
import de.outlook_klon.logik.Benutzer.MailChecker;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.StringTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Diese Klasse stellt ein Mailkonto dar. Hier�ber k�nnen Mails gesendet und
 * empfangen werden.
 *
 * @author Hendrik Karwanni
 */
public class MailAccount {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailAccount.class);

    private static final String MAIL_FOLDER_PATTERN = "Mail/%s";

    @JsonProperty("incomingMailServer")
    private EmpfangsServer incomingMailServer;

    @JsonProperty("outgoingMailServer")
    private SendServer outgoingMailServer;

    @JsonProperty("address")
    private InternetAddress address;

    @JsonProperty("user")
    private String user;

    @JsonProperty("password")
    private String password;

    @JsonIgnore
    private MailInfoDAO mailInfoDAO;

    /**
     * Bei manchen Anbietern, z.B. Hotmail oder Yahoo, kann die MessageID nicht
     * auf normalem Wege mit dem standardm��igen <code>MessageIDTerm</code>
     * abgerufen werden. Daher wird hier ein neuer <code>SeachTerm</code>
     * implementiert, der die Mails zuerst �ffnet und dann erst die ID ausliest.
     * Sollte nur verwendet werden, wenn keine Mail gefunden wurde, da dieses
     * Suchverfahren langsamer ist, als der urspr�ngliche
     * <code>MessageIDTerm</code>.
     *
     * @author Hendrik Karwanni
     */
    private class MyMessageIDTerm extends StringTerm {

        private static final long serialVersionUID = -298319831328120350L;

        /**
         * Erstellt eine neue Instanz der Klasse MyMessageIDTerm mit der zu
         * suchenden ID
         *
         * @param messageID ID der zu suchenden Mail
         */
        public MyMessageIDTerm(final String messageID) {
            super(messageID);
        }

        @Override
        public boolean match(final Message message) {
            boolean result = false;

            try {
                String[] tmpId = message.getHeader("Message-Id");
                String id = null;

                if (tmpId != null && tmpId.length == 1) {
                    id = tmpId[0];
                } else if (message instanceof MimeMessage) {
                    id = ((MimeMessage) message).getMessageID();
                }

                if (id != null) {
                    result = super.match(id);
                }
            } catch (MessagingException ex) {
                LOGGER.warn("Error while getting message properies", ex);
            }

            return result;
        }

    }

    /**
     * Erstellt eine neue Instanz der Klasse Mailkonto mit den �bergebenen
     * Parametern
     *
     * @param incomingMailServer Server-Instanz, die zum Empfangen von Mails
     * verwendet wird
     * @param outgoingMailServer Server-Instanz, die zum Senden von Mails
     * verwendet wird
     * @param address E-Mail-Adresse, das dem Konto zugeordnet ist
     * @param user Benutzername, der zur Anmeldung verwendet werden soll
     * @param password Passwort, das zur Anmeldung verwendet werden soll
     * @throws NullPointerException Tritt auf, wenn mindestens eine der
     * Server-Instanzen null ist
     * @throws IllegalArgumentException Tritt auf, wenn die �bergebene
     * Mailadresse ung�ltig ist
     * @throws java.io.IOException Tritt auf, wenn die MailInfoDAO nicht
     * erstellt werden konnte.
     */
    @JsonCreator
    public MailAccount(
            @JsonProperty("incomingMailServer") EmpfangsServer incomingMailServer,
            @JsonProperty("outgoingMailServer") SendServer outgoingMailServer,
            @JsonProperty("address") InternetAddress address,
            @JsonProperty("user") String user,
            @JsonProperty("password") String password)
            throws NullPointerException, IllegalArgumentException, IOException {
        if (incomingMailServer == null || outgoingMailServer == null) {
            throw new NullPointerException("Die �bergebenen Server d�rfen nicht <null> sein");
        }

        this.incomingMailServer = incomingMailServer;
        this.outgoingMailServer = outgoingMailServer;

        this.address = address;
        this.user = user;
        this.password = password;

        File mailFolder = new File(String.format(MAIL_FOLDER_PATTERN, this.address.getAddress()));
        this.mailInfoDAO = new MailInfoDAOFilePersistence(mailFolder);
    }

    @Override
    public String toString() {
        if (address == null) {
            return "[No mail address set]";
        }
        return address.toUnicodeString();
    }

    /**
     * Sendet eine Nachricht an einen Mailserver
     *
     * @param to Ziele der Mail
     * @param cc CCs der Mail
     * @param subject Betreff der Mail
     * @param text Text der Mail
     * @param format Format der Mail
     * @param attachment Pfade der Anh�nge der Mail
     * @throws MessagingException Tritt auf, wenn der Sendevorgang
     * fehlgeschlagen ist
     */
    public void sendeMail(final Address[] to, final Address[] cc, final String subject,
            final String text, final String format, final File[] attachment)
            throws MessagingException {

        Message gesendet;
        try {
            gesendet = outgoingMailServer.sendeMail(user, password, address, to,
                    cc, subject, text, format, attachment);
        } catch (MessagingException ex) {
            throw new MessagingException("Could not send mail", ex);
        }

        if (incomingMailServer.supportsMultipleFolders()) {
            Store mailStore = null;
            Folder sentFolder = null;
            try {
                // TODO Testen!
                mailStore = connectToMailStore();
                sentFolder = getSentFolder(mailStore);

                if (sentFolder != null) {
                    sentFolder.appendMessages(new Message[]{gesendet});
                }
            } catch (MessagingException ex) {
                throw new MessagingException("Could not store sent email", ex);
            } finally {
                closeMailFolder(sentFolder, true);
                closeMailStore(mailStore);
            }
        }
    }

    private Folder getSentFolder(Store mailStore) throws MessagingException {
        if (incomingMailServer.supportsMultipleFolders()) {
            final Folder[] folders = mailStore.getDefaultFolder().list("*");

            for (final Folder folder : folders) {
                if (folder instanceof IMAPFolder) {
                    final IMAPFolder imap = (IMAPFolder) folder;
                    final String[] attributes = imap.getAttributes();
                    for (String attribute : attributes) {
                        if (attribute.equalsIgnoreCase("\\Sent")) {
                            return imap;
                        }
                    }
                }

                if (folder.getName().equalsIgnoreCase("sent")
                        || folder.getName().equalsIgnoreCase("gesendet")) {
                    return folder;
                }
            }
        }

        return null;
    }

    private Store connectToMailStore() throws MessagingException {
        Store mailStore = incomingMailServer.getMailStore(user, password);
        ServerSettings inServerSettings = incomingMailServer.getSettings();
        mailStore.connect(inServerSettings.getHost(), inServerSettings.getPort(), user, password);

        return mailStore;
    }

    /**
     * Gibt die Pfade aller Ordner des Servers zum Mailempfang zur�ck
     *
     * @return Pfade aller Ordner des Servers zum Mailempfang
     * @throws MessagingException
     */
    @JsonIgnore
    public OrdnerInfo[] getOrdnerstruktur() throws MessagingException {
        OrdnerInfo[] paths = null;

        Store store = null;
        try {
            store = connectToMailStore();
            final Folder[] folders = store.getDefaultFolder().list("*");

            paths = new OrdnerInfo[folders.length];
            for (int i = 0; i < paths.length; i++) {
                Folder folder = folders[i];

                paths[i] = new OrdnerInfo(folder.getName(), folder.getFullName(), 0);
            }

        } finally {
            closeMailStore(store);
        }

        return paths;
    }

    private void closeMailStore(Store mailStore) {
        if (mailStore != null && mailStore.isConnected()) {
            try {
                mailStore.close();
            } catch (MessagingException ex) {
                LOGGER.warn("Could not close MailStore", ex);
            }
        }
    }

    /**
     * Gibt die ID zur �bergebenen Mail zur�ck
     *
     * @param message Mail, f�r die die ID bestimmt werden soll
     * @return ID der Mail, oder <code>null</code>, wenn nicht gefunden
     */
    private String getID(Message message) throws MessagingException {
        String[] tmpID = message.getHeader("Message-ID");
        if (tmpID != null && tmpID.length > 0) {
            return tmpID[0];
        }

        String id = null;
        if (message instanceof MimeMessage) {
            MimeMessage mime = (MimeMessage) message;

            id = mime.getMessageID();
            if (id == null) {
                id = mime.getContentID();
            }
        }

        return id;
    }

    /**
     * Gibt die MailInfos aller Messages in dem �bergebenen Pfad zur�ck.
     *
     * @param pfad Pfad, in dem die Mails gesucht werden.
     * @return Array von MailInfos mit der ID, Betreff, Sender und SendDatum
     * @throws javax.mail.MessagingException wenn die Nachrichten nicht geladen
     * werden konnten
     * @throws de.outlook_klon.dao.DAOException wenn ein Fehler beim Zugriff auf
     * die MailInfoDAO ein Fehler auftritt
     */
    public MailInfo[] getMessages(final String pfad)
            throws MessagingException, DAOException {
        Set<MailInfo> set = new HashSet<>();

        Store store = null;
        Folder folder = null;
        try {
            store = connectToMailStore();

            folder = store.getFolder(pfad);
            folder.open(Folder.READ_ONLY);

            final Message[] messages = folder.getMessages();
            FetchProfile fp = new FetchProfile();
            fp.add("Message-Id");
            folder.fetch(messages, fp);

            for (Message message : messages) {
                String id = getID(message);
                if (id == null) {
                    continue;
                }

                MailInfo tmp = mailInfoDAO.loadMailInfo(id, pfad);
                if (tmp == null) {
                    tmp = new MailInfo(id);
                    tmp.loadData(message, EnumSet.of(
                            MailContent.READ,
                            MailContent.SUBJECT,
                            MailContent.SENDER,
                            MailContent.DATE));

                    mailInfoDAO.saveMailInfo(tmp, pfad);
                }

                set.add(tmp);
            }

        } catch (MessagingException | IOException ex) {
            throw new MessagingException("Could not get messages", ex);
        } finally {
            closeMailFolder(folder, true);
            closeMailStore(store);
        }

        return set.toArray(new MailInfo[set.size()]);
    }

    private void closeMailFolder(Folder mailFolder, boolean expurge) {
        if (mailFolder != null) {
            try {
                mailFolder.close(expurge);
            } catch (MessagingException ex) {
                LOGGER.error("Could not close folder", ex);
            }
        }
    }

    /**
     * Liest die angegebenen Daten zur E-Mail in die �bergebene
     * <code>MailInfo</code> ein
     *
     * @param path Ordnerpfad innerhalb des MailServers
     * @param mailInfo Zu f�llende <code>MailInfo</code>
     * @param mailContent Eine Aufz�hlung der auszulesenden Daten
     * @throws javax.mail.MessagingException wenn die Nachrichten nicht geladen
     * werden konnten
     * @throws de.outlook_klon.dao.DAOException wenn ein Fehler beim Zugriff auf
     * die MailInfoDAO auftritt
     */
    public void loadMessageData(String path, MailInfo mailInfo, Set<MailContent> mailContent)
            throws MessagingException, DAOException {
        if (path == null || path.trim().length() == 0) {
            throw new NullPointerException("path ist NULL oder leer");
        }
        if (mailInfo == null) {
            throw new NullPointerException("�bergebene MailInfo ist NULL");
        }
        if (mailContent == null) {
            throw new NullPointerException("mailContent ist NULL");
        }

        if (mailInfo.hasAlreadyLoadedData(mailContent)) {
            return;
        }

        Store store = null;
        Folder folder = null;
        try {
            store = connectToMailStore();

            folder = store.getFolder(path);
            folder.open(Folder.READ_WRITE);

            final Message message = infoToMessage(mailInfo, folder);

            if (message != null) {
                message.setFlag(Flag.SEEN, true);
                mailInfo.loadData(message, mailContent);

                mailInfoDAO.saveMailInfo(mailInfo, path);
            }
        } catch (IOException | MessagingException ex) {
            throw new MessagingException("Could not load message data", ex);
        } finally {
            closeMailFolder(folder, true);
            closeMailStore(store);
        }
    }

    /**
     * Gibt das <code>Message</code>-Objekt zur ID in der �bergebenen
     * <code>MailInfo</code> im �bergebenen Ordner zur�ck.
     *
     * @param mail <code>MailInfo</code>-Objekt, das die ID zur suchenden
     * Message enth�llt
     * @param ordner Ordner, in dem gesucht werden soll
     * @return <code>Message</code>-Objekt zur �bergebenen ID
     */
    private Message infoToMessage(final MailInfo mail, final Folder ordner)
            throws MessagingException {

        Message[] result = infoToMessage(new MailInfo[]{mail}, ordner);
        return result == null || result.length == 0 ? null : result[0];
    }

    /**
     * Gibt die <code>Message</code>-Objekte zu den IDs in den �bergebenen
     * MailInfos im �bergebenen Ordner zur�ck.
     *
     * @param mail <code>MailInfo</code>-Objekte, die die IDs zu den zu
     * suchenden Messages enthallten
     * @param ordner Ordner, in dem gesucht werden soll
     * @return <code>Message</code>-Objekte zu den �bergebenen IDs
     */
    private Message[] infoToMessage(final MailInfo[] mails, final Folder ordner)
            throws MessagingException {
        Message[] messages = new Message[mails.length];
        Message[] folderMails = ordner.getMessages();

        FetchProfile fp = new FetchProfile();
        fp.add("Message-Id");
        ordner.fetch(folderMails, fp);

        for (int i = 0; i < mails.length; i++) {
            String id = mails[i].getID();

            Message[] tmpMessages = ordner.search(new MessageIDTerm(id));
            if (tmpMessages.length == 0) {
                tmpMessages = ordner.search(new MyMessageIDTerm(id));
            }

            messages[i] = tmpMessages.length == 0 ? null : tmpMessages[0];
        }

        return messages;
    }

    /**
     * Kopiert die �bergebenen Mails in den Zielordner
     *
     * @param mails MailInfos, die die IDs der zu kopierenden Messages enthalten
     * @param quellOrdner Quellordner
     * @param zielOrdner Zielordner
     * @param l�schen Wert, der angibt, ob die Mails nach dem Kopieren im
     * Quellordner gel�scht werden sollen
     * @throws de.outlook_klon.dao.DAOException wenn ein Fehler beim Zugriff auf
     * die MailInfoDAO ein Fehler auftritt
     */
    private void kopieren(final MailInfo[] mails, final Folder quellOrdner,
            final Folder zielOrdner, final boolean l�schen)
            throws MessagingException, DAOException {
        final Message[] messages = infoToMessage(mails, quellOrdner);
        final String quellPfad = quellOrdner.getFullName();
        final String zielPfad = zielOrdner.getFullName();

        quellOrdner.copyMessages(messages, zielOrdner);
        for (int i = 0; i < messages.length; i++) {
            mailInfoDAO.saveMailInfo(mails[i], zielPfad);

            if (l�schen) {
                if (!messages[i].isExpunged()) {
                    messages[i].setFlag(Flags.Flag.DELETED, true);
                }
                mailInfoDAO.deleteMailInfo(mails[i].getID(), quellPfad);
            }
        }

        if (l�schen) {
            quellOrdner.expunge();
        }
    }

    /**
     * Verschiebe die Mails vom Quell- in den Zielordner
     *
     * @param mails MailInfos der zu verschiebenen Mails
     * @param quellPfad Pfad zum Quellordner
     * @param zielPfad Pfad zum Zielordner
     * @throws javax.mail.MessagingException wenn ein Fehler seitens der
     * Mail-Library auftritt
     * @throws de.outlook_klon.dao.DAOException wenn ein Fehler beim Zugriff auf
     * die MailInfoDAO ein Fehler auftritt
     */
    public void verschiebeMails(final MailInfo[] mails, final String quellPfad,
            final String zielPfad)
            throws MessagingException, DAOException {
        Store mailStore = null;

        try {
            mailStore = connectToMailStore();

            final Folder quellOrdner = mailStore.getFolder(quellPfad);
            quellOrdner.open(Folder.READ_WRITE);
            final Folder zielOrdner = mailStore.getFolder(zielPfad);
            zielOrdner.open(Folder.READ_WRITE);

            kopieren(mails, quellOrdner, zielOrdner, true);
        } finally {
            closeMailStore(mailStore);
        }
    }

    /**
     * Kopiere die Mails vom Quell- in den Zielordner
     *
     * @param mails MailInfos der zu kopieren Mails
     * @param quellPfad Pfad zum Quellordner
     * @param zielPfad Pfad zum Zielordner
     * @throws javax.mail.MessagingException wenn ein Fehler seitens der
     * Mail-Library auftritt
     * @throws de.outlook_klon.dao.DAOException wenn ein Fehler beim Zugriff auf
     * die MailInfoDAO ein Fehler auftritt
     */
    public void kopiereMails(final MailInfo[] mails, final String quellPfad,
            final String zielPfad)
            throws MessagingException, DAOException {
        Store mailStore = null;

        try {
            mailStore = connectToMailStore();

            final Folder quellOrdner = mailStore.getFolder(quellPfad);
            quellOrdner.open(Folder.READ_ONLY);
            final Folder zielOrdner = mailStore.getFolder(zielPfad);
            zielOrdner.open(Folder.READ_WRITE);

            kopieren(mails, quellOrdner, zielOrdner, false);
        } finally {
            closeMailStore(mailStore);
        }
    }

    /**
     * L�sche die Mails aus dem �bergebenen Ordner
     *
     * @param mails MailInfos der zu l�schenden Mails
     * @param pfad Pfad zum Ordner
     * @return true, wenn das l�schen erfolgreich war; sonst false
     * @throws javax.mail.MessagingException wenn ein Fehler seitens der
     * Mail-Library auftritt
     * @throws de.outlook_klon.dao.DAOException wenn ein Fehler beim Zugriff auf
     * die MailInfoDAO ein Fehler auftritt
     */
    public boolean loescheMails(final MailInfo[] mails, final String pfad)
            throws MessagingException, DAOException {
        boolean result = false;

        Store mailStore = null;
        Folder folder = null;
        Folder binFolder = null;
        try {
            mailStore = connectToMailStore();

            folder = mailStore.getFolder(pfad);
            folder.open(Folder.READ_WRITE);

            binFolder = getTrashFolder(mailStore);
            if (binFolder != null) {
                final String binPfad = binFolder.getFullName();
                if (!pfad.equals(binPfad)) {
                    kopieren(mails, folder, binFolder, true);
                    return true;
                }
            }
            final Message[] messages = infoToMessage(mails, folder);
            for (final Message m : messages) {
                if (!m.isExpunged()) {
                    m.setFlag(Flags.Flag.DELETED, true);
                }
            }

            folder.expunge();

            result = true;
        } finally {
            closeMailFolder(binFolder, true);
            closeMailFolder(folder, true);
            closeMailStore(mailStore);
        }

        return result;
    }

    private Folder getTrashFolder(Store mailStore) throws MessagingException {
        if (incomingMailServer.supportsMultipleFolders()) {
            final Folder[] folders = mailStore.getDefaultFolder().list("*");

            for (final Folder mailFolder : folders) {
                if (mailFolder instanceof IMAPFolder) {
                    final IMAPFolder imap = (IMAPFolder) mailFolder;
                    final String[] attributes = imap.getAttributes();
                    for (String attribute : attributes) {
                        if (attribute.equalsIgnoreCase("\\Trash")) {
                            return imap;
                        }
                    }
                }

                String ordnerName = mailFolder.getName();
                if (ordnerName.equalsIgnoreCase("trash")
                        || ordnerName.equalsIgnoreCase("deleted")
                        || ordnerName.equalsIgnoreCase("papierkorb")
                        || ordnerName.equalsIgnoreCase("gel�scht")) {
                    return mailFolder;
                }
            }
        }

        return null;
    }

    /**
     * Speichert den Anhang der �bergebenen Mail am �bergebenen Ort
     *
     * @param mail <code>MailInfo</code>-Objekt
     * @param pfad Ordnerpfad innerhalb des MailStores
     * @param anhangName Name des zu speichernden Anhangs
     * @param zielPfad Zielpfad, an dem die Datei gespeichert werden soll
     * @throws IOException Tritt auf, wenn die Datei nicht gespeichert werden
     * konnte
     * @throws MessagingException Triff auf, wenn es einen Fehler bez�glich der
     * Nachricht gab
     */
    public void anhangSpeichern(final MailInfo mail, final String pfad,
            final String anhangName, final String zielPfad)
            throws IOException, MessagingException {
        Store mailStore = null;
        Folder folder = null;
        try {
            mailStore = connectToMailStore();

            folder = mailStore.getFolder(pfad);
            folder.open(Folder.READ_WRITE);

            final Message message = infoToMessage(mail, folder);
            final String contentType = message.getContentType();
            if (contentType.contains("multipart")) {
                final Multipart multipart = (Multipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    final MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
                    final String disposition = part.getDisposition();
                    final String fileName = part.getFileName();

                    if (Part.ATTACHMENT.equalsIgnoreCase(disposition)
                            || (fileName != null && !fileName.trim().isEmpty())) {
                        if (anhangName.equals(fileName)) {
                            part.saveFile(zielPfad);
                        }
                    }
                }
            }
        } finally {
            closeMailFolder(folder, true);
            closeMailStore(mailStore);
        }
    }

    /**
     * Pr�ft, ob mit den Daten der <code>MailAccount</code>-Instanz eine
     * erfolgreiche Verbindung zum Empfangs- und zum Versandtserver hergestellt
     * werden konnte
     *
     * @return <code>true</code>, wenn die Verbindungen erfolgreich waren; sonst
     * <code>false</code>
     */
    public boolean validieren() {
        return validieren(user, password);
    }

    /**
     * Pr�ft, ob mit den �bergebenen Daten eine erfolgreiche Verbindung zum
     * Empfangs- und zum Versandtserver hergestellt werden konnte
     *
     * @return <code>true</code>, wenn die Verbindungen erfolgreich waren; sonst
     * <code>false</code>
     */
    private boolean validieren(String user, String passwd) {
        boolean inValid = false;
        boolean outValid = false;

        if (incomingMailServer != null) {
            inValid = incomingMailServer.pruefeLogin(user, passwd);
        }

        if (outgoingMailServer != null) {
            outValid = outgoingMailServer.pruefeLogin(user, passwd);
        }

        return inValid && outValid;
    }

    /**
     * Gibt die <code>MailServer</code>-Instanz zum Empfangen von Mails zur�ck
     *
     * @return <code>MailServer</code> zum Empfangen von Mails
     */
    public EmpfangsServer getIncomingMailServer() {
        return incomingMailServer;
    }

    /**
     * Gibt die <code>MailServer</code>-Instanz zum Versandt von Mails zur�ck
     *
     * @return <code>MailServer</code> zum Versandt von Mails
     */
    public SendServer getOutgoingMailServer() {
        return outgoingMailServer;
    }

    /**
     * Gibt die Mailadresse des MailAccounts zur�ck
     *
     * @return Mailadresse des MailAccounts
     */
    public InternetAddress getAddress() {
        return address;
    }

    /**
     * Gibt den Benutzernamen f�r den <code>MailAccount</code> zur�ck
     *
     * @return Benutzername f�r den <code>MailAccount</code>
     */
    public String getUser() {
        return user;
    }

    /**
     * Versucht, das Passwort des Accounts neu zu setzen
     *
     * @param password Zu setzendes Passwort
     * @throws AuthenticationFailedException Tritt auf, wenn die Anmeldung mit
     * dem Passwort fehlgeschlagen ist
     */
    public void setPasswort(String password) throws AuthenticationFailedException {
        if (!validieren(user, password)) {
            throw new AuthenticationFailedException("Das �bergebene Passwort ist ung�ltig");
        }

        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        // Es d�rfen keine MailAccounts hinzugef�gt werden, deren MailAdresse
        // bereit enthalten ist

        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        if (obj instanceof MailAccount) {
            MailAccount acc = (MailAccount) obj;
            String thisAddress = this.address.getAddress();
            String accAddress = acc.address.getAddress();

            return thisAddress.equalsIgnoreCase(accAddress);
        }

        if (obj instanceof MailChecker) {
            MailChecker checker = (MailChecker) obj;
            return this.equals(checker.getAccount());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.address.getAddress().hashCode();
    }
}
