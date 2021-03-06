package de.outlook_klon.logik;

import java.util.EventObject;

import de.outlook_klon.logik.Benutzer.MailChecker;
import de.outlook_klon.logik.mailclient.MailInfo;

/**
 * Enth�lt alle relevanten Daten �ber eine neu erhaltene Mail
 * 
 * @author Hendrik Karwanni
 */
public class NewMailEvent extends EventObject {
	private static final long serialVersionUID = 6139379408716473287L;

	private String folder;
	private MailInfo info;

	/**
	 * Erstellt eine neue Instanz der Klasse mit den �bergebenen Werten
	 * 
	 * @param sender
	 *            Objekt, das das Event urspr�nglich ausgel�st hat
	 * @param account
	 *            MailAccount
	 * @param folder
	 *            Pfad zu Ordner
	 * @param info
	 *            Infos zur Mail
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
