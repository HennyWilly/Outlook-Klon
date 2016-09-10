package de.outlookklon.logik.mailclient.checker;

import de.outlookklon.dao.DAOException;
import de.outlookklon.logik.mailclient.MailAccount;
import de.outlookklon.logik.mailclient.StoredMailInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.mail.FolderNotFoundException;
import javax.mail.MessagingException;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Diese Klasse dient zum automatischen, intervallweisen Abfragen des
 * Posteingangs eines MailAccounts.
 */
public class MailAccountChecker extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailAccountChecker.class);

    private static final int SLEEP_TIME = 60000;
    private static final String FOLDER = "INBOX";
    private final MailAccount account;
    private final Set<StoredMailInfo> mails;
    private final List<NewMailListener> listenerList;

    /**
     * Erzeugt ein neues MailChecker-Objekt für den übergebenen Account
     *
     * @param account MailAccount, der abgehört werden soll
     */
    public MailAccountChecker(@NonNull MailAccount account) {
        this.account = account;
        this.mails = new HashSet<>();
        this.listenerList = new ArrayList<>();
    }

    /**
     * Registriert einen neuen NewMailListener für Events innerhalb der Klasse
     *
     * @param mcl Neuer NewMailListener
     */
    public void addNewMessageListener(@NonNull NewMailListener mcl) {
        synchronized (listenerList) {
            listenerList.add(mcl);
        }
    }

    /**
     * Feuert ein neues NewMessageEvent für die übergebene StoredMailInfo an
     * alle registrierten Listener
     *
     * @param info StoredMailInfo-Objekt, aus dem das Event erzeugt wird
     */
    private void fireNewMessageEvent(StoredMailInfo info) {
        NewMailEvent ev = new NewMailEvent(this, FOLDER, info);

        synchronized (listenerList) {
            for (NewMailListener listener : listenerList) {
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
        initiallyQueryInbox();

        while (true) {
            try {
                // Pausiere für eine gegebene Zeit
                Thread.sleep(SLEEP_TIME);

                // HashSet, da oft darin gesucht wird (s.u.)
                Set<StoredMailInfo> tmpSet = new HashSet<>(Arrays.asList(account.getMessages(FOLDER)));

                checkForRemovedMails(tmpSet);
                checkForNewMails(tmpSet);
            } catch (InterruptedException ex) {
                // Bricht die Ausführung ab
                LOGGER.error("Thread interrupted", ex);
                break;
            } catch (FolderNotFoundException ex) {
                // Ignorieren, da INBOX immer vorhanden sein sollte!
                LOGGER.error("INBOX not found", ex);
            } catch (MessagingException | DAOException ex) {
                LOGGER.error("While getting messages", ex);
            }
        }
    }

    private void checkForRemovedMails(Set<StoredMailInfo> querriedInfos) {
        // Prüfe, ob eine bekannte StoredMailInfo weggefallen ist
        Iterator<StoredMailInfo> iterator = mails.iterator();
        while (iterator.hasNext()) {
            StoredMailInfo current = iterator.next();

            if (!querriedInfos.contains(current)) {
                // Entferne weggefallene StoredMailInfo aus dem Speicher
                iterator.remove();
            }
        }
    }

    private void checkForNewMails(Set<StoredMailInfo> querriedInfos) {
        synchronized (mails) {
            for (StoredMailInfo info : querriedInfos) {
                if (mails.add(info)) {
                    // Wird eine neue StoredMailInfo hinzugefügt, werden
                    // die Listener benachrichtigt
                    fireNewMessageEvent(info);
                }
            }
        }
    }

    /**
     * Erstmaliges Abfragen des Posteingangs
     */
    private void initiallyQueryInbox() {
        synchronized (mails) {
            try {
                for (StoredMailInfo info : account.getMessages(FOLDER)) {
                    mails.add(info);
                    if (!info.isRead()) {
                        fireNewMessageEvent(info);
                    }
                }
            } catch (FolderNotFoundException ex) {
                // Ignorieren, da INBOX immer vorhanden sein sollte!
                LOGGER.error("INBOX not found", ex);
            } catch (MessagingException | DAOException ex) {
                LOGGER.error("Error while getting messages", ex);
            }
        }
    }

    /**
     * Wenn der Thread aktiv und der gesuchte Ordner "INBOX" ist, werden die
     * MailInfos aus dem Speicher des MailCheckers zurückgegeben; sonst wird die
     * getMessages-Methode des internen MailAccount-Objekts aufgerufen
     *
     * @param path Zu durchsuchender Ordnerpfad
     * @return MailInfos des gesuchten Ordners
     * @throws javax.mail.MessagingException wenn die Nachrichten nicht
     * abgerufen werden konnten
     * @throws de.outlookklon.dao.DAOException wenn das Laden der Nachrichten
     * fehlschlägt
     */
    public StoredMailInfo[] getMessages(@NonNull String path)
            throws MessagingException, DAOException {
        boolean threadOK = this.isAlive() && !this.isInterrupted();

        StoredMailInfo[] array;
        if (threadOK && path.equalsIgnoreCase(FOLDER)) {
            synchronized (mails) {
                array = mails.toArray(new StoredMailInfo[mails.size()]);
            }
        } else {
            array = account.getMessages(path);
        }

        return array;
    }

    /**
     * Entfernt die übergebenen MailInfos aus dem Speicher
     *
     * @param infos Zu entfernende MailInfos
     */
    public void removeMailInfos(@NonNull StoredMailInfo[] infos) {
        synchronized (mails) {
            for (StoredMailInfo info : infos) {
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

        if (other instanceof MailAccountChecker) {
            MailAccountChecker checker = (MailAccountChecker) other;
            return this.getAccount().equals(checker.getAccount());
        }
        if (other instanceof MailAccount) {
            MailAccount mailAccount = (MailAccount) other;
            return this.getAccount().equals(mailAccount);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getAccount().hashCode();
    }
}
