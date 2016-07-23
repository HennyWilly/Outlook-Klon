package de.outlookklon;

import de.outlookklon.gui.MainFrame;
import de.outlookklon.logik.UserException;
import java.awt.Frame;
import javax.swing.JFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Program {

    private static final Logger LOGGER = LoggerFactory.getLogger(Program.class);

    private Program() {
    }

    /**
     * Hier wird das MainFrame erzeugt und angezeigt
     *
     * @param args Komandozeilenparamenter
     */
    public static void main(final String[] args) {
        try {
            JFrame mainFrame = new MainFrame();

            mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setVisible(true);
        } catch (UserException ex) {
            LOGGER.error("Could not start MainFrame", ex);
        }
    }
}
