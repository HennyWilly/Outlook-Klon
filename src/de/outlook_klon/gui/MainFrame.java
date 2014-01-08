package de.outlook_klon.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.GroupLayout;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import de.outlook_klon.logik.Benutzer;
import de.outlook_klon.logik.Benutzer.MailChecker;
import de.outlook_klon.logik.kontakte.Kontakt;
import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.mailclient.MailInfo;
import de.outlook_klon.logik.mailclient.OrdnerInfo;

import javax.swing.JSeparator;

/**
 * Diese Klasse stellt das Hauptfenster der Anwendung dar.
 * Hier werden die Ordnerstrukturen der Mailkonten und deren Mails angezeigt.
 * Von hier aus können alle anderen Fenster der Anwendung aufgerufen werden.
 * 
 * @author Hendrik Karwanni
 */
public class MainFrame extends ExtendedFrame {
	private static final long serialVersionUID = 817918826034684858L;
	
	private static DateFormat dateFormater = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM, Locale.getDefault());
	
	private JPopupMenu tablePopup;
	private JMenuItem popupOeffnen;
	private JMenuItem popupLoeschen;
	private JMenuItem popupAntworten;
	private JMenuItem popupWeiterleiten;
	private JMenu popupKopieren;
	private JMenu popupVerschieben;
	
	private JTable tblMails;
    private JMenuItem mntmEmail;
    private JMenuItem mntmKontakt;
    private JMenuItem mntmTermin;
    private JMenuItem mntmBeenden;
    private JButton btnAbrufen;
    private JTree tree;
    private HtmlEditorPane tpPreview;
    
    private final Benutzer benutzer;
    private JMenuItem mntmKonteneinstellungen;
    private JMenuItem mntmAdressbuch;
    private JMenuItem mntmKalendar;
	
    private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnDatei = new JMenu("Datei");
		menuBar.add(mnDatei);
		
		JMenu mnNewMenu = new JMenu("Neu");
		mnDatei.add(mnNewMenu);
		
		mntmEmail = new JMenuItem("E-Mail");
		mntmEmail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				neueMail();
			}
		});
		mnNewMenu.add(mntmEmail);
		
		mntmKontakt = new JMenuItem("Kontakt");
		mntmKontakt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				oeffneAdressbuchFrame(true);
			}
		});
		mnNewMenu.add(mntmKontakt);
		
		mntmTermin = new JMenuItem("Termin");
		mntmTermin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				oeffneKalenderFrame(true);
			}
		});
		mnNewMenu.add(mntmTermin);
		
		mntmBeenden = new JMenuItem("Beenden");
		mntmBeenden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		mnDatei.add(mntmBeenden);
		
		JMenu mnMeldungen = new JMenu("Meldungen");
		menuBar.add(mnMeldungen);
		
		JMenuItem mnMeldungenVerwalten = new JMenuItem("Meldungen verwalten");
		mnMeldungenVerwalten.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				oeffneMeldungen();
			}
		});
		mnMeldungen.add(mnMeldungenVerwalten);
		
		mnMeldungen.add(new JSeparator());
		
		JCheckBoxMenuItem mnMeldungenAbwesenheit = new JCheckBoxMenuItem("Abwesend");
		mnMeldungenAbwesenheit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem sender = (JCheckBoxMenuItem) e.getSource();
				
				boolean selected = sender.isSelected();
				benutzer.setAnwesend(!selected);
			}
		});
		mnMeldungen.add(mnMeldungenAbwesenheit);
		
		JMenu mnExtras = new JMenu("Extras");
		menuBar.add(mnExtras);
		
		mntmKonteneinstellungen = new JMenuItem("Konteneinstellungen");
		mntmKonteneinstellungen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				oeffneKontoverwaltungFrame();
			}
		});
		
		mntmAdressbuch = new JMenuItem("Adressbuch");
		mntmAdressbuch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				oeffneAdressbuchFrame(false);
			}
		});
		mnExtras.add(mntmAdressbuch);
		
		mntmKalendar = new JMenuItem("Kalendar");
		mntmKalendar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				oeffneKalenderFrame(false);
			}
		});
		mnExtras.add(mntmKalendar);
		
		mnExtras.add(new JSeparator());
		mnExtras.add(mntmKonteneinstellungen);
    }
    
    private void initTabellePopup() {
    	popupOeffnen = new JMenuItem("Öffnen");
    	popupOeffnen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel)tblMails.getModel();
			  
				int viewZeile = tblMails.getSelectedRow();
				if(viewZeile < 0)
					return;
				
				int row = tblMails.convertRowIndexToModel(viewZeile);
				MailInfo mailID = (MailInfo)model.getValueAt(row, 0);
				
				oeffneMail(mailID);
			}
		});
    	
    	popupLoeschen = new JMenuItem("Löschen");
		popupLoeschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loescheMail();
			}
		});
		
		popupAntworten = new JMenuItem("Antworten");
		popupAntworten.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel)tblMails.getModel();
				  
				int viewZeile = tblMails.getSelectedRow();
				if(viewZeile < 0)
					return;
				
				int row = tblMails.convertRowIndexToModel(viewZeile);
				MailInfo mailID = (MailInfo)model.getValueAt(row, 0);
				
				antworten(mailID);
			}
		});
		
		popupWeiterleiten = new JMenuItem("Weiterleiten");
		popupWeiterleiten.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel)tblMails.getModel();
				  
				int viewZeile = tblMails.getSelectedRow();
				if(viewZeile < 0)
					return;
				
				int row = tblMails.convertRowIndexToModel(viewZeile);
				MailInfo mailID = (MailInfo)model.getValueAt(row, 0);
				
				weiterleiten(mailID);
			}
		});
		
		popupKopieren = new JMenu("Kopieren");
		popupVerschieben = new JMenu("Verschieben");
		
		tablePopup = new JPopupMenu();
		tablePopup.add(popupOeffnen);
		tablePopup.add(popupLoeschen);
		tablePopup.add(popupAntworten);
		tablePopup.add(popupWeiterleiten);
		tablePopup.add(popupKopieren);
		tablePopup.add(popupVerschieben);
    }
    
    private void initTabelle(JSplitPane verticalSplitPane) {
		tblMails = new JTable() {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {                
	        	return false;               
	        };
	    };	
		tblMails.setModel(new DefaultTableModel(new Object[][] { }, new String[] { "MailInfo", "Betreff", "Von", "Datum" }) {
				private static final long serialVersionUID = 1L;
				Class<?>[] columnTypes = new Class<?>[] {
					MailInfo.class, String.class, InternetAddress.class, Date.class
				};
				public Class<?> getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
			});    		
		
		DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 6837957351164997131L;

			public Component getTableCellRendererComponent(final JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				if(value instanceof Date) {
					value = dateFormater.format(value);
				}
				else if(value instanceof InternetAddress) {
					InternetAddress data = (InternetAddress)value;
					String personal = data.getPersonal();
					String address = data.getAddress();
					
					if(personal != null && !personal.trim().isEmpty()) {
						value = personal;
					}
					else {
						value = address;
					}
				}
				
				Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        	
	        	DefaultTableModel model = (DefaultTableModel)table.getModel();
	        	Object obj = model.getValueAt(row, 0);
	        	if(obj instanceof MailInfo) {
	        		MailInfo info = (MailInfo) obj;
	        		if(isSelected || info.isRead()) 
    					comp.setFont(comp.getFont().deriveFont(Font.PLAIN));
    				else
    					comp.setFont(comp.getFont().deriveFont(Font.BOLD)); 
	        	}
	        	
	        	return comp;
    		}
		};
		
		tblMails.setDefaultRenderer(String.class, cellRenderer);
		tblMails.setDefaultRenderer(Date.class, cellRenderer);
		tblMails.setDefaultRenderer(InternetAddress.class, cellRenderer);
		
	    TableRowSorter<TableModel> myRowSorter = new TableRowSorter<TableModel>(tblMails.getModel());
	    myRowSorter.setSortsOnUpdates(true);
	    tblMails.setRowSorter(myRowSorter);
		
		tblMails.removeColumn(tblMails.getColumn("MailInfo"));
		tblMails.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()) {
					DefaultTableModel model =  (DefaultTableModel)tblMails.getModel();
					int viewZeile = tblMails.getSelectedRow();
					
					MailInfo info = null;
					
					if(viewZeile >= 0) {
						int zeile = tblMails.convertRowIndexToModel(viewZeile);
						info = (MailInfo) model.getValueAt(zeile, 0);
					}
					
					previewAnzeigen(info);
				}
			}
		});
		
		tblMails.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					DefaultTableModel model = (DefaultTableModel)tblMails.getModel();
					  
					int viewZeile = tblMails.getSelectedRow();
					if(viewZeile < 0)
						return;
					
					int row = tblMails.convertRowIndexToModel(viewZeile);
					MailInfo mailID = (MailInfo)model.getValueAt(row, 0);
					  
					oeffneMail(mailID);
				}
			}
			
			public void mousePressed(MouseEvent e) {
				oeffnePopupTabelle(e);
			}
			
			public void mouseReleased(MouseEvent e) {
				oeffnePopupTabelle(e);
			}
		});
		
		JScrollPane mailScroller = new JScrollPane(tblMails);
		verticalSplitPane.setLeftComponent(mailScroller);
    }
    
    private void initTree(JSplitPane splitPane) {
    	DefaultMutableTreeNode root = new DefaultMutableTreeNode("[root]");
		tree = new JTree();
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			private static final long serialVersionUID = 3057355870823054419L;
			
			private Icon mailIcon = new ImageIcon("data/mail.png");
			private Icon openFolderIcon = UIManager.getIcon("Tree.openIcon");
			private Icon closedFolderIcon = UIManager.getIcon("Tree.closedIcon");
			
			@Override
            public Component getTreeCellRendererComponent(JTree tree,
                    Object value, boolean selected, boolean expanded,
                    boolean isLeaf, int row, boolean focused) {

				
				if(value instanceof DefaultMutableTreeNode) {
                	Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
					if(userObject instanceof OrdnerInfo) {
                		OrdnerInfo ordner = (OrdnerInfo) userObject;
                		if(ordner.getAnzahlUngelesen() > 0) {
                			value = String.format("<html><b>%s (%d)</b></html>", ordner.getName(), ordner.getAnzahlUngelesen());
                		}
                		
					}
				}
				
                Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);

                if(value instanceof DefaultMutableTreeNode) {
                	Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                	if(userObject instanceof MailChecker) {
	                	setIcon(mailIcon);
                	}
                	else if(userObject instanceof OrdnerInfo) {
		                if(expanded) {
		                	setIcon(openFolderIcon);
		                }
		                else {
		                	setIcon(closedFolderIcon);
		                }
                	}
                }
                return c;
            }
		});
		tree.addTreeExpansionListener(new TreeExpansionListener() {
			@Override
			public void treeExpanded(TreeExpansionEvent arg0) { }
			
			@Override
			public void treeCollapsed(TreeExpansionEvent e) {
				DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
				
				TreePath nodePath = new TreePath(model.getPathToRoot(node));
				TreePath path = e.getPath();
				
				if(path.isDescendant(nodePath)){
					tree.setSelectionPath(path);
				}
			}
		});
		tree.setRootVisible(false);
		tree.setEditable(true);
		tree.setModel(new DefaultTreeModel(root));
		tree.expandPath(new TreePath(root.getPath()));
		ladeOrdner();
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
				if(selectedNode == null) {
					throw new NullPointerException("Hier tritt manchmal eine Exception auf :(");
				}
				
				Object userObject = selectedNode.getUserObject();
				
				if(!(userObject instanceof MailChecker)) {
					//TODO Test Mich!!!
					
					MailChecker checker = ausgewaehlterChecker();
					MailAccount account = checker.getAccount();
					OrdnerInfo ordner = nodeZuOrdner(selectedNode);
					
					String ordnerName = selectedNode.toString();
					
					setTitle(ordnerName + " - " + account.getAdresse().getAddress());
					
					ladeMails(checker, ordner.getPfad());
				}
				else {
					DefaultTableModel model = (DefaultTableModel)tblMails.getModel();
					model.setRowCount(0);
					
					setTitle(((MailChecker)userObject).getAccount().getAdresse().getAddress());
				}
			}
		});
		
		JScrollPane treeScroller = new JScrollPane(tree);
		splitPane.setLeftComponent(treeScroller);
    }
    
	public MainFrame() {
		setTitle("MailClient");
		benutzer = Benutzer.getInstanz();
		
		JSplitPane horizontalSplitPane = new JSplitPane();
		
		initTabellePopup();
		initTree(horizontalSplitPane);
		
		JSplitPane verticalSplitPane = new JSplitPane();
		verticalSplitPane.setContinuousLayout(true);
		verticalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		horizontalSplitPane.setRightComponent(verticalSplitPane);
		
		initTabelle(verticalSplitPane);
		
		tpPreview = new HtmlEditorPane();
		tpPreview.setEditable(false);
		
		JScrollPane previewScroller = new JScrollPane(tpPreview);
		verticalSplitPane.setRightComponent(previewScroller);
		
		JToolBar toolBar = new JToolBar();
		
		btnAbrufen = new JButton("Abrufen");
		btnAbrufen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		toolBar.add(btnAbrufen);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(toolBar, GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
				.addComponent(horizontalSplitPane, GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(horizontalSplitPane, GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
					.addGap(0))
		);
		getContentPane().setLayout(groupLayout);
		
		initMenu();
		
		this.addWindowListener(new WindowAdapter() {
			@Override
		    public void windowClosing(WindowEvent windowEvent) {
				try {
					benutzer.speichern();
				} catch (IOException e) {
					Component component = windowEvent.getComponent();
					JOptionPane.showMessageDialog(component, "Die Einstellungen konnten nicht gespeichert werden!", 
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
		        System.exit(0);
		    }
		});
		
		benutzer.starteChecker();
	}
	
	private void antworten(MailInfo info) {
		//TODO Testen
		
		MailAccount acc = ausgewaehlterAccount();
		OrdnerInfo pfad = nodeZuOrdner((DefaultMutableTreeNode)tree.getLastSelectedPathComponent());
	     
		MailFrame mf;
		try {
			mf = new MailFrame(info, pfad.getPfad(), acc, false);
			
			mf.setSize(this.getSize());
			mf.setExtendedState(this.getExtendedState());
			mf.setVisible(true);
		} catch (MessagingException e) {
			JOptionPane.showMessageDialog(this, "Antworten fehlgeschlagen: \n" + e.getMessage(), 
					"Fehler", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void weiterleiten(MailInfo info) {
		//TODO Testen
		
		MailAccount acc = ausgewaehlterAccount();
		OrdnerInfo pfad = nodeZuOrdner((DefaultMutableTreeNode)tree.getLastSelectedPathComponent());
	     
		MailFrame mf;
		try {
			mf = new MailFrame(info, pfad.getPfad(), acc, true);
			
			mf.setSize(this.getSize());
			mf.setExtendedState(this.getExtendedState());
			mf.setVisible(true);
		} catch (MessagingException e) {
			JOptionPane.showMessageDialog(this, "Weiterleiten fehlgeschlagen: \n" + e.getMessage(), 
					"Fehler", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void neueMail() {
		if(benutzer.getAnzahlKonten() == 0) {
			keinMailAccount();
			return;
		}
		
		MailFrame mf = new MailFrame();
		
		mf.setSize(this.getSize());
		mf.setExtendedState(this.getExtendedState());
		mf.setVisible(true);
	}
	
	public void neueMail(Kontakt[] kontakte) {
		if(benutzer.getAnzahlKonten() == 0) {
			keinMailAccount();
			return;
		}
		
		MailFrame mf = new MailFrame(kontakte);
		
		mf.setSize(this.getSize());
		mf.setExtendedState(this.getExtendedState());
		mf.setVisible(true);
	}
	
	private void oeffneKalenderFrame(boolean neu) {
		TerminkalenderFrame tkf = new TerminkalenderFrame(neu);
		
		tkf.setSize(this.getSize());
		tkf.setExtendedState(this.getExtendedState());
		tkf.setVisible(true);
	}
	
	private void oeffneAdressbuchFrame(boolean neu) {
		AdressbuchFrame af = new AdressbuchFrame(this, neu);

		af.setSize(this.getSize());
		af.setExtendedState(this.getExtendedState());
		af.setVisible(true);
	}
	
	private void oeffneKontoverwaltungFrame() {
		KontoverwaltungFrame vf = new KontoverwaltungFrame();

		MailAccount[] accounts = vf.showDialog();
		if(accounts != null) {
			boolean refresh = false;
			
			DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
			
			outer:
			for(int i = 0; i < rootNode.getChildCount(); i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode.getChildAt(i);
				MailChecker treeChecker = (MailChecker) node.getUserObject();
				MailAccount treeAccount = treeChecker.getAccount();
				
				for(MailAccount acc : accounts) {
					if(acc == treeAccount) 
						continue outer;
				}
				
				try {
					benutzer.entferneMailAccount(treeAccount);
					rootNode.remove(node);
					treeModel.reload();
					refresh = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			for(MailAccount acc : accounts) {
				if(benutzer.addMailAccount(acc)) {
					refresh = true;
				}
			}
			if(refresh)
				ladeOrdner();
		}
	}
	
	private void sortTable() {
		TableRowSorter<?> sorter = (TableRowSorter<?>) tblMails.getRowSorter();
		
	    ArrayList<RowSorter.SortKey> keys = new ArrayList<RowSorter.SortKey>();
	    RowSorter.SortKey key = new RowSorter.SortKey(0, SortOrder.ASCENDING);
	    keys.add(key);
	    sorter.setSortKeys(keys);
	    sorter.sort();
	}

	private OrdnerInfo nodeZuOrdner(DefaultMutableTreeNode knoten) {
		//TODO Testen
		
		Object userObject = knoten.getUserObject();
		if(!(userObject instanceof OrdnerInfo))
			return null;
		
		return (OrdnerInfo) userObject;
	}
	
	private void ordnerZuNode(OrdnerInfo ordner, DefaultMutableTreeNode parent, int tiefe) {
		//TODO Testen
		
		String pfad = ordner.getPfad();
		String[] parts = ordner.getPfad().split("/");
		
		if(pfad.contains("/") && tiefe < parts.length - 1) {
			DefaultMutableTreeNode pfadKnoten = null;
			
			for(int j = 0; j < parent.getChildCount(); j++) {
				String pfadParent = parts[tiefe];
				
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(j);
				Object childObject = child.getUserObject();
				
				if(childObject instanceof OrdnerInfo) {
					OrdnerInfo childOrdner = (OrdnerInfo) childObject;
					if(childOrdner.getName().equals(pfadParent)) {
						pfadKnoten = child;
						break;
					}
				}
			}
			
			if(pfadKnoten == null)
				pfadKnoten = new DefaultMutableTreeNode(ordner);
			
			ordnerZuNode(ordner, pfadKnoten, tiefe + 1);
		}
		else {
			parent.add(new DefaultMutableTreeNode(ordner));
		}
	}
	
	private void ladeOrdner() {
		//TODO Testen
		
		DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
		
		int i = 0;
		outer:
		for(MailChecker checker : benutzer) {
			DefaultMutableTreeNode node = null;
			Enumeration<?> e = rootNode.children();
			while (e.hasMoreElements()) {
				node = (DefaultMutableTreeNode) e.nextElement();
			  	if (checker.equals(node.getUserObject())) {
				  continue outer;
			    }
		    }
		    
			DefaultMutableTreeNode accNode = new DefaultMutableTreeNode(checker);
			OrdnerInfo[] ordner = checker.getAccount().getOrdnerstruktur();
			
			for(int j = 0; j < ordner.length; j++) {
				ordnerZuNode(ordner[j], accNode, 0);
			}
			
			treeModel.insertNodeInto(accNode, rootNode, i);
			i++;
		}
		
		if(rootNode.getChildCount() != 0) {
			tree.setRootVisible(true);
			tree.expandPath(new TreePath(rootNode));
			tree.setRootVisible(false);
		}
	}
	
	private void ladeMails(MailChecker checker, String pfad) {
		MailInfo[] messages = checker.getMessages(pfad);
		
		DefaultTableModel model = (DefaultTableModel)tblMails.getModel();
		model.setRowCount(0);
		for(MailInfo info : messages) {
			model.addRow(new Object[] {info, info.getSubject(), info.getSender(), info.getDate()});
		}
		sortTable();
	}

	private MailInfo[] ausgewaehlteMailInfo() {
		DefaultTableModel model = (DefaultTableModel)tblMails.getModel();
		
		MailInfo[] infos = new MailInfo[tblMails.getSelectedRowCount()];
		int[] indizes = tblMails.getSelectedRows();
		
		for(int i = 0; i < infos.length; i++) {
			int modelIndex = tblMails.convertRowIndexToModel(indizes[i]);
			
			infos[i] = (MailInfo)model.getValueAt(modelIndex, 0);
		}
		
		return infos;
	}
	
	private MailAccount ausgewaehlterAccount() {
		MailChecker checker = ausgewaehlterChecker();
		if(checker == null)
			return null;
		
		return checker.getAccount();
	}
	
	private MailChecker ausgewaehlterChecker() {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if(selectedNode == null)
			return null;
		
		Object userObject = null;
		
		do {
  		  userObject = selectedNode.getUserObject();
  		  if(userObject instanceof MailChecker)
  			  break;
  		  selectedNode = (DefaultMutableTreeNode)selectedNode.getParent();
  	  	} while(true);
		
		return (MailChecker)userObject;
	}
	
	private void oeffneMail(MailInfo info) {
		MailAccount acc = ausgewaehlterAccount();		
		DefaultMutableTreeNode selected = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();		
		OrdnerInfo pfad = nodeZuOrdner(selected);
	     
		MailFrame mf;
		try {
			mf = new MailFrame(info, pfad.getPfad(), acc);
			
			mf.setSize(this.getSize());
			mf.setExtendedState(this.getExtendedState());
			mf.setVisible(true);
		} catch (MessagingException e1) {
			JOptionPane.showMessageDialog(this, "Es ist ein Fehler beim Öffnen der Mail aufgetreten:\n" + e1.getMessage(), 
					"Fehler", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void oeffnePopupTabelle(MouseEvent e) {
		if (e.isPopupTrigger()) {
			int zeile = tblMails.rowAtPoint(e.getPoint());
			int spalte = tblMails.columnAtPoint(e.getPoint());
			
			if(zeile >= 0 && spalte >= 0) {
				tblMails.setRowSelectionInterval(zeile, zeile);
				
				tablePopup.remove(popupKopieren);
				popupKopieren = generiereOrdnerMenu(popupKopieren.getText(), "In Ordner kopieren");
				tablePopup.add(popupKopieren);

				tablePopup.remove(popupVerschieben);
				popupVerschieben = generiereOrdnerMenu(popupVerschieben.getText(), "In Ordner verschieben");
				tablePopup.add(popupVerschieben);
				
				tablePopup.show(tblMails, e.getX(), e.getY());
			}
	    }
	}
	
	private JMenuItem generiereOrdnerMenu(String pfad, String operation, DefaultMutableTreeNode node, boolean ordner, String itemTitel) {
		ActionListener menuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem item = (JMenuItem) e.getSource();
				String pfad = (String) item.getClientProperty("PFAD");
				String typ = (String) item.getClientProperty("TYP");
				
				if(typ.equals("Kopieren")) {
					kopiereMail(pfad);
				}
				else if(typ.equals("Verschieben")) {
					verschiebeMail(pfad);
				}
				else
					throw new IllegalArgumentException("Typ \'" + typ + "\' ungültig");
			}
		};
		
		String menuTitel = null;
		Object userObject = node.getUserObject();
		
		if(userObject instanceof MailChecker) {
			MailChecker checker = (MailChecker)userObject;
			MailAccount acc = checker.getAccount();
			menuTitel = acc.getAdresse().getAddress();
		}
		else {
			menuTitel = node.getUserObject().toString();
		}
		
		pfad += menuTitel;
		
		JMenuItem untermenu = null;
		int childCount = node.getChildCount();
		
		if(childCount > 0) {
			untermenu = new JMenu(menuTitel);
			
			if(ordner) {
				JMenuItem item = new JMenuItem(itemTitel);
				item.putClientProperty("TYP", operation);
				item.putClientProperty("PFAD", pfad);
				item.addActionListener(menuListener);
				
				untermenu.add(item);
				untermenu.add(new JSeparator());
			}
			
			for(int i = 0; i < childCount; i++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
				untermenu.add(generiereOrdnerMenu(pfad + "/", operation, child, true, itemTitel));
			}
		}
		else {
			untermenu = new JMenuItem(menuTitel);
			untermenu.putClientProperty("TYP", operation);
			untermenu.putClientProperty("PFAD", pfad);
			untermenu.addActionListener(menuListener);
		}
		
		return untermenu;
	}
	
	private JMenu generiereOrdnerMenu(String titel, String itemTitel) {
		DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
		
		for(int i = 0; i < rootNode.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) rootNode.getChildAt(i);
			
			if(child.getUserObject() == ausgewaehlterChecker()) {
				JMenu neu = new JMenu(titel);

				for(int j = 0; j < child.getChildCount(); j++) {
					DefaultMutableTreeNode subchild = (DefaultMutableTreeNode) child.getChildAt(j);
					neu.add(generiereOrdnerMenu("", titel, subchild, true, itemTitel));
				}
				
				return neu;
			}
		}
		
		return null;
	}
	
	private void kopiereMail(String ziel) {
		//TODO Testen
		
		MailAccount acc = ausgewaehlterAccount();
		MailInfo[] infos = ausgewaehlteMailInfo();
		OrdnerInfo quelle = nodeZuOrdner((DefaultMutableTreeNode)tree.getLastSelectedPathComponent());
		
		try {
			acc.kopiereMails(infos, quelle.getPfad(), ziel);
		} catch (MessagingException e) {
			JOptionPane.showMessageDialog(this, "Kopieren der Mail fehlgeschlagen: \n" + e.getMessage(), 
					"Fehler", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void verschiebeMail(String ziel) {
		//TODO Testen

		MailChecker checker = ausgewaehlterChecker();
		MailAccount acc = checker.getAccount();
		MailInfo[] infos = ausgewaehlteMailInfo();
		OrdnerInfo quelle = nodeZuOrdner((DefaultMutableTreeNode)tree.getLastSelectedPathComponent());
		
		try {
			acc.verschiebeMails(infos, quelle.getPfad(), ziel);
			checker.removeMailInfos(infos);
			
			DefaultTableModel model = (DefaultTableModel) tblMails.getModel();
			int row = tblMails.getSelectedRow();
			while(row != -1) {
				int mapped = tblMails.convertRowIndexToModel(row);
				model.removeRow(mapped);
				
				row = tblMails.getSelectedRow();
			}
		} catch (MessagingException e) {
			JOptionPane.showMessageDialog(this, "Verschieben der Mail fehlgeschlagen: \n" + e.getMessage(), 
					"Fehler", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void loescheMail() {
		//TODO Testen
		
		MailInfo[] infos = ausgewaehlteMailInfo();
		OrdnerInfo pfad = nodeZuOrdner((DefaultMutableTreeNode)tree.getLastSelectedPathComponent());
		MailChecker checker = ausgewaehlterChecker();
		MailAccount acc = checker.getAccount();
		
		try {
			if(acc.loescheMails(infos, pfad.getPfad())) {
				checker.removeMailInfos(infos);
				
				DefaultTableModel model = (DefaultTableModel) tblMails.getModel();
				int row = tblMails.getSelectedRow();
				while(row != -1) {
					int mapped = tblMails.convertRowIndexToModel(row);
					model.removeRow(mapped);
					
					row = tblMails.getSelectedRow();
				}
			}
		} catch (MessagingException e) {
			JOptionPane.showMessageDialog(this, "Löschen fehlgeschlagen: \n" + e.getMessage(), 
					"Fehler", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void previewAnzeigen(MailInfo info) {
		if(info == null) {
			tpPreview.setEditable(true);
			tpPreview.setText("");
			tpPreview.setEditable(false);
			
			return;
		}
		
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		OrdnerInfo pfad = nodeZuOrdner(selectedNode);
		
		MailAccount account = ausgewaehlterAccount();
		
		try {
			account.getMessageText(pfad.getPfad(), info);
		} catch (MessagingException ex) {
			JOptionPane.showMessageDialog(this, "Es ist ein Fehler beim Auslesen des Mail-Textes aufgetreten:\n" + ex.getMessage(),
					"Fehler", JOptionPane.ERROR_MESSAGE);
		}

		String text = info.getText();
		String contentType = info.getContentType();
		
		tpPreview.setText(text, contentType, true);
		tpPreview.setEditable(false);
		
		tpPreview.setCaretPosition(0);
	}
	
	private void keinMailAccount() {
		JOptionPane.showMessageDialog(this, "Es wurde noch kein Mail-Account hinzugefügt!\n" + 
				"Unter dem Menü \"Extras\" -> \"Konteneinstellungen\" können Sie Konten hinzufügen", 
				"Fehler", JOptionPane.ERROR_MESSAGE);
	}
	
	private void oeffneMeldungen() {
		MeldungsFrame mf = new MeldungsFrame();
		
		mf.setSize(this.getSize());
		mf.setExtendedState(this.getExtendedState());
		mf.setVisible(true);
	}

	public static void main(final String[] args) {
		final MainFrame mainFrame = new MainFrame();	
		
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
	}
}
