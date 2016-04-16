package de.outlookklon.logik.mailclient.checker;

import de.outlookklon.logik.mailclient.StoredMailInfo;
import java.util.EventObject;

/**
 * Enth�lt alle relevanten Daten �ber eine neu erhaltene Mail
 *
 * @author Hendrik Karwanni
 */
public class NewMailEvent extends EventObject {

    private static final long serialVersionUID = 6139379408716473287L;

    private final String folder;
    private final StoredMailInfo info;

    /**
     * Erstellt eine neue Instanz der Klasse mit den �bergebenen Werten
     *
     * @param sender Objekt, das das Event urspr�nglich ausgel�st hat
     * @param folder Pfad zu Ordner
     * @param info Infos zur Mail
     */
    public NewMailEvent(MailAccountChecker sender, String folder, StoredMailInfo info) {
        super(sender);

        this.folder = folder;
        this.info = info;
    }

    /**
     * Gibt den Ordnerpfad zur�ck, in dem die neue Mail gefunden wurde
     *
     * @return Ordnerpfad der Mail
     */
    public String getFolder() {
        return folder;
    }

    /**
     * Gibt das Info-Objekt zur Mail zur�ck
     *
     * @return StoredMailInfo-Objekt
     */
    public StoredMailInfo getInfo() {
        return info;
    }
}
