package de.outlook_klon.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.outlook_klon.logik.Benutzer;
import de.outlook_klon.logik.Benutzer.MailChecker;
import de.outlook_klon.logik.mailclient.EmpfangsServer;
import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.mailclient.SendServer;
import de.outlook_klon.logik.mailclient.ServerSettings;

/**
 * In diesem Frame werden alle registrierten MailAccount-Instanzen verwaltet. Es
 * können neue Instanzen erstellt, bearbeitet und entfernt werden.
 * 
 * @author Hendrik Karwanni
 */
public class KontoverwaltungFrame extends ExtendedDialog<MailAccount[]> {
	private static final long serialVersionUID = -5036893845172118794L;

	private MailAccount[] meineAccounts;

	private JButton btnNeuesKonto;
	private JList<MailAccount> lstKonten;
	private JButton btnAbbrechen;
	private JButton btnOK;
	private JButton btnKontoEntfernen;

	private JTextField txtUser;
	private JTextField txtMail;
	private JTextField txtName;
	private JTextField txtEingangTyp;
	private JTextField txtEingangServer;
	private JTextField txtEingangPort;
	private JTextField txtEingangSicherheit;
	private JTextField txtEingangAuthentifizierung;
	private JTextField txtAusgangTyp;
	private JTextField txtAusgangServer;
	private JTextField txtAusgangPort;
	private JTextField txtAusgangSicherheit;
	private JTextField txtAusgangAuthentifizierung;

	/**
	 * Erstellt eine neue Instanz der Klasse zum Verwalten aller bestehenden
	 * MailAccounts
	 */
	public KontoverwaltungFrame() {
		super(711, 695);

		meineAccounts = null;

		setTitle("Konten-Einstellungen");
		getContentPane().setLayout(null);

		DefaultListModel<MailAccount> listModel = new DefaultListModel<MailAccount>();
		for (MailChecker checker : Benutzer.getInstanz()) {
			MailAccount acc = checker.getAccount();
			listModel.addElement(acc);
		}

		lstKonten = new JList<MailAccount>(listModel);
		lstKonten.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				MailAccount acc = (MailAccount) value;

				return super.getListCellRendererComponent(list, acc
						.getAdresse().getAddress(), index, isSelected,
						cellHasFocus);
			}
		});
		lstKonten.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					MailAccount acc = lstKonten.getSelectedValue();

					btnKontoEntfernen.setEnabled(acc != null);
					aktualisiereDetails(acc);
				}
			}
		});
		lstKonten.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					MailAccount acc = lstKonten.getSelectedValue();
					MailAccount edit = editiereKonto(acc);

					if (acc != edit) {
						lstKonten.setSelectedValue(edit, true);
					}
				}
			}
		});
		lstKonten.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstKonten.setBounds(10, 11, 239, 570);

		JScrollPane kontenScroller = new JScrollPane(lstKonten);
		kontenScroller.setBounds(lstKonten.getBounds());
		getContentPane().add(kontenScroller);

		btnNeuesKonto = new JButton("Neues Konto");
		btnNeuesKonto.setBounds(10, 592, 106, 23);
		btnNeuesKonto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				erstelleKonto();
			}
		});
		getContentPane().add(btnNeuesKonto);

		btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.setBounds(583, 633, 112, 23);
		btnAbbrechen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				meineAccounts = null;
				close();
			}
		});
		getContentPane().add(btnAbbrechen);

		btnOK = new JButton("OK");
		btnOK.setBounds(484, 633, 89, 23);
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<MailAccount> model = (DefaultListModel<MailAccount>) lstKonten
						.getModel();
				meineAccounts = new MailAccount[model.getSize()];
				for (int i = 0; i < meineAccounts.length; i++) {
					meineAccounts[i] = model.get(i);
				}
				close();
			}
		});
		getContentPane().add(btnOK);

		btnKontoEntfernen = new JButton("Konto entfernen");
		btnKontoEntfernen.setEnabled(false);
		btnKontoEntfernen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultListModel<MailAccount> model = (DefaultListModel<MailAccount>) lstKonten
						.getModel();
				MailAccount acc = lstKonten.getSelectedValue();

				loeschekonto(acc);
				if (model.indexOf(acc) == -1) {
					if (!model.isEmpty())
						lstKonten.setSelectedIndex(0);
				}

			}
		});
		btnKontoEntfernen.setBounds(123, 592, 126, 23);
		getContentPane().add(btnKontoEntfernen);

		JLabel lblIhrName = new JLabel("Ihr Name: ");
		lblIhrName.setBounds(259, 13, 99, 14);
		getContentPane().add(lblIhrName);

		JLabel lblEmailadresse = new JLabel("E-Mail-Adresse: ");
		lblEmailadresse.setBounds(259, 38, 99, 14);
		getContentPane().add(lblEmailadresse);

		JLabel lblEingangBenutzername = new JLabel("Benutzername: ");
		lblEingangBenutzername.setBounds(259, 63, 99, 14);
		getContentPane().add(lblEingangBenutzername);

		JPanel panelEingang = new JPanel();
		panelEingang.setBounds(259, 88, 436, 126);
		panelEingang.setBorder(BorderFactory
				.createTitledBorder("Eingangs-Server"));
		getContentPane().add(panelEingang);
		panelEingang.setLayout(null);

		JLabel lblEingangServer = new JLabel("Server: ");
		lblEingangServer.setBounds(10, 52, 46, 14);
		panelEingang.add(lblEingangServer);

		JLabel lblEingangPort = new JLabel("Port: ");
		lblEingangPort.setBounds(285, 52, 46, 14);
		panelEingang.add(lblEingangPort);

		JLabel lblEingangVerbindungssicherheit = new JLabel(
				"Verbindungssicherheit: ");
		lblEingangVerbindungssicherheit.setBounds(10, 77, 143, 14);
		panelEingang.add(lblEingangVerbindungssicherheit);

		JLabel lblEingangAuthentifizierungsart = new JLabel(
				"Authentifizierungsart: ");
		lblEingangAuthentifizierungsart.setBounds(10, 102, 143, 14);
		panelEingang.add(lblEingangAuthentifizierungsart);

		JLabel lblServertyp = new JLabel("Servertyp: ");
		lblServertyp.setBounds(10, 27, 67, 14);
		panelEingang.add(lblServertyp);

		txtEingangTyp = new JTextField();
		txtEingangTyp.setBackground(Color.WHITE);
		txtEingangTyp.setEditable(false);
		txtEingangTyp.setBounds(75, 24, 351, 20);
		panelEingang.add(txtEingangTyp);
		txtEingangTyp.setColumns(10);

		txtEingangServer = new JTextField();
		txtEingangServer.setBackground(Color.WHITE);
		txtEingangServer.setEditable(false);
		txtEingangServer.setBounds(75, 49, 200, 20);
		panelEingang.add(txtEingangServer);
		txtEingangServer.setColumns(10);

		txtEingangPort = new JTextField();
		txtEingangPort.setBackground(Color.WHITE);
		txtEingangPort.setEditable(false);
		txtEingangPort.setBounds(314, 49, 112, 20);
		panelEingang.add(txtEingangPort);
		txtEingangPort.setColumns(10);

		txtEingangSicherheit = new JTextField();
		txtEingangSicherheit.setBackground(Color.WHITE);
		txtEingangSicherheit.setEditable(false);
		txtEingangSicherheit.setBounds(153, 74, 273, 20);
		panelEingang.add(txtEingangSicherheit);
		txtEingangSicherheit.setColumns(10);

		txtEingangAuthentifizierung = new JTextField();
		txtEingangAuthentifizierung.setBackground(Color.WHITE);
		txtEingangAuthentifizierung.setEditable(false);
		txtEingangAuthentifizierung.setColumns(10);
		txtEingangAuthentifizierung.setBounds(153, 99, 273, 20);
		panelEingang.add(txtEingangAuthentifizierung);

		JPanel panelAusgang = new JPanel();
		panelAusgang.setLayout(null);
		panelAusgang.setBorder(BorderFactory
				.createTitledBorder("Ausgangs-Server"));
		panelAusgang.setBounds(259, 225, 436, 126);
		getContentPane().add(panelAusgang);

		JLabel lblAusgangServer = new JLabel("Server: ");
		lblAusgangServer.setBounds(10, 48, 46, 14);
		panelAusgang.add(lblAusgangServer);

		JLabel lblAusgangPort = new JLabel("Port: ");
		lblAusgangPort.setBounds(285, 48, 46, 14);
		panelAusgang.add(lblAusgangPort);

		JLabel lblAusgangVerbindungssicherheit = new JLabel(
				"Verbindungssicherheit: ");
		lblAusgangVerbindungssicherheit.setBounds(10, 73, 143, 14);
		panelAusgang.add(lblAusgangVerbindungssicherheit);

		JLabel lblAusgangAuthentifizierungsart = new JLabel(
				"Authentifizierungsart: ");
		lblAusgangAuthentifizierungsart.setBounds(10, 98, 143, 14);
		panelAusgang.add(lblAusgangAuthentifizierungsart);

		txtAusgangServer = new JTextField();
		txtAusgangServer.setBackground(Color.WHITE);
		txtAusgangServer.setEditable(false);
		txtAusgangServer.setColumns(10);
		txtAusgangServer.setBounds(75, 45, 200, 20);
		panelAusgang.add(txtAusgangServer);

		txtAusgangPort = new JTextField();
		txtAusgangPort.setBackground(Color.WHITE);
		txtAusgangPort.setEditable(false);
		txtAusgangPort.setColumns(10);
		txtAusgangPort.setBounds(314, 45, 112, 20);
		panelAusgang.add(txtAusgangPort);

		txtAusgangSicherheit = new JTextField();
		txtAusgangSicherheit.setBackground(Color.WHITE);
		txtAusgangSicherheit.setEditable(false);
		txtAusgangSicherheit.setColumns(10);
		txtAusgangSicherheit.setBounds(153, 70, 273, 20);
		panelAusgang.add(txtAusgangSicherheit);

		txtAusgangAuthentifizierung = new JTextField();
		txtAusgangAuthentifizierung.setBackground(Color.WHITE);
		txtAusgangAuthentifizierung.setEditable(false);
		txtAusgangAuthentifizierung.setColumns(10);
		txtAusgangAuthentifizierung.setBounds(153, 95, 273, 20);
		panelAusgang.add(txtAusgangAuthentifizierung);

		JLabel label = new JLabel("Servertyp: ");
		label.setBounds(10, 23, 65, 14);
		panelAusgang.add(label);

		txtAusgangTyp = new JTextField();
		txtAusgangTyp.setBackground(Color.WHITE);
		txtAusgangTyp.setEditable(false);
		txtAusgangTyp.setColumns(10);
		txtAusgangTyp.setBounds(75, 20, 351, 20);
		panelAusgang.add(txtAusgangTyp);

		txtUser = new JTextField();
		txtUser.setEditable(false);
		txtUser.setBackground(Color.WHITE);
		txtUser.setBounds(357, 60, 338, 20);
		getContentPane().add(txtUser);
		txtUser.setColumns(10);

		txtMail = new JTextField();
		txtMail.setEditable(false);
		txtMail.setBackground(Color.WHITE);
		txtMail.setColumns(10);
		txtMail.setBounds(357, 35, 338, 20);
		getContentPane().add(txtMail);

		txtName = new JTextField();
		txtName.setEditable(false);
		txtName.setBackground(Color.WHITE);
		txtName.setColumns(10);
		txtName.setBounds(357, 10, 338, 20);
		getContentPane().add(txtName);
	}

	/**
	 * Trägt die Daten der übergebenen MailAccount-Instanz in die dafür
	 * vorgesehenen Felder ein
	 * 
	 * @param acc
	 *            MailAccount-Instanz aus der die Daten ausgelesen werden sollen
	 */
	private void aktualisiereDetails(MailAccount acc) {
		if (acc == null) {
			txtUser.setText(null);
			txtMail.setText(null);
			txtName.setText(null);
			txtEingangTyp.setText(null);
			txtEingangServer.setText(null);
			txtEingangPort.setText(null);
			txtEingangSicherheit.setText(null);
			txtEingangAuthentifizierung.setText(null);
			txtAusgangTyp.setText(null);
			txtAusgangServer.setText(null);
			txtAusgangPort.setText(null);
			txtAusgangSicherheit.setText(null);
			txtAusgangAuthentifizierung.setText(null);
		} else {
			txtUser.setText(acc.getBenutzer());
			txtMail.setText(acc.getAdresse().getAddress());
			txtName.setText(acc.getAdresse().getPersonal());
			
			EmpfangsServer empfServer = acc.getEmpfangsServer();
			ServerSettings empfSettings = empfServer.getSettings();

			txtEingangTyp.setText(empfServer.getServerTyp());
			txtEingangServer.setText(empfSettings.getHost());
			txtEingangPort.setText(Integer.toString(empfSettings.getPort()));
			txtEingangSicherheit.setText(empfSettings
					.getVerbingungssicherheit().toString());
			txtEingangAuthentifizierung.setText(empfSettings
					.getAuthentifizierungsart().toString());
			
			SendServer sendServer = acc.getSendServer();
			ServerSettings sendSettings = sendServer.getSettings();
			
			txtAusgangTyp.setText(sendServer.getServerTyp());
			txtAusgangServer.setText(sendSettings.getHost());
			txtAusgangPort.setText(Integer.toString(sendSettings.getPort()));
			txtAusgangSicherheit.setText(sendSettings
					.getVerbingungssicherheit().toString());
			txtAusgangAuthentifizierung.setText(sendSettings
					.getAuthentifizierungsart().toString());
		}
	}

	/**
	 * Öffnet ein neues Konto-Frame zum Erstellen einer neuen
	 * MailAccount-Instanz
	 */
	private void erstelleKonto() {
		KontoFrame kf = new KontoFrame();
		MailAccount acc = kf.showDialog();
		if (acc != null) {
			DefaultListModel<MailAccount> model = (DefaultListModel<MailAccount>) lstKonten
					.getModel();
			//Füge die neue Instanz der JList hinzu
			model.addElement(acc);
		}
	}

	/**
	 * Öffnet ein neues Konto-Frame zum Bearbeiten einer bestehenden
	 * MailAccount-Instanz
	 * 
	 * @param acc
	 *            Zu bearbeitender MailAccount
	 * @return Instanz des veränderten MailAccounts
	 */
	private MailAccount editiereKonto(MailAccount acc) {
		KontoFrame kf = new KontoFrame(acc);

		MailAccount result = kf.showDialog();
		if (result != null) {
			DefaultListModel<MailAccount> model = (DefaultListModel<MailAccount>) lstKonten
					.getModel();
			int index = model.indexOf(acc);

			//Entfernt die alte Instanz aus der JList
			model.remove(index);
			//Füge die neue Instanz der JList hinzu
			model.add(index, result);

			acc = result;
		}

		return acc;
	}

	/**
	 * Löscht die übergebene MailAccount-Instanz aus der Liste
	 * @param acc Zu löschender MailAccount
	 */
	private void loeschekonto(MailAccount acc) {
		if (acc != null) {
			int result = JOptionPane.showConfirmDialog(this,
					"Wollen Sie das Konto mit der Mailadresse \'"
							+ acc.getAdresse().getAddress()
							+ "\' wirklich löschen?", "Löschen bestätigen",
					JOptionPane.YES_NO_OPTION);

			if (result == JOptionPane.YES_OPTION) {
				DefaultListModel<MailAccount> model = (DefaultListModel<MailAccount>) lstKonten
						.getModel();

				//Entfernt die Instanz aus der JList
				model.removeElement(acc);
			}
		}
	}

	@Override
	protected MailAccount[] getDialogResult() {
		return meineAccounts;
	}
}
