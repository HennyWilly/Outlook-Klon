package de.outlook_klon.logik;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import de.outlook_klon.logik.kontakte.Kontaktverwaltung;
import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.kalendar.Terminkalendar;

/**
 * Diese Klasse stellt den Benutzer dar.
 * Bietet Zugriff auf die Termin- und Kontaktverwaltung.
 * Zudem ist es möglich, über die Mailkonten des Nutzers zu iterieren.
 * 
 * @author Hendrik Karwanni
 */
public class Benutzer implements Iterable<MailAccount> {
	private Kontaktverwaltung kontakte;
	private Terminkalendar termine;
	private ArrayList<MailAccount> konten;
	
	public Benutzer() {
		kontakte = new Kontaktverwaltung();
		termine = new Terminkalendar();
		
		konten = new ArrayList<MailAccount>();
		
		File file = new File("Mail").getAbsoluteFile();
		String[] directories = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File dir, String name) {
		    return new File(dir, name).isDirectory();
		  }
		});
		
		for(String directory : directories) {
			String settings = file.getAbsolutePath() + "\\" + directory + "\\settings.bin";
			File datei = new File(settings).getAbsoluteFile();
			if(!datei.exists())
				continue;
			
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(settings);
				ois = new ObjectInputStream(fis);
				
				MailAccount geladen;
				geladen = (MailAccount)ois.readObject();
				
				konten.add(geladen);
			} catch (IOException e) { 
				continue;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				try {
					ois.close();
				} catch (IOException e) { }
			}
		}
	}

	@Override
	public Iterator<MailAccount> iterator() {
		return konten.iterator();
	}

	public Kontaktverwaltung getKontakte() {
		return kontakte;
	}
	
	public Terminkalendar getTermine() {
		return termine;
	}
	
	public boolean addMailAccount(MailAccount ma) {
		if(ma == null)
			return false;
		
		konten.add(ma);
		return true;
	}
	
	public void speichern() {
		for(MailAccount acc : konten) {
			try {
				acc.speichern();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
