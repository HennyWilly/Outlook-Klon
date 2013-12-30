package de.outlook_klon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
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

public class AdressbuchFrame extends JFrame {
	private static final long serialVersionUID = 2142631007771154882L;

	private JPopupMenu tablePopup;
	private JMenuItem popupOeffnen;
	private JMenuItem popupLoeschen;
	
	private JTable tblKontakte;
	private JTextPane txtDetails;
	private JList<String> lstListen;

	private JMenuItem mntDateiNeuKontakt;
	private JMenuItem mntDateiNeuListe;
	private JMenuItem mntDateiBeenden;
	
	private Kontaktverwaltung verwaltung;
	
	private void close() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
	
	private void initTabelle(JSplitPane verticalSplit) {
		tablePopup = new JPopupMenu();
		
		popupOeffnen = new JMenuItem("Öffnen");
    	popupOeffnen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel)tblKontakte.getModel();
			  
				int viewZeile = tblKontakte.getSelectedRow();
				if(viewZeile < 0)
					return;
				
				int row = tblKontakte.convertRowIndexToModel(viewZeile);
				Kontakt referenz = (Kontakt)model.getValueAt(row, 0);
				
				bearbeiteKontakt(referenz);
			}
		});
    	tablePopup.add(popupOeffnen);
    	
    	popupLoeschen = new JMenuItem("Löschen");
		popupLoeschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Kontakt[] kontakte = ausgewaehlteKontakte();
				String liste = aktuelleListe();
				
				for(Kontakt k : kontakte) 
					verwaltung.löscheKontakt(k, liste);
				
				aktualisiereTabelle(liste);
			}
		});
		tablePopup.add(popupLoeschen);
		
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
		tblKontakte.getSelectionModel().addListSelectionListener(new ListSelectionListener() {	
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()) {					
					int row = e.getFirstIndex();
					if(row == -1)
						return;
					
					int zeileModel = tblKontakte.convertRowIndexToModel(row);
					DefaultTableModel model = (DefaultTableModel)tblKontakte.getModel();
					int length = model.getDataVector().size();
					
					if(length > 0) {
						Kontakt referenz = (Kontakt)model.getValueAt(zeileModel, 0);
						aktualisiereDetails(referenz);
					}
					else
						txtDetails.setText("");
				}
			}
		});
		
		tblKontakte.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					DefaultTableModel model = (DefaultTableModel)tblKontakte.getModel();
					  
					int viewZeile = tblKontakte.getSelectedRow();
					if(viewZeile < 0)
						return;
					
					int row = tblKontakte.convertRowIndexToModel(viewZeile);
					Kontakt referenz = (Kontakt)model.getValueAt(row, 0);
					  
					bearbeiteKontakt(referenz);
				}
			}
			
			public void mousePressed(MouseEvent e) {
				oeffnePopupTabelle(e);
			}
			
			public void mouseReleased(MouseEvent e) {
				oeffnePopupTabelle(e);
			}
		});
		
		JScrollPane kontakteScroller = new JScrollPane(tblKontakte);
		verticalSplit.setLeftComponent(kontakteScroller);
	}
	
	private void initGUI() {
		JSplitPane horizontalSplit = new JSplitPane();
		getContentPane().add(horizontalSplit, BorderLayout.CENTER);
		
		JSplitPane verticalSplit = new JSplitPane();
		verticalSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		horizontalSplit.setRightComponent(verticalSplit);
		
		initTabelle(verticalSplit);
		
		txtDetails = new JTextPane();
		verticalSplit.setRightComponent(txtDetails);
		
		lstListen = new JList<String>(new DefaultListModel<String>());
		lstListen.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				aktualisiereTabelle(aktuelleListe());
			}
		});
		
		JScrollPane listerScroller = new JScrollPane(lstListen);
		horizontalSplit.setLeftComponent(listerScroller);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnDatei = new JMenu("Datei");
		menuBar.add(mnDatei);
		
		JMenu mnNewMenu = new JMenu("Neu");
		mnDatei.add(mnNewMenu);
		
		mntDateiNeuKontakt = new JMenuItem("Neuer Kontakt");
		mntDateiNeuKontakt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				KontaktFrame kf = new KontaktFrame();
				Kontakt returnKontakt = kf.showDialog();
				
				if(returnKontakt != null) {
					verwaltung.addKontakt(returnKontakt);
					aktualisiereTabelle(aktuelleListe());
				}
			}
		});
		mnNewMenu.add(mntDateiNeuKontakt);
		
		mntDateiNeuListe = new JMenuItem("Neue Kontaktliste");
		mntDateiNeuListe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO Neue Kontaktliste erstellen
				
				aktualisiereKontaktlisten();
			}
		});
		mnNewMenu.add(mntDateiNeuListe);
		
		mntDateiBeenden = new JMenuItem("Beenden");
		mntDateiBeenden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		mnDatei.add(mntDateiBeenden);
	}
	
	public AdressbuchFrame(Kontaktverwaltung kv, boolean neu) {
		verwaltung = kv;
		
		initGUI();
		
		aktualisiereKontaktlisten();
		lstListen.setSelectedIndex(0);
		
		if(neu)
			neuerKontakt();
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
	
	private String aktuelleListe() {
		return (String)lstListen.getSelectedValue();
	}
	
	private void aktualisiereDetails(Kontakt k) {
		//TODO Detailansicht aktualisieren
	}
	
	private void neuerKontakt() {
		KontaktFrame kf = new KontaktFrame();
		Kontakt k = kf.showDialog();
		
		if(k != null) {
			verwaltung.addKontakt(k);
			aktualisiereTabelle(aktuelleListe());
		}
	}
	
	private void bearbeiteKontakt(Kontakt k) {
		KontaktFrame kf = new KontaktFrame(k);
		kf.showDialog();
		
		if( k != null) {
			aktualisiereTabelle(aktuelleListe());
		}
	}
	
	private Kontakt[] ausgewaehlteKontakte() {
		Kontakt[] kontakte = new Kontakt[tblKontakte.getSelectedRowCount()];
		int[] indizes = tblKontakte.getSelectedRows();
		DefaultTableModel model = (DefaultTableModel)tblKontakte.getModel();
		
		for(int i = 0; i < kontakte.length; i++) {
			kontakte[i] = (Kontakt)model.getValueAt(indizes[i], 0);
		}
		
		return kontakte;
	}

	private void oeffnePopupTabelle(MouseEvent e) {
		if (e.isPopupTrigger()) {
			int zeile = tblKontakte.rowAtPoint(e.getPoint());
			int spalte = tblKontakte.columnAtPoint(e.getPoint());
			
			if(zeile >= 0 && spalte >= 0) {
				tblKontakte.setRowSelectionInterval(zeile, zeile);
				
				tablePopup.show(tblKontakte, e.getX(), e.getY());
			}
	    }
	}
}
