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

import javax.mail.Address;
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
		
		public MyMessageIDTerm(String messageID) {
			this.messageID = messageID;
		}
		
		@Override
		public boolean match(Message message) {
			try {
				if(message instanceof MimeMessage) {
					MimeMessage mime = (MimeMessage)message;
					String id = mime.getMessageID();
					
					if(id.equals(messageID))
						return true;
				}
			} catch (MessagingException ex) {
				ex.printStackTrace();
		    }
		    return false;
		}
		
	}
	
	private EmpfangsServer inServer;
	private SendServer outServer;
	
	private InternetAddress adresse;
	private String benutzer;
	private String passwort;
	
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
	public MailAccount(EmpfangsServer inServer, SendServer outServer, InternetAddress adresse, String benutzer, String passwort) 
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
	public void sendeMail(InternetAddress[] to, InternetAddress[] cc, String subject, String text, String format, File[] attachment) throws MessagingException {
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
	public String[] getOrdnerstruktur() {
		String[] paths = null;
		
		try {
			Store store = inServer.getMailStore(benutzer, passwort);
			store.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			Folder[] folders = store.getDefaultFolder().list("*");

			paths = new String[folders.length];
			for(int i = 0; i < paths.length; i++) {
				paths[i] = folders[i].getFullName();
			}
			
			store.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return paths;
	}
	
	/**
	 * Gibt die MailInfos aller Messages in dem �bergebenen Pfad zur�ck.
	 * @param pfad Pfad, in dem die Mails gesucht werden.
	 * @return Array von MailInfos mit der ID, Betreff, Sender und SendDatum
	 */
	public MailInfo[] getMessages(String pfad) {
		MailInfo[] ret = null;

		Store store = null;
		try {
			store = inServer.getMailStore(benutzer, passwort);
			store.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			Folder folder = store.getFolder(pfad);
			folder.open(Folder.READ_ONLY);
			
			Message[] messages = folder.getMessages();
			ret = new MailInfo[messages.length];
			
			for(int i = 0; i < messages.length; i++) {
				Message message = messages[i];
				String id = message.getHeader("Message-ID")[0];
				
				String dateiname = id.replace(">", "").replace("<", "");
				File lokalerPfad = new File("Mail/" + adresse.getAddress() + "/" + pfad + "/"  + dateiname + ".mail").getAbsoluteFile();
				MailInfo tmp = ladeMailInfo(lokalerPfad);
				
				if(tmp != null) {
					ret[i] = tmp;
				}
				else {
					boolean read = message.isSet(Flag.SEEN);
					String subject = message.getSubject();
					Address from = message.getFrom()[0];
					Date sendDate = message.getSentDate();
					
					ret[i] = new MailInfo();
					ret[i].setID(id);
					ret[i].setRead(read);
					ret[i].setSubject(subject);
					ret[i].setSender(from);
					ret[i].setDate(sendDate);
					
					speichereMailInfo(ret[i], lokalerPfad);
				}
			}
			
			folder.close(true);
		} catch (Exception e) {
			if(ret == null) 
				ret = new MailInfo[0];
		} finally {
			if(store != null && store.isConnected())
				try {
					store.close();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return ret;
	}
	
	/**
	 * Durchsucht den �bergebenen Part nach dem Text der E-Mail
	 * @param p Part-Objekt, indem der Text gesucht werden soll
	 * @return Text der E-Mail
	 */
	private String getText(Part p) throws MessagingException, IOException {
		if (p.isMimeType("text/*")) {
			return (String)p.getContent();
		}
		
		if (p.isMimeType("multipart/alternative")) {
			Multipart mp = (Multipart)p.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
			    Part bp = mp.getBodyPart(i);
			    if (bp.isMimeType("text/plain")) {
				    if (text == null)
				        text = getText(bp);
				    continue;
					} 
			    else if (bp.isMimeType("text/html")) {
			        String s = getText(bp);
			        if (s != null)
			            return s;
				} 
				else 
					return getText(bp);
			}
			return text;
		} 
		else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart)p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
			    String s = getText(mp.getBodyPart(i));
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
	private String getTyp(Part p) throws IOException, MessagingException {
		if(p.isMimeType("text/plain") || p.isMimeType("text/html"))
			return p.getContentType();
		
		Object content = p.getContent();
		if (content instanceof Multipart) {
		    Multipart mp = (Multipart) content;
		    for (int i = 0; i < mp.getCount(); i++) {
		        BodyPart bp = mp.getBodyPart(i);
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
	public void getMessageText(String pfad, MailInfo messageInfo) throws MessagingException {
		if(messageInfo == null || messageInfo.getID() == null)
			throw new NullPointerException("�bergebene MailInfo ist NULL");
		
		if(messageInfo.getText() != null && messageInfo.getContentType() != null)
			return;
		
		Store store = null;
		
		try {
			store = inServer.getMailStore(benutzer, passwort);
			store.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			
			Folder folder = store.getFolder(pfad);
			folder.open(Folder.READ_WRITE);
			
			Message message = infoToMessage(messageInfo, folder);
			
			if(message != null) {
				if(messageInfo.getText() == null)
					messageInfo.setText(getText(message));
				if(messageInfo.getContentType() == null)
					messageInfo.setContentType(getTyp(message));
				if(!messageInfo.isRead()) {
					message.setFlag(Flag.SEEN, true);
					messageInfo.setRead(true);
				}

				String id = messageInfo.getID();
				String dateiname = id.replace(">", "").replace("<", "");
				File lokalerPfad = new File("Mail/" + adresse.getAddress() + "/" + pfad + "/"  + dateiname + ".mail").getAbsoluteFile();
				
				speichereMailInfo(messageInfo, lokalerPfad);
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
	public void getWholeMessage(String pfad, MailInfo messageInfo) throws MessagingException {
		if(messageInfo.getText() != null && messageInfo.getContentType() != null && 
				messageInfo.getSubject() != null && messageInfo.getSender() != null && 
				messageInfo.getDate() != null && messageInfo.getTo() != null && 
				messageInfo.getCc() != null && messageInfo.getAttachment() != null) 
			return;
		
		Store store = null;
		
		try {
			store = inServer.getMailStore(benutzer, passwort);
			store.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			
			Folder folder = store.getFolder(pfad);
			folder.open(Folder.READ_ONLY);
			
			Message message = infoToMessage(messageInfo, folder);
			
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
					ArrayList<String> attachment = new ArrayList<String>();
					if(message.getContent() instanceof Multipart) {
						Multipart mp = (Multipart)message.getContent();
						for(int i = 0; i < mp.getCount(); i++) {
							BodyPart bp = mp.getBodyPart(i);
							String filename = bp.getFileName();
							
							if(filename != null && !filename.isEmpty())
								attachment.add(bp.getFileName());
						}
					}
					
					messageInfo.setAttachment(attachment.toArray(new String[attachment.size()]));
				}

				String id = messageInfo.getID();
				String dateiname = id.replace(">", "").replace("<", "");
				File lokalerPfad = new File("Mail/" + adresse.getAddress() + "/" + pfad + "/"  + dateiname + ".mail").getAbsoluteFile();
				
				speichereMailInfo(messageInfo, lokalerPfad);
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
	private Message infoToMessage(MailInfo mail, Folder ordner) throws MessagingException {		
		String id = mail.getID();
		Message[] tmpMessages = ordner.search(new MessageIDTerm(id));
		
		if(tmpMessages.length == 0)
			tmpMessages = ordner.search(new MyMessageIDTerm(mail.getID()));
		
		return tmpMessages[0];
	}

	/**
	 * Gibt die Message-Objekte zu den IDs in den �bergebenen MailInfos im �bergebenen Ordner zur�ck.
	 * @param mail MailInfo-Objekte, die die IDs zu den zu suchenden Messages enthallten
	 * @param ordner Ordner, in dem gesucht werden soll
	 * @return Message-Objekte zu den �bergebenen IDs
	 */
	private Message[] infoToMessage(MailInfo[] mails, Folder ordner) throws MessagingException {
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
	private void kopieren(MailInfo[] mails, Folder quellOrdner, Folder zielOrdner, boolean l�schen) throws MessagingException {
		Message[] messages = infoToMessage(mails, quellOrdner);
		
		quellOrdner.copyMessages(messages, zielOrdner);
		
		if(l�schen) {
			for(Message m : messages) {
				m.setFlag(Flags.Flag.DELETED, true);
			}
			
			quellOrdner.expunge();
		}
	}

	/**
	 * Verschiebe die Mails vom Quell- in den Zielordner
	 * @param mails MailInfos der zu verschiebenen Mails
	 * @param quellPfad Pfad zum Quellordner
	 * @param zielPfad Pfad zum Zielordner
	 */
	public void verschiebeMails(MailInfo[] mails, String quellPfad, String zielPfad) throws MessagingException {
		Store mailStore = null;
		
		try {
			mailStore = inServer.getMailStore(benutzer, passwort);
			mailStore.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			
			Folder quellOrdner = mailStore.getFolder(quellPfad);
			Folder zielOrdner = mailStore.getFolder(zielPfad);
			
			kopieren(mails, quellOrdner, zielOrdner, true);
		} finally {
			if(mailStore != null && mailStore.isConnected()) {
				try {
					mailStore.close();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Kopiere die Mails vom Quell- in den Zielordner
	 * @param mails MailInfos der zu kopieren Mails
	 * @param quellPfad Pfad zum Quellordner
	 * @param zielPfad Pfad zum Zielordner
	 */
	public void kopiereMails(MailInfo[] mails, String quellPfad, String zielPfad) throws MessagingException {
		Store mailStore = null;
		
		try {
			mailStore = inServer.getMailStore(benutzer, passwort);
			mailStore.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			
			Folder quellOrdner = mailStore.getFolder(quellPfad);
			Folder zielOrdner = mailStore.getFolder(zielPfad);
			
			kopieren(mails, quellOrdner, zielOrdner, false);
		} finally {
			if(mailStore != null && mailStore.isConnected()) {
				try {
					mailStore.close();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * L�sche die Mails aus dem �bergebenen Ordner
	 * @param mails MailInfos der zu l�schenden Mails
	 * @param pfad Pfad zum Ordner
	 * @return true, wenn das l�schen erfolgreich war; sonst false
	 */
	public boolean loescheMails(MailInfo[] mails, String pfad) throws MessagingException {
		boolean result = false;
		
		Store mailStore = null;
		
		try {
			mailStore = inServer.getMailStore(benutzer, passwort);
			mailStore.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			
			Folder ordner = mailStore.getFolder(pfad);
			ordner.open(Folder.READ_WRITE);
			
			Folder binFolder = null;
			
			Folder[] folders = mailStore.getDefaultFolder().list("*");
			
			outer:
			for(Folder folder : folders) {
				IMAPFolder imap = (IMAPFolder)folder;
				String[] attr = imap.getAttributes();
				
				for(int i = 0; i < attr.length; i++) {
					if(attr[i].equals("\\Trash")){
						binFolder = imap;
						break outer;
					}
				}
			}
			
			if(binFolder != null) {
				String binPfad = binFolder.getFullName();
				if(!pfad.equals(binPfad)) {
					kopieren(mails, ordner, binFolder, true);
				}
				else {
					Message[] messages = infoToMessage(mails, ordner);
					for(Message m : messages) {
						if(!m.isExpunged())
							m.setFlag(Flags.Flag.DELETED, true);
					}
					
					ordner.expunge();
				}
			}
			
			//TODO L�schen von Mails, ohne dass die Ordner eine Trash-Flag haben
			
			result = true;
		} finally {
			if(mailStore != null && mailStore.isConnected()) {
				try {
					mailStore.close();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	public boolean anhangSpeichern(MailInfo mail, String pfad, String anhangName, String zielPfad) throws IOException, MessagingException {
		boolean result = true;
		Store mailStore = null;
		
		try {
			mailStore = inServer.getMailStore(benutzer, passwort);
			mailStore.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			
			Folder ordner = mailStore.getFolder(pfad);
			ordner.open(Folder.READ_WRITE);
			
			Message message = infoToMessage(mail, ordner);
			String contentType = message.getContentType();
			if(contentType.contains("multipart")) {
				Multipart multipart = (Multipart)message.getContent();
				for(int i = 0; i < multipart.getCount(); i++) {
					MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
					String disposition = part.getDisposition();
					String fileName = part.getFileName();
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
		
		return result;
	}
	
	private void speichereMailInfo(MailInfo info, File zielDatei) throws IOException {
		zielDatei = zielDatei.getAbsoluteFile();
		File ordner = zielDatei.getParentFile();
		
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
	
	private MailInfo ladeMailInfo(File datei) throws ClassNotFoundException, IOException {
		MailInfo geladen = null;

		datei = datei.getAbsoluteFile();
		if(datei.exists()) {
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(datei);
				ois = new ObjectInputStream(fis);
				
				geladen = (MailInfo)ois.readObject();
			}
			finally {
				ois.close();
			}
		}
		
		return geladen;
	}
	
	/**
	 * Pr�ft, ob mit den Daten der MailAccount-Instanz eine erfolgreiche Verbindung 
	 * zum Empfangs- und zum Versandtserver hergestellt werden konnte 
	 * @return true, wenn die Verbindungen erfolgreich waren; sonst false
	 */
	public boolean validieren() {
		boolean inValid = false;
		boolean outValid = false;
		
		if(inServer != null)
			inValid = inServer.pr�feLogin(benutzer, passwort);
		
		if(outServer != null)
			outValid = outServer.pr�feLogin(benutzer, passwort);
		
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
}
