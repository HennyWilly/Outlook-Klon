package de.outlook_klon.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextPane;
import javax.swing.JList;

import de.outlook_klon.logik.kontakte.Kontakt;
import de.outlook_klon.logik.kontakte.Kontaktverwaltung;

public class AdressbuchFrame extends ExtendedFrame {
	private static final long serialVersionUID = 2142631007771154882L;

	private JPopupMenu tablePopup;
	private JMenuItem popupTabelleOeffnen;
	private JMenuItem popupTabelleLoeschen;
	private JMenuItem popupTabelleVerfassen;
	private JMenu popupTabelleListeHinzufügen;
	
	private JPopupMenu listenPopup;
	private JMenuItem popupListenUmbennen;
	private JMenuItem popupListenLoeschen;
	private JMenuItem popupListenVerfassen;
	
	private JTable tblKontakte;
	private JTextPane txtDetails;
	private JList<String> lstListen;

	private JMenuItem mntDateiNeuKontakt;
	private JMenuItem mntDateiNeuListe;
	private JMenuItem mntDateiBeenden;
	
	private MainFrame parent;
	private Kontaktverwaltung verwaltung;
	
	private final class ListenDialog extends ExtendedDialog<String> {
		private static final long serialVersionUID = 1L;
		
		private String mListe;
		private JTextField txtListe;
		
		private void initGUI() {
			setSize(355, 130);
			
			txtListe = new JTextField();
			txtListe.setColumns(10);
			
			JButton btnOK = new JButton("OK");
			btnOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					mListe = txtListe.getText();
					close();
				}
			});
			
			JButton btnAbbruch = new JButton("Abbruch");
			btnAbbruch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					close();
				}
			});
			
			JLabel lblNameDerListe = new JLabel("Name der Liste:");
			GroupLayout groupLayout = new GroupLayout(getContentPane());
			groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addGroup(groupLayout.createSequentialGroup()
								.addComponent(btnOK, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btnAbbruch))
							.addComponent(lblNameDerListe)
							.addComponent(txtListe, GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE))
						.addContainerGap())
			);
			groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
						.addGap(10)
						.addComponent(lblNameDerListe)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(txtListe, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnOK)
							.addComponent(btnAbbruch))
						.addContainerGap())
			);
			getContentPane().setLayout(groupLayout);
		}
		
		public ListenDialog() {
			initGUI();

			setTitle("Neue Liste erstellen");
		}
		
		public ListenDialog(String liste) {
			initGUI();
			
			txtListe.setText(liste);
			setTitle("Liste bearbeiten");
		}
		
		@Override
		protected String getDialogResult() {
			return mListe;
		}
	}
	
	private void initTabelle(JSplitPane verticalSplit) {
		tablePopup = new JPopupMenu();
		
		popupTabelleOeffnen = new JMenuItem("Öffnen");
		popupTabelleOeffnen.addActionListener(new ActionListener() {
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
    	tablePopup.add(popupTabelleOeffnen);
		
    	popupTabelleVerfassen = new JMenuItem("Verfassen");
    	popupTabelleVerfassen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				verfassen(ausgewaehlteKontakte());
			}
		});
    	tablePopup.add(popupTabelleVerfassen);
    	
    	popupTabelleLoeschen = new JMenuItem("Löschen");
    	popupTabelleLoeschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Kontakt[] kontakte = ausgewaehlteKontakte();
				String liste = aktuelleListe();
				
				for(Kontakt k : kontakte) 
					verwaltung.löscheKontakt(k, liste);
				
				aktualisiereTabelle(liste);
			}
		});
		tablePopup.add(popupTabelleLoeschen);
		
		popupTabelleListeHinzufügen = new JMenu();
		tablePopup.add(popupTabelleListeHinzufügen);
		
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
				"Referenz", "Name", "E-Mail-Adresse", "Tel. dienstlich"
			}
		) {
			private static final long serialVersionUID = 1L;
			Class<?>[] columnTypes = new Class<?>[] {
				Kontakt.class, String.class, String.class, String.class
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
					DefaultListSelectionModel sender = (DefaultListSelectionModel) e.getSource();			
					int row = sender.getMinSelectionIndex();
					if(row == -1)
						return;
					
					DefaultTableModel model = (DefaultTableModel)tblKontakte.getModel();
					int length = model.getDataVector().size();
					
					if(length > 0) {
						int zeileModel = tblKontakte.convertRowIndexToModel(row);
						
						Kontakt referenz = (Kontakt)model.getValueAt(zeileModel, 0);
						aktualisiereDetails(referenz);
					}
					else {
						txtDetails.setEditable(true);
						txtDetails.setText(null);
						txtDetails.setEditable(false);
					}
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
	
	private void initListen(JSplitPane horizontalSplit) {
		listenPopup = new JPopupMenu();
		
		popupListenUmbennen = new JMenuItem("Umbennen");
		popupListenUmbennen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listeUmbenennen(aktuelleListe());
			}
		});
		listenPopup.add(popupListenUmbennen);
		
		popupListenVerfassen = new JMenuItem("Verfassen");
		popupListenVerfassen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Kontakt[] kontakte = verwaltung.getKontakte(aktuelleListe());
				verfassen(kontakte);
			}
		});
		listenPopup.add(popupListenVerfassen);
    	
    	popupListenLoeschen = new JMenuItem("Löschen");
    	popupListenLoeschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<String> model = (DefaultListModel<String>) lstListen.getModel();
				String liste = aktuelleListe();
				
				verwaltung.löscheListe(liste);
				model.removeElement(liste);
				
				aktualisiereTabelle(liste);
			}
		});
    	listenPopup.add(popupListenLoeschen);
		
		lstListen = new JList<String>(new DefaultListModel<String>());
		lstListen.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				aktualisiereTabelle(aktuelleListe());
			}
		});

		lstListen.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					String liste = aktuelleListe();
					
					if(!Kontaktverwaltung.DEFAULT.equals(liste))
						listeUmbenennen(aktuelleListe());
				}
			}
			
			public void mousePressed(MouseEvent e) {
				oeffnePopupListen(e);
			}
			
			public void mouseReleased(MouseEvent e) {
				oeffnePopupListen(e);
			}
		});
		
		JScrollPane listerScroller = new JScrollPane(lstListen);
		horizontalSplit.setLeftComponent(listerScroller);
	}
	
	private void initGUI() {
		JSplitPane horizontalSplit = new JSplitPane();
		getContentPane().add(horizontalSplit, BorderLayout.CENTER);
		
		JSplitPane verticalSplit = new JSplitPane();
		verticalSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		horizontalSplit.setRightComponent(verticalSplit);
		
		initTabelle(verticalSplit);
		
		txtDetails = new JTextPane();
		JScrollPane detailsScroller = new JScrollPane(txtDetails);
		verticalSplit.setRightComponent(detailsScroller);
		
		initListen(horizontalSplit);
		
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
				neuerKontakt();
			}
		});
		mnNewMenu.add(mntDateiNeuKontakt);
		
		mntDateiNeuListe = new JMenuItem("Neue Kontaktliste");
		mntDateiNeuListe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ListenDialog ld = new ListenDialog();
				String liste = ld.showDialog();
				
				if(liste != null) {
					verwaltung.addListe(liste);
					aktualisiereKontaktlisten();
				}
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
	
	public AdressbuchFrame(MainFrame parent, Kontaktverwaltung kv, boolean neu) {
		setTitle("Adressbuch");
		this.parent = parent;
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
		
		tablePopup.remove(popupTabelleListeHinzufügen);
		popupTabelleListeHinzufügen = generiereListenmenü();
		if(popupTabelleListeHinzufügen.getMenuComponentCount() > 0)
			tablePopup.add(popupTabelleListeHinzufügen);
	}
	
	private void aktualisiereTabelle(String liste) {		
		if(liste == null) 
			return;
		
		DefaultTableModel model = (DefaultTableModel)tblKontakte.getModel();
		model.setRowCount(0);
		
		Kontakt[] kontakte = verwaltung.getKontakte(liste);
		if(kontakte == null)
			return;
		
		for(Kontakt k : kontakte) {
			model.addRow(new Object[] {k, k.getAnzeigename(), k.getMail1(), k.getTelDienst()});
		}
		
		aktualisiereDetails(null);
	}
	
	private String aktuelleListe() {
		return (String)lstListen.getSelectedValue();
	}
	
	private void aktualisiereDetails(Kontakt k) {
		StringBuilder sb = new StringBuilder();
		
		if(k != null) {
			if(!k.getVorname().trim().isEmpty())
				sb.append("Vorname: ").append(k.getVorname()).append('\n');
			if(!k.getNachname().trim().isEmpty())
				sb.append("Nachname: ").append(k.getNachname()).append('\n');
			if(!k.getAnzeigename().trim().isEmpty())
				sb.append("Anzeigename: ").append(k.getAnzeigename()).append('\n');
			if(!k.getSpitzname().trim().isEmpty())
				sb.append("Spitzname: ").append(k.getSpitzname()).append('\n');
			if(k.getMail1() != null)
				sb.append("E-Mail-Adresse: ").append(k.getMail1().toUnicodeString()).append('\n');
			if(k.getMail2() != null)
				sb.append("2. E-Mail-Adresse: ").append(k.getMail2().toUnicodeString()).append('\n');
			if(!k.getTelDienst().trim().isEmpty())
				sb.append("Telefonnummer (dienstlich): ").append(k.getTelDienst()).append('\n');
			if(!k.getTelPrivat().trim().isEmpty())
				sb.append("Telefonnummer (privat): ").append(k.getTelPrivat()).append('\n');
			if(!k.getTelMobil().trim().isEmpty())
				sb.append("Telefonnummer (mobil): ").append(k.getTelMobil()).append('\n');
		}
		
		txtDetails.setEditable(true);
		txtDetails.setText(sb.toString());
		txtDetails.setEditable(false);
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
			int zeile = tblKontakte.convertRowIndexToModel(tblKontakte.getSelectedRow());
			aktualisiereTabelle(aktuelleListe());
			int zeileView = tblKontakte.convertRowIndexToView(zeile);
			tblKontakte.setRowSelectionInterval(zeileView, zeileView);
		}
	}
	
	private void listeUmbenennen(String liste) {
		ListenDialog ld = new ListenDialog(liste);
		String neuerName = ld.showDialog();
		
		if(neuerName != null) {
			try {
				verwaltung.renameListe(liste, neuerName);
				aktualisiereKontaktlisten();
				lstListen.setSelectedValue(neuerName, true);
			}
			catch(IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
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
	
	private void verfassen(Kontakt[] kontakte) {
		parent.neueMail(kontakte);
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
	
	private void oeffnePopupListen(MouseEvent e) {
		if (e.isPopupTrigger()) {
			int zeile = lstListen.locationToIndex(e.getPoint());
			
			if(zeile >= 0) {
				lstListen.setSelectedIndex(zeile);
				
				String liste = lstListen.getSelectedValue();
				boolean istAdressbuch = liste.equals(Kontaktverwaltung.DEFAULT);
				
				popupListenLoeschen.setEnabled(!istAdressbuch);
				popupListenUmbennen.setEnabled(!istAdressbuch);
				
				listenPopup.show(lstListen, e.getX(), e.getY());
			}
	    }
	}
	
	private JMenu generiereListenmenü() {
		JMenu menu = new JMenu("Zu Liste zuordnen");
		DefaultListModel<String> model = (DefaultListModel<String>) lstListen.getModel();

		for(int i = 0; i < model.getSize(); i++) {
			String item = model.get(i);
			if(!Kontaktverwaltung.DEFAULT.equals(item)) {
				JMenuItem menuItem = new JMenuItem(item);
				menuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						String titel = ((JMenuItem)arg0.getSource()).getText();
						listeHinzufuegen(ausgewaehlteKontakte(), titel);
					}
				});
				menu.add(menuItem);
			}
		}
		
		return menu;
	}
	
	private void listeHinzufuegen(Kontakt[] kontakte, String liste) {
		for(Kontakt k : kontakte) {
			try {
				verwaltung.addKontaktZuListe(k, liste);
			} catch (IllegalArgumentException ex) {
				//Ignoriere Fehler
			}
		}
	}
}
