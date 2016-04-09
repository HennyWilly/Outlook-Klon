package de.outlookklon.logik;

import de.outlookklon.logik.User.MailChecker;
import de.outlookklon.logik.mailclient.MailInfo;
import java.util.EventObject;

/**
 * Enth�lt alle relevanten Daten �ber eine neu erhaltene Mail
 *
 * @author Hendrik Karwanni
 */
public class NewMailEvent extends EventObject {

    private static final long serialVersionUID = 6139379408716473287L;

    private final String folder;
    private final MailInfo info;

    /**
     * Erstellt eine neue Instanz der Klasse mit den �bergebenen Werten
     *
     * @param sender Objekt, das das Event urspr�nglich ausgel�st hat
     * @param folder Pfad zu Ordner
     * @param info Infos zur Mail
     */
    public NewMailEvent(MailChecker sender, String folder, MailInfo info) {
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
     * @return MailInfo-Objekt
     */
    public MailInfo getInfo() {
        return info;
    }
}
