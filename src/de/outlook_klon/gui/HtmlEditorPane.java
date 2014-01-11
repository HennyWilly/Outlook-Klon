package de.outlook_klon.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Diese JEditorPane unterstützt das vollständige Umschalten des Textinhalts von
 * Klartext zu HTML-Code.
 * 
 * @author Hendrik Karwanni
 */
public class HtmlEditorPane extends JEditorPane {
	private static final long serialVersionUID = -4765175082709293453L;
	
	private static final String HTML = "TEXT/html";
	private static final String PLAIN = "TEXT/plain";

	private HTMLEditorKit htmlEditor;

	/**
	 * Bestimmt, ob es sich beim übergebenen String um einen Html-Code handelt
	 * 
	 * @param text
	 *            Zu prüfender Text
	 * @return true, wenn der Text ein Html-Code ist; sonst false
	 */
	public static boolean istHtml(String text) {
		Pattern pattern = Pattern
				.compile(".*?<(\"[^\"]*\"|'[^']*'|[^'\">])*>.*?");
		Matcher matcher = pattern.matcher(text);
		
		return matcher.matches();
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
	 * Erstellt ein neues HtmlEditorPane basierend den übergebenen
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
	 * Erstellt ein neues HtmlEditorPane mit den übergebenen Einstellungen
	 * 
	 * @param type
	 *            Typ des übergebenen Texts
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
	 *            Ist der Wert true, so wird aus dem übergebenen Text bestimmt,
	 *            um welchen ContentType es sich handelt
	 */
	public void setText(String text, String contentType, boolean autoChange) {
		String tmpText = text.replace("\r\n", "<br/>");

		if (autoChange && !contentType.startsWith(HTML)) {
			if (istHtml(tmpText))
				contentType = contentType.replace(PLAIN, HTML);
		}

		setContentType(contentType);
		if (contentType.startsWith(HTML)) {
			text = tmpText;

			if (getEditorKit() != htmlEditor)
				setEditorKit(htmlEditor);
		}
		setText(text);
	}
}
