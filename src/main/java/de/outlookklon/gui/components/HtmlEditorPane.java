package de.outlookklon.gui.components;

import de.outlookklon.gui.dialogs.AppointmentFrame;
import de.outlookklon.gui.helpers.Dialogs;
import de.outlookklon.localization.Localization;
import de.outlookklon.logik.User;
import de.outlookklon.logik.calendar.Appointment;
import de.outlookklon.logik.calendar.AppointmentCalendar;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Window;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Diese JEditorPane unterstützt das vollständige Umschalten des Textinhalts von
 * Klartext zu HTML-Code.
 *
 * @author Hendrik Karwanni
 */
public class HtmlEditorPane extends JEditorPane {

    private static final long serialVersionUID = -4765175082709293453L;

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlEditorPane.class);

    private static final String HTML = "TEXT/html";
    private static final String PLAIN = "TEXT/plain";

    private static final String PREFIX = "date://";
    private static final String REPLACE_PATTERN = "<a href=\"date://%s\">%s</a>";

    private static final Pattern HTMLPATTERN;
    private static final DateFormat DATEFORMAT1;
    private static final DateFormat DATEFORMAT2;
    private static final Pattern TIMEPATTERN;

    private HTMLEditorKit htmlEditor;

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
     * @param url String der URL der automatisch anzuzeigenden Seite
     * @throws IOException Tritt auf, wenn der String null ist, oder auf die Url
     * nicht zugegriffen werden kann
     */
    public HtmlEditorPane(String url) throws IOException {
        super(url);
        init();
    }

    /**
     * Erstellt ein neues HtmlEditorPane basierend den übergebenen
     * Url-Spzifikationen
     *
     * @param url URL der automatisch anzuzeigenden Seite
     * @throws IOException Tritt auf, wenn die URL null ist, oder darauf nicht
     * zugegriffen werden kann
     */
    public HtmlEditorPane(URL url) throws IOException {
        super(url);
        init();
    }

    /**
     * Erstellt ein neues HtmlEditorPane mit den übergebenen Einstellungen
     *
     * @param type Typ des übergebenen Texts
     * @param text Anzuzeigender Text
     */
    public HtmlEditorPane(String type, String text) {
        super(type, text);
        init();
    }

    // Statischer Konstruktor
    static {
        HTMLPATTERN = Pattern.compile("<(\"[^\"]*\"|'[^']*'|[^'\">])*>");

        DATEFORMAT1 = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        DATEFORMAT1.setLenient(false);

        DATEFORMAT2 = new SimpleDateFormat("dd.MM.yyyy");
        DATEFORMAT2.setLenient(false);

        TIMEPATTERN = Pattern.compile("\\d{1,2}\\.\\d{1,2}\\.\\d{2,4}( \\d{1,2}:\\d{1,2})?");
    }

    /**
     * Bestimmt, ob es sich beim übergebenen String um einen Html-Code handelt
     *
     * @param text Zu prüfender Text
     * @return true, wenn der Text ein Html-Code ist; sonst false
     */
    public static boolean isHtml(String text) {
        Matcher matcher = HTMLPATTERN.matcher(text);

        return matcher.find();
    }

    /**
     * Initialisiert die Attribute des Steuerelements
     */
    private void init() {
        htmlEditor = new HTMLEditorKit();

        this.addHyperlinkListener(new HtmlHyperlinkListener());
    }

    /**
     * Setzt den Text und den ContentType der EditorPane
     *
     * @param text Zu setzender Text
     * @param contentType Zu setzender ContentType
     * @param autoChange Ist der Wert true, so wird aus dem übergebenen Text
     * bestimmt, um welchen ContentType es sich handelt
     */
    public void setText(String text, String contentType, boolean autoChange) {
        String newContentType;
        if (autoChange && !contentType.startsWith(HTML) && isHtml(text)) {
            newContentType = contentType.replaceAll("(?i)" + PLAIN, HTML);
        } else {
            newContentType = contentType;
        }

        setContentType(newContentType);

        String finalText;
        if (newContentType.startsWith(HTML)) {
            finalText = text.replace("\r\n", "<br/>");

            if (getEditorKit() != htmlEditor) {
                setEditorKit(htmlEditor);
            }
        } else {
            finalText = text;
        }

        setText(finalText);
    }

    @Override
    public void setText(String text) {
        if (text == null) {
            super.setText("");
        } else {
            String finalText = text;

            // In Plaintexte können keine Hyperlinks eingefügt werden
            if ("text/html".equalsIgnoreCase(getContentType())) {
                finalText = buildHyperlinkText(finalText);
            }

            super.setText(finalText);
        }
    }

    private String buildHyperlinkText(String text) {
        StringBuilder sb = new StringBuilder(text);
        Matcher matcher = TIMEPATTERN.matcher(sb);

        boolean skip = false;
        boolean rematch = false;
        while (matcher.find()) {
            if (rematch) {
                rematch = false;
                matcher = TIMEPATTERN.matcher(sb);
                continue;
            }

            if (skip) {
                skip = false;
                continue;
            }
            int start = matcher.start();
            int end = matcher.end();

            String match = matcher.group();
            if (sb.length() - start < 0) {
                continue;
            }

            int index = start - PREFIX.length();
            if (index >= 0) {
                String preText = sb.substring(index, start);
                // Falls das Datum schon in einem Date-Hyperlink steht
                if (preText.equalsIgnoreCase(PREFIX)) {
                    // Überspringe den nächsten Fund
                    skip = true;
                    continue;
                }
            }

            if (tryParseDate(match, DATEFORMAT1) == null) {
                if (tryParseDate(match, DATEFORMAT2) == null) {
                    continue;
                }

                // Entferne eine eventuelle falsche Uhrzeit
                match = match.replaceAll(" \\d{1,2}:\\d{1,2}", "");
                end = start + match.length();
            }

            // Bilde Hyperlink und ersetze im Sting mit ebenjenem
            String replacement = String.format(REPLACE_PATTERN, match, match);
            sb.replace(start, end, replacement);
            rematch = true;
        }

        return sb.toString();
    }

    private Date tryParseDate(String text, DateFormat parser) {
        try {
            return parser.parse(text);
        } catch (ParseException ex) {
            LOGGER.debug("Could not parse date to string", ex);
            return null;
        }
    }

    private class HtmlHyperlinkListener implements HyperlinkListener {

        @Override
        public void hyperlinkUpdate(HyperlinkEvent event) {
            if (event.getEventType() == EventType.ACTIVATED) {
                String url = event.getDescription();
                if (url.startsWith(PREFIX)) {
                    String strDate = url.substring(PREFIX.length());

                    try {
                        Date date;
                        try {
                            date = DATEFORMAT1.parse(strDate);
                        } catch (ParseException ex) {
                            LOGGER.info(Localization.getString("HtmlEditorPane_ErrorFirstParser"), ex);
                            date = DATEFORMAT2.parse(strDate);
                        }

                        Container parent = getParent();
                        if (parent instanceof Window) {
                            Window parentWindow = (Window) parent;

                            AppointmentFrame appointmentFrame = new AppointmentFrame(parentWindow, date);
                            Appointment appointment = appointmentFrame.showDialog();

                            if (appointment != null) {
                                AppointmentCalendar calendar = User.getInstance().getAppointments();
                                calendar.addAppointment(appointment);
                            }
                        }
                    } catch (ParseException ex) {
                        LOGGER.info(Localization.getString("HtmlEditorPane_ErrorSecondParser"), ex);
                        Dialogs.showErrorDialog(HtmlEditorPane.this, Localization.getString("HtmlEditorPane_InvalidDate"));
                    }

                    return;
                }

                startUrl(url);
            }
        }

        private void startUrl(String url) {
            if (Desktop.isDesktopSupported()) {
                Desktop myDesktop = Desktop.getDesktop();

                try {
                    myDesktop.browse(new URI(url));
                } catch (UnsupportedOperationException | URISyntaxException | IOException ex) {
                    LOGGER.error(Localization.getString("HtmlEditorPane_CouldNotLaunchUrl"), ex);
                }
            } else {
                Runtime myRuntime = Runtime.getRuntime();
                try {
                    // Sollte bei OS mit X-Server funktionieren
                    myRuntime.exec("xdg-open " + url);
                } catch (IOException ex) {
                    LOGGER.error(Localization.getString("HtmlEditorPane_CouldNotLaunchUrl"), ex);
                }
            }
        }
    }
}
