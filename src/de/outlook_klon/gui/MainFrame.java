package de.outlook_klon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextPane;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;

import de.outlook_klon.logik.Benutzer;
import de.outlook_klon.logik.mailclient.Authentifizierungsart;
import de.outlook_klon.logik.mailclient.ImapServer;
import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.mailclient.MailInfo;
import de.outlook_klon.logik.mailclient.ServerSettings;
import de.outlook_klon.logik.mailclient.SmtpServer;
import de.outlook_klon.logik.mailclient.Verbindungssicherheit;

public class MainFrame extends JFrame implements ActionListener, TreeSelectionListener, ListSelectionListener {
	private static final long serialVersionUID = 817918826034684858L;
	
	private JTable tblMails;
    private JMenuItem mntmEmail;
    private JMenuItem mntmKontakt;
    private JMenuItem mntmTermin;
    private JMenuItem mntmBeenden;
    private JButton btnAbrufen;
    private JTree tree;
    private JTextPane tpPreview;
    
    private Benutzer benutzer;
	
	public MainFrame() {
		benutzer = new Benutzer();
		/*
		benutzer.addMailAccount(
		  	new MailAccount(
				new ImapServer(
					new ServerSettings(
					<Host>, 
					<Port>, 
						Verbindungssicherheit.SSL_TLS, 
						Authentifizierungsart.NORMAL
					)	
				),
				new SmtpServer(
					new ServerSettings(
						<Host>,
						<Port>,
						Verbindungssicherheit.STARTTLS,
						Authentifizierungsart.NORMAL
					)
				), 
				<Mail>, 
				<User>, 
				<PW>
			)
		);
		*/
		
		JSplitPane horizontalSplitPane = new JSplitPane();
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("[root]");
		tree = new JTree();
		tree.setRootVisible(false);
		tree.setEditable(true);
		tree.setModel(new DefaultTreeModel(root));
		tree.expandPath(new TreePath(root.getPath()));
		ladeOrdner();
		tree.addTreeSelectionListener(this);
		
		horizontalSplitPane.setLeftComponent(tree);
		
		JSplitPane verticalSplitPane = new JSplitPane();
		verticalSplitPane.setContinuousLayout(true);
		verticalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		horizontalSplitPane.setRightComponent(verticalSplitPane);
		
		tblMails = new JTable() {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {                
	                return false;               
	        };
	    };
		tblMails.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Betreff", "Von", "Datum"
			}
		) {
			private static final long serialVersionUID = 1L;
			Class<?>[] columnTypes = new Class<?>[] {
				String.class, String.class, String.class, String.class
			};
			public Class<?> getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		tblMails.removeColumn(tblMails.getColumn("ID"));
		
		tblMails.getColumnModel().getColumn(0).setResizable(false);
		tblMails.getColumnModel().getColumn(0).setPreferredWidth(19);
		tblMails.getColumnModel().getSelectionModel().addListSelectionListener(this);
		verticalSplitPane.setLeftComponent(tblMails);
		
		tpPreview = new JTextPane();
		verticalSplitPane.setRightComponent(tpPreview);
		
		JToolBar toolBar = new JToolBar();
		
		btnAbrufen = new JButton("Abrufen");
		btnAbrufen.addActionListener(this);
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
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mndatei = new JMenu("Datei");
		menuBar.add(mndatei);
		
		JMenu mnNewMenu = new JMenu("Neu");
		mndatei.add(mnNewMenu);
		
		mntmEmail = new JMenuItem("E-Mail");
		mntmEmail.addActionListener(this);
		mnNewMenu.add(mntmEmail);
		
		mntmKontakt = new JMenuItem("Kontakt");
		mntmKontakt.addActionListener(this);
		mnNewMenu.add(mntmKontakt);
		
		mntmTermin = new JMenuItem("Termin");
		mntmTermin.addActionListener(this);
		mnNewMenu.add(mntmTermin);
		
		mntmBeenden = new JMenuItem("Beenden");
		mntmBeenden.addActionListener(this);
		mndatei.add(mntmBeenden);
	}
	
	private void pfadZuNode(String pfad, DefaultMutableTreeNode parent) {
		String name = null;
		
		if(pfad.contains("/")) {
			DefaultMutableTreeNode pfadKnoten = null;
			name = pfad.substring(0, pfad.indexOf("/"));
			
			for(int j = 0; j < parent.getChildCount(); j++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(j);
				if(child.getUserObject().equals(name)) {
					pfadKnoten = child;
					break;
				}
			}
			
			if(pfadKnoten == null)
				pfadKnoten = new DefaultMutableTreeNode(name);
			
			pfadZuNode(pfad.substring(pfad.indexOf("/") + 1), pfadKnoten);
		}
		else {
			name = pfad;
			parent.add(new DefaultMutableTreeNode(name));
		}	
	}
	
	private void ladeOrdner() {
		DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)tree.getModel().getRoot();
		
		int i = 0;
		for(MailAccount acc : benutzer) {
			DefaultMutableTreeNode accNode = new DefaultMutableTreeNode(acc);
			String[] ordner = acc.getOrdnerstruktur();
			
			for(int j = 0; j < ordner.length; j++) {
				pfadZuNode(ordner[j], accNode);
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
	
	private void ladeMails(MailAccount ac, String pfad) {
		MailInfo[] messages = ac.getMessages(pfad);
		
		DefaultTableModel model = (DefaultTableModel)tblMails.getModel();
		model.setRowCount(0);
		for(MailInfo info : messages) {
			model.addRow(new Object[] {info.getID(), info.getSubject(), info.getSender(), info.getDate()});
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object sender = arg0.getSource();

		if(sender == btnAbrufen) {
			
		}
		else if(sender == mntmEmail) {
			MailFrame mf = new MailFrame();
			
			for(MailAccount ac : benutzer) {
				mf.addMailAccount(ac);
			}
			
			mf.setSize(this.getSize());
			mf.setExtendedState(this.getExtendedState());
			mf.setVisible(true);
		}
		else if(sender == mntmKontakt) {
			AdressbuchFrame af = new AdressbuchFrame();

			af.setSize(this.getSize());
			af.setExtendedState(this.getExtendedState());
			af.setVisible(true);
			
		}
		else if(sender == mntmTermin) {
			
		}
		else if(sender == mntmBeenden) {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		TreeNode[] path = selectedNode.getPath();
		
		if(path.length > 2) {
			DefaultMutableTreeNode mutableNode = (DefaultMutableTreeNode)path[1];
			MailAccount account = (MailAccount)mutableNode.getUserObject();
			
			String strPfad = "";
			for(int i = 2; i < path.length; i++) {
				strPfad += path[i].toString();
				
				if(i != path.length - 1)
					strPfad += "/";
			}
			
			ladeMails(account, strPfad);
		}
		else {
			DefaultTableModel model = (DefaultTableModel)tblMails.getModel();
			model.setRowCount(0);
		}
			
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int zeile = e.getFirstIndex();
		String id = tblMails.getModel().getValueAt(zeile, 0).toString();
		
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		TreeNode[] path = selectedNode.getPath();
		
		DefaultMutableTreeNode mutableNode = (DefaultMutableTreeNode)path[1];
		MailAccount account = (MailAccount)mutableNode.getUserObject();
		
		String strPfad = "";
		for(int i = 2; i < path.length; i++) {
			strPfad += path[i].toString();
			
			if(i != path.length - 1)
				strPfad += "/";
		}
		
		String text = account.getMessageText(strPfad, id);
		tpPreview.setText(text);
	}

	public static void main(String[] args) {
		MainFrame mf = new MainFrame();	
		
		mf.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mf.setVisible(true);
	}
}
