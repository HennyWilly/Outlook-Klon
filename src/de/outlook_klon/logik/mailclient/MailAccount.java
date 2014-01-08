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
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
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
import javax.mail.search.SearchTerm;

import com.sun.mail.imap.IMAPFolder;

/**
 * Diese Klasse stellt ein Mailkonto dar.
 * Hier�ber k�nnen Mails gesendet und empfangen werden.
 * 
 * @author Hendrik Karwanni
 */
public class MailAccount implements Serializable {
	private static final long serialVersionUID = -6324237474768366352L;
	
	private EmpfangsServer inServer;
	private SendServer outServer;
	
	private InternetAddress adresse;
	private String benutzer;
	private String passwort;
	
	/**
	 * Bei manchen Anbietern, z.B. Hotmail oder Yahoo, kann die MessageID nicht auf normalem Wege
	 * mit dem standardm��igen MessageIDTerm abgerufen werden.
	 * Daher wird hier ein neuer SeachTerm implementiert, der die Mails zuerst �ffnet
	 * und dann die ID ausliest.
	 * 
	 * @author Hendrik Karwanni
	 */
	private class MyMessageIDTerm extends SearchTerm {
		private static final long serialVersionUID = -298319831328120350L;
		private String messageID;
		
		public MyMessageIDTerm(final String messageID) {
			super();
			
			this.messageID = messageID;
		}
		
		@Override
		public boolean match(final Message message) {
			boolean result = false;
			
			try {
				if(message instanceof MimeMessage) {
					final MimeMessage mime = (MimeMessage)message;
					final String id = mime.getMessageID();
					if(id != null && id.equals(messageID))
						result = true;
				}
			} catch (MessagingException ex) { 
				
			} 
			
		    return result;
		}
		
	}
	
	/**
	 * Erstellt eine neue Instanz der Klasse Mailkonto mit den �bergebenen Parametern
	 * @param inServer Server-Instanz, die zum Empfangen von Mails verwendet wird
	 * @param outServer Server-Instanz, die zum Senden von Mails verwendet wird
	 * @param adresse E-Mail-Adresse, das dem Konto zugeordnet ist
	 * @param benutzer Benutzername, der zur Anmeldung verwendet werden soll
	 * @param passwort Passwort, das zur Anmeldung verwendet werden soll
	 * @throws NullPointerException Tritt auf, wenn mindestens eine der Server-Instanzen null ist
	 * @throws IllegalArgumentException Tritt auf, wenn die �bergebene Mailadresse ung�ltig ist
	 */
	public MailAccount(final EmpfangsServer inServer, final SendServer outServer, final InternetAddress adresse, final String benutzer, final String passwort) 
						throws NullPointerException, IllegalArgumentException {
		if(inServer == null || outServer == null)
			throw new NullPointerException("Die �bergebenen Server d�rfen nicht <null> sein");
		
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
	 * @param to Ziele der Mail
	 * @param cc CCs der Mail
	 * @param subject Betreff der Mail
	 * @param text Text der Mail
	 * @throws MessagingException Tritt auf, wenn der Sendevorgang fehlgeschlagen ist
	 */
	public void sendeMail(final InternetAddress[] to, final InternetAddress[] cc, final String subject, 
			final String text, final String format, final File[] attachment) throws MessagingException {
		try {
			outServer.sendeMail(benutzer, passwort, adresse, to, cc, subject, text, format, attachment);
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}
	
	/**
	 * Gibt die Pfade aller Ordner des Servers zum Mailempfang zur�ck
	 * @return Pfade aller Ordner des Servers zum Mailempfang
	 */
	public OrdnerInfo[] getOrdnerstruktur() {
		OrdnerInfo[] paths = null;
		
		Store store = null;
		try {
			store = inServer.getMailStore(benutzer, passwort);
			store.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			final Folder[] folders = store.getDefaultFolder().list("*");

			paths = new OrdnerInfo[folders.length];
			for(int i = 0; i < paths.length; i++) {
				Folder folder = folders[i];
				int msgCount;
				try {
					msgCount = folder.getNewMessageCount();
				} catch (MessagingException ex) {
					msgCount = 0;
				}
					
				paths[i] = new OrdnerInfo(folder.getName(), folder.getFullName(), msgCount);
			}
			
			store.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(store != null && store.isConnected())
				try {
					store.close();
				} catch (MessagingException e) { }
		}
		
		return paths;
	}
	
	private String getID(Message message) throws MessagingException {
		String id = null;
		
		if(message instanceof MimeMessage) {
			MimeMessage mime = (MimeMessage)message;
			
			id = mime.getMessageID();
			if(id == null) {
				id = mime.getContentID();
			}
		}
		else {
			String[] tmpID = message.getHeader("Message-ID");
			if(tmpID != null && tmpID.length > 0)
				id = tmpID[0];
		}	
		
		return id;
	}
	
	/**
	 * Gibt die MailInfos aller Messages in dem �bergebenen Pfad zur�ck.
	 * @param pfad Pfad, in dem die Mails gesucht werden.
	 * @return Array von MailInfos mit der ID, Betreff, Sender und SendDatum
	 */
	public MailInfo[] getMessages(final String pfad) {
		HashSet<MailInfo> set = new HashSet<MailInfo>();

		Store store = null;
		try {
			store = inServer.getMailStore(benutzer, passwort);
			store.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			final Folder folder = store.getFolder(pfad);
			folder.open(Folder.READ_ONLY);
			
			final Message[] messages = folder.getMessages();
			
			for(int i = 0; i < messages.length; i++) {
				final Message message = messages[i];
				
				String id = getID(message);
				if(id == null)
					continue;
				
				final String dateiname = id.replace(">", "").replace("<", "");
				final String strPfad = String.format("Mail/%s/%s/%s.mail", adresse.getAddress(), pfad, dateiname);
				final File lokalerPfad = new File(strPfad).getAbsoluteFile(); //TODO TESTEN!!!
				
				MailInfo tmp = ladeMailInfo(lokalerPfad);
				if(tmp == null) {
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
		} catch (Exception e) { 
			
		} finally {
			if(store != null && store.isConnected())
				try {
					store.close();
				} catch (MessagingException e) { }
		}
		
		return set.toArray(new MailInfo[set.size()]);
	}
	
	/**
	 * Durchsucht den �bergebenen Part nach dem Text der E-Mail
	 * @param p Part-Objekt, indem der Text gesucht werden soll
	 * @return Text der E-Mail
	 */
	private String getText(final Part p) throws MessagingException, IOException {
		if (p.isMimeType("text/*")) {
			return (String)p.getContent();
		}
		
		if (p.isMimeType("multipart/alternative")) {
			final Multipart mp = (Multipart)p.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
			    final Part bp = mp.getBodyPart(i);
			    if (bp.isMimeType("text/plain")) {
				    if (text == null)
				        text = getText(bp);
				    continue;
					} 
			    else if (bp.isMimeType("text/html")) {
			        final String s = getText(bp);
			        if (s != null)
			            return s;
				} 
				else 
					return getText(bp);
			}
			return text;
		} 
		else if (p.isMimeType("multipart/*")) {
			final Multipart mp = (Multipart)p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
			    final String s = getText(mp.getBodyPart(i));
			    if (s != null)
			        return s;
			}
		}
		
		return null;
	}
	
	/**
	 * Durchsucht den �bergebenen Part nach dem ContentType der E-Mail
	 * @param p Part-Objekt, indem der Text gesucht werden soll
	 * @return ContentType der E-Mail
	 */
	private String getTyp(final Part p) throws IOException, MessagingException {
		if(p.isMimeType("text/plain") || p.isMimeType("text/html"))
			return p.getContentType();
		
		final Object content = p.getContent();
		if (content instanceof Multipart) {
		    final Multipart mp = (Multipart) content;
		    for (int i = 0; i < mp.getCount(); i++) {
		        final BodyPart bp = mp.getBodyPart(i);
		        if(bp.getDisposition() == Part.ATTACHMENT)
		        	continue;
		        
		        return getTyp(bp);
		    }
		}
		return "text/plain";
	}
	
	/**
	 * Liest den Text zur E-Mail mit der �bergebenen ID in die �bergebene MailInfo ein 
	 * @param pfad Ordnerpfad innerhalb des MailServers
	 * @param messageInfo Zu f�llende MailInfo
	 */
	public void getMessageText(final String pfad, final MailInfo messageInfo) throws MessagingException {
		if(messageInfo == null || messageInfo.getID() == null)
			throw new NullPointerException("�bergebene MailInfo ist NULL");
		
		if(messageInfo.getText() != null && messageInfo.getContentType() != null)
			return;
		
		Store store = null;
		
		try {
			store = inServer.getMailStore(benutzer, passwort);
			store.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			
			final Folder folder = store.getFolder(pfad);
			folder.open(Folder.READ_WRITE);
			
			final Message message = infoToMessage(messageInfo, folder);
			
			if(message != null) {
				if(messageInfo.getText() == null)
					messageInfo.setText(getText(message));
				if(messageInfo.getContentType() == null)
					messageInfo.setContentType(getTyp(message));
				if(!messageInfo.isRead()) {
					message.setFlag(Flag.SEEN, true);
					messageInfo.setRead(true);
				}
				
				speichereMailInfo(messageInfo, pfad);
			}
			
			folder.close(true);
		} catch(IOException ex) {
			//Not auto-generated catch-block
		} finally {
			if(store != null && store.isConnected())
				store.close();
		}
	}
	
	/**
	 * Liest alle Daten zur E-Mail mit der �bergebenen ID in die �bergebene MailInfo ein 
	 * @param pfad Ordnerpfad innerhalb des MailServers
	 * @param messageInfo Zu f�llende MailInfo
	 */
	public void getWholeMessage(final String pfad, final MailInfo messageInfo) throws MessagingException {
		if(messageInfo.getText() != null && messageInfo.getContentType() != null && 
				messageInfo.getSubject() != null && messageInfo.getSender() != null && 
				messageInfo.getDate() != null && messageInfo.getTo() != null && 
				messageInfo.getCc() != null && messageInfo.getAttachment() != null) 
			return;
		
		Store store = null;
		
		try {
			store = inServer.getMailStore(benutzer, passwort);
			store.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			
			final Folder folder = store.getFolder(pfad);
			folder.open(Folder.READ_WRITE);
			
			final Message message = infoToMessage(messageInfo, folder);
			if(message != null) {
				if(messageInfo.getText() == null)
					messageInfo.setText(getText(message));
				if(messageInfo.getContentType() == null)
					messageInfo.setContentType(getTyp(message));
				if(messageInfo.getSubject() == null)
					messageInfo.setSubject(message.getSubject());
				if(messageInfo.getSender() == null) 
					messageInfo.setSender(message.getFrom()[0]);
				if(messageInfo.getDate() == null)
					messageInfo.setDate(message.getSentDate());
				if(messageInfo.getTo() == null)
				{
					Address[] to = message.getRecipients(RecipientType.TO);
					if(to == null)
						to = new Address[0];
					messageInfo.setTo(to);
				}
				if(messageInfo.getCc() == null) {
					Address[] cc = message.getRecipients(RecipientType.CC);
					if(cc == null)
						cc = new Address[0];
					messageInfo.setCc(cc);
				}
				if(!messageInfo.isRead()) {
					message.setFlag(Flag.SEEN, true);
					messageInfo.setRead(true);
				}
				if(messageInfo.getAttachment() == null) {
					final ArrayList<String> attachment = new ArrayList<String>();
					if(message.getContent() instanceof Multipart) {
						final Multipart mp = (Multipart)message.getContent();
						
						for(int i = 0; i < mp.getCount(); i++) {
							final BodyPart bp = mp.getBodyPart(i);
							final String filename = bp.getFileName();
							
							if(filename != null && !filename.isEmpty())
								attachment.add(bp.getFileName());
						}
					}
					
					messageInfo.setAttachment(attachment.toArray(new String[attachment.size()]));
				}
				
				speichereMailInfo(messageInfo, pfad);
			}
			
			folder.close(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(store != null && store.isConnected())
				store.close();
		}
	}
	
	/**
	 * Gibt das Message-Objekt zur ID in der �bergebenen MailInfo im �bergebenen Ordner zur�ck.
	 * @param mail MailInfo-Objekt, das die ID zur suchenden Message enth�llt
	 * @param ordner Ordner, in dem gesucht werden soll
	 * @return Message-Objekt zur �bergebenen ID
	 */
	private Message infoToMessage(final MailInfo mail, final Folder ordner) throws MessagingException {		
		final String id = mail.getID();
		
		Message[] tmpMessages = ordner.search(new MessageIDTerm(id));
		if(tmpMessages.length == 0)
			tmpMessages = ordner.search(new MyMessageIDTerm(id));
		
		return tmpMessages.length == 0 ? null : tmpMessages[0];
	}

	/**
	 * Gibt die Message-Objekte zu den IDs in den �bergebenen MailInfos im �bergebenen Ordner zur�ck.
	 * @param mail MailInfo-Objekte, die die IDs zu den zu suchenden Messages enthallten
	 * @param ordner Ordner, in dem gesucht werden soll
	 * @return Message-Objekte zu den �bergebenen IDs
	 */
	private Message[] infoToMessage(final MailInfo[] mails, final Folder ordner) throws MessagingException {
		Message[] messages = new Message[mails.length];
		
		for(int i = 0; i < mails.length; i++) {
			messages[i] = infoToMessage(mails[i], ordner);
		}
		
		return messages;
	}
	
	/**
	 * Kopiert die �bergebenen Mails in den Zielordner
	 * @param mails MailInfos, die die IDs der zu kopierenden Messages enthalten
	 * @param quellOrdner Quellordner
	 * @param zielOrdner Zielordner
	 * @param l�schen Wert, der angibt, ob die Mails nach dem Kopieren im Quellordner gel�scht werden sollen
	 */
	private void kopieren(final MailInfo[] mails, final Folder quellOrdner, final Folder zielOrdner, final boolean l�schen) 
			throws MessagingException {
		final Message[] messages = infoToMessage(mails, quellOrdner);
		final String quellPfad = quellOrdner.getFullName();
		final String zielPfad = zielOrdner.getFullName();
		
		quellOrdner.copyMessages(messages, zielOrdner);
		for(int i = 0; i < messages.length; i++) {
			try {
				speichereMailInfo(mails[i], zielPfad);
			} catch (IOException e) { }
			
			if(l�schen) {
				if(!messages[i].isExpunged())
					messages[i].setFlag(Flags.Flag.DELETED, true);
				l�scheMailInfo(mails[i], quellPfad);
			}
		}
		
		if(l�schen) 
			quellOrdner.expunge();
	}

	/**
	 * Verschiebe die Mails vom Quell- in den Zielordner
	 * @param mails MailInfos der zu verschiebenen Mails
	 * @param quellPfad Pfad zum Quellordner
	 * @param zielPfad Pfad zum Zielordner
	 */
	public void verschiebeMails(final MailInfo[] mails, final String quellPfad, final String zielPfad) throws MessagingException {
		Store mailStore = null;
		
		try {
			mailStore = inServer.getMailStore(benutzer, passwort);
			mailStore.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			
			final Folder quellOrdner = mailStore.getFolder(quellPfad);
			quellOrdner.open(Folder.READ_WRITE);
			final Folder zielOrdner = mailStore.getFolder(zielPfad);
			zielOrdner.open(Folder.READ_WRITE);
			
			kopieren(mails, quellOrdner, zielOrdner, true);
		} finally {
			if(mailStore != null && mailStore.isConnected()) {
				try {
					mailStore.close();
				} catch (MessagingException e) { }
			}
		}
	}

	/**
	 * Kopiere die Mails vom Quell- in den Zielordner
	 * @param mails MailInfos der zu kopieren Mails
	 * @param quellPfad Pfad zum Quellordner
	 * @param zielPfad Pfad zum Zielordner
	 */
	public void kopiereMails(final MailInfo[] mails, final String quellPfad, final String zielPfad) throws MessagingException {
		Store mailStore = null;
		
		try {
			mailStore = inServer.getMailStore(benutzer, passwort);
			mailStore.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			
			final Folder quellOrdner = mailStore.getFolder(quellPfad);
			quellOrdner.open(Folder.READ_ONLY);
			final Folder zielOrdner = mailStore.getFolder(zielPfad);
			zielOrdner.open(Folder.READ_WRITE);
			
			kopieren(mails, quellOrdner, zielOrdner, false);
		} finally {
			if(mailStore != null && mailStore.isConnected()) {
				try {
					mailStore.close();
				} catch (MessagingException e) { }
			}
		}
	}

	/**
	 * L�sche die Mails aus dem �bergebenen Ordner
	 * @param mails MailInfos der zu l�schenden Mails
	 * @param pfad Pfad zum Ordner
	 * @return true, wenn das l�schen erfolgreich war; sonst false
	 */
	public boolean loescheMails(final MailInfo[] mails, final String pfad) throws MessagingException {
		boolean result = false;
		
		Store mailStore = null;
		
		try {
			mailStore = inServer.getMailStore(benutzer, passwort);
			mailStore.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			
			final Folder ordner = mailStore.getFolder(pfad);
			ordner.open(Folder.READ_WRITE);
			
			Folder binFolder = null;
			
			final Folder[] folders = mailStore.getDefaultFolder().list("*");
			
			outer:
			for(final Folder folder : folders) {
				final IMAPFolder imap = (IMAPFolder)folder;
				final String[] attr = imap.getAttributes();
				
				if(imap.getName().toLowerCase().equals("trash"))
					binFolder = imap;
				
				for(int i = 0; i < attr.length; i++) {					
					if(attr[i].equals("\\Trash")){
						binFolder = imap;
						break outer;
					}
				}
			}
			
			if(binFolder != null) {
				final String binPfad = binFolder.getFullName();
				if(!pfad.equals(binPfad)) {
					kopieren(mails, ordner, binFolder, true);
					return true;
				}
			}
			final Message[] messages = infoToMessage(mails, ordner);
			for(final Message m : messages) {
				if(!m.isExpunged())
					m.setFlag(Flags.Flag.DELETED, true);
			}
			
			ordner.expunge();
			
			//TODO TESTEN!!!
			
			result = true;
		} finally {
			if(mailStore != null && mailStore.isConnected()) {
				try {
					mailStore.close();
				} catch (MessagingException e) { }
			}
		}
		
		return result;
	}
	
	/**
	 * Speichert den Anhang der �bergebenen Mail am �bergebenen Ort
	 * @param mail MailInfo-Objekt
	 * @param pfad Ordnerpfad innerhalb des MailStores
	 * @param anhangName Name des zu speichernden Anhangs
	 * @param zielPfad Zielpfad, an dem die Datei gespeichert werden soll
	 * @throws IOException Tritt auf, wenn die Datei nicht gespeichert werden konnte
	 * @throws MessagingException Triff auf, wenn es einen Fehler bez�glich der Nachricht gab
	 */
	public void anhangSpeichern(final MailInfo mail, final String pfad, final String anhangName, final String zielPfad) 
			throws IOException, MessagingException {
		Store mailStore = null;
		
		try {
			mailStore = inServer.getMailStore(benutzer, passwort);
			mailStore.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			
			final Folder ordner = mailStore.getFolder(pfad);
			ordner.open(Folder.READ_WRITE);
			
			final Message message = infoToMessage(mail, ordner);
			final String contentType = message.getContentType();
			if(contentType.contains("multipart")) {
				final Multipart multipart = (Multipart)message.getContent();
				for(int i = 0; i < multipart.getCount(); i++) {
					final MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
					final String disposition = part.getDisposition();
					final String fileName = part.getFileName();
					
				    if (Part.ATTACHMENT.equalsIgnoreCase(disposition) || (fileName != null && !fileName.trim().isEmpty())) {
				    	if(anhangName.equals(fileName))
				    		part.saveFile(zielPfad);
				    }
				}
			}
		} finally {
			if(mailStore != null && mailStore.isConnected())
				try {
					mailStore.close();
				} catch (MessagingException e) { }
		}
	}
	
	private void speichereMailInfo(final MailInfo info, final String pfad) throws IOException {
		final String id = info.getID();
		final String dateiname = id.replace(">", "").replace("<", "");
		final File zielDatei = new File("Mail/" + adresse.getAddress() + "/" + pfad + "/"  + dateiname + ".mail").getAbsoluteFile();
		
		final File ordner = zielDatei.getParentFile();
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			if(!ordner.exists()) {
				ordner.mkdirs();
			}
			
			fos = new FileOutputStream(zielDatei);
			oos = new ObjectOutputStream(fos);
			
			oos.writeObject(info);
		} 
		finally {
			if(oos != null)
				oos.close();
		}
	}
	
	private MailInfo ladeMailInfo(final File datei) throws ClassNotFoundException, IOException {
		MailInfo geladen = null;

		final File absDatei = datei.getAbsoluteFile();
		if(absDatei.exists()) {
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(absDatei);
				ois = new ObjectInputStream(fis);
				
				geladen = (MailInfo)ois.readObject();
			}
			finally {
				ois.close();
			}
		}
		
		return geladen;
	}
	
	private void l�scheMailInfo(final MailInfo info, final String pfad) {
		final String id = info.getID();
		final String dateiname = id.replace(">", "").replace("<", "");
		final File zielDatei = new File("Mail/" + adresse.getAddress() + "/" + pfad + "/"  + dateiname + ".mail").getAbsoluteFile();
		
		zielDatei.delete();
	}
	
	/**
	 * Pr�ft, ob mit den Daten der MailAccount-Instanz eine erfolgreiche Verbindung 
	 * zum Empfangs- und zum Versandtserver hergestellt werden konnte 
	 * @return true, wenn die Verbindungen erfolgreich waren; sonst false
	 */
	public boolean validieren() {
		return validieren(benutzer, passwort);
	}
	
	private boolean validieren(String user, String passwd) {
		boolean inValid = false;
		boolean outValid = false;
		
		if(inServer != null)
			inValid = inServer.pruefeLogin(user, passwd);
		
		if(outServer != null)
			outValid = outServer.pruefeLogin(user, passwd);
		
		return inValid && outValid;
	}
	
	/**
	 * Gibt die MailServer-Instanz zum Empfangen von Mails zur�ck
	 * @return MailServer zum Empfangen von Mails
	 */
	public EmpfangsServer getEmpfangsServer() {
		return inServer;
	}

	/**
	 * Gibt die MailServer-Instanz zum Versandt von Mails zur�ck
	 * @return MailServer zum Versandt von Mails
	 */
	public SendServer getSendServer() {
		return outServer;
	}
	
	/**
	 * Gibt die Mailadresse des MailAccounts zur�ck
	 * @return Mailadresse des MailAccounts
	 */
	public InternetAddress getAdresse() {
		return adresse;
	}

	/**
	 * Gibt den Benutzernamen f�r den MailAccount zur�ck
	 * @return Benutzername f�r den MailAccount
	 */
	public String getBenutzer() {
		return benutzer;
	}
	
	/**
	 * Versucht, das Passwort des Accounts neu zu setzen
	 * @param passwd Zu setzendes Passwort
	 * @throws AuthenticationFailedException Tritt auf, wenn die Anmeldung mit dem Passwort fehlgeschlagen ist
	 */
	public void setPasswort(String passwd) throws AuthenticationFailedException {
		if(!validieren(benutzer, passwd))
			throw new AuthenticationFailedException("Das �bergebene Passwort ist ung�ltig");
			
		this.passwort = passwd;
	}
}
