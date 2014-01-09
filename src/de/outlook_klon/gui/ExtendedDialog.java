package de.outlook_klon.gui;

import java.awt.event.WindowEvent;

import javax.swing.JDialog;

/**
 * Diese abstrakte Klasse stellt Methoden bereit, die bei JDialogs häufig
 * benötigt, aber leider nicht standardmäßig in Java implementiert wurden.
 * 
 * @author Hendrik Karwanni
 * @param <TDialogTyp>
 *            Datentyp, der vom Dialog zurückgegeben werden soll
 */
public abstract class ExtendedDialog<TDialogTyp> extends JDialog {
	private static final long serialVersionUID = -8078692720731679550L;

	/**
	 * Wird von Subklassen aufgerufen, um einige häufig in Dialogen verwendete
	 * Werte zu setzen.
	 * 
	 * @param breite
	 *            Erzwingt die Übergabe der initialen Breite des Dialogs
	 * @param hoehe
	 *            Erzwingt die Übergabe der initialen Höhe des Dialogs
	 */
	protected ExtendedDialog(int breite, int hoehe) {
		this.setSize(breite, hoehe);
		this.setLocationRelativeTo(null);
		this.setModal(true);
		this.setResizable(false);
	}

	/**
	 * Muss implementiert werden, um den Wert, der beim Schließen des Dialogs
	 * zurückgegeben wird, festzulegen
	 * 
	 * @return Wert, der beim Schließen des Dialogs zurückgegeben wird
	 */
	protected abstract TDialogTyp getDialogResult();

	/**
	 * Öffnet den Dialog und gibt nach dem Schließen das Ergebnis des Dialogs
	 * zurück
	 * 
	 * @return Ergbnis des Dialogs
	 */
	public TDialogTyp showDialog() {
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
