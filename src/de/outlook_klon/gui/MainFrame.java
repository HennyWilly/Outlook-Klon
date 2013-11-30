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

public class MainFrame extends JFrame implements ActionListener, TreeSelectionListener {
	private static final long serialVersionUID = 817918826034684858L;
	
	private JTable tblMails;
    private JMenuItem mntmEmail;
    private JMenuItem mntmKontakt;
    private JMenuItem mntmTermin;
    private JMenuItem mntmBeenden;
	
	public MainFrame() {
		
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
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(horizontalSplitPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(horizontalSplitPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
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

		if(sender == mntmEmail) {
			MailFrame mf = new MailFrame();
			
			/*MailAccount ma = new MailAccount(null, 
			new SmtpServer(
					new ServerSettings(
							Hostname,
							Port,
							Verbindungssicherheit.STARTTLS,
							Authentifizierungsart.NORMAL)), 
			Mail, 
			User, 
			Pw);

			mf.addMailAccount(ma);*/
			
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
