package de.outlook_klon.logik.mailclient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Store;
import javax.mail.search.MessageIDTerm;

/**
 * Diese Klasse stellt ein Mailkonto dar.
 * Hierüber können Mails gesendet und empfangen werden.
 * 
 * @author Hendrik Karwanni
 */
public class MailAccount implements Serializable {
	private static final long serialVersionUID = -6324237474768366352L;
	
	//Quelle "mkyong.com"
	private static final Pattern mailPattern = 
			Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	
	private EmpfangsServer inServer;
	private SendServer outServer;
	
	private String anzeigename;
	private String adresse;
	private String benutzer;
	private String passwort;
	
	/**
	 * Erstellt eine neue Instanz der Klasse Mailkonto mit den übergebenen Parametern
	 * @param inServer Server-Instanz, die zum Empfangen von Mails verwendet wird
	 * @param outServer Server-Instanz, die zum Senden von Mails verwendet wird
	 * @param anzeigename Anzeigename für ausgehende E-Mails
	 * @param adresse E-Mail-Adresse, das dem Konto zugeordnet ist
	 * @param benutzer Benutzername, der zur Anmeldung verwendet werden soll
	 * @param passwort Passwort, das zur Anmeldung verwendet werden soll
	 * @throws NullPointerException Tritt auf, wenn mindestens eine der Server-Instanzen null ist
	 * @throws IllegalArgumentException Tritt auf, wenn die übergebene Mailadresse ungültig ist
	 */
	public MailAccount(EmpfangsServer inServer, SendServer outServer, String anzeigename, String adresse, String benutzer, String passwort) 
						throws NullPointerException, IllegalArgumentException {
		if(inServer == null || outServer == null)
			throw new NullPointerException("Die übergebenen Server dürfen nicht <null> sein");
		
		this.inServer = inServer;
		this.outServer = outServer;
		
		if(!mailPattern.matcher(adresse).matches())
			throw new IllegalArgumentException("Die übergebene Zeichenfolge entspricht keiner gültigen Mailadresse!");
		
		this.anzeigename = anzeigename;
		this.adresse = adresse;
		this.benutzer = benutzer;
		this.passwort = passwort;
	}
	
	@Override
	public String toString() {
		return adresse;
	}

	/**
	 * Sendet eine Nachricht an einen Mailserver
	 * @param to Ziele der Mail
	 * @param cc CCs der Mail
	 * @param subject Betreff der Mail
	 * @param text Text der Mail
	 * @throws MessagingException Tritt auf, wenn der Sendevorgang fehlgeschlagen ist
	 */
	public void sendeMail(String[] to, String[] cc, String subject, String text, String format, File[] attachment) throws MessagingException {
		try {
			outServer.sendeMail(benutzer, passwort, anzeigename, to, cc, subject, text, format, attachment);
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
	}
	
	/**
	 * Gibt die Pfade aller Ordner des Servers zum Mailempfang zurück
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
	
	public MailInfo[] getMessages(String pfad) {
		MailInfo[] ret = null;
		
		try {
			Store store = inServer.getMailStore(benutzer, passwort);
			store.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			Folder folder = store.getFolder(pfad);
			folder.open(Folder.READ_ONLY);
			
			Message[] messages = folder.getMessages();
			ret = new MailInfo[messages.length];
			
			for(int i = 0; i < messages.length; i++) {
				Message message = messages[i];
				ret[i] = new MailInfo(message.getHeader("Message-ID")[0], message.getSubject(), message.getFrom()[0].toString(), message.getSentDate());
			}
			
			store.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public String getMessageText(String pfad, String messageID) {
		String ret = null;
		
		try {
			Store store = inServer.getMailStore(benutzer, passwort);
			store.connect(inServer.settings.getHost(), inServer.settings.getPort(), benutzer, passwort);
			
			Folder folder = store.getFolder(pfad);
			folder.open(Folder.READ_ONLY);
			
			Message[] messages = folder.search(new MessageIDTerm(messageID));
			
			if(messages != null && messages.length == 1) {
				Object content = messages[0].getContent();
				
				if (content instanceof String) 
			    {
					ret = (String)content;
			    } 
			    else if (content instanceof Multipart) 
			    {
			        Multipart multipart = (Multipart) content;
			        BodyPart part = multipart.getBodyPart(0);
			        ret = part.getContent().toString();
			    }   
			}
			
			store.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * Speichert die Instanz des MailAccounts auf der Festplatte
	 * @throws IOException Tritt auf, wenn die Daten nicht gespeicherten werden konnten.
	 */
	public void speichern() throws IOException {
		File pfad = new File("Mail/" + adresse + "/settings.bin").getAbsoluteFile();
		File ordner = pfad.getParentFile();
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			if(!ordner.exists()) {
				ordner.mkdirs();
			}
			
			fos = new FileOutputStream(pfad.getAbsolutePath());
			oos = new ObjectOutputStream(fos);
			
			oos.writeObject(this);
		} 
		finally {
			if(oos != null)
				oos.close();
		}
	}
	
	/**
	 * Prüft, ob mit den Daten der MailAccount-Instanz eine erfolgreiche Verbindung 
	 * zum Empfangs- und zum Versandtserver hergestellt werden konnte 
	 * @return true, wenn die Verbindungen erfolgreich waren; sonst false
	 */
	public boolean validieren() {
		return inServer.prüfeLogin(benutzer, passwort) 
				&& outServer.prüfeLogin(benutzer, passwort);
	}
	
	/**
	 * Gibt die MailServer-Instanz zum Empfangen von Mails zurück
	 * @return MailServer zum Empfangen von Mails
	 */
	public EmpfangsServer getEmpfangsServer() {
		return inServer;
	}

	/**
	 * Gibt die MailServer-Instanz zum Versandt von Mails zurück
	 * @return MailServer zum Versandt von Mails
	 */
	public SendServer getSendServer() {
		return outServer;
	}
	
	/**
	 * Gibt die Mailadresse des MailAccounts zurück
	 * @return Mailadresse des MailAccounts
	 */
	public String getAdresse() {
		return adresse;
	}

	/**
	 * Gibt den Benutzernamen für den MailAccount zurück
	 * @return Benutzername für den MailAccount
	 */
	public String getBenutzer() {
		return benutzer;
	}
	
	public String getAnzeigename() {
		return anzeigename;
	}
}
