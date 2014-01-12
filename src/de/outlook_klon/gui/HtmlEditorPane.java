package de.outlook_klon.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import de.outlook_klon.logik.Benutzer;
import de.outlook_klon.logik.kalendar.Termin;
import de.outlook_klon.logik.kalendar.Terminkalender;

/**
 * Diese JEditorPane unterst�tzt das vollst�ndige Umschalten des Textinhalts von
 * Klartext zu HTML-Code.
 * 
 * @author Hendrik Karwanni
 */
public class HtmlEditorPane extends JEditorPane {
	private static final long serialVersionUID = -4765175082709293453L;
	
	private static final String HTML = "TEXT/html";
	private static final String PLAIN = "TEXT/plain";
	
	private static final String PREFIX = "date://";
	private static final String REPLACE_PATTERN = "<a href=\"date://%s\">%s</a>";

	private static DateFormat formater1;
	private static DateFormat formater2;
	private static Pattern timePattern;

	private HTMLEditorKit htmlEditor;
	
	//Statischer Konstruktor
	static {
		formater1 = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		formater1.setLenient(false);
		
		formater2 = new SimpleDateFormat("dd.MM.yyyy");
		formater2.setLenient(false);
		
		timePattern = Pattern.compile("\\d{1,2}\\.\\d{1,2}\\.\\d{4}( \\d{1,2}:\\d{1,2})?");
	}
	
	/**
	 * Bestimmt, ob es sich beim �bergebenen String um einen Html-Code handelt
	 * 
	 * @param text
	 *            Zu pr�fender Text
	 * @return true, wenn der Text ein Html-Code ist; sonst false
	 */
	public static boolean istHtml(String text) {
		Pattern pattern = Pattern
				.compile("<(\"[^\"]*\"|'[^']*'|[^'\">])*>");
		Matcher matcher = pattern.matcher(text);
		
		return matcher.find();
	}

	/**
	 * Initialisiert die Attribute des Steuerelements
	 */
	private void init() {
		htmlEditor = new HTMLEditorKit();

		this.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent arg0) {
				if (arg0.getEventType() == EventType.ACTIVATED) {
					String url = arg0.getDescription();
					if(url.startsWith(PREFIX)) {
						String strDatum = url.substring(PREFIX.length());
						
						try {
							Date datum = null; 
							
							try {
								datum = formater1.parse(strDatum);
							} catch (ParseException ex) {
								datum = formater2.parse(strDatum);
							}
							
							TerminFrame tf = new TerminFrame(datum);
			            	Termin t = tf.showDialog();
			            	
			            	if(t != null) {
			            		Terminkalender kalender = Benutzer.getInstanz().getTermine();
			            		kalender.addTermin(t);
			            	}
							
						} catch(ParseException e) {
							JOptionPane.showMessageDialog(null, "Kein g�ltiges Datum", "Fehler", JOptionPane.ERROR_MESSAGE);
						}
						
						return;
					}

					if (Desktop.isDesktopSupported()) {
						Desktop meinDesktop = Desktop.getDesktop();

						try {
							meinDesktop.browse(new URI(url));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						Runtime meineLaufzeit = Runtime.getRuntime();
						try {
							// Sollte bei OS mit X-Server funktionieren
							meineLaufzeit.exec("xdg-open " + url); 
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	/**
	 * Erstellt ein neues HtmlEditorPane
	 */
	public HtmlEditorPane() {
		super();
		init();
	}

	/**
	 * Erstellt ein neues HtmlEditorPane basierend auf einem String mit
	 * Url-Spzifikationen
	 * 
	 * @param url
	 *            String der URL der automatisch anzuzeigenden Seite
	 * @throws IOException
	 *             Tritt auf, wenn der String null ist, oder auf die Url nicht
	 *             zugegriffen werden kann
	 */
	public HtmlEditorPane(String url) throws IOException {
		super(url);
		init();
	}

	/**
	 * Erstellt ein neues HtmlEditorPane basierend den �bergebenen
	 * Url-Spzifikationen
	 * 
	 * @param url
	 *            URL der automatisch anzuzeigenden Seite
	 * @throws IOException
	 *             Tritt auf, wenn die URL null ist, oder darauf nicht
	 *             zugegriffen werden kann
	 */
	public HtmlEditorPane(URL url) throws IOException {
		super(url);
		init();
	}

	/**
	 * Erstellt ein neues HtmlEditorPane mit den �bergebenen Einstellungen
	 * 
	 * @param type
	 *            Typ des �bergebenen Texts
	 * @param text
	 *            Anzuzeigender Text
	 */
	public HtmlEditorPane(String type, String text) {
		super(type, text);
		init();
	}

	/**
	 * Setzt den Text und den ContentType der EditorPane
	 * 
	 * @param text
	 *            Zu setzender Text
	 * @param contentType
	 *            Zu setzender ContentType
	 * @param autoChange
	 *            Ist der Wert true, so wird aus dem �bergebenen Text bestimmt,
	 *            um welchen ContentType es sich handelt
	 */
	public void setText(String text, String contentType, boolean autoChange) {
		if (autoChange && !contentType.startsWith(HTML)) {
			if (istHtml(text))
				contentType = contentType.replaceAll("(?i)" + PLAIN, HTML);
		}

		setContentType(contentType);
		if (contentType.startsWith(HTML)) {
			text = text.replace("\r\n", "<br/>");

			if (getEditorKit() != htmlEditor)
				setEditorKit(htmlEditor);
		}
		setText(text);
	}
	
	@Override
	public void setText(String text) {
		if(getContentType().equalsIgnoreCase("text/html")) {		
			StringBuilder sb = new StringBuilder(text);
			Matcher matcher = timePattern.matcher(sb);

			boolean skip = false;
			boolean rematch = false;
			while(matcher.find()) {
				if(rematch) {
					rematch = false;
					matcher = timePattern.matcher(sb);
					continue;
				}
				
				if(skip) {
					skip = false;
					continue;
				}
				int start = matcher.start();
				int ende = matcher.end();
				
				String match = matcher.group();
				if(sb.length() - start < 0) {
					continue;
				}
				
				int index = start - PREFIX.length();
				if(index >= 0) {
					String preText = sb.substring(index, start);
					if(preText.equalsIgnoreCase(PREFIX)) {
						skip = true;
						continue;
					}
				}
				
				try {
					try {
						formater1.parse(match);
					} catch (ParseException ex) {
						formater2.parse(match);
						
						match = match.replaceAll(" \\d{1,2}:\\d{1,2}", "");
						ende = start + match.length();
					}
					
					String replacement = String.format(REPLACE_PATTERN, match, match);
					sb.replace(start, ende, replacement);
					rematch = true;
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			text = sb.toString();
		}
		
		super.setText(text);
	}
}
