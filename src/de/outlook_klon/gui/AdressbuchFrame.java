package de.outlook_klon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import java.awt.BorderLayout;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextPane;
import javax.swing.JList;

import de.outlook_klon.logik.kontakte.Kontakt;
import de.outlook_klon.logik.kontakte.Kontaktverwaltung;

public class AdressbuchFrame extends JFrame implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = 2142631007771154882L;
	
	private JTable tblKontakte;
	private JTextPane txtDetails;
	private JList<String> lstListen;

	private JMenuItem mntDateiNeuKontakt;
	private JMenuItem mntDateiNeuListe;
	private JMenuItem mntDateiBeenden;
	
	private Kontaktverwaltung verwaltung;
	
	public AdressbuchFrame(Kontaktverwaltung kv) {
		verwaltung = kv;
		
		JSplitPane horizontalSplit = new JSplitPane();
		getContentPane().add(horizontalSplit, BorderLayout.CENTER);
		
		JSplitPane verticalSplit = new JSplitPane();
		verticalSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		horizontalSplit.setRightComponent(verticalSplit);
		
		tblKontakte = new JTable() {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {                
	                return false;               
	        };
	    };
		tblKontakte.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null},
			},
			new String[] {
				"Referenz", "Name", "E-Mail-Adresse"
			}
		) {
			private static final long serialVersionUID = 1L;
			Class<?>[] columnTypes = new Class<?>[] {
				Kontakt.class, String.class, String.class
			};
			public Class<?> getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tblKontakte.removeColumn(tblKontakte.getColumn("Referenz"));
		
		tblKontakte.getColumnModel().getColumn(1).setPreferredWidth(91);
		tblKontakte.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblKontakte.getColumnModel().getSelectionModel().addListSelectionListener(this);
		verticalSplit.setLeftComponent(tblKontakte);
		
		txtDetails = new JTextPane();
		verticalSplit.setRightComponent(txtDetails);
		
		lstListen = new JList<String>(new DefaultListModel<String>());
		lstListen.addListSelectionListener(this);
		horizontalSplit.setLeftComponent(lstListen);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnDatei = new JMenu("Datei");
		menuBar.add(mnDatei);
		
		JMenu mnNewMenu = new JMenu("Neu");
		mnDatei.add(mnNewMenu);
		
		mntDateiNeuKontakt = new JMenuItem("Neuer Kontakt");
		mntDateiNeuKontakt.addActionListener(this);
		mnNewMenu.add(mntDateiNeuKontakt);
		
		mntDateiNeuListe = new JMenuItem("Neue Kontaktliste");
		mntDateiNeuListe.addActionListener(this);
		mnNewMenu.add(mntDateiNeuListe);
		
		mntDateiBeenden = new JMenuItem("Beenden");
		mntDateiBeenden.addActionListener(this);
		menuBar.add(mntDateiBeenden);
		
		aktualisiereKontaktlisten();
		lstListen.setSelectedIndex(0);
	}

	private void aktualisiereKontaktlisten() {
		String selected = lstListen.getSelectedValue();
		
		DefaultListModel<String> model = (DefaultListModel<String>)lstListen.getModel();
		model.clear();
		
		String[] listen = verwaltung.getListen();
		for(String liste : listen) {
			model.addElement(liste);
		}
		
		lstListen.setSelectedValue(selected, true);
	}
	
	private void aktualisiereTabelle(String liste) {
		DefaultTableModel model = (DefaultTableModel)tblKontakte.getModel();
		model.setRowCount(0);
		
		Kontakt[] kontakte = verwaltung.getKontakte(liste);
		for(Kontakt k : kontakte) {
			model.addRow(new Object[] {k, k.getVorname() + " " + k.getNachname(), k.getMail1()});
		}
	}
	
	private void aktualisiereDetails(Kontakt k) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object sender = e.getSource();

		if(sender == mntDateiNeuKontakt) {
			KontaktFrame kf = new KontaktFrame();
			Kontakt returnKontakt = kf.showDialog();
			
			if(returnKontakt != null) {
				verwaltung.addKontakt(returnKontakt);
				aktualisiereTabelle((String)lstListen.getSelectedValue());
			}
		}
		else if(sender == mntDateiNeuListe) { 
			
			
			aktualisiereKontaktlisten();
		}
		else if(sender == mntDateiBeenden) {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		Object sender = e.getSource();
		
		if(sender == lstListen) {
			aktualisiereTabelle((String)lstListen.getSelectedValue());
		}
		else if(sender == tblKontakte.getColumnModel().getSelectionModel()) {
			int row = e.getFirstIndex();
			DefaultTableModel model = (DefaultTableModel)tblKontakte.getModel();
			Kontakt referenz = (Kontakt)model.getValueAt(row, 0);
			
			aktualisiereDetails(referenz);
		}
	}

}
