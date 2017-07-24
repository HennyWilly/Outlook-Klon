package de.outlookklon;

import de.outlookklon.application.UserException;
import de.outlookklon.view.frames.MainFrame;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Die Hauptklasse der Anwendung.
 */
public final class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private Application() {
        // Privater Util-Konstruktor
    }

    /**
     * Hier wird das MainFrame erzeugt und angezeigt
     *
     * @param args Komandozeilenparamenter
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    JFrame mainFrame = new MainFrame();

                    mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    mainFrame.setVisible(true);
                } catch (UserException ex) {
                    // Konnte das MainFrame nicht gestartet werden, wird der Swing-Thread auch beendet
                    LOGGER.error("Could not start MainFrame", ex);
                }
            }
        });
    }
}
