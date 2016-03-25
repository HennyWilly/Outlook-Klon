package de.outlook_klon.logik.mailclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.mail.imap.IMAPFolder;
import de.outlook_klon.logik.Benutzer.MailChecker;
import de.outlook_klon.serializers.Serializer;
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
 * Diese Klasse stellt ein Mailkonto dar. Hierüber können Mails gesendet und
 * empfangen werden.
 *
 * @author Hendrik Karwanni
 */
public class MailAccount {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailAccount.class);

    private static final String MAIL_PATTERN = "Mail/%s/%s/%s.json";

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
     * Erstellt eine neue Instanz der Klasse Mailkonto mit den übergebenen
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
     * @throws IllegalArgumentException Tritt auf, wenn die übergebene
     * Mailadresse ungültig ist
     */
    @JsonCreator
    public MailAccount(
            @JsonProperty("incomingMailServer") EmpfangsServer incomingMailServer,
            @JsonProperty("outgoingMailServer") SendServer outgoingMailServer,
            @JsonProperty("address") InternetAddress address,
            @JsonProperty("user") String user,
            @JsonProperty("password") String password)
            throws NullPointerException, IllegalArgumentException {
        if (incomingMailServer == null || outgoingMailServer == null) {
            throw new NullPointerException("Die übergebenen Server dürfen nicht <null> sein");
        }

        this.incomingMailServer = incomingMailServer;
        this.outgoingMailServer = outgoingMailServer;

        this.address = address;
        this.user = user;
        this.password = password;
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
     * @param attachment Pfade der Anhänge der Mail
     * @throws MessagingException Tritt auf, wenn der Sendevorgang
     * fehlgeschlagen ist
     */
    public void sendeMail(final Address[] to, final Address[] cc, final String subject,
            final String text, final String format, final File[] attachment) throws MessagingException {

        Message gesendet;
        try {
            gesendet = outgoingMailServer.sendeMail(user, password, address, to, cc, subject, text, format, attachment);
        } catch (MessagingException ex) {
            throw new MessagingException("Could not send mail", ex);
        }

        if (gesendet != null && !(incomingMailServer instanceof Pop3Server)) {
            try {
                // TODO Testen!
                Store mailStore = connectToMailStore();
                final Folder[] folders = mailStore.getDefaultFolder().list("*");

                Folder sendFolder = null;
                outer:
                for (final Folder folder : folders) {
                    final IMAPFolder imap = (IMAPFolder) folder;
                    final String[] attr = imap.getAttributes();

                    if (imap.getName().equalsIgnoreCase("sent") || imap.getName().equalsIgnoreCase("gesendet")) {
                        sendFolder = imap;
                    }

                    for (String attr1 : attr) {
                        if (attr1.equalsIgnoreCase("\\Sent")) {
                            sendFolder = imap;
                            break outer;
                        }
                    }
                }

                if (sendFolder != null) {
                    sendFolder.appendMessages(new Message[]{gesendet});
                }
            } catch (MessagingException ex) {
                throw new MessagingException("Could not store sent email", ex);
            }
        }
    }

    private Store connectToMailStore() throws MessagingException {
        Store mailStore = incomingMailServer.getMailStore(user, password);
        mailStore.connect(incomingMailServer.settings.getHost(), incomingMailServer.settings.getPort(), user, password);

        return mailStore;
    }

    /**
     * Gibt die Pfade aller Ordner des Servers zum Mailempfang zurück
     *
     * @return Pfade aller Ordner des Servers zum Mailempfang
     * @throws MessagingException
     */
    @JsonIgnore
    public OrdnerInfo[] getOrdnerstruktur() throws MessagingException {
        OrdnerInfo[] paths = null;

        Store store = null;
        try {
            store = incomingMailServer.getMailStore(user, password);
            store.connect(incomingMailServer.settings.getHost(), incomingMailServer.settings.getPort(), user, password);
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
     * Gibt die ID zur übergebenen Mail zurück
     *
     * @param message Mail, für die die ID bestimmt werden soll
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
     * Gibt die MailInfos aller Messages in dem übergebenen Pfad zurück.
     *
     * @param pfad Pfad, in dem die Mails gesucht werden.
     * @return Array von MailInfos mit der ID, Betreff, Sender und SendDatum
     * @throws javax.mail.MessagingException wenn die Nachrichten nicht geladen
     * werden konnten
     */
    public MailInfo[] getMessages(final String pfad) throws MessagingException {
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

                final String dateiname = id.replace(">", "").replace("<", "");
                final String strPfad = String.format(MAIL_PATTERN, address.getAddress(), pfad, dateiname);
                final File lokalerPfad = new File(strPfad).getAbsoluteFile();

                MailInfo tmp = ladeMailInfo(lokalerPfad);
                if (tmp == null) {
                    tmp = new MailInfo(id);
                    tmp.loadData(message, EnumSet.of(
                            MailContent.READ,
                            MailContent.SUBJECT,
                            MailContent.SENDER,
                            MailContent.DATE));

                    speichereMailInfo(tmp, pfad);
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
     * Liest den Text zur E-Mail mit der übergebenen ID in die übergebene
     * <code>MailInfo</code> ein
     *
     * @param pfad Ordnerpfad innerhalb des MailServers
     * @param messageInfo Zu füllende <code>MailInfo</code>
     * @throws javax.mail.MessagingException wenn die Nachrichten nicht geladen
     * werden konnten
     */
    public void getMessageText(final String pfad, final MailInfo messageInfo) throws MessagingException {
        if (messageInfo == null || messageInfo.getID() == null) {
            throw new NullPointerException("Übergebene MailInfo ist NULL");
        }

        if (messageInfo.getText() != null && messageInfo.getContentType() != null) {
            return;
        }

        Store store = null;
        Folder folder = null;
        try {
            store = connectToMailStore();

            folder = store.getFolder(pfad);
            folder.open(Folder.READ_WRITE);

            final Message message = infoToMessage(messageInfo, folder);

            if (message != null) {
                message.setFlag(Flag.SEEN, true);
                messageInfo.loadData(message, EnumSet.of(
                        MailContent.TEXT,
                        MailContent.CONTENTTYPE,
                        MailContent.READ));

                speichereMailInfo(messageInfo, pfad);
            }
        } catch (IOException | MessagingException ex) {
            throw new MessagingException("Could not get message text", ex);
        } finally {
            closeMailFolder(folder, true);
            closeMailStore(store);
        }
    }

    /**
     * Liest alle Daten zur E-Mail mit der übergebenen ID in die übergebene
     * <code>MailInfo</code> ein
     *
     * @param pfad Ordnerpfad innerhalb des MailServers
     * @param messageInfo Zu füllende <code>MailInfo</code>
     * @throws javax.mail.MessagingException wenn die Nachrichten nicht geladen
     * werden konnten
     */
    public void getWholeMessage(final String pfad, final MailInfo messageInfo) throws MessagingException {
        if (messageInfo.getText() != null && messageInfo.getContentType() != null && messageInfo.getSubject() != null
                && messageInfo.getSender() != null && messageInfo.getDate() != null && messageInfo.getTo() != null
                && messageInfo.getCc() != null && messageInfo.getAttachment() != null) {
            return;
        }

        Store store = null;
        Folder folder = null;
        try {
            store = connectToMailStore();

            folder = store.getFolder(pfad);
            folder.open(Folder.READ_WRITE);

            final Message message = infoToMessage(messageInfo, folder);
            if (message != null) {
                message.setFlag(Flag.SEEN, true);
                messageInfo.loadData(message, EnumSet.allOf(MailContent.class));

                speichereMailInfo(messageInfo, pfad);
            }
        } catch (IOException | MessagingException ex) {
            throw new MessagingException("Could not get whole message", ex);
        } finally {
            closeMailFolder(folder, true);
            closeMailStore(store);
        }
    }

    /**
     * Gibt das <code>Message</code>-Objekt zur ID in der übergebenen
     * <code>MailInfo</code> im übergebenen Ordner zurück.
     *
     * @param mail <code>MailInfo</code>-Objekt, das die ID zur suchenden
     * Message enthällt
     * @param ordner Ordner, in dem gesucht werden soll
     * @return <code>Message</code>-Objekt zur übergebenen ID
     */
    private Message infoToMessage(final MailInfo mail, final Folder ordner) throws MessagingException {

        Message[] result = infoToMessage(new MailInfo[]{mail}, ordner);
        return result == null || result.length == 0 ? null : result[0];
    }

    /**
     * Gibt die <code>Message</code>-Objekte zu den IDs in den übergebenen
     * MailInfos im übergebenen Ordner zurück.
     *
     * @param mail <code>MailInfo</code>-Objekte, die die IDs zu den zu
     * suchenden Messages enthallten
     * @param ordner Ordner, in dem gesucht werden soll
     * @return <code>Message</code>-Objekte zu den übergebenen IDs
     */
    private Message[] infoToMessage(final MailInfo[] mails, final Folder ordner) throws MessagingException {
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
     * Kopiert die übergebenen Mails in den Zielordner
     *
     * @param mails MailInfos, die die IDs der zu kopierenden Messages enthalten
     * @param quellOrdner Quellordner
     * @param zielOrdner Zielordner
     * @param löschen Wert, der angibt, ob die Mails nach dem Kopieren im
     * Quellordner gelöscht werden sollen
     */
    private void kopieren(final MailInfo[] mails, final Folder quellOrdner, final Folder zielOrdner,
            final boolean löschen) throws MessagingException {
        final Message[] messages = infoToMessage(mails, quellOrdner);
        final String quellPfad = quellOrdner.getFullName();
        final String zielPfad = zielOrdner.getFullName();

        quellOrdner.copyMessages(messages, zielOrdner);
        for (int i = 0; i < messages.length; i++) {
            try {
                speichereMailInfo(mails[i], zielPfad);
            } catch (IOException ex) {
                LOGGER.error("Could not save MailInfo", ex);
            }

            if (löschen) {
                if (!messages[i].isExpunged()) {
                    messages[i].setFlag(Flags.Flag.DELETED, true);
                }
                löscheMailInfo(mails[i], quellPfad);
            }
        }

        if (löschen) {
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
     */
    public void verschiebeMails(final MailInfo[] mails, final String quellPfad, final String zielPfad)
            throws MessagingException {
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
     */
    public void kopiereMails(final MailInfo[] mails, final String quellPfad, final String zielPfad)
            throws MessagingException {
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
     * Lösche die Mails aus dem übergebenen Ordner
     *
     * @param mails MailInfos der zu löschenden Mails
     * @param pfad Pfad zum Ordner
     * @return true, wenn das löschen erfolgreich war; sonst false
     * @throws javax.mail.MessagingException wenn ein Fehler seitens der
     * Mail-Library auftritt
     */
    public boolean loescheMails(final MailInfo[] mails, final String pfad) throws MessagingException {
        boolean result = false;

        Store mailStore = null;
        Folder folder = null;
        try {
            mailStore = connectToMailStore();

            folder = mailStore.getFolder(pfad);
            folder.open(Folder.READ_WRITE);

            Folder binFolder = null;

            final Folder[] folders = mailStore.getDefaultFolder().list("*");

            if (!(incomingMailServer instanceof Pop3Server)) {
                outer:
                for (final Folder mailFolder : folders) {
                    final IMAPFolder imap = (IMAPFolder) mailFolder;
                    final String[] attr = imap.getAttributes();

                    String ordnerName = imap.getName();
                    if (ordnerName.equalsIgnoreCase("trash") || ordnerName.equalsIgnoreCase("deleted")
                            || ordnerName.equalsIgnoreCase("papierkorb") || ordnerName.equalsIgnoreCase("gelöscht")) {
                        binFolder = imap;
                    }

                    for (String attr1 : attr) {
                        if (attr1.equalsIgnoreCase("\\Trash")) {
                            binFolder = imap;
                            break outer;
                        }
                    }
                }
            }

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
            closeMailFolder(folder, true);
            closeMailStore(mailStore);
        }

        return result;
    }

    /**
     * Speichert den Anhang der übergebenen Mail am übergebenen Ort
     *
     * @param mail <code>MailInfo</code>-Objekt
     * @param pfad Ordnerpfad innerhalb des MailStores
     * @param anhangName Name des zu speichernden Anhangs
     * @param zielPfad Zielpfad, an dem die Datei gespeichert werden soll
     * @throws IOException Tritt auf, wenn die Datei nicht gespeichert werden
     * konnte
     * @throws MessagingException Triff auf, wenn es einen Fehler bezüglich der
     * Nachricht gab
     */
    public void anhangSpeichern(final MailInfo mail, final String pfad, final String anhangName, final String zielPfad)
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
     * Speichert die übergebene <code>MailInfo</code> am übergebenen Pfad
     *
     * @param info Zu speichernde <code>MailInfo</code>
     * @param pfad Pfad, in dem das Objekt gespeichert werden soll
     * @throws IOException Tritt auf, wenn das Objekt nicht gespeichert werden
     * konnte
     */
    private void speichereMailInfo(final MailInfo info, final String pfad) throws IOException {
        final String id = info.getID();
        final String dateiname = id.replace(">", "").replace("<", "");
        final String strPfad = String.format(MAIL_PATTERN, address.getAddress(), pfad, dateiname);
        final File zielDatei = new File(strPfad).getAbsoluteFile();

        final File ordner = zielDatei.getParentFile();

        if (!ordner.exists()) {
            ordner.mkdirs();
        }

        Serializer.serializeObjectToJson(zielDatei, info);
    }

    /**
     * Lese das <code>MailInfo</code>-Objekt aus der übergebenen Datei
     *
     * @param datei Enthällt den Pfad, in dem das Objekt liegt
     * @return Deserialisiertes <code>MailInfo</code>-Objekt
     * @throws IOException Die Datei konnte nicht gelesen werden
     */
    private MailInfo ladeMailInfo(final File datei) {
        MailInfo geladen = null;

        final File absDatei = datei.getAbsoluteFile();
        if (absDatei.exists()) {
            try {
                geladen = Serializer.deserializeJson(absDatei, MailInfo.class);
            } catch (IOException ex) {
                LOGGER.error("Could not load MailInfo", ex);
            }
        }

        return geladen;
    }

    /**
     * Löscht die gespeicherte Datei der übergebenen <code>MailInfo</code>
     *
     * @param info <code>MailInfo</code>, dessen Datei gelöscht werden soll
     * @param pfad Pfad zur zu löschenden Datei
     */
    private void löscheMailInfo(final MailInfo info, final String pfad) {
        final String id = info.getID();
        final String dateiname = id.replace(">", "").replace("<", "");
        final String strPfad = String.format(MAIL_PATTERN, address.getAddress(), pfad, dateiname);
        final File zielDatei = new File(strPfad).getAbsoluteFile();

        zielDatei.delete();
    }

    /**
     * Prüft, ob mit den Daten der <code>MailAccount</code>-Instanz eine
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
     * Prüft, ob mit den übergebenen Daten eine erfolgreiche Verbindung zum
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
     * Gibt die <code>MailServer</code>-Instanz zum Empfangen von Mails zurück
     *
     * @return <code>MailServer</code> zum Empfangen von Mails
     */
    public EmpfangsServer getIncomingMailServer() {
        return incomingMailServer;
    }

    /**
     * Gibt die <code>MailServer</code>-Instanz zum Versandt von Mails zurück
     *
     * @return <code>MailServer</code> zum Versandt von Mails
     */
    public SendServer getOutgoingMailServer() {
        return outgoingMailServer;
    }

    /**
     * Gibt die Mailadresse des MailAccounts zurück
     *
     * @return Mailadresse des MailAccounts
     */
    public InternetAddress getAddress() {
        return address;
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
    public void setPasswort(String password) throws AuthenticationFailedException {
        if (!validieren(user, password)) {
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
