package de.outlook_klon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class MeldungsFrame extends ExtendedFrame {
	private static final long serialVersionUID = 1L;

	private void initMenu() {
		JMenuBar bar = new JMenuBar();
		setJMenuBar(bar);
		
		JMenu mnDatei = new JMenu("Datei");
		bar.add(mnDatei);
		
		JMenu mnDateiNeu = new JMenu("Neu");
		mnDatei.add(mnDateiNeu);
		
		JMenuItem mntDateiNeuKrankmeldung = new JMenuItem("Neue Krankmeldung");
		mnDateiNeu.add(mntDateiNeuKrankmeldung);
		
		JMenuItem mntDateiNeuAbwesenheitmeldung = new JMenuItem("Neue Abwesenheitsmeldung");
		mnDateiNeu.add(mntDateiNeuAbwesenheitmeldung);
		
		mnDatei.add(new JSeparator());
		
		JMenuItem mntDateiBeenden = new JMenuItem("Beenden");
		mntDateiBeenden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		mnDatei.add(mntDateiBeenden);
	}
	
	public MeldungsFrame() {
		initMenu();
	}
}
