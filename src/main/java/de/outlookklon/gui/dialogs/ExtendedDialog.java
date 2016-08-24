package de.outlookklon.gui.dialogs;

import de.outlookklon.localization.ILocalizable;
import de.outlookklon.localization.WindowLocalizer;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Diese abstrakte Klasse stellt Methoden bereit, die bei JDialogs häufig
 * benötigt, aber leider nicht standardmäßig in Java implementiert wurden.
 *
 * @author Hendrik Karwanni
 * @param <T> Datentyp, der vom Dialog zurückgegeben werden soll
 */
public abstract class ExtendedDialog<T> extends JDialog implements ILocalizable {

    private static final long serialVersionUID = -8078692720731679550L;

    /**
     * Wird von Subklassen aufgerufen, um einige häufig in Dialogen verwendete
     * Werte zu setzen.
     *
     * @param parent Das Vaterfenster des Dialogs
     * @param width Erzwingt die übergabe der initialen Breite des Dialogs
     * @param height Erzwingt die übergabe der initialen Höhe des Dialogs
     */
    protected ExtendedDialog(Window parent, int width, int height) {
        initBaseSettings(parent, width, height);
    }

    private void initBaseSettings(Window parent, int width, int height) {
        this.setSize(width, height);

        this.setLocationRelativeTo(parent);
        this.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        this.setResizable(false);

        this.addWindowListener(new WindowLocalizer());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * Muss implementiert werden, um den Wert, der beim Schließen des Dialogs
     * zurückgegeben wird, festzulegen
     *
     * @return Wert, der beim Schließen des Dialogs zurückgegeben wird
     */
    protected abstract T getDialogResult();

    /**
     * Öffnet den Dialog und gibt nach dem Schließen das Ergebnis des Dialogs
     * zurück
     *
     * @return Ergbnis des Dialogs
     */
    public T showDialog() {
        // Durch "ModalityType=DEFAULT_MODALITY_TYPE" wird bei setVisible(true)
        // blockiert
        setVisible(true);

        return getDialogResult();
    }

    /**
     * Schließt den Dialog und gibt das entsprechende Event an alle
     * hinzugefügten WindowListener weiter
     */
    public void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}
