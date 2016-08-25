package de.outlookklon.logik.mailclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.mail.imap.IMAPFolder;
import de.outlookklon.dao.DAOException;
import de.outlookklon.dao.StoredMailInfoDAO;
import de.outlookklon.logik.mailclient.checker.MailAccountChecker;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
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
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Diese Klasse stellt ein Mailkonto dar. Hierüber können Mails gesendet und
 * empfangen werden.
 *
 * @author Hendrik Karwanni
 */
public class MailAccount {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailAccount.class);

    private static final String MESSAGE_ID_HEADER_NAME = "Message-Id";

    @JsonProperty("inboxMailServer")
    private InboxServer inboxMailServer;

    @JsonProperty("outboxMailServer")
    private OutboxServer outboxMailServer;

    @JsonProperty("address")
    private InternetAddress address;

    @JsonProperty("user")
    private String user;

    @JsonProperty("password")
    private String password;

    @JsonIgnore
    @Autowired
    private StoredMailInfoDAO mailInfoDAO;

    /**
     * Bei manchen Anbietern, z.B. Hotmail oder Yahoo, kann die MessageID nicht
     * auf normalem Wege mit dem standardmäßigen <code>MessageIDTerm</code>
     * abgerufen werden. Daher wird hier ein neuer <code>SeachTerm</code>
     * implementiert, der die Mails zuerst öffnet und dann erst die ID ausliest.
     * Sollte nur verwendet werden, wenn keine Mail gefunden wurde, da dieses
     * Suchverfahren langsamer ist, als der ursprüngliche
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
                String[] tmpId = message.getHeader(MESSAGE_ID_HEADER_NAME);
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
     * Erstellt eine neue Instanz der Klasse Mailkonto mit den übergebenen
     * Parametern
     *
     * @param inboxMailServer Server-Instanz, die zum Empfangen von Mails
     * verwendet wird
     * @param outboxMailServer Server-Instanz, die zum Senden von Mails
     * verwendet wird
     * @param address E-Mail-Adresse, das dem Konto zugeordnet ist
     * @param user Benutzername, der zur Anmeldung verwendet werden soll
     * @param password Passwort, das zur Anmeldung verwendet werden soll
     * @throws NullPointerException Tritt auf, wenn mindestens eine der
     * Server-Instanzen null ist
     * @throws IllegalArgumentException Tritt auf, wenn die übergebene
     * Mailadresse ungültig ist
     * @throws java.io.IOException Tritt auf, wenn die StoredMailInfoDAO nicht
     * erstellt werden konnte.
     */
    @JsonCreator
    public MailAccount(
            @JsonProperty("inboxMailServer") @NonNull InboxServer inboxMailServer,
            @JsonProperty("outboxMailServer") @NonNull OutboxServer outboxMailServer,
            @JsonProperty("address") @NonNull InternetAddress address,
            @JsonProperty("user") @NonNull String user,
            @JsonProperty("password") @NonNull String password)
            throws NullPointerException, IllegalArgumentException, IOException {
        this.inboxMailServer = inboxMailServer;
        this.outboxMailServer = outboxMailServer;

        this.address = address;
        this.user = user;
        this.password = password;
    }

    @Override
    public String toString() {
        return address.toUnicodeString();
    }

    /**
     * Setzt das interne MailInfoDAO
     *
     * @param mailInfoDAO Zu setzendes MailInfoDAO
     */
    public void setStoredMailInfoDAO(@NonNull StoredMailInfoDAO mailInfoDAO) {
        this.mailInfoDAO = mailInfoDAO;
    }

    /**
     * Sendet eine Nachricht an einen Mailserver
     *
     * @param mailToSend Objekt, das alle zu sendenden Daten enthält
     * @throws MessagingException Tritt auf, wenn der Sendevorgang
     * fehlgeschlagen ist
     */
    public void sendMail(MailInfo mailToSend)
            throws MessagingException {

        Message sendMail;
        try {
            mailToSend.setSender(address);
            sendMail = outboxMailServer.sendMail(user, password, mailToSend);
        } catch (MessagingException ex) {
            throw new MessagingException("Could not send mail", ex);
        }

        if (inboxMailServer.supportsMultipleFolders()) {
            Store mailStore = null;
            Folder sentFolder = null;
            try {
                // TODO Testen!
                mailStore = connectToMailStore();
                sentFolder = getSentFolder(mailStore);

                if (sentFolder != null) {
                    sentFolder.appendMessages(new Message[]{sendMail});
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
        if (inboxMailServer.supportsMultipleFolders()) {
            final Folder[] folders = mailStore.getDefaultFolder().list("*");

            for (final Folder folder : folders) {
                if (folder instanceof IMAPFolder) {
                    final IMAPFolder imap = (IMAPFolder) folder;
                    final String[] attributes = imap.getAttributes();
                    if (attributesContainValue(attributes, "\\Sent")) {
                        return imap;
                    }
                }

                if (isSentFolderName(folder.getName())) {
                    return folder;
                }
            }
        }

        return null;
    }

    private boolean attributesContainValue(String[] attributes, String value) {
        for (String attribute : attributes) {
            if (value.equalsIgnoreCase(attribute)) {
                return true;
            }
        }

        return false;
    }

    private boolean isSentFolderName(@NonNull String folderName) {
        return "sent".equalsIgnoreCase(folderName)
                || "gesendet".equalsIgnoreCase(folderName);
    }

    private Store connectToMailStore() throws MessagingException {
        Store mailStore = inboxMailServer.getMailStore(user, password);
        ServerSettings inServerSettings = inboxMailServer.getSettings();
        mailStore.connect(inServerSettings.getHost(), inServerSettings.getPort(), user, password);

        return mailStore;
    }

    /**
     * Gibt die Pfade aller Ordner des Servers zum Mailempfang zurück
     *
     * @return Pfade aller Ordner des Servers zum Mailempfang
     * @throws MessagingException
     */
    @JsonIgnore
    public FolderInfo[] getFolderStructure() throws MessagingException {
        FolderInfo[] paths = null;

        Store store = null;
        try {
            store = connectToMailStore();
            final Folder[] folders = store.getDefaultFolder().list("*");

            paths = new FolderInfo[folders.length];
            for (int i = 0; i < paths.length; i++) {
                Folder folder = folders[i];

                paths[i] = new FolderInfo(folder.getName(), folder.getFullName(), 0);
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
     * Gibt die ID zur übergebenen Mail zurück
     *
     * @param message Mail, für die die ID bestimmt werden soll
     * @return ID der Mail, oder <code>null</code>, wenn nicht gefunden
     */
    private String getID(Message message) throws MessagingException {
        String[] tmpID = message.getHeader(MESSAGE_ID_HEADER_NAME);
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
     * Gibt die MailInfos aller Messages in dem übergebenen Pfad zurück.
     *
     * @param path Pfad, in dem die Mails gesucht werden.
     * @return Array von MailInfos mit der ID, Betreff, Sender und SendDatum
     * @throws javax.mail.MessagingException wenn die Nachrichten nicht geladen
     * werden konnten
     * @throws de.outlookklon.dao.DAOException wenn ein Fehler beim Zugriff auf
     * die StoredMailInfoDAO ein Fehler auftritt
     */
    public StoredMailInfo[] getMessages(final String path)
            throws MessagingException, DAOException {
        Set<StoredMailInfo> set = new HashSet<>();

        Store store = null;
        Folder folder = null;
        try {
            store = connectToMailStore();

            folder = store.getFolder(path);
            folder.open(Folder.READ_ONLY);

            final Message[] messages = folder.getMessages();
            FetchProfile fp = new FetchProfile();
            fp.add(MESSAGE_ID_HEADER_NAME);
            folder.fetch(messages, fp);

            for (Message message : messages) {
                String id = getID(message);
                if (id == null) {
                    continue;
                }

                StoredMailInfo tmp = mailInfoDAO.loadStoredMailInfo(getAddress(), id, path);
                if (tmp == null) {
                    tmp = new StoredMailInfo(id);
                    tmp.loadData(message, EnumSet.of(
                            MailContent.READ,
                            MailContent.SUBJECT,
                            MailContent.SENDER,
                            MailContent.DATE));

                    mailInfoDAO.saveStoredMailInfo(getAddress(), tmp, path);
                }

                set.add(tmp);
            }

        } catch (MessagingException | IOException ex) {
            throw new MessagingException("Could not get messages", ex);
        } finally {
            closeMailFolder(folder, true);
            closeMailStore(store);
        }

        return set.toArray(new StoredMailInfo[set.size()]);
    }

    private void closeMailFolder(Folder mailFolder, boolean expurge) {
        if (mailFolder != null) {
            try {
                if (mailFolder.isOpen()) {
                    mailFolder.close(expurge);
                }
            } catch (MessagingException ex) {
                LOGGER.error("Could not close folder", ex);
            }
        }
    }

    /**
     * Liest die angegebenen Daten zur E-Mail in die übergebene
     * <code>StoredMailInfo</code> ein
     *
     * @param path Ordnerpfad innerhalb des MailServers
     * @param mailInfo Zu füllende <code>StoredMailInfo</code>
     * @param mailContent Eine Aufzählung der auszulesenden Daten
     * @throws javax.mail.MessagingException wenn die Nachrichten nicht geladen
     * werden konnten
     * @throws de.outlookklon.dao.DAOException wenn ein Fehler beim Zugriff auf
     * die StoredMailInfoDAO auftritt
     */
    public void loadMessageData(@NonNull String path, @NonNull StoredMailInfo mailInfo,
            @NonNull Set<MailContent> mailContent)
            throws MessagingException, DAOException {
        if (path.trim().length() == 0) {
            throw new NullPointerException("path ist leer");
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

                mailInfoDAO.saveStoredMailInfo(getAddress(), mailInfo, path);
            }
        } catch (IOException | MessagingException ex) {
            throw new MessagingException("Could not load message data", ex);
        } finally {
            closeMailFolder(folder, true);
            closeMailStore(store);
        }
    }

    /**
     * Gibt das <code>Message</code>-Objekt zur ID in der übergebenen
     * <code>StoredMailInfo</code> im übergebenen Ordner zurück.
     *
     * @param mail <code>StoredMailInfo</code>-Objekt, das die ID zur suchenden
     * Message enthällt
     * @param folder Ordner, in dem gesucht werden soll
     * @return <code>Message</code>-Objekt zur übergebenen ID
     */
    private Message infoToMessage(final StoredMailInfo mail, final Folder folder)
            throws MessagingException {

        Message[] result = infoToMessage(new StoredMailInfo[]{mail}, folder);
        return result == null || result.length == 0 ? null : result[0];
    }

    /**
     * Gibt die <code>Message</code>-Objekte zu den IDs in den übergebenen
     * MailInfos im übergebenen Ordner zurück.
     *
     * @param mail <code>StoredMailInfo</code>-Objekte, die die IDs zu den zu
     * suchenden Messages enthallten
     * @param folder Ordner, in dem gesucht werden soll
     * @return <code>Message</code>-Objekte zu den übergebenen IDs
     */
    private Message[] infoToMessage(final StoredMailInfo[] mails, final Folder folder)
            throws MessagingException {
        Message[] messages = new Message[mails.length];
        Message[] folderMails = folder.getMessages();

        FetchProfile fp = new FetchProfile();
        fp.add(MESSAGE_ID_HEADER_NAME);
        folder.fetch(folderMails, fp);

        for (int i = 0; i < mails.length; i++) {
            String id = mails[i].getID();

            Message[] tmpMessages = folder.search(new MessageIDTerm(id));
            if (tmpMessages.length == 0) {
                tmpMessages = folder.search(new MyMessageIDTerm(id));
            }

            messages[i] = tmpMessages.length == 0 ? null : tmpMessages[0];
        }

        return messages;
    }

    /**
     * Kopiert die übergebenen Mails in den Zielordner
     *
     * @param mails MailInfos, die die IDs der zu kopierenden Messages enthalten
     * @param sourceFolder Quellordner
     * @param targetFolder Zielordner
     * @param delete Wert, der angibt, ob die Mails nach dem Kopieren im
     * Quellordner gelöscht werden sollen
     * @throws de.outlookklon.dao.DAOException wenn ein Fehler beim Zugriff auf
     * die StoredMailInfoDAO ein Fehler auftritt
     */
    private void copy(final StoredMailInfo[] mails, final Folder sourceFolder,
            final Folder targetFolder, final boolean delete)
            throws MessagingException, DAOException {
        final Message[] messages = infoToMessage(mails, sourceFolder);
        final String sourcePath = sourceFolder.getFullName();
        final String targetPath = targetFolder.getFullName();

        sourceFolder.copyMessages(messages, targetFolder);
        for (int i = 0; i < messages.length; i++) {
            mailInfoDAO.saveStoredMailInfo(getAddress(), mails[i], targetPath);

            if (delete) {
                if (!messages[i].isExpunged()) {
                    messages[i].setFlag(Flags.Flag.DELETED, true);
                }
                mailInfoDAO.deleteStoredMailInfo(getAddress(), mails[i].getID(), sourcePath);
            }
        }

        if (delete) {
            sourceFolder.expunge();
        }
    }

    /**
     * Verschiebe die Mails vom Quell- in den Zielordner
     *
     * @param mails MailInfos der zu verschiebenen Mails
     * @param sourcePath Pfad zum Quellordner
     * @param targetPath Pfad zum Zielordner
     * @throws javax.mail.MessagingException wenn ein Fehler seitens der
     * Mail-Library auftritt
     * @throws de.outlookklon.dao.DAOException wenn ein Fehler beim Zugriff auf
     * die StoredMailInfoDAO ein Fehler auftritt
     */
    public void moveMails(final StoredMailInfo[] mails, final String sourcePath,
            final String targetPath)
            throws MessagingException, DAOException {
        Store mailStore = null;

        try {
            mailStore = connectToMailStore();

            final Folder sourceFolder = mailStore.getFolder(sourcePath);
            sourceFolder.open(Folder.READ_WRITE);
            final Folder targetFolder = mailStore.getFolder(targetPath);
            targetFolder.open(Folder.READ_WRITE);

            copy(mails, sourceFolder, targetFolder, true);
        } finally {
            closeMailStore(mailStore);
        }
    }

    /**
     * Kopiere die Mails vom Quell- in den Zielordner
     *
     * @param mails MailInfos der zu kopieren Mails
     * @param sourcePath Pfad zum Quellordner
     * @param targetPath Pfad zum Zielordner
     * @throws javax.mail.MessagingException wenn ein Fehler seitens der
     * Mail-Library auftritt
     * @throws de.outlookklon.dao.DAOException wenn ein Fehler beim Zugriff auf
     * die StoredMailInfoDAO ein Fehler auftritt
     */
    public void copyMails(final StoredMailInfo[] mails, final String sourcePath,
            final String targetPath)
            throws MessagingException, DAOException {
        Store mailStore = null;

        try {
            mailStore = connectToMailStore();

            final Folder sourceFolder = mailStore.getFolder(sourcePath);
            sourceFolder.open(Folder.READ_ONLY);
            final Folder targetFolder = mailStore.getFolder(targetPath);
            targetFolder.open(Folder.READ_WRITE);

            copy(mails, sourceFolder, targetFolder, false);
        } finally {
            closeMailStore(mailStore);
        }
    }

    /**
     * Lösche die Mails aus dem übergebenen Ordner
     *
     * @param mails MailInfos der zu löschenden Mails
     * @param path Pfad zum Ordner
     * @throws javax.mail.MessagingException wenn ein Fehler seitens der
     * Mail-Library auftritt
     * @throws de.outlookklon.dao.DAOException wenn ein Fehler beim Zugriff auf
     * die StoredMailInfoDAO ein Fehler auftritt
     */
    public void deleteMails(final StoredMailInfo[] mails, final String path)
            throws MessagingException, DAOException {
        Store mailStore = null;
        Folder folder = null;
        Folder binFolder = null;
        try {
            mailStore = connectToMailStore();

            folder = mailStore.getFolder(path);
            folder.open(Folder.READ_WRITE);

            binFolder = getTrashFolder(mailStore);
            if (binFolder != null) {
                final String binPfad = binFolder.getFullName();
                if (!path.equals(binPfad)) {
                    copy(mails, folder, binFolder, true);
                    return;
                }
            }
            final Message[] messages = infoToMessage(mails, folder);
            for (final Message m : messages) {
                if (!m.isExpunged()) {
                    m.setFlag(Flags.Flag.DELETED, true);
                }
            }

            folder.expunge();
        } finally {
            closeMailFolder(binFolder, true);
            closeMailFolder(folder, true);
            closeMailStore(mailStore);
        }
    }

    private Folder getTrashFolder(Store mailStore) throws MessagingException {
        if (inboxMailServer.supportsMultipleFolders()) {
            final Folder[] folders = mailStore.getDefaultFolder().list("*");

            for (final Folder mailFolder : folders) {
                if (mailFolder instanceof IMAPFolder) {
                    final IMAPFolder imap = (IMAPFolder) mailFolder;
                    final String[] attributes = imap.getAttributes();
                    if (attributesContainValue(attributes, "\\Trash")) {
                        return imap;
                    }
                }

                if (isTrashFolderName(mailFolder.getName())) {
                    return mailFolder;
                }
            }
        }

        return null;
    }

    private boolean isTrashFolderName(@NonNull String folderName) {
        return "trash".equalsIgnoreCase(folderName)
                || "deleted".equalsIgnoreCase(folderName)
                || "papierkorb".equalsIgnoreCase(folderName)
                || "gelöscht".equalsIgnoreCase(folderName);
    }

    /**
     * Speichert den Anhang der übergebenen Mail am übergebenen Ort
     *
     * @param mail <code>StoredMailInfo</code>-Objekt
     * @param path Ordnerpfad innerhalb des MailStores
     * @param attachmentName Name des zu speichernden Anhangs
     * @param targetPath Zielpfad, an dem die Datei gespeichert werden soll
     * @throws IOException Tritt auf, wenn die Datei nicht gespeichert werden
     * konnte
     * @throws MessagingException Triff auf, wenn es einen Fehler bezüglich der
     * Nachricht gab
     */
    public void saveAttachment(final StoredMailInfo mail, final String path,
            final String attachmentName, final String targetPath)
            throws IOException, MessagingException {
        Store mailStore = null;
        Folder folder = null;
        try {
            mailStore = connectToMailStore();

            folder = mailStore.getFolder(path);
            folder.open(Folder.READ_WRITE);

            final Message message = infoToMessage(mail, folder);
            final String contentType = message.getContentType();
            if (contentType.contains("multipart")) {
                final Multipart multipart = (Multipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    final MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
                    final String disposition = part.getDisposition();
                    final String fileName = part.getFileName();

                    if ((Part.ATTACHMENT.equalsIgnoreCase(disposition)
                            || StringUtils.isBlank(fileName))
                            && attachmentName.equals(fileName)) {
                        part.saveFile(targetPath);
                    }
                }
            }
        } finally {
            closeMailFolder(folder, true);
            closeMailStore(mailStore);
        }
    }

    /**
     * Prüft, ob mit den Daten der <code>MailAccount</code>-Instanz eine
     * erfolgreiche Verbindung zum Empfangs- und zum Versandtserver hergestellt
     * werden konnte
     *
     * @return <code>true</code>, wenn die Verbindungen erfolgreich waren; sonst
     * <code>false</code>
     */
    public boolean validate() {
        return validate(user, password);
    }

    /**
     * Prüft, ob mit den übergebenen Daten eine erfolgreiche Verbindung zum
     * Empfangs- und zum Versandtserver hergestellt werden konnte
     *
     * @return <code>true</code>, wenn die Verbindungen erfolgreich waren; sonst
     * <code>false</code>
     */
    private boolean validate(String user, String passwd) {
        return inboxMailServer.checkLogin(user, passwd) && outboxMailServer.checkLogin(user, passwd);
    }

    /**
     * Gibt die <code>MailServer</code>-Instanz zum Empfangen von Mails zurück
     *
     * @return <code>MailServer</code> zum Empfangen von Mails
     */
    public InboxServer getInboxMailServer() {
        return inboxMailServer;
    }

    /**
     * Gibt die <code>MailServer</code>-Instanz zum Versandt von Mails zurück
     *
     * @return <code>MailServer</code> zum Versandt von Mails
     */
    public OutboxServer getOutboxMailServer() {
        return outboxMailServer;
    }

    /**
     * Gibt die Mailadresse des MailAccounts zurück
     *
     * @return Mailadresse des MailAccounts
     */
    @JsonIgnore
    public String getAddress() {
        return address.getAddress();
    }

    /**
     * Gibt die Namen der Mailadresse des MailAccounts zurück
     *
     * @return Name der Mailadresse des MailAccounts
     */
    @JsonIgnore
    public String getPersonal() {
        return address.getPersonal();
    }

    /**
     * Gibt den Benutzernamen für den <code>MailAccount</code> zurück
     *
     * @return Benutzername für den <code>MailAccount</code>
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
    public void setPassword(@NonNull String password) throws AuthenticationFailedException {
        if (!validate(user, password)) {
            throw new AuthenticationFailedException("Das übergebene Passwort ist ungültig");
        }

        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        // Es dürfen keine MailAccounts hinzugefügt werden, deren MailAdresse
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

        if (obj instanceof MailAccountChecker) {
            MailAccountChecker checker = (MailAccountChecker) obj;
            return this.equals(checker.getAccount());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.address.getAddress().hashCode();
    }
}
