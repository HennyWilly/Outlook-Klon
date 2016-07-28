package de.outlookklon.gui;

import de.outlookklon.localization.ILocalizable;
import de.outlookklon.localization.WindowLocalizer;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 * Diese abstrakte Klasse stellt Methoden bereit, die bei JFrames häufig
 * benötigt, aber leider nicht standardmäßig in Java implementiert wurden.
 *
 * @author Hendrik Karwanni
 */
public abstract class ExtendedFrame extends JFrame implements ILocalizable {

    private static final long serialVersionUID = 1L;

    /**
     * Wird implizit durch Subklassen aufgerufen, um den WindowLocalizer als
     * WindowListener zu registrieren.
     */
    protected ExtendedFrame() {
        this.addWindowListener(new WindowLocalizer());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * Schließt das Fenster und gibt das entsprechende Event an alle
     * hinzugefügten WindowListener weiter
     */
    public void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}
