package de.outlook_klon.logik.mailclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.StringTerm;

import com.sun.mail.imap.IMAPFolder;

import de.outlook_klon.logik.Benutzer.MailChecker;

/**
 * Diese Klasse stellt ein Mailkonto dar. Hierüber können Mails gesendet und
 * empfangen werden.
 * 
 * @author Hendrik Karwanni
 */
public class MailAccount implements Serializable {
	private static final long serialVersionUID = -6324237474768366352L;

	private static final String MAIL_PATTERN = "Mail/%s/%s/%s.mail";

	private EmpfangsServer inServer;
	private SendServer outServer;

	private InternetAddress adresse;
	private String benutzer;
	private String passwort;

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
		 * @param messageID
		 *            ID der zu suchenden Mail
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
				
				if(tmpId != null && tmpId.length == 1) {
					id = tmpId[0];
				}
				else if (message instanceof MimeMessage) {
					id = ((MimeMessage) message).getMessageID();
				}
				
				if (id != null)
					result = super.match(id);
			} catch (MessagingException ex) {

			}

			return result;
		}

	}

	/**
	 * Erstellt eine neue Instanz der Klasse Mailkonto mit den übergebenen
	 * Parametern
	 * 
	 * @param inServer
	 *            Server-Instanz, die zum Empfangen von Mails verwendet wird
	 * @param outServer
	 *            Server-Instanz, die zum Senden von Mails verwendet wird
	 * @param adresse
	 *            E-Mail-Adresse, das dem Konto zugeordnet ist
	 * @param benutzer
	 *            Benutzername, der zur Anmeldung verwendet werden soll
	 * @param passwort
	 *            Passwort, das zur Anmeldung verwendet werden soll
	 * @throws NullPointerException
	 *             Tritt auf, wenn mindestens eine der Server-Instanzen null ist
	 * @throws IllegalArgumentException
	 *             Tritt auf, wenn die übergebene Mailadresse ungültig ist
	 */
	public MailAccount(final EmpfangsServer inServer,
			final SendServer outServer, final InternetAddress adresse,
			final String benutzer, final String passwort)
			throws NullPointerException, IllegalArgumentException {
		if (inServer == null || outServer == null)
			throw new NullPointerException(
					"Die übergebenen Server dürfen nicht <null> sein");

		this.inServer = inServer;
		this.outServer = outServer;

		this.adresse = adresse;
		this.benutzer = benutzer;
		this.passwort = passwort;
	}

	@Override
	public String toString() {
		return adresse.toUnicodeString();
	}

	/**
	 * Sendet eine Nachricht an einen Mailserver
	 * 
	 * @param to
	 *            Ziele der Mail
	 * @param cc
	 *            CCs der Mail
	 * @param subject
	 *            Betreff der Mail
	 * @param text
	 *            Text der Mail
	 * @throws MessagingException
	 *             Tritt auf, wenn der Sendevorgang fehlgeschlagen ist
	 */
	public void sendeMail(final InternetAddress[] to,
			final InternetAddress[] cc, final String subject,
			final String text, final String format, final File[] attachment)
			throws MessagingException {
		try {
			Message gesendet = outServer.sendeMail(benutzer, passwort, adresse,
					to, cc, subject, text, format, attachment);
			if (gesendet != null && !(inServer instanceof Pop3Server)) {
				//TODO Testen!
				Store mailStore = inServer.getMailStore(benutzer, passwort);
				mailStore.connect(inServer.settings.getHost(),
						inServer.settings.getPort(), benutzer, passwort);
				Folder sendFolder = null;

				final Folder[] folders = mailStore.getDefaultFolder().list("*");

				outer: for (final Folder folder : folders) {
					final IMAPFolder imap = (IMAPFolder) folder;
					final String[] attr = imap.getAttributes();

					if (imap.getName().equalsIgnoreCase("sent") || imap.getName().equalsIgnoreCase("gesendet"))
						sendFolder = imap;

					for (int i = 0; i < attr.length; i++) {
						if (attr[i].equalsIgnoreCase("\\Sent")) {
							sendFolder = imap;
							break outer;
						}
					}
				}

				if (sendFolder != null) {
					sendFolder.appendMessages(new Message[] { gesendet });
				}
			}
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}

	/**
	 * Gibt die Pfade aller Ordner des Servers zum Mailempfang zurück
	 * 
	 * @return Pfade aller Ordner des Servers zum Mailempfang
	 */
	public OrdnerInfo[] getOrdnerstruktur() {
		OrdnerInfo[] paths = null;

		Store store = null;
		try {
			store = inServer.getMailStore(benutzer, passwort);
			store.connect(inServer.settings.getHost(),
					inServer.settings.getPort(), benutzer, passwort);
			final Folder[] folders = store.getDefaultFolder().list("*");

			paths = new OrdnerInfo[folders.length];
			for (int i = 0; i < paths.length; i++) {
				Folder folder = folders[i];
				/*int msgCount;
				try {
					msgCount = folder.getUnreadMessageCount(); 
				} catch (MessagingException ex) {
					msgCount = 0;
				}*/

				paths[i] = new OrdnerInfo(folder.getName(),
						folder.getFullName(), 0);
			}

			store.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (store != null && store.isConnected())
				try {
					store.close();
				} catch (MessagingException e) {
				}
		}

		return paths;
	}

	/**
	 * Gibt die ID zur übergebenen Mail zurück
	 * 
	 * @param message
	 *            Mail, für die die ID bestimmt werden soll
	 * @return ID der Mail, oder <code>null</code>, wenn nicht gefunden
	 */
	private String getID(Message message) throws MessagingException {
		String[] tmpID = message.getHeader("Message-ID");
		if (tmpID != null && tmpID.length > 0)
			return tmpID[0];

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
	 * @param pfad
	 *            Pfad, in dem die Mails gesucht werden.
	 * @return Array von MailInfos mit der ID, Betreff, Sender und SendDatum
	 * @throws FolderNotFoundException
	 *             Tritt auf, wenn ein Ordner nicht gefunden werden konnte
	 */
	public MailInfo[] getMessages(final String pfad) throws FolderNotFoundException {
		HashSet<MailInfo> set = new HashSet<MailInfo>();

		Store store = null;
		try {
			store = inServer.getMailStore(benutzer, passwort);
			store.connect(inServer.settings.getHost(),
					inServer.settings.getPort(), benutzer, passwort);
			final Folder folder = store.getFolder(pfad);
			folder.open(Folder.READ_ONLY);

			final Message[] messages = folder.getMessages();
			FetchProfile fp = new FetchProfile();
			fp.add("Message-Id");
			folder.fetch(messages, fp);
			
			for (int i = 0; i < messages.length; i++) {
				final Message message = messages[i];

				String id = getID(message);
				if (id == null)
					continue;

				final String dateiname = id.replace(">", "").replace("<", "");
				final String strPfad = String.format(MAIL_PATTERN,
						adresse.getAddress(), pfad, dateiname);
				final File lokalerPfad = new File(strPfad).getAbsoluteFile();

				MailInfo tmp = ladeMailInfo(lokalerPfad);
				if (tmp == null) {
					final boolean read = message.isSet(Flag.SEEN);
					final String subject = message.getSubject();
					final Address from = message.getFrom()[0];
					final Date sendDate = message.getSentDate();

					tmp = new MailInfo(id);
					tmp.setRead(read);
					tmp.setSubject(subject);
					tmp.setSender(from);
					tmp.setDate(sendDate);

					speichereMailInfo(tmp, pfad);
				}

				set.add(tmp);
			}

			folder.close(true);
		} catch (FolderNotFoundException e) {
			throw e;
		} catch (Exception e) {

		} finally {
			if (store != null && store.isConnected())
				try {
					store.close();
				} catch (MessagingException e) {
				}
		}

		return set.toArray(new MailInfo[set.size()]);
	}

	/**
	 * Durchsucht den übergebenen <code>Part</code> nach dem Text der E-Mail
	 * 
	 * @param p
	 *            <code>Part</code>-Objekt, indem der Text gesucht werden soll
	 * @return Text der E-Mail
	 */
	private String getText(final Part p) throws MessagingException, IOException {
		if (p.isMimeType("text/*")) {
			return (String) p.getContent();
		}

		if (p.isMimeType("multipart/alternative")) {
			final Multipart mp = (Multipart) p.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
				final Part bp = mp.getBodyPart(i);
				if (bp.isMimeType("text/plain")) {
					if (text == null)
						text = getText(bp);
					continue;
				} else if (bp.isMimeType("text/html")) {
					final String s = getText(bp);
					if (s != null)
						return s;
				} else
					return getText(bp);
			}
			return text;
		} else if (p.isMimeType("multipart/*")) {
			final Multipart mp = (Multipart) p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				final String s = getText(mp.getBodyPart(i));
				if (s != null)
					return s;
			}
		}

		return null;
	}

	/**
	 * Durchsucht den übergebenen <code>Part</code> nach dem ContentType der
	 * E-Mail
	 * 
	 * @param p
	 *            <code>Part</code>-Objekt, indem der Text gesucht werden soll
	 * @return ContentType der E-Mail
	 */
	private String getTyp(final Part p) throws IOException, MessagingException {
		if (p.isMimeType("text/*"))
			return p.getContentType();

		final Object content = p.getContent();
		if (content instanceof Multipart) {
			final Multipart mp = (Multipart) content;
			for (int i = 0; i < mp.getCount(); i++) {
				final BodyPart bp = mp.getBodyPart(i);
				if (bp.getDisposition() == Part.ATTACHMENT)
					continue;

				return getTyp(bp);
			}
		}
		return "text/plain";
	}

	/**
	 * Liest den Text zur E-Mail mit der übergebenen ID in die übergebene
	 * <code>MailInfo</code> ein
	 * 
	 * @param pfad
	 *            Ordnerpfad innerhalb des MailServers
	 * @param messageInfo
	 *            Zu füllende <code>MailInfo</code>
	 */
	public void getMessageText(final String pfad, final MailInfo messageInfo)
			throws MessagingException {
		if (messageInfo == null || messageInfo.getID() == null)
			throw new NullPointerException("Übergebene MailInfo ist NULL");

		if (messageInfo.getText() != null
				&& messageInfo.getContentType() != null)
			return;

		Store store = null;

		try {
			store = inServer.getMailStore(benutzer, passwort);
			store.connect(inServer.settings.getHost(),
					inServer.settings.getPort(), benutzer, passwort);

			final Folder folder = store.getFolder(pfad);
			folder.open(Folder.READ_WRITE);

			final Message message = infoToMessage(messageInfo, folder);

			if (message != null) {
				if (messageInfo.getText() == null)
					messageInfo.setText(getText(message));
				if (messageInfo.getContentType() == null)
					messageInfo.setContentType(getTyp(message));
				if (!messageInfo.isRead()) {
					message.setFlag(Flag.SEEN, true);
					messageInfo.setRead(true);
				}

				speichereMailInfo(messageInfo, pfad);
			}

			folder.close(true);
		} catch (IOException ex) {
			// Not auto-generated catch-block
		} finally {
			if (store != null && store.isConnected())
				store.close();
		}
	}

	/**
	 * Liest alle Daten zur E-Mail mit der übergebenen ID in die übergebene
	 * <code>MailInfo</code> ein
	 * 
	 * @param pfad
	 *            Ordnerpfad innerhalb des MailServers
	 * @param messageInfo
	 *            Zu füllende <code>MailInfo</code>
	 */
	public void getWholeMessage(final String pfad, final MailInfo messageInfo)
			throws MessagingException {
		if (messageInfo.getText() != null
				&& messageInfo.getContentType() != null
				&& messageInfo.getSubject() != null
				&& messageInfo.getSender() != null
				&& messageInfo.getDate() != null && messageInfo.getTo() != null
				&& messageInfo.getCc() != null
				&& messageInfo.getAttachment() != null)
			return;

		Store store = null;

		try {
			store = inServer.getMailStore(benutzer, passwort);
			store.connect(inServer.settings.getHost(),
					inServer.settings.getPort(), benutzer, passwort);

			final Folder folder = store.getFolder(pfad);
			folder.open(Folder.READ_WRITE);

			final Message message = infoToMessage(messageInfo, folder);
			if (message != null) {
				if (messageInfo.getText() == null)
					messageInfo.setText(getText(message));
				if (messageInfo.getContentType() == null)
					messageInfo.setContentType(getTyp(message));
				if (messageInfo.getSubject() == null)
					messageInfo.setSubject(message.getSubject());
				if (messageInfo.getSender() == null)
					messageInfo.setSender(message.getFrom()[0]);
				if (messageInfo.getDate() == null)
					messageInfo.setDate(message.getSentDate());
				if (messageInfo.getTo() == null) {
					Address[] to = message.getRecipients(RecipientType.TO);
					if (to == null)
						to = new Address[0];
					messageInfo.setTo(to);
				}
				if (messageInfo.getCc() == null) {
					Address[] cc = message.getRecipients(RecipientType.CC);
					if (cc == null)
						cc = new Address[0];
					messageInfo.setCc(cc);
				}
				if (!messageInfo.isRead()) {
					message.setFlag(Flag.SEEN, true);
					messageInfo.setRead(true);
				}
				if (messageInfo.getAttachment() == null) {
					final ArrayList<String> attachment = new ArrayList<String>();
					if (message.getContent() instanceof Multipart) {
						final Multipart mp = (Multipart) message.getContent();

						for (int i = 0; i < mp.getCount(); i++) {
							final BodyPart bp = mp.getBodyPart(i);
							final String filename = bp.getFileName();

							if (filename != null && !filename.isEmpty())
								attachment.add(bp.getFileName());
						}
					}

					messageInfo.setAttachment(attachment
							.toArray(new String[attachment.size()]));
				}

				speichereMailInfo(messageInfo, pfad);
			}

			folder.close(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (store != null && store.isConnected())
				store.close();
		}
	}

	/**
	 * Gibt das <code>Message</code>-Objekt zur ID in der übergebenen
	 * <code>MailInfo</code> im übergebenen Ordner zurück.
	 * 
	 * @param mail
	 *            <code>MailInfo</code>-Objekt, das die ID zur suchenden Message
	 *            enthällt
	 * @param ordner
	 *            Ordner, in dem gesucht werden soll
	 * @return <code>Message</code>-Objekt zur übergebenen ID
	 */
	private Message infoToMessage(final MailInfo mail, final Folder ordner)
			throws MessagingException {

		Message[] result = infoToMessage(new MailInfo[] { mail }, ordner);
		return result == null || result.length == 0 ? null : result[0];
	}

	/**
	 * Gibt die <code>Message</code>-Objekte zu den IDs in den übergebenen
	 * MailInfos im übergebenen Ordner zurück.
	 * 
	 * @param mail
	 *            <code>MailInfo</code>-Objekte, die die IDs zu den zu suchenden
	 *            Messages enthallten
	 * @param ordner
	 *            Ordner, in dem gesucht werden soll
	 * @return <code>Message</code>-Objekte zu den übergebenen IDs
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
			if (tmpMessages.length == 0)
				tmpMessages = ordner.search(new MyMessageIDTerm(id));
			
			messages[i] = tmpMessages.length == 0 ? null : tmpMessages[0];
		}

		return messages;
	}

	/**
	 * Kopiert die übergebenen Mails in den Zielordner
	 * 
	 * @param mails
	 *            MailInfos, die die IDs der zu kopierenden Messages enthalten
	 * @param quellOrdner
	 *            Quellordner
	 * @param zielOrdner
	 *            Zielordner
	 * @param löschen
	 *            Wert, der angibt, ob die Mails nach dem Kopieren im
	 *            Quellordner gelöscht werden sollen
	 */
	private void kopieren(final MailInfo[] mails, final Folder quellOrdner,
			final Folder zielOrdner, final boolean löschen)
			throws MessagingException {
		final Message[] messages = infoToMessage(mails, quellOrdner);
		final String quellPfad = quellOrdner.getFullName();
		final String zielPfad = zielOrdner.getFullName();

		quellOrdner.copyMessages(messages, zielOrdner);
		for (int i = 0; i < messages.length; i++) {
			try {
				speichereMailInfo(mails[i], zielPfad);
			} catch (IOException e) {
			}

			if (löschen) {
				if (!messages[i].isExpunged())
					messages[i].setFlag(Flags.Flag.DELETED, true);
				löscheMailInfo(mails[i], quellPfad);
			}
		}

		if (löschen)
			quellOrdner.expunge();
	}

	/**
	 * Verschiebe die Mails vom Quell- in den Zielordner
	 * 
	 * @param mails
	 *            MailInfos der zu verschiebenen Mails
	 * @param quellPfad
	 *            Pfad zum Quellordner
	 * @param zielPfad
	 *            Pfad zum Zielordner
	 */
	public void verschiebeMails(final MailInfo[] mails, final String quellPfad,
			final String zielPfad) throws MessagingException {
		Store mailStore = null;

		try {
			mailStore = inServer.getMailStore(benutzer, passwort);
			mailStore.connect(inServer.settings.getHost(),
					inServer.settings.getPort(), benutzer, passwort);

			final Folder quellOrdner = mailStore.getFolder(quellPfad);
			quellOrdner.open(Folder.READ_WRITE);
			final Folder zielOrdner = mailStore.getFolder(zielPfad);
			zielOrdner.open(Folder.READ_WRITE);

			kopieren(mails, quellOrdner, zielOrdner, true);
		} finally {
			if (mailStore != null && mailStore.isConnected()) {
				try {
					mailStore.close();
				} catch (MessagingException e) {
				}
			}
		}
	}

	/**
	 * Kopiere die Mails vom Quell- in den Zielordner
	 * 
	 * @param mails
	 *            MailInfos der zu kopieren Mails
	 * @param quellPfad
	 *            Pfad zum Quellordner
	 * @param zielPfad
	 *            Pfad zum Zielordner
	 */
	public void kopiereMails(final MailInfo[] mails, final String quellPfad,
			final String zielPfad) throws MessagingException {
		Store mailStore = null;

		try {
			mailStore = inServer.getMailStore(benutzer, passwort);
			mailStore.connect(inServer.settings.getHost(),
					inServer.settings.getPort(), benutzer, passwort);

			final Folder quellOrdner = mailStore.getFolder(quellPfad);
			quellOrdner.open(Folder.READ_ONLY);
			final Folder zielOrdner = mailStore.getFolder(zielPfad);
			zielOrdner.open(Folder.READ_WRITE);

			kopieren(mails, quellOrdner, zielOrdner, false);
		} finally {
			if (mailStore != null && mailStore.isConnected()) {
				try {
					mailStore.close();
				} catch (MessagingException e) {
				}
			}
		}
	}

	/**
	 * Lösche die Mails aus dem übergebenen Ordner
	 * 
	 * @param mails
	 *            MailInfos der zu löschenden Mails
	 * @param pfad
	 *            Pfad zum Ordner
	 * @return true, wenn das löschen erfolgreich war; sonst false
	 */
	public boolean loescheMails(final MailInfo[] mails, final String pfad)
			throws MessagingException {
		boolean result = false;

		Store mailStore = null;

		try {
			mailStore = inServer.getMailStore(benutzer, passwort);
			mailStore.connect(inServer.settings.getHost(),
					inServer.settings.getPort(), benutzer, passwort);

			final Folder ordner = mailStore.getFolder(pfad);
			ordner.open(Folder.READ_WRITE);

			Folder binFolder = null;

			final Folder[] folders = mailStore.getDefaultFolder().list("*");

			if(!(inServer instanceof Pop3Server)) {
				outer: 
				for (final Folder folder : folders) {
					final IMAPFolder imap = (IMAPFolder) folder;
					final String[] attr = imap.getAttributes();
	
					if (imap.getName().equalsIgnoreCase("trash"))
						binFolder = imap;
	
					for (int i = 0; i < attr.length; i++) {
						if (attr[i].equalsIgnoreCase("\\Trash")) {
							binFolder = imap;
							break outer;
						}
					}
				}
			}

			if (binFolder != null) {
				final String binPfad = binFolder.getFullName();
				if (!pfad.equals(binPfad)) {
					kopieren(mails, ordner, binFolder, true);
					return true;
				}
			}
			final Message[] messages = infoToMessage(mails, ordner);
			for (final Message m : messages) {
				if (!m.isExpunged())
					m.setFlag(Flags.Flag.DELETED, true);
			}

			ordner.expunge();

			// TODO TESTEN!!!

			result = true;
		} finally {
			if (mailStore != null && mailStore.isConnected()) {
				try {
					mailStore.close();
				} catch (MessagingException e) {
				}
			}
		}

		return result;
	}

	/**
	 * Speichert den Anhang der übergebenen Mail am übergebenen Ort
	 * 
	 * @param mail
	 *            <code>MailInfo</code>-Objekt
	 * @param pfad
	 *            Ordnerpfad innerhalb des MailStores
	 * @param anhangName
	 *            Name des zu speichernden Anhangs
	 * @param zielPfad
	 *            Zielpfad, an dem die Datei gespeichert werden soll
	 * @throws IOException
	 *             Tritt auf, wenn die Datei nicht gespeichert werden konnte
	 * @throws MessagingException
	 *             Triff auf, wenn es einen Fehler bezüglich der Nachricht gab
	 */
	public void anhangSpeichern(final MailInfo mail, final String pfad,
			final String anhangName, final String zielPfad) throws IOException,
			MessagingException {
		Store mailStore = null;

		try {
			mailStore = inServer.getMailStore(benutzer, passwort);
			mailStore.connect(inServer.settings.getHost(),
					inServer.settings.getPort(), benutzer, passwort);

			final Folder ordner = mailStore.getFolder(pfad);
			ordner.open(Folder.READ_WRITE);

			final Message message = infoToMessage(mail, ordner);
			final String contentType = message.getContentType();
			if (contentType.contains("multipart")) {
				final Multipart multipart = (Multipart) message.getContent();
				for (int i = 0; i < multipart.getCount(); i++) {
					final MimeBodyPart part = (MimeBodyPart) multipart
							.getBodyPart(i);
					final String disposition = part.getDisposition();
					final String fileName = part.getFileName();

					if (Part.ATTACHMENT.equalsIgnoreCase(disposition)
							|| (fileName != null && !fileName.trim().isEmpty())) {
						if (anhangName.equals(fileName))
							part.saveFile(zielPfad);
					}
				}
			}
		} finally {
			if (mailStore != null && mailStore.isConnected())
				try {
					mailStore.close();
				} catch (MessagingException e) {
				}
		}
	}

	/**
	 * Speichert die übergebene <code>MailInfo</code> am übergebenen Pfad
	 * 
	 * @param info
	 *            Zu speichernde <code>MailInfo</code>
	 * @param pfad
	 *            Pfad, in dem das Objekt gespeichert werden soll
	 * @throws IOException
	 *             Tritt auf, wenn das Objekt nicht gespeichert werden konnte
	 */
	private void speichereMailInfo(final MailInfo info, final String pfad)
			throws IOException {
		final String id = info.getID();
		final String dateiname = id.replace(">", "").replace("<", "");
		final String strPfad = String.format(MAIL_PATTERN,
				adresse.getAddress(), pfad, dateiname);
		final File zielDatei = new File(strPfad).getAbsoluteFile();

		final File ordner = zielDatei.getParentFile();

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			if (!ordner.exists()) {
				ordner.mkdirs();
			}

			fos = new FileOutputStream(zielDatei);
			oos = new ObjectOutputStream(fos);

			oos.writeObject(info);
		} finally {
			if (oos != null)
				oos.close();
		}
	}

	/**
	 * Lese das <code>MailInfo</code>-Objekt aus der übergebenen Datei
	 * 
	 * @param datei
	 *            Enthällt den Pfad, in dem das Objekt liegt
	 * @return Deserialisiertes <code>MailInfo</code>-Objekt
	 * @throws ClassNotFoundException
	 *             Die Datei enthällt unbekannte Klassendaten
	 * @throws IOException
	 *             Die Datei konnte nicht gelesen werden
	 */
	private MailInfo ladeMailInfo(final File datei)
			throws ClassNotFoundException, IOException {
		MailInfo geladen = null;

		final File absDatei = datei.getAbsoluteFile();
		if (absDatei.exists()) {
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(absDatei);
				ois = new ObjectInputStream(fis);

				geladen = (MailInfo) ois.readObject();
			} finally {
				ois.close();
			}
		}

		return geladen;
	}

	/**
	 * Löscht die gespeicherte Datei der übergebenen <code>MailInfo</code>
	 * 
	 * @param info
	 *            <code>MailInfo</code>, dessen Datei gelöscht werden soll
	 * @param pfad
	 *            Pfad zur zu löschenden Datei
	 */
	private void löscheMailInfo(final MailInfo info, final String pfad) {
		final String id = info.getID();
		final String dateiname = id.replace(">", "").replace("<", "");
		final String strPfad = String.format(MAIL_PATTERN,
				adresse.getAddress(), pfad, dateiname);
		final File zielDatei = new File(strPfad).getAbsoluteFile();

		zielDatei.delete();
	}

	/**
	 * Prüft, ob mit den Daten der <code>MailAccount</code>-Instanz eine
	 * erfolgreiche Verbindung zum Empfangs- und zum Versandtserver hergestellt
	 * werden konnte
	 * 
	 * @return <code>true</code>, wenn die Verbindungen erfolgreich waren; sonst
	 *         <code>false</code>
	 */
	public boolean validieren() {
		return validieren(benutzer, passwort);
	}

	/**
	 * Prüft, ob mit den übergebenen Daten eine erfolgreiche Verbindung zum
	 * Empfangs- und zum Versandtserver hergestellt werden konnte
	 * 
	 * @return <code>true</code>, wenn die Verbindungen erfolgreich waren; sonst
	 *         <code>false</code>
	 */
	private boolean validieren(String user, String passwd) {
		boolean inValid = false;
		boolean outValid = false;

		if (inServer != null)
			inValid = inServer.pruefeLogin(user, passwd);

		if (outServer != null)
			outValid = outServer.pruefeLogin(user, passwd);

		return inValid && outValid;
	}

	/**
	 * Gibt die <code>MailServer</code>-Instanz zum Empfangen von Mails zurück
	 * 
	 * @return <code>MailServer</code> zum Empfangen von Mails
	 */
	public EmpfangsServer getEmpfangsServer() {
		return inServer;
	}

	/**
	 * Gibt die <code>MailServer</code>-Instanz zum Versandt von Mails zurück
	 * 
	 * @return <code>MailServer</code> zum Versandt von Mails
	 */
	public SendServer getSendServer() {
		return outServer;
	}

	/**
	 * Gibt die Mailadresse des MailAccounts zurück
	 * 
	 * @return Mailadresse des MailAccounts
	 */
	public InternetAddress getAdresse() {
		return adresse;
	}

	/**
	 * Gibt den Benutzernamen für den <code>MailAccount</code> zurück
	 * 
	 * @return Benutzername für den <code>MailAccount</code>
	 */
	public String getBenutzer() {
		return benutzer;
	}

	/**
	 * Versucht, das Passwort des Accounts neu zu setzen
	 * 
	 * @param passwd
	 *            Zu setzendes Passwort
	 * @throws AuthenticationFailedException
	 *             Tritt auf, wenn die Anmeldung mit dem Passwort fehlgeschlagen
	 *             ist
	 */
	public void setPasswort(String passwd) throws AuthenticationFailedException {
		if (!validieren(benutzer, passwd))
			throw new AuthenticationFailedException(
					"Das übergebene Passwort ist ungültig");

		this.passwort = passwd;
	}

	@Override
	public boolean equals(Object obj) {
		// Es dürfen keine MailAccounts hinzugefügt werden, deren MailAdresse
		// bereit enthalten ist

		if (obj == null)
			return false;
		if (this == obj)
			return true;

		if(obj instanceof MailAccount) {
			MailAccount acc = (MailAccount) obj;
			String thisAddress = this.adresse.getAddress();
			String accAddress = acc.adresse.getAddress();
			
			return thisAddress.equalsIgnoreCase(accAddress);
		}
		
		if(obj instanceof MailChecker) {
			MailChecker checker = (MailChecker)obj;
			return this.equals(checker.getAccount());
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		return this.adresse.getAddress().hashCode();
	}
}
