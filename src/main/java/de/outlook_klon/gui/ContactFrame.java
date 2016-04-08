package de.outlook_klon.gui;

import de.outlook_klon.gui.helpers.ListFocusTraversalPolicy;
import de.outlook_klon.logik.kontakte.Contact;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.mail.Address;
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

/**
 * In diesem Frame werden neue Kontakte erstellt, bzw. bestehende Kontakte
 * bearbeitet.
 *
 * @author Hendrik Karwanni
 */
public class ContactFrame extends ExtendedDialog<Contact> {

    private static final long serialVersionUID = 1466530984514818388L;

    private static final String FORMAT_STRING_CREATE1 = "Neuer Kontakt";
    private static final String FORMAT_STRING_CREATE2 = "Neuer Kontakt f�r %s";
    private static final String FORMAT_STRING_EDIT1 = "Kontakt bearbeiten";
    private static final String FORMAT_STRING_EDIT2 = "Kontakt von %s bearbeiten";

    private Contact mContact;

    private JTextField tForename;
    private JTextField tSurname;
    private JTextField tDisplayname;
    private JTextField tNickname;
    private JTextField tEmailaddress_1;
    private JTextField tEmailaddress_2;
    private JTextField tDutyphone;
    private JTextField tPrivatephone;
    private JTextField tMobilephone;

    private JButton btnOK;
    private JButton btnAbort;

    /**
     * Initialisiert die Komponenten der GUI
     */
    private void initFrame() {
        final JLabel lblForename = new JLabel("Vorname: ");
        final JLabel lblSurname = new JLabel("Name: ");
        final JLabel lblDisplayname = new JLabel("Anzeigename: ");
        final JLabel lblNickname = new JLabel("Spitzname: ");
        final JLabel lblEmailaddress_1 = new JLabel("E-Mail-Adresse: ");
        final JLabel lblEmailaddress_2 = new JLabel("2. E-Mail-Adresse: ");
        final JLabel lblDutyphone = new JLabel("Dienstlich: ");
        final JLabel lblPrivatephone = new JLabel("Privat: ");
        final JLabel lblMobilephone = new JLabel("Mobil: ");

        tForename = new JTextField();
        tForename.setColumns(10);
        tForename.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                // Aktualisiere den Anzeigenamen beim Entfernen eines Zeichens
                refreshDisplayname();
            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                // Aktualisiere den Anzeigenamen beim Einf�gen eines Zeichens
                refreshDisplayname();
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                // Aktualisiere den Anzeigenamen beim Ver�ndern eines Zeichens
                refreshDisplayname();
            }
        });

        tSurname = new JTextField();
        tSurname.setColumns(10);
        tSurname.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                // Aktualisiere den Anzeigenamen beim Entfernen eines Zeichens
                refreshDisplayname();
            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                // Aktualisiere den Anzeigenamen beim Einf�gen eines Zeichens
                refreshDisplayname();
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                // Aktualisiere den Anzeigenamen beim Ver�ndern eines Zeichens
                refreshDisplayname();
            }
        });

        tDisplayname = new JTextField();
        tDisplayname.setColumns(10);
        tDisplayname.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                // Aktualisiere den FrameTitel beim Entfernen eines Zeichens
                refreshTitle();
            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                // Aktualisiere den FrameTitel beim Einf�gen eines Zeichens
                refreshTitle();
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                // Aktualisiere den FrameTitel beim Ver�ndern eines Zeichens
                refreshTitle();
            }
        });

        tNickname = new JTextField();
        tNickname.setColumns(10);

        tEmailaddress_1 = new JTextField();
        tEmailaddress_1.setColumns(10);

        tEmailaddress_2 = new JTextField();
        tEmailaddress_2.setColumns(10);

        tDutyphone = new JTextField();
        tDutyphone.setColumns(10);

        tPrivatephone = new JTextField();
        tPrivatephone.setColumns(10);

        tMobilephone = new JTextField();
        tMobilephone.setColumns(10);

        btnOK = new JButton("OK");
        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                finalizeFrame();
            }
        });

        btnAbort = new JButton("Abbrechen");
        btnAbort.addActionListener(new ActionListener() {
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
                                .addComponent(lblNickname, Alignment.TRAILING)
                                .addComponent(lblEmailaddress_1, Alignment.TRAILING)
                                .addComponent(lblEmailaddress_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                .addGroup(groupLayout.createSequentialGroup().addGap(20)
                                        .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                                .addComponent(lblSurname).addComponent(lblDisplayname)))
                                .addComponent(lblForename, Alignment.TRAILING))
                        .addGap(18)
                        .addGroup(
                                groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
                                .createSequentialGroup()
                                .addGroup(
                                        groupLayout.createParallelGroup(Alignment.LEADING, false)
                                        .addComponent(tForename, GroupLayout.DEFAULT_SIZE, 244,
                                                Short.MAX_VALUE)
                                        .addComponent(tSurname).addComponent(tDisplayname)
                                        .addComponent(tNickname).addComponent(tEmailaddress_1)
                                        .addComponent(tEmailaddress_2))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                        .addComponent(lblDutyphone)
                                        .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                .addComponent(lblMobilephone).addComponent(lblPrivatephone)))
                                .addPreferredGap(ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(tPrivatephone, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                                        .addComponent(tDutyphone, GroupLayout.DEFAULT_SIZE, 208,
                                                Short.MAX_VALUE)
                                        .addComponent(tMobilephone, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)))
                                .addComponent(btnAbort, GroupLayout.PREFERRED_SIZE, 97,
                                        GroupLayout.PREFERRED_SIZE))
                        .addContainerGap()));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblForename)
                                .addComponent(tForename, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblDutyphone).addComponent(tDutyphone, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblSurname)
                                .addComponent(tSurname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblPrivatephone).addComponent(tPrivatephone, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblDisplayname)
                                .addComponent(tDisplayname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblMobilephone).addComponent(tMobilephone, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblNickname)
                                .addComponent(tNickname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE))
                        .addGap(18)
                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblEmailaddress_1)
                                .addComponent(tEmailaddress_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE))
                        .addGap(18)
                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblEmailaddress_2)
                                .addComponent(tEmailaddress_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE))
                        .addGap(18).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(btnOK)
                        .addComponent(btnAbort))
                        .addGap(23)));
        getContentPane().setLayout(groupLayout);

        // Liste, die die Reihenfolge speichert, in der bei einem Druck der
        // Tab-Taste durch die Komponenten gewandert werden soll
        List<Component> tabOrder = new ArrayList<>();
        tabOrder.add(tForename);
        tabOrder.add(tSurname);
        tabOrder.add(tDisplayname);
        tabOrder.add(tNickname);
        tabOrder.add(tEmailaddress_1);
        tabOrder.add(tEmailaddress_2);
        tabOrder.add(tDutyphone);
        tabOrder.add(tPrivatephone);
        tabOrder.add(tMobilephone);
        tabOrder.add(btnOK);
        tabOrder.add(btnAbort);

        setFocusTraversalPolicy(new ListFocusTraversalPolicy(tabOrder));
    }

    /**
     * Erstellt eine neue Instanz der Klasse zum Erstellen eines Kontakts
     */
    public ContactFrame() {
        super(685, 285);

        mContact = null;
        this.setTitle(FORMAT_STRING_CREATE1);

        initFrame();
    }

    /**
     * Erstellt eine neue Instanz der Klasse zum Bearbeiten des �bergebenen
     * Kontakts
     *
     * @param k Contact-Instanz, die in dem Frame bearbeitet werden soll
     */
    public ContactFrame(Contact k) {
        super(685, 285);

        mContact = k;
        this.setTitle(String.format(FORMAT_STRING_EDIT2, mContact));

        initFrame();

        String mail1 = mContact.getAddress1AsString();
        String mail2 = mContact.getAddress2AsString();

        tForename.setText(mContact.getForename());
        tSurname.setText(mContact.getSurname());
        tDisplayname.setText(mContact.getDisplayname());
        tNickname.setText(mContact.getNickname());
        tEmailaddress_1.setText(mail1);
        tEmailaddress_2.setText(mail2);
        tDutyphone.setText(mContact.getDutyphone());
        tPrivatephone.setText(mContact.getPrivatephone());
        tMobilephone.setText(mContact.getMobilephone());
    }

    /**
     * Aktualisiert den Anzeigenamen des Kontakts
     */
    private void refreshDisplayname() {
        tDisplayname.setText(tForename.getText() + " " + tSurname.getText());
    }

    /**
     * Aktualisiert den Titel des Frames
     */
    private void refreshTitle() {
        String name = tDisplayname.getText();

        String title;
        if (mContact == null) {
            if (name != null & !name.trim().isEmpty()) {
                title = String.format(FORMAT_STRING_CREATE2, name);
            } else {
                title = FORMAT_STRING_CREATE1;
            }
        } else if (name != null & !name.trim().isEmpty()) {
            title = String.format(FORMAT_STRING_EDIT2, name);
        } else {
            title = FORMAT_STRING_EDIT1;
        }

        setTitle(title);
    }

    /**
     * Wird beim Klick auf den OK-Buttom aufgerufen, um den Dialog auf die
 R�ckgabe des finalen Contact-Objekts vorzubereiten.
     */
    private void finalizeFrame() {
        try {
            String strMail1 = tEmailaddress_1.getText().trim();
            String strMail2 = tEmailaddress_2.getText().trim();

            Address mail1 = strMail1.isEmpty() ? null : new InternetAddress(tEmailaddress_1.getText(), true);
            Address mail2 = strMail2.isEmpty() ? null : new InternetAddress(tEmailaddress_2.getText(), true);

            if (mail1 == null && mail2 != null) {
                mail1 = mail2;
                mail2 = null;
            }

            if (mail1 == null && tForename.getText().trim().isEmpty() && tSurname.getText().trim().isEmpty()
                    && tDisplayname.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Sie m�ssen mindestens eine der folgenden Angaben machen:\n"
                        + "E-Mail-Adresse, Vorname, Name, Anzeigename",
                        "Informationen fehlen", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (mContact == null) {
                // Erstelle neuen Contact
                mContact = new Contact(tSurname.getText(), tForename.getText(), tDisplayname.getText(),
                        tNickname.getText(), mail1, mail2, tPrivatephone.getText(), tDutyphone.getText(), tMobilephone.getText());
            } else {
                // Bearbeite existierenden Contact
                mContact.setForename(tForename.getText());
                mContact.setSurname(tSurname.getText());
                mContact.setDisplayname(tDisplayname.getText());
                mContact.setNickname(tNickname.getText());
                mContact.setAddress1(mail1);
                mContact.setAddress2(mail2);
                mContact.setDutyphone(tDutyphone.getText());
                mContact.setPrivatephone(tPrivatephone.getText());
                mContact.setMobilephone(tMobilephone.getText());
            }

            close();
        } catch (AddressException ex) {
            JOptionPane.showMessageDialog(this,
                    "Es ist ein Fehler beim Parsen einer Mailadresse aufgetreten:\n" + ex.getMessage(), "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected Contact getDialogResult() {
        return mContact;
    }
}
