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

public class HtmlEditorPane extends JEditorPane {
	private static final long serialVersionUID = -4765175082709293453L;
	
	private static final String HTML = "TEXT/html";
	private static final String PLAIN = "TEXT/plain";

	private HTMLEditorKit htmlEditor;

	public static boolean istHtml(String text) {
		Pattern pattern = Pattern
				.compile(".*?<(\"[^\"]*\"|'[^']*'|[^'\">])*>.*?");
		Matcher matcher = pattern.matcher(text);
		
		return matcher.matches();
	}

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

	public HtmlEditorPane() {
		super();
		init();
	}

	public HtmlEditorPane(String arg) throws IOException {
		super(arg);
		init();
	}

	public HtmlEditorPane(URL url) throws IOException {
		super(url);
		init();
	}

	public HtmlEditorPane(String type, String text) {
		super(type, text);
		init();
	}

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
