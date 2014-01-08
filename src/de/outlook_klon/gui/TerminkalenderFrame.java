package de.outlook_klon.gui;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import de.outlook_klon.logik.Benutzer;
import de.outlook_klon.logik.Benutzer.MailChecker;
import de.outlook_klon.logik.kalendar.Termin;
import de.outlook_klon.logik.kalendar.Terminkalender;
import de.outlook_klon.logik.mailclient.MailAccount;

import javax.swing.JPanel;
import javax.swing.JCheckBox;
import java.awt.GridLayout;

public class TerminkalenderFrame extends ExtendedFrame {

	private static final long serialVersionUID = 1L;

	private JTable tblTermine;
	private JTextPane textDetails;
	private Terminkalender kalender;	
	
	private JPopupMenu terminPopup;
	private JMenuItem popupTerminOeffnen;
	private JMenuItem popupTerminLoeschen;
	private JMenuItem popupTerminVerfassen;
	
	private ArrayList<Termin> hiddenTermine;
	private ArrayList<Termin> allTermine;
	
	private JPanel panel;
	
	public TerminkalenderFrame(boolean neu) {
		terminPopup = new JPopupMenu();
		
		popupTerminOeffnen = new JMenuItem("Öffnen");
		popupTerminOeffnen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel)tblTermine.getModel();
			  
				int viewZeile = tblTermine.getSelectedRow();
				if(viewZeile < 0)
					return;
				
				int row = tblTermine.convertRowIndexToModel(viewZeile);
				Termin referenz = (Termin)model.getValueAt(row, 0);
				
				bearbeiteTermin(referenz);
			}
		});
    	terminPopup.add(popupTerminOeffnen);
    	
    	popupTerminVerfassen = new JMenuItem("Verfassen");
    	popupTerminVerfassen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				neuerTermin();
			}
		});
    	terminPopup.add(popupTerminVerfassen);
    	
    	popupTerminLoeschen = new JMenuItem("Löschen");
    	popupTerminLoeschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Termin[] termine = ausgewaehlterTermin();
				
				for(Termin t : termine) {
					kalender.löscheTermin(t);
					allTermine.remove(t);
				}
				aktualisiere2Tabelle();
			}
		});
    	
		terminPopup.add(popupTerminLoeschen);
		
		
		
		
		
		kalender = Benutzer.getInstanz().getTermine();
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu mnDatei = new JMenu("Datei");
		menuBar.add(mnDatei);
		
		
		
		JMenuItem mntmTerminHinzufgen = new JMenuItem("Termin hinzufügen");
		mntmTerminHinzufgen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				neuerTermin();
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
		
		panel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		splitPane.setLeftComponent(scrollPane);
		
		final JSplitPane splitPane_1 = new JSplitPane();
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
				"Referenz", "Betreff", "Beschreibung", "Datum"
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
		

	
		tblTermine.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					DefaultTableModel model = (DefaultTableModel)tblTermine.getModel();
				  
					int viewZeile = tblTermine.getSelectedRow();
					if(viewZeile < 0)
						return;
				
					int row = tblTermine.convertRowIndexToModel(viewZeile);
					Termin referenz = (Termin)model.getValueAt(row, 0);
				  
					bearbeiteTermin(referenz);
				}
			}
			
			public void mousePressed(MouseEvent e) {
				oeffneTerminPopup(e);
			}
			
			public void mouseReleased(MouseEvent e) {
				oeffneTerminPopup(e);
			}
			
		});
		
		
	
	
		hiddenTermine = new ArrayList<Termin>();
	
		allTermine = new ArrayList<Termin>(); //speichert alle existierenden Termine in mango ab
		for (Termin t : Benutzer.getInstanz().getTermine()){
			allTermine.add(t);
		}
	
		final JScrollPane scrollPane_1 = new JScrollPane(tblTermine);
		splitPane_1.setLeftComponent(scrollPane_1);
	
		textDetails = new JTextPane();
		splitPane_1.setRightComponent(textDetails);
		getContentPane().add(splitPane);
		
		
		aktualisiere2Tabelle();
		ladeBenutzer();	

		if(neu)
			neuerTermin();
}
	
	
	
	
	
	
	
	
	/*private void aktualisiereTabelle() {
		DefaultTableModel model = (DefaultTableModel)tblTermine.getModel();
		model.setRowCount(0);
		
		for(Termin t : kalender) {
			model.addRow(new Object[] {t.getBetreff(), t.getText(), t.getStart().toString()});
		}
	}*/

/*	private void aktualisiere2Tabelle() {//Backup
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
			EinwegKalender.löscheTermin(a);
		}
	}*/
	
	
	private void aktualisiere2Tabelle() {
		DefaultTableModel model = (DefaultTableModel)tblTermine.getModel();
		model.setRowCount(0);
		
		Terminkalender einwegKalender = new Terminkalender();		
		
		for(int i=0; i< allTermine.size(); i++)
		{
			if(hiddenTermine.size()>0)
			{
				if(!hiddenTermine.contains(allTermine.get(i)))
				{
					einwegKalender.addTermin(allTermine.get(i));
				}
			}
			else
			{
				einwegKalender.addTermin(allTermine.get(i));
			}
		}
		
		int anzahl = einwegKalender.getSize();
		
		for(int i=0; i<anzahl;i++) {
			Termin a = einwegKalender.getOldest();
			model.addRow(new Object[] {a, a.getBetreff(), a.getText(), a.getStart().toString()});
			einwegKalender.löscheTermin(a);
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
	
	
	
	private void neuerTermin() {
		TerminFrame tf = new TerminFrame();
		Termin dummy = tf.showDialog();
		
		if(dummy != null)
		{
			kalender.addTermin(dummy);
			allTermine.add(dummy);
			if(kalender.ueberschneidung(dummy))
			{
				JOptionPane.showMessageDialog(this, "ACHTUNG! Überschneidung mit bereits vorhandenem Termin. Evtl. Sollten Sie ihre Termine überprüfen.", "Warning", JOptionPane.WARNING_MESSAGE);
			}
			aktualisiere2Tabelle();
		}
		
		
	}
	
	private void bearbeiteTermin(Termin t){
		TerminFrame tf = new TerminFrame(t);
		t = tf.showDialog();
		
		int zeile = tblTermine.convertRowIndexToModel(tblTermine.getSelectedRow());
		aktualisiere2Tabelle();
		int zeileView = tblTermine.convertRowIndexToView(zeile);
		tblTermine.setRowSelectionInterval(zeileView, zeileView);
		
	}
	
	
	
	private void ladeBenutzer() {
		

		ArrayList<String> apfel = new ArrayList<String>();//speichert alle Konten in apfel
		for (MailChecker checker : Benutzer.getInstanz()) {
			MailAccount ma = checker.getAccount();
			
			apfel.add(ma.getBenutzer());
		}
		
		for(String ben : apfel) {
			JCheckBox cb = new JCheckBox(ben);
			
			cb.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					JCheckBox cb = (JCheckBox) arg0.getSource();
					String text = cb.getText();
					
					if(arg0.getStateChange()==ItemEvent.SELECTED)
					{
						ArrayList<Termin> temp = new ArrayList<Termin>();
						for(Termin t : hiddenTermine){
							String benutzer = t.getBenutzerkonto();
							
							if(text.equals(benutzer))
							{
								temp.add(t);
							}
						}
						for(Termin t : temp){
							hiddenTermine.remove(t);
						}
							
					}
					else
					{
						for(Termin t: allTermine){
							String benutzer = t.getBenutzerkonto();
							if(text.equals(benutzer))//Name?
							{
								hiddenTermine.add(t);
							}
						}
						
					}
					aktualisiere2Tabelle();
				}
			});
			
			cb.setSelected(true);
			panel.add(cb);
			panel.revalidate();
			panel.repaint();
		}
	}
	
	
	private Termin[] ausgewaehlterTermin(){
		Termin[] termine = new Termin[tblTermine.getSelectedRowCount()];
		int[] indizes = tblTermine.getSelectedRows();
		DefaultTableModel model = (DefaultTableModel)tblTermine.getModel();
		
		for(int i = 0; i < termine.length; i++) {
			termine[i] = (Termin)model.getValueAt(indizes[i], 0);
		}
		
		return termine;
	}
	
	
	private void oeffneTerminPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			int zeile = tblTermine.rowAtPoint(e.getPoint());
			int spalte = tblTermine.columnAtPoint(e.getPoint());
			
			if(zeile >= 0 && spalte >= 0) {
				tblTermine.setRowSelectionInterval(zeile, zeile);
				
				terminPopup.show(tblTermine, e.getX(), e.getY());
			}
	    }
	}
	
}
