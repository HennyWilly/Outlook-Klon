package de.outlook_klon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
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

import de.outlook_klon.logik.mailclient.MailAccount;

public class MailFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 5976953616015664148L;
	
	private JComboBox<MailAccount> cBSender;
	private JTextField tTo;
	private JTextField tCC;
	private JTextField tSubject;

	public MailFrame() {
		
		JToolBar toolBar = new JToolBar();
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		
		JLabel lSender = new JLabel("Von:");
		JLabel lTo = new JLabel("An:");
		JLabel lCC = new JLabel("CC:");
		JLabel lSubject = new JLabel("Betreff:");
		
		cBSender = new JComboBox<MailAccount>();
		
		tTo = new JTextField();
		tTo.setColumns(10);
		
		
		tCC = new JTextField();
		tCC.setColumns(10);
		
		tSubject = new JTextField();
		tSubject.setColumns(10);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGap(0, 508, Short.MAX_VALUE)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(10)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(lSender, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(45)
							.addComponent(cBSender, 0, 445, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(45)
							.addComponent(tTo, GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE))
						.addComponent(lTo, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
						.addComponent(lCC, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(45)
							.addComponent(tCC, GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE))
						.addComponent(lSubject, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(45)
							.addComponent(tSubject, GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)))
					.addGap(8))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGap(0, 101, Short.MAX_VALUE)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(8)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(3)
							.addComponent(lSender))
						.addComponent(cBSender, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(5)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(tTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(3)
							.addComponent(lTo)))
					.addGap(5)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(3)
							.addComponent(lCC))
						.addComponent(tCC, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(3)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(3)
							.addComponent(lSubject))
						.addComponent(tSubject, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
		);
		panel.setLayout(gl_panel);
		
		JTextPane tpMailtext = new JTextPane();
		splitPane.setRightComponent(tpMailtext);
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
		
		JButton btnSenden = new JButton("Senden");
		toolBar.add(btnSenden);
		
		JButton btnAnhang = new JButton("Anhang");
		toolBar.add(btnAnhang);
		getContentPane().setLayout(groupLayout);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnDatei = new JMenu("Datei");
		menuBar.add(mnDatei);
		
		JMenu mnAnhaengen = new JMenu("Anh\u00E4ngen");
		mnDatei.add(mnAnhaengen);
		
		JMenuItem mntmDateiAnhaengen = new JMenuItem("Datei anh\u00E4ngen");
		mnAnhaengen.add(mntmDateiAnhaengen);
		
		JMenuItem mntmSchliessen = new JMenuItem("Schlie\u00DFen");
		mnDatei.add(mntmSchliessen);
		
		JMenu mnOptionen = new JMenu("Optionen");
		menuBar.add(mnOptionen);
		
		JMenu mnEmailFormat = new JMenu("E-Mail Format");
		mnOptionen.add(mnEmailFormat);
		
		JMenuItem mntmReintext = new JMenuItem("Reintext");
		mnEmailFormat.add(mntmReintext);
		
		JMenuItem mntmHtml = new JMenuItem("Html");
		mnEmailFormat.add(mntmHtml);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}
}
