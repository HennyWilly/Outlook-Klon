package de.outlook_klon.gui;

import java.awt.Dialog;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;

/**
 * Diese abstrakte Klasse stellt Methoden bereit, die bei JDialogs h�ufig
 * ben�tigt, aber leider nicht standardm��ig in Java implementiert wurden.
 *
 * @author Hendrik Karwanni
 * @param <TDialogTyp> Datentyp, der vom Dialog zur�ckgegeben werden soll
 */
public abstract class ExtendedDialog<TDialogTyp> extends JDialog {

    private static final long serialVersionUID = -8078692720731679550L;

    /**
     * Wird von Subklassen aufgerufen, um einige h�ufig in Dialogen verwendete
     * Werte zu setzen.
     *
     * @param width Erzwingt die �bergabe der initialen Breite des Dialogs
     * @param height Erzwingt die �bergabe der initialen H�he des Dialogs
     */
    protected ExtendedDialog(int width, int height) {
        this.setSize(width, height);
        // Fenster in der Mitte des Bildschirms
        this.setLocationRelativeTo(null);
        this.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        this.setResizable(false);
    }

    /**
     * Muss implementiert werden, um den Wert, der beim Schlie�en des Dialogs
     * zur�ckgegeben wird, festzulegen
     *
     * @return Wert, der beim Schlie�en des Dialogs zur�ckgegeben wird
     */
    protected abstract TDialogTyp getDialogResult();

    /**
     * �ffnet den Dialog und gibt nach dem Schlie�en das Ergebnis des Dialogs
     * zur�ck
     *
     * @return Ergbnis des Dialogs
     */
    public TDialogTyp showDialog() {
        // Durch "ModalityType=DEFAULT_MODALITY_TYPE" wird bei setVisible(true)
        // blockiert
        setVisible(true);

        return getDialogResult();
    }

    /**
     * Schlie�t den Dialog und gibt das entsprechende Event an alle
     * hinzugef�gten WindowListener weiter
     */
    public void close() {
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}
