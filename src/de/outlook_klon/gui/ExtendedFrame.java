package de.outlook_klon.gui;

import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * Diese abstrakte Klasse stellt Methoden bereit, die bei JFrames häufig benötigt, <br/>
 * aber leider nicht standardmäßig in Java implementiert wurden.
 * 
 * @author Hendrik Karwanni
 */
public abstract class ExtendedFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Schließt das Fenster und gibt das entsprechende Event an alle hinzugefügten WindowListener weiter
	 */
	public void close() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}
