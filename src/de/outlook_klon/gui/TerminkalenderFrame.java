package de.outlook_klon.gui;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import de.outlook_klon.logik.Benutzer;
import de.outlook_klon.logik.kalendar.Termin;
import de.outlook_klon.logik.kalendar.Terminkalender;

public class TerminkalenderFrame extends ExtendedFrame {

	private static final long serialVersionUID = 1L;

	private JTable tblTermine;
	private JTextPane textDetails;
	private Terminkalender kalender;	


	public TerminkalenderFrame() {
		kalender = Benutzer.getInstanz().getTermine();
		
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

		tblTermine = new JTable(){
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {                
	                return false;               
	        };
	    };

		tblTermine.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Referenz", "Betreff", "Kontakt", "Datum"
			}
		));
		
		tblTermine.removeColumn(tblTermine.getColumn("Referenz"));
		tblTermine.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()) {		
					DefaultListSelectionModel sender = (DefaultListSelectionModel) e.getSource();			
					int row = sender.getMinSelectionIndex();
					if(row == -1)
						return;
					
					DefaultTableModel model = (DefaultTableModel)tblTermine.getModel();
					int length = model.getDataVector().size();
					
					if(length > 0) {
						int zeileModel = tblTermine.convertRowIndexToModel(row);
						
						Termin referenz = (Termin)model.getValueAt(zeileModel, 0);
						aktualisiereDetails(referenz);
					}
					else {
						textDetails.setEditable(true);
						textDetails.setText(null);
						textDetails.setEditable(false);
					}
				}
				
			}	
			
		});
		
		
		
		JScrollPane scrollPane_1 = new JScrollPane(tblTermine);
		splitPane_1.setLeftComponent(scrollPane_1);
		
		textDetails = new JTextPane();
		splitPane_1.setRightComponent(textDetails);
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
			model.addRow(new Object[] {a, a.getBetreff(), a.getText(), a.getStart().toString()});
			EinwegKalender.l�scheTermin(a);
		}
	}
	
	
	
	private void aktualisiereDetails(Termin t) {
		StringBuilder sbshop = new StringBuilder();
		
		if(t != null) {
			
			if(!t.getBetreff().trim().isEmpty())
				sbshop.append("Betreff: ").append(t.getBetreff()).append('\n');
			if(!t.getOrt().trim().isEmpty())
				sbshop.append("Ort: ").append(t.getOrt()).append('\n');
			if(!t.getStart().toString().trim().isEmpty())
				sbshop.append("Startzeit: ").append(t.getStart().toString()).append('\n');
			if(!t.getEnde().toString().trim().isEmpty())
				sbshop.append("Ende: ").append(t.getEnde().toString()).append('\n');
			if(!t.getText().trim().isEmpty())
				sbshop.append("Info: ").append(t.getText()).append('\n');
			}
		
		textDetails.setEditable(true);
		textDetails.setText(sbshop.toString());
		textDetails.setEditable(false);		
	}
	
	
	
	private void oeffneTerminFrame() {
		TerminFrame Tf = new TerminFrame();
		
		Termin dummy = Tf.showDialog();
		
		if(dummy != null)
		{
			kalender.addTermin(dummy);
			if(kalender.ueberschneidung(dummy))
			{
				JOptionPane.showMessageDialog(this, "ACHTUNG! �berschneidung mit bereits vorhandenem Termin. Evtl. Sollten Sie ihre Termine �berpr�fen.", "KAMEHAME HAAAA", JOptionPane.WARNING_MESSAGE);
			}
			
			aktualisiere2Tabelle();
		}
		
		
	}
	
	
}
