package de.outlook_klon.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JButton;

import de.outlook_klon.logik.kontakte.Kontakt;

public class KontaktFrame extends JDialog {
	private static final long serialVersionUID = 1466530984514818388L;
	private static final String formatStringErstellen = "Kontakt erstellen";
	private static final String formatStringBearbeiten = "Kontakt von %s bearbeiten";

	private Kontakt mKontakt;
	
	private JTextField tVorname;
	private JTextField tName;
	private JTextField tAnzeigename;
	private JTextField tSpitzname;
	private JTextField tEmailadresse_1;
	private JTextField tEmailadresse_2;
	private JTextField tDienstlich;
	private JTextField tPrivat;
	private JTextField tMobil;

	private JButton btnOK;
	private JButton btnAbbrechen;

	private void close() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
	
	private void initFrame() {
		this.setModal(true);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setSize(685, 285);
		
		JLabel lblVorname = new JLabel("Vorname: ");
		JLabel lblName = new JLabel("Name: ");
		JLabel lblAnzeigename = new JLabel("Anzeigename: ");
		JLabel lblSpitzname = new JLabel("Spitzname: ");
		JLabel lblEmailadresse_1 = new JLabel("E-Mail-Adresse: ");
		JLabel lblEmailadresse_2 = new JLabel("2. E-Mail-Adresse: ");
		JLabel lblDienstlich = new JLabel("Dienstlich: ");
		JLabel lblPrivat = new JLabel("Privat: ");
		JLabel lblMobil = new JLabel("Mobil: ");

		tVorname = new JTextField();
		tVorname.setColumns(10);
		
		tName = new JTextField();
		tName.setColumns(10);

		tAnzeigename = new JTextField();
		tAnzeigename.setColumns(10);

		tSpitzname = new JTextField();
		tSpitzname.setColumns(10);

		tEmailadresse_1 = new JTextField();
		tEmailadresse_1.setColumns(10);

		tEmailadresse_2 = new JTextField();
		tEmailadresse_2.setColumns(10);

		tDienstlich = new JTextField();
		tDienstlich.setColumns(10);

		tPrivat = new JTextField();
		tPrivat.setColumns(10);

		tMobil = new JTextField();
		tMobil.setColumns(10);
		
		btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					InternetAddress mail1 = tEmailadresse_1.getText().trim().isEmpty() ? null : new InternetAddress(tEmailadresse_1.getText());
					InternetAddress mail2 = tEmailadresse_2.getText().trim().isEmpty() ? null : new InternetAddress(tEmailadresse_2.getText());
					
					if(mKontakt == null)
						mKontakt = new Kontakt(tName.getText(), tVorname.getText(), 
								tAnzeigename.getText(), tSpitzname.getText(),
								mail1, mail2,
								tPrivat.getText(), tDienstlich.getText(), tMobil.getText());
					else {
						mKontakt.setVorname(tVorname.getText());
						mKontakt.setNachname(tName.getText());
						mKontakt.setAnzeigename(tAnzeigename.getText());
						mKontakt.setSpitzname(tSpitzname.getText());
						mKontakt.setMail1(mail1);
						mKontakt.setMail2(mail2);
						mKontakt.setTelDienst(tDienstlich.getText());
						mKontakt.setTelPrivat(tPrivat.getText());
						mKontakt.setTelMobil(tMobil.getText());
					}
					
					close();
				} catch (AddressException ex) {
					parseFehler(ex);
				}
				
			}
		});
		
		btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnOK, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblSpitzname, Alignment.TRAILING)
						.addComponent(lblEmailadresse_1, Alignment.TRAILING)
						.addComponent(lblEmailadresse_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(20)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblName)
								.addComponent(lblAnzeigename)))
						.addComponent(lblVorname, Alignment.TRAILING))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(tVorname, GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
								.addComponent(tName)
								.addComponent(tAnzeigename)
								.addComponent(tSpitzname)
								.addComponent(tEmailadresse_1)
								.addComponent(tEmailadresse_2))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblDienstlich)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
									.addComponent(lblMobil)
									.addComponent(lblPrivat)))
							.addPreferredGap(ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(tPrivat, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
								.addComponent(tDienstlich, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
								.addComponent(tMobil, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)))
						.addComponent(btnAbbrechen, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblVorname)
						.addComponent(tVorname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblDienstlich)
						.addComponent(tDienstlich, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblName)
						.addComponent(tName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPrivat)
						.addComponent(tPrivat, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAnzeigename)
						.addComponent(tAnzeigename, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMobil)
						.addComponent(tMobil, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSpitzname)
						.addComponent(tSpitzname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEmailadresse_1)
						.addComponent(tEmailadresse_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEmailadresse_2)
						.addComponent(tEmailadresse_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOK)
						.addComponent(btnAbbrechen))
					.addGap(23))
		);
		getContentPane().setLayout(groupLayout);
	}
	
	public KontaktFrame() {
		mKontakt = null;
		this.setTitle(formatStringErstellen);
		
		initFrame();
	}
	
	public KontaktFrame(Kontakt k) {
		mKontakt = k;
		this.setTitle(String.format(formatStringBearbeiten, mKontakt));
		
		initFrame();
		
		String mail1 = mKontakt.getMail1() == null ? "" : mKontakt.getMail1().toUnicodeString();
		String mail2 = mKontakt.getMail2() == null ? "" : mKontakt.getMail2().toUnicodeString();
		
		tVorname.setText(mKontakt.getVorname());
		tName.setText(mKontakt.getNachname());
		tAnzeigename.setText(mKontakt.getAnzeigename());
		tSpitzname.setText(mKontakt.getSpitzname());
		tEmailadresse_1.setText(mail1);
		tEmailadresse_2.setText(mail2);
		tDienstlich.setText(mKontakt.getTelDienst());
		tPrivat.setText(mKontakt.getTelPrivat());
		tMobil.setText(mKontakt.getTelMobil());
	}
	
	private void parseFehler(AddressException ex) {
		JOptionPane.showMessageDialog(this, "Es ist ein Fehler beim Parsen einer Mailadresse aufgetreten:\n" + ex.getLocalizedMessage(),
				"Fehler", JOptionPane.OK_OPTION);
	}

	public Kontakt showDialog() {
		setVisible(true);
		
		return mKontakt;
	}
}
