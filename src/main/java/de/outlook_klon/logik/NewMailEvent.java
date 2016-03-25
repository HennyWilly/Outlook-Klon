package de.outlook_klon.logik;

import de.outlook_klon.logik.Benutzer.MailChecker;
import de.outlook_klon.logik.mailclient.MailInfo;
import java.util.EventObject;

/**
 * Enthält alle relevanten Daten über eine neu erhaltene Mail
 *
 * @author Hendrik Karwanni
 */
public class NewMailEvent extends EventObject {

    private static final long serialVersionUID = 6139379408716473287L;

    private final String folder;
    private final MailInfo info;

    /**
     * Erstellt eine neue Instanz der Klasse mit den übergebenen Werten
     *
     * @param sender Objekt, das das Event ursprünglich ausgelöst hat
     * @param folder Pfad zu Ordner
     * @param info Infos zur Mail
     */
    public NewMailEvent(MailChecker sender, String folder, MailInfo info) {
        super(sender);

        this.folder = folder;
        this.info = info;
    }

    /**
     * Gibt den Ordnerpfad zurück, in dem die neue Mail gefunden wurde
     *
     * @return Ordnerpfad der Mail
     */
    public String getFolder() {
        return folder;
    }

    /**
     * Gibt das Info-Objekt zur Mail zurück
     *
     * @return MailInfo-Objekt
     */
    public MailInfo getInfo() {
        return info;
    }
}
