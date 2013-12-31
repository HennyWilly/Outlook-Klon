package de.outlook_klon.gui;

import java.awt.event.WindowEvent;

import javax.swing.JDialog;

/**
 * Diese abstrakte Klasse stellt Methoden bereit, die bei JDialogs h�ufig ben�tigt, <br/>
 * aber leider nicht standardm��ig in Java implementiert wurden.
 * 
 * @author Hendrik Karwanni
 * @param <TDialogTyp> Datentyp, der vom Dialog zur�ckgegeben werden soll
 */
public abstract class ExtendedDialog<TDialogTyp> extends JDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Wird von Subklassen aufgerufen, um einige h�ufig in Dialogen verwendete Werte zu setzen.
	 */
	protected ExtendedDialog() {
		this.setModal(true);
		this.setResizable(false);
	}
	
	/**
	 * Muss implementiert werden, um den Wert, der beim Schlie�en des Dialogs zur�ckgegeben wird, festzulegen
	 * @return Wert, der beim Schlie�en des Dialogs zur�ckgegeben wird
	 */
	protected abstract TDialogTyp getDialogResult();
	
	/**
	 * �ffnet den Dialog und gibt nach dem Schlie�en das Ergebnis des Dialogs zur�ck
	 * @return Ergbnis des Dialogs
	 */
	public TDialogTyp showDialog() {
		setVisible(true);
		
		return getDialogResult();		
	}

	/**
	 * Schlie�t den Dialog und gibt das entsprechende Event an alle hinzugef�gten WindowListener weiter
	 */
	public void close() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}
