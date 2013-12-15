package de.outlook_klon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JList;

import java.io.File;

import javax.mail.MessagingException;

import de.outlook_klon.logik.mailclient.MailAccount;

public class MailFrame extends JFrame implements ActionListener, ItemListener {
	private static final long serialVersionUID = 5976953616015664148L;
	
	private JComboBox<MailAccount> cBSender;
	private JTextField tTo;
	private JTextField tCC;
	private JTextField tSubject;
	private JTextPane tpMailtext;
	
	private JButton btnSenden;
	private JButton btnAnhang;
	
	private JMenuItem mntmDateiAnhaengen;
	private JMenuItem mntmSchliessen;
	
	private JRadioButtonMenuItem rdbtnmntmReintext;
	private JRadioButtonMenuItem rdbtnmntmHtml;
	
	private JList<File> lstAnhang;
	
	public MailFrame() {
		
		JToolBar toolBar = new JToolBar();
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		
		JSplitPane splitHead = new JSplitPane();
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addComponent(splitHead, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addComponent(splitHead, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
		);
		
		JPanel panel_1 = new JPanel();
		splitHead.setLeftComponent(panel_1);
		
		JLabel lSender = new JLabel("Von:");
		JLabel lTo = new JLabel("An:");
		JLabel lCC = new JLabel("CC:");
		JLabel lSubject = new JLabel("Betreff:");
		
		tTo = new JTextField();
		tTo.setColumns(10);
		
		tCC = new JTextField();
		tCC.setColumns(10);
		
		tSubject = new JTextField();
		tSubject.setColumns(10);
		
		cBSender = new JComboBox<MailAccount>();
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
							.addComponent(lSender, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
							.addComponent(lTo, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))
						.addComponent(lCC, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
						.addComponent(lSubject, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))
					.addGap(2)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(cBSender, 0, 301, Short.MAX_VALUE)
						.addComponent(tTo, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
						.addComponent(tCC, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
						.addComponent(tSubject, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lSender)
						.addComponent(cBSender, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(tTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(5)
							.addComponent(tCC, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(3)
							.addComponent(tSubject, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lTo)
							.addGap(11)
							.addComponent(lCC)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lSubject)))
					.addContainerGap())
		);
		panel_1.setLayout(gl_panel_1);
		
		lstAnhang = new JList<File>(new DefaultListModel<File>());
		
		JScrollPane anhangScroller = new JScrollPane(lstAnhang);
		splitHead.setRightComponent(anhangScroller);
		
		panel.setLayout(gl_panel);
		
		tpMailtext = new JTextPane();
		
		JScrollPane textScroller = new JScrollPane(tpMailtext);
		splitPane.setRightComponent(textScroller);
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(toolBar, GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
						.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE))
					.addGap(0))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE))
		);
		
		btnSenden = new JButton("Senden");
		toolBar.add(btnSenden);
		btnSenden.addActionListener(this);
		
		btnAnhang = new JButton("Anhang");
		toolBar.add(btnAnhang);
		btnAnhang.addActionListener(this);
		
		getContentPane().setLayout(groupLayout);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnDatei = new JMenu("Datei");
		menuBar.add(mnDatei);
		
		JMenu mnAnhaengen = new JMenu("Anh\u00E4ngen");
		mnDatei.add(mnAnhaengen);
		
		mntmDateiAnhaengen = new JMenuItem("Datei anh\u00E4ngen");
		mntmDateiAnhaengen.addActionListener(this);
		mnAnhaengen.add(mntmDateiAnhaengen);
		
		mntmSchliessen = new JMenuItem("Schlie\u00DFen");
		mntmSchliessen.addActionListener(this);
		mnDatei.add(mntmSchliessen);
		
		JMenu mnOptionen = new JMenu("Optionen");
		menuBar.add(mnOptionen);
		
		JMenu mnEmailFormat = new JMenu("E-Mail Format");
		mnOptionen.add(mnEmailFormat);
		
		rdbtnmntmReintext = new JRadioButtonMenuItem("Reintext");
		rdbtnmntmReintext.setSelected(true);
		rdbtnmntmReintext.addItemListener(this);
		mnEmailFormat.add(rdbtnmntmReintext);
		
		rdbtnmntmHtml = new JRadioButtonMenuItem("Html");
		rdbtnmntmHtml.addItemListener(this);
		mnEmailFormat.add(rdbtnmntmHtml);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnmntmReintext);
		group.add(rdbtnmntmHtml);
	}

	public void addMailAccount(MailAccount ac) {
		cBSender.addItem(ac);
		
		if(cBSender.getSelectedIndex() == -1)
			cBSender.setSelectedIndex(0);
	}
	
	private void sendeMail() {
		String[] to = tTo.getText().split(",");
		String[] cc = tCC.getText().split(",");
		String subject = tSubject.getText();
		String text = tpMailtext.getText();
		
		MailAccount acc = (MailAccount)cBSender.getSelectedItem();
		if(acc == null) {
			JOptionPane.showMessageDialog(this, "Es wurde keine Mailadresse angegeben, �ber die die Mail gesendet werden soll",
					"Fehler", JOptionPane.OK_OPTION);
		}
		else {
			DefaultListModel<File> model = (DefaultListModel<File>)lstAnhang.getModel();
			File[] anh�nge = new File[model.getSize()];
			for(int i = 0; i < anh�nge.length; i++) {
				anh�nge[i] = model.get(i);
			}
			
			try {
				acc.sendeMail(to, cc, subject, text, tpMailtext.getContentType(), anh�nge);
				this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
			} catch(MessagingException ex) {
				JOptionPane.showMessageDialog(this, "Es ist ein Fehler beim Senden der Mail aufgetreten:\n" + ex.getMessage(),
						"Fehler", JOptionPane.OK_OPTION);
			}
		}
	}
	
	private void anhangHinzuf�gen() {
		JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File[] files = fc.getSelectedFiles();
			DefaultListModel<File> model = (DefaultListModel<File>)lstAnhang.getModel();
			
			for(File file : files) {
				model.addElement(file);
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object sender = arg0.getSource();
		
		if(sender == btnSenden) {
			sendeMail();
		}
		else if(sender == mntmDateiAnhaengen || sender == btnAnhang) {
			anhangHinzuf�gen();
		}
		else if(sender == mntmSchliessen) {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		JRadioButtonMenuItem sender = (JRadioButtonMenuItem)arg0.getSource();		
		if(arg0.getStateChange() == ItemEvent.SELECTED) {
			if(sender == rdbtnmntmReintext) {
				tpMailtext.setContentType("text/plain");
			}
			else if(sender == rdbtnmntmHtml) {
				tpMailtext.setContentType("text/html");
			}
		}
	}
}
