package de.outlook_klon.gui;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;

import de.outlook_klon.logik.kalendar.Termin;
import de.outlook_klon.logik.kalendar.Terminkalender;

public class TerminkalenderFrame extends ExtendedFrame {

	private static final long serialVersionUID = 1L;

	private JTable tblTermine;
	private Terminkalender kalender;	

	public TerminkalenderFrame(Terminkalender Tk) {
		kalender = Tk;
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnDatei = new JMenu("Datei");
		menuBar.add(mnDatei);
		
		JMenuItem mntmTerminHinzufgen = new JMenuItem("Termin hinzuf\u00FCgen");
		mntmTerminHinzufgen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				oeffneTerminFrame();
			}
		});
		mnDatei.add(mntmTerminHinzufgen);
		
		JMenuItem mntmBeenden = new JMenuItem("Beenden");
		mntmBeenden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		
		mnDatei.add(mntmBeenden);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		
		JScrollPane scrollPane = new JScrollPane((Component) null);
		splitPane.setLeftComponent(scrollPane);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setRightComponent(splitPane_1);

		tblTermine = new JTable();
		tblTermine.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Betreff", "Kontakt", "Datum"
			}
		));
		
		JScrollPane scrollPane_1 = new JScrollPane(tblTermine);
		splitPane_1.setLeftComponent(scrollPane_1);
		
		JTextPane textPane = new JTextPane();
		splitPane_1.setRightComponent(textPane);
		getContentPane().add(splitPane);
		
		aktualisiere2Tabelle();

	}
	
	
	/*private void aktualisiereTabelle() {
		DefaultTableModel model = (DefaultTableModel)tblTermine.getModel();
		model.setRowCount(0);
		
		for(Termin t : kalender) {
			model.addRow(new Object[] {t.getBetreff(), t.getText(), t.getStart().toString()});
		}
	}*/

	private void aktualisiere2Tabelle() {
		DefaultTableModel model = (DefaultTableModel)tblTermine.getModel();
		model.setRowCount(0);
		
		
		ArrayList<Termin> dummdumm = new ArrayList<Termin>();
		for(Termin a:kalender)
		{
			dummdumm.add(a);
		}
		
		Terminkalender EinwegKalender = new Terminkalender();		
		
		for(int i=0; i< dummdumm.size(); i++)
		{
			EinwegKalender.addTermin(dummdumm.get(i));
		}
		
		int anzahl = EinwegKalender.getSize();
		
		for(int i=0; i<anzahl;i++) {
			Termin a = EinwegKalender.getOldest();
			model.addRow(new Object[] {a.getBetreff(), a.getText(), a.getStart().toString()});
			EinwegKalender.löscheTermin(a);
		}
	}
	
	
	

	
	private void oeffneTerminFrame() {
		TerminFrame Tf = new TerminFrame();
		
		Termin dummy = Tf.showDialog();
		
		if(dummy != null)
		{
			kalender.addTermin(dummy);
			if(kalender.ueberschneidung(dummy))
			{
				JOptionPane.showMessageDialog(this, "ACHTUNG! Überschneidung mit bereits vorhandenem Termin. Evtl. Sollten Sie ihre Termine überprüfen.", "KAMEHAME HAAAA", JOptionPane.WARNING_MESSAGE);
			}
			
			aktualisiere2Tabelle();
		}
		
		
	}
	
	
}
