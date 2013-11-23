package de.outlook_klon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Component;

public class KontoFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = -8114432074006047938L;
	private JTextField textField;
	private JPasswordField passwordField;
	private JTextField txtInServer;
	private JTextField txtOutServer;
	private JTextField txtBenutzername;
	
	public KontoFrame() {
		
		this.setSize(711, 305);
		this.setResizable(false);
		
		textField = new JTextField();
		textField.setBounds(95, 11, 167, 20);
		textField.setColumns(10);
		
		JLabel lblMail = new JLabel("E-Mail-Adresse:");
		lblMail.setBounds(10, 14, 75, 14);
		
		JLabel lblPasswort = new JLabel("Passwort:");
		lblPasswort.setBounds(37, 45, 48, 14);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(95, 42, 167, 20);
		
		JPanel GroupBox = new JPanel();
		GroupBox.setBounds(10, 87, 685, 137);
		
		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.setBounds(610, 235, 85, 23);
		
		JButton btnErneutTesten = new JButton("Erneut testen");
		btnErneutTesten.setBounds(406, 235, 99, 23);
		
		JLabel lblPosteingangserver = new JLabel("Posteingang-Server:");
		lblPosteingangserver.setBounds(10, 37, 99, 14);
		
		JComboBox cbInProtokoll = new JComboBox();
		cbInProtokoll.setBounds(119, 34, 57, 20);
		
		txtInServer = new JTextField();
		txtInServer.setBounds(187, 34, 162, 20);
		txtInServer.setColumns(10);
		
		JSpinner spInPort = new JSpinner();
		spInPort.setBounds(355, 34, 54, 20);
		
		JComboBox cBInVerbindungssicherheit = new JComboBox();
		cBInVerbindungssicherheit.setBounds(415, 34, 127, 20);
		
		JComboBox cBInAuthentifizierung = new JComboBox();
		cBInAuthentifizierung.setBounds(548, 34, 127, 20);
		
		JLabel lblPostausgangsserver = new JLabel("Postausgang-Server:");
		lblPostausgangsserver.setBounds(10, 65, 105, 14);
		
		txtOutServer = new JTextField();
		txtOutServer.setBounds(187, 65, 162, 20);
		txtOutServer.setColumns(10);
		
		JSpinner spOutPort = new JSpinner();
		spOutPort.setBounds(355, 65, 54, 20);
		
		JComboBox cBOutVerbindungssicherheit = new JComboBox();
		cBOutVerbindungssicherheit.setBounds(415, 65, 127, 20);
		
		JComboBox cBOutAuthentifizierung = new JComboBox();
		cBOutAuthentifizierung.setBounds(548, 64, 127, 20);
		
		JLabel lblSmtp = new JLabel("SMTP");
		lblSmtp.setBounds(121, 65, 26, 14);
		
		JLabel lblBenutzername = new JLabel("Benutzername:");
		lblBenutzername.setBounds(36, 106, 73, 14);
		
		txtBenutzername = new JTextField();
		txtBenutzername.setBounds(119, 103, 139, 20);
		txtBenutzername.setColumns(10);
		getContentPane().setLayout(null);
		getContentPane().add(lblMail);
		getContentPane().add(lblPasswort);
		getContentPane().add(textField);
		getContentPane().add(passwordField);
		getContentPane().add(GroupBox);
		GroupBox.setLayout(null);
		GroupBox.add(lblPosteingangserver);
		GroupBox.add(cbInProtokoll);
		GroupBox.add(lblBenutzername);
		GroupBox.add(lblPostausgangsserver);
		GroupBox.add(lblSmtp);
		GroupBox.add(txtOutServer);
		GroupBox.add(txtInServer);
		GroupBox.add(spInPort);
		GroupBox.add(spOutPort);
		GroupBox.add(cBInVerbindungssicherheit);
		GroupBox.add(cBOutVerbindungssicherheit);
		GroupBox.add(cBInAuthentifizierung);
		GroupBox.add(cBOutAuthentifizierung);
		GroupBox.add(txtBenutzername);
		getContentPane().add(btnErneutTesten);
		getContentPane().add(btnAbbrechen);
		
		JButton btnFertig = new JButton("Fertig");
		btnFertig.setBounds(515, 235, 85, 23);
		getContentPane().add(btnFertig);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}
}
