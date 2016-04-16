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
     * Erzeugt ein neues MailChecker-Objekt f�r den �bergebenen Account
     *
     * @param account MailAccount, der abgeh�rt werden soll
     */
    public MailAccountChecker(@NonNull MailAccount account) {
        this.account = account;
        this.mails = new HashSet<>();
        this.listenerList = new ArrayList<>();
    }

    /**
     * Registriert einen neuen NewMailListener f�r Events innerhalb der Klasse
     *
     * @param mcl Neuer NewMailListener
     */
    public void addNewMessageListener(@NonNull NewMailListener mcl) {
        synchronized (listenerList) {
            listenerList.add(mcl);
        }
    }

    /**
     * Feuert ein neues NewMessageEvent f�r die �bergebene StoredMailInfo an
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
     * Gibt den internen MailAccount zur�ck
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
                // Pausiere f�r eine gegebene Zeit
                Thread.sleep(SLEEP_TIME);

                // HashSet, da oft darin gesucht wird (s.u.)
                Set<StoredMailInfo> tmpSet = new HashSet<>();

                // F�lle mit abgefragten MailInfos
                StoredMailInfo[] mailTmp = account.getMessages(FOLDER);
                tmpSet.addAll(Arrays.asList(mailTmp));

                // Pr�fe, ob eine bekannte StoredMailInfo weggefallen ist
                Iterator<StoredMailInfo> iterator = mails.iterator();
                while (iterator.hasNext()) {
                    StoredMailInfo current = iterator.next();

                    if (!tmpSet.contains(current)) {
                        // Entferne weggefallene StoredMailInfo aus dem Speicher
                        iterator.remove();
                    }
                }

                synchronized (mails) {
                    for (StoredMailInfo info : mailTmp) {
                        if (mails.add(info)) {
                            // Wird eine neue StoredMailInfo hinzugef�gt, werden
                            // die Listener benachrichtigt
                            fireNewMessageEvent(info);
                        }
                    }
                }
            } catch (InterruptedException ex) {
                // Bricht die Ausf�hrung ab
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
     * MailInfos aus dem Speicher des MailCheckers zur�ckgegeben; sonst wird die
     * getMessages-Methode des internen MailAccount-Objekts aufgerufen
     *
     * @param path Zu durchsuchender Ordnerpfad
     * @return MailInfos des gesuchten Ordners
     * @throws javax.mail.MessagingException wenn die Nachrichten nicht
     * abgerufen werden konnten
     * @throws de.outlookklon.dao.DAOException wenn das Laden der Nachrichten
     * fehlschl�gt
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
     * Entfernt die �bergebenen MailInfos aus dem Speicher
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
}
