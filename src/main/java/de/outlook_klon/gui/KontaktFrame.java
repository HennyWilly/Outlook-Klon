package de.outlook_klon.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.outlook_klon.logik.kontakte.Kontakt;

/**
 * In diesem Frame werden neue Kontakte erstellt, bzw. bestehende Kontakte
 * bearbeitet.
 * 
 * @author Hendrik Karwanni
 */
public class KontaktFrame extends ExtendedDialog<Kontakt> {
	private static final long serialVersionUID = 1466530984514818388L;

	private static final String formatStringErstellen1 = "Neuer Kontakt";
	private static final String formatStringErstellen2 = "Neuer Kontakt für %s";
	private static final String formatStringBearbeiten1 = "Kontakt bearbeiten";
	private static final String formatStringBearbeiten2 = "Kontakt von %s bearbeiten";

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

	/**
	 * Erweitert die FocusTraversalPolicy-Klasse zum einfachen Wandern per
	 * Tab-Taste über alle Steuerelemente in der Reihenfolge, die im Vector
	 * beschrieben ist.
	 */
	public static class VectorFocusTraversalPolicy extends FocusTraversalPolicy {
		private Vector<Component> order;

		public VectorFocusTraversalPolicy(Vector<Component> order) {
			this.order = new Vector<Component>(order.size());
			this.order.addAll(order);
		}

		@Override
		public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
			int vectorIndex = order.indexOf(aComponent) + 1;
			if (vectorIndex >= order.size()) {
				vectorIndex = 0;
			}
			return order.get(vectorIndex);
		}

		@Override
		public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
			int vectorIndex = order.indexOf(aComponent) - 1;
			if (vectorIndex < 0) {
				vectorIndex = order.size() - 1;
			}
			return order.get(vectorIndex);
		}

		@Override
		public Component getDefaultComponent(Container focusCycleRoot) {
			return getFirstComponent(focusCycleRoot);
		}

		@Override
		public Component getLastComponent(Container focusCycleRoot) {
			return order.lastElement();
		}

		@Override
		public Component getFirstComponent(Container focusCycleRoot) {
			return order.firstElement();
		}
	}

	/**
	 * Initialisiert die Komponenten der GUI
	 */
	private void initFrame() {
		final JLabel lblVorname = new JLabel("Vorname: ");
		final JLabel lblName = new JLabel("Name: ");
		final JLabel lblAnzeigename = new JLabel("Anzeigename: ");
		final JLabel lblSpitzname = new JLabel("Spitzname: ");
		final JLabel lblEmailadresse_1 = new JLabel("E-Mail-Adresse: ");
		final JLabel lblEmailadresse_2 = new JLabel("2. E-Mail-Adresse: ");
		final JLabel lblDienstlich = new JLabel("Dienstlich: ");
		final JLabel lblPrivat = new JLabel("Privat: ");
		final JLabel lblMobil = new JLabel("Mobil: ");

		tVorname = new JTextField();
		tVorname.setColumns(10);
		tVorname.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				// Aktualisiere den Anzeigenamen beim Entfernen eines Zeichens
				aktualisiereAnzeigename();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				// Aktualisiere den Anzeigenamen beim Einfügen eines Zeichens
				aktualisiereAnzeigename();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// Aktualisiere den Anzeigenamen beim Verändern eines Zeichens
				aktualisiereAnzeigename();
			}
		});

		tName = new JTextField();
		tName.setColumns(10);
		tName.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				// Aktualisiere den Anzeigenamen beim Entfernen eines Zeichens
				aktualisiereAnzeigename();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				// Aktualisiere den Anzeigenamen beim Einfügen eines Zeichens
				aktualisiereAnzeigename();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// Aktualisiere den Anzeigenamen beim Verändern eines Zeichens
				aktualisiereAnzeigename();
			}
		});

		tAnzeigename = new JTextField();
		tAnzeigename.setColumns(10);
		tAnzeigename.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				// Aktualisiere den FrameTitel beim Entfernen eines Zeichens
				aktualisiereTitel();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				// Aktualisiere den FrameTitel beim Einfügen eines Zeichens
				aktualisiereTitel();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// Aktualisiere den FrameTitel beim Verändern eines Zeichens
				aktualisiereTitel();
			}
		});

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
				finalisiereFrame();
			}
		});

		btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnOK, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addComponent(lblSpitzname, Alignment.TRAILING)
								.addComponent(lblEmailadresse_1, Alignment.TRAILING)
								.addComponent(lblEmailadresse_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup().addGap(20)
										.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
												.addComponent(lblName).addComponent(lblAnzeigename)))
								.addComponent(lblVorname, Alignment.TRAILING))
						.addGap(18)
						.addGroup(
								groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
										.createSequentialGroup()
										.addGroup(
												groupLayout.createParallelGroup(Alignment.LEADING, false)
														.addComponent(tVorname, GroupLayout.DEFAULT_SIZE, 244,
																Short.MAX_VALUE)
														.addComponent(tName).addComponent(tAnzeigename)
														.addComponent(tSpitzname).addComponent(tEmailadresse_1)
														.addComponent(tEmailadresse_2))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
												.addComponent(lblDienstlich)
												.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
														.addComponent(lblMobil).addComponent(lblPrivat)))
										.addPreferredGap(ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
												.addComponent(tPrivat, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
												.addComponent(tDienstlich, GroupLayout.DEFAULT_SIZE, 208,
														Short.MAX_VALUE)
												.addComponent(tMobil, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)))
										.addComponent(btnAbbrechen, GroupLayout.PREFERRED_SIZE, 97,
												GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblVorname)
								.addComponent(tVorname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lblDienstlich).addComponent(tDienstlich, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblName)
								.addComponent(tName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lblPrivat).addComponent(tPrivat, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblAnzeigename)
								.addComponent(tAnzeigename, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lblMobil).addComponent(tMobil, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblSpitzname)
								.addComponent(tSpitzname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addGap(18)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblEmailadresse_1)
								.addComponent(tEmailadresse_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addGap(18)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblEmailadresse_2)
								.addComponent(tEmailadresse_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addGap(18).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(btnOK)
								.addComponent(btnAbbrechen))
						.addGap(23)));
		getContentPane().setLayout(groupLayout);

		// Vector, der die Reihenfolge speichert, in der bei einem Druck der
		// Tab-Taste durch die Komponenten gewandert werden soll
		Vector<Component> tabOrder = new Vector<Component>();
		tabOrder.add(tVorname);
		tabOrder.add(tName);
		tabOrder.add(tAnzeigename);
		tabOrder.add(tSpitzname);
		tabOrder.add(tEmailadresse_1);
		tabOrder.add(tEmailadresse_2);
		tabOrder.add(tDienstlich);
		tabOrder.add(tPrivat);
		tabOrder.add(tMobil);
		tabOrder.add(btnOK);
		tabOrder.add(btnAbbrechen);

		setFocusTraversalPolicy(new VectorFocusTraversalPolicy(tabOrder));
	}

	/**
	 * Erstellt eine neue Instanz der Klasse zum Erstellen eines Kontakts
	 */
	public KontaktFrame() {
		super(685, 285);

		mKontakt = null;
		this.setTitle(formatStringErstellen1);

		initFrame();
	}

	/**
	 * Erstellt eine neue Instanz der Klasse zum Bearbeiten des übergebenen
	 * Kontakts
	 * 
	 * @param k
	 *            Kontakt-Instanz, die in dem Frame bearbeitet werden soll
	 */
	public KontaktFrame(Kontakt k) {
		super(685, 285);

		mKontakt = k;
		this.setTitle(String.format(formatStringBearbeiten2, mKontakt));

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

	/**
	 * Aktualisiert den Anzeigenamen des Kontakts
	 */
	private void aktualisiereAnzeigename() {
		tAnzeigename.setText(tVorname.getText() + " " + tName.getText());
	}

	/**
	 * Aktualisiert den Titel des Frames
	 */
	private void aktualisiereTitel() {
		String name = tAnzeigename.getText();
		String titel = null;

		if (mKontakt == null) {
			if (name != null & !name.trim().isEmpty()) {
				titel = String.format(formatStringErstellen2, name);
			} else {
				titel = formatStringErstellen1;
			}
		} else {
			if (name != null & !name.trim().isEmpty()) {
				titel = String.format(formatStringBearbeiten2, name);
			} else {
				titel = formatStringBearbeiten1;
			}
		}

		setTitle(titel);
	}

	/**
	 * Wird beim Klick auf den OK-Buttom aufgerufen, um den Dialog auf die
	 * Rückgabe des finalen Kontakt-Objekts vorzubereiten.
	 */
	private void finalisiereFrame() {
		try {
			String strMail1 = tEmailadresse_1.getText().trim();
			String strMail2 = tEmailadresse_2.getText().trim();

			InternetAddress mail1 = strMail1.isEmpty() ? null : new InternetAddress(tEmailadresse_1.getText(), true);
			InternetAddress mail2 = strMail2.isEmpty() ? null : new InternetAddress(tEmailadresse_2.getText(), true);

			if (mail1 == null && mail2 != null) {
				mail1 = mail2;
				mail2 = null;
			}

			if (mail1 == null && tVorname.getText().trim().isEmpty() && tName.getText().trim().isEmpty()
					&& tAnzeigename.getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(this,
						"Sie müssen mindestens eine der folgenden Angaben machen:\n"
								+ "E-Mail-Adresse, Vorname, Name, Anzeigename",
						"Informationen fehlen", JOptionPane.WARNING_MESSAGE);
				return;
			}

			if (mKontakt == null) {
				// Erstelle neuen Kontakt
				mKontakt = new Kontakt(tName.getText(), tVorname.getText(), tAnzeigename.getText(),
						tSpitzname.getText(), mail1, mail2, tPrivat.getText(), tDienstlich.getText(), tMobil.getText());
			} else {
				// Bearbeite existierenden Kontakt
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
			JOptionPane.showMessageDialog(this,
					"Es ist ein Fehler beim Parsen einer Mailadresse aufgetreten:\n" + ex.getMessage(), "Fehler",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected Kontakt getDialogResult() {
		return mKontakt;
	}
}
