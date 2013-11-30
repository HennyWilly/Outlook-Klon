package de.outlook_klon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTree;
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
import de.outlook_klon.logik.mailclient.ServerSettings;
import de.outlook_klon.logik.mailclient.SmtpServer;
import de.outlook_klon.logik.mailclient.Verbindungssicherheit;

public class MainFrame extends JFrame implements ActionListener, TreeSelectionListener {
	private static final long serialVersionUID = 817918826034684858L;
	
	private JTable tblMails;
    private JMenuItem mntmEmail;
    private JMenuItem mntmKontakt;
    private JMenuItem mntmTermin;
    private JMenuItem mntmBeenden;
    private JButton btnAbrufen;
    
    private Benutzer benutzer;
	
	public MainFrame() {
		benutzer = new Benutzer();
		/*benutzer.addMailAccount(new MailAccount(
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
		);*/
		
		JSplitPane horizontalSplitPane = new JSplitPane();
		
		JTree tree = new JTree();
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("[root]") {
				{
				}
			}
		));
		tree.setRootVisible(false);
		horizontalSplitPane.setLeftComponent(tree);
		
		JSplitPane verticalSplitPane = new JSplitPane();
		verticalSplitPane.setContinuousLayout(true);
		verticalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		horizontalSplitPane.setRightComponent(verticalSplitPane);
		
		tblMails = new JTable();
		tblMails.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Betreff", "Von", "Datum"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		verticalSplitPane.setLeftComponent(tblMails);
		
		JTextPane tpPreview = new JTextPane();
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

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object sender = arg0.getSource();

		if(sender == btnAbrufen) {
			for(MailAccount ac : benutzer) {
				ac.getOrdnerstruktur();
			}
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
		TreePath path = e.getPath();
	}

	public static void main(String[] args) {
		MainFrame mf = new MainFrame();	
		
		mf.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mf.setVisible(true);
	}
}
