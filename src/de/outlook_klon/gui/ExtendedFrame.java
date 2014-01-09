package de.outlook_klon.gui;

import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * Diese abstrakte Klasse stellt Methoden bereit, die bei JFrames h�ufig
 * ben�tigt, aber leider nicht standardm��ig in Java implementiert wurden.
 * 
 * @author Hendrik Karwanni
 */
public abstract class ExtendedFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Schlie�t das Fenster und gibt das entsprechende Event an alle
	 * hinzugef�gten WindowListener weiter
	 */
	public void close() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}
