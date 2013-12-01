package de.outlook_klon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JButton;

import de.outlook_klon.logik.mailclient.Authentifizierungsart;
import de.outlook_klon.logik.mailclient.EmpfangsServer;
import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.mailclient.SendServer;
import de.outlook_klon.logik.mailclient.ServerSettings;
import de.outlook_klon.logik.mailclient.Verbindungssicherheit;

public class KontoFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = -8114432074006047938L;
	private JTextField txtMail;
	private JPasswordField passwordField;
	private JTextField txtInServer;
	private JTextField txtOutServer;
	private JTextField txtBenutzername;
	
	private JComboBox<String> cbInProtokoll;
	private JSpinner spInPort;
	private JComboBox<Verbindungssicherheit> cBInVerbindungssicherheit;
	private JComboBox<Authentifizierungsart> cBInAuthentifizierung;
	
	private JSpinner spOutPort;
	private JComboBox<Verbindungssicherheit> cBOutVerbindungssicherheit;
	private JComboBox<Authentifizierungsart> cBOutAuthentifizierung;
	
	private void Init() {
		this.setSize(711, 305);
		this.setResizable(false);
		
		txtMail = new JTextField();
		txtMail.setBounds(95, 11, 167, 20);
		txtMail.setColumns(10);
		
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
		
		JButton btnTesten = new JButton("Testen");
		btnTesten.setBounds(420, 235, 85, 23);
		
		JLabel lblPosteingangserver = new JLabel("Posteingang-Server:");
		lblPosteingangserver.setBounds(10, 37, 99, 14);
		
		cbInProtokoll = new JComboBox<String>();
		cbInProtokoll.setBounds(119, 34, 57, 20);
		cbInProtokoll.setModel(
				new DefaultComboBoxModel<String>(new String[] {"POP3", "IMAP" }));
		
		txtInServer = new JTextField();
		txtInServer.setBounds(187, 34, 162, 20);
		txtInServer.setColumns(10);
		
		spInPort = new JSpinner();
		spInPort.setBounds(355, 34, 54, 20);
		
		cBInVerbindungssicherheit = new JComboBox<Verbindungssicherheit>();
		cBInVerbindungssicherheit.setBounds(415, 34, 127, 20);
		cBInVerbindungssicherheit.setModel(
				new DefaultComboBoxModel<Verbindungssicherheit>(Verbindungssicherheit.values()));
		
		cBInAuthentifizierung = new JComboBox<Authentifizierungsart>();
		cBInAuthentifizierung.setBounds(548, 34, 127, 20);
		cBInAuthentifizierung.setModel(
				new DefaultComboBoxModel<Authentifizierungsart>(Authentifizierungsart.values()));
		
		JLabel lblPostausgangsserver = new JLabel("Postausgang-Server:");
		lblPostausgangsserver.setBounds(10, 65, 105, 14);
		
		txtOutServer = new JTextField();
		txtOutServer.setBounds(187, 65, 162, 20);
		txtOutServer.setColumns(10);
		
		spOutPort = new JSpinner();
		spOutPort.setBounds(355, 65, 54, 20);
		
		cBOutVerbindungssicherheit = new JComboBox<Verbindungssicherheit>();
		cBOutVerbindungssicherheit.setBounds(415, 65, 127, 20);
		cBOutVerbindungssicherheit.setModel(
				new DefaultComboBoxModel<Verbindungssicherheit>(Verbindungssicherheit.values()));
		
		cBOutAuthentifizierung = new JComboBox<Authentifizierungsart>();
		cBOutAuthentifizierung.setBounds(548, 64, 127, 20);
		cBOutAuthentifizierung.setModel(
				new DefaultComboBoxModel<Authentifizierungsart>(Authentifizierungsart.values()));
		
		JLabel lblSmtp = new JLabel("SMTP");
		lblSmtp.setBounds(121, 65, 26, 14);
		
		JLabel lblBenutzername = new JLabel("Benutzername:");
		lblBenutzername.setBounds(36, 106, 73, 14);
		
		txtBenutzername = new JTextField();
		txtBenutzername.setBounds(187, 103, 162, 20);
		txtBenutzername.setColumns(10);
		getContentPane().setLayout(null);
		getContentPane().add(lblMail);
		getContentPane().add(lblPasswort);
		getContentPane().add(txtMail);
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
		
		JLabel lblServeradresse = new JLabel("Server-Adresse");
		lblServeradresse.setBounds(187, 11, 87, 14);
		GroupBox.add(lblServeradresse);
		
		JLabel lblPort = new JLabel("Port");
		lblPort.setBounds(355, 11, 46, 14);
		GroupBox.add(lblPort);
		
		JLabel lblSsl = new JLabel("SSL");
		lblSsl.setBounds(415, 11, 46, 14);
		GroupBox.add(lblSsl);
		
		JLabel lblAuthentifizierung = new JLabel("Authentifizierung");
		lblAuthentifizierung.setBounds(548, 11, 99, 14);
		GroupBox.add(lblAuthentifizierung);
		getContentPane().add(btnTesten);
		getContentPane().add(btnAbbrechen);
		
		JButton btnFertig = new JButton("Fertig");
		btnFertig.setEnabled(false);
		btnFertig.setBounds(515, 235, 85, 23);
		getContentPane().add(btnFertig);
	}
	
	public KontoFrame() {
		Init();
	}
	
	public KontoFrame(MailAccount acc) {
		Init();
		
		EmpfangsServer inServer = acc.getEmpfangsServer();
		SendServer outServer = acc.getSendServer();

		txtMail.setText(acc.getAdresse());
		txtBenutzername.setText(acc.getBenutzer());
		
		if(inServer != null) {
			ServerSettings settings = inServer.getSettings();

			cbInProtokoll.setSelectedItem(inServer.getServerTyp());
			txtInServer.setText(settings.getHost());
			spInPort.setValue(settings.getPort());
			cBInVerbindungssicherheit.setSelectedItem(settings.getVerbingungssicherheit());
			cBInAuthentifizierung.setSelectedItem(settings.getAuthentifizierungsart());
		}
		if(outServer != null) {
			ServerSettings settings = outServer.getSettings();
			
			txtOutServer.setText(settings.getHost());
			spOutPort.setValue(settings.getPort());
			cBOutVerbindungssicherheit.setSelectedItem(settings.getVerbingungssicherheit());
			cBOutAuthentifizierung.setSelectedItem(settings.getAuthentifizierungsart());
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}
}
