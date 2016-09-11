package de.outlookklon.gui.dialogs;

import de.outlookklon.gui.helpers.Dialogs;
import de.outlookklon.gui.helpers.ListFocusTraversalPolicy;
import de.outlookklon.localization.Localization;
import de.outlookklon.logik.contacts.Contact;
import java.awt.Component;
import java.awt.Window;
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
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In diesem Frame werden neue Kontakte erstellt, bzw. bestehende Kontakte
 * bearbeitet.
 *
 * @author Hendrik Karwanni
 */
public class ContactFrame extends ExtendedDialog<Contact> {

    private static final long serialVersionUID = 1466530984514818388L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactFrame.class);

    private static final int DIALOG_WIDTH = 685;
    private static final int DIALOG_HEIGHT = 285;

    private static final int TEXTFIELD_COLUMNS = 10;

    private Contact mContact;

    private final JLabel lblForename;
    private final JTextField tForename;
    private final JLabel lblSurname;
    private final JTextField tSurname;
    private final JLabel lblDisplayname;
    private final JTextField tDisplayname;
    private final JLabel lblNickname;
    private final JTextField tNickname;
    private final JLabel lblEmailaddress1;
    private final JTextField tEmailaddress1;
    private final JLabel lblEmailaddress2;
    private final JTextField tEmailaddress2;
    private final JLabel lblDutyphone;
    private final JTextField tDutyphone;
    private final JLabel lblPrivatephone;
    private final JTextField tPrivatephone;
    private final JLabel lblMobilephone;
    private final JTextField tMobilephone;

    private final JButton btnOK;
    private final JButton btnAbort;

    /**
     * Erstellt eine neue Instanz der Klasse zum Erstellen eines Kontakts
     *
     * @param parent Das Vaterfenster des Dialogs
     */
    public ContactFrame(Window parent) {
        this(parent, null);
    }

    /**
     * Erstellt eine neue Instanz der Klasse zum Bearbeiten des übergebenen
     * Kontakts
     *
     * @param parent Das Vaterfenster des Dialogs
     * @param contact Contact-Instanz, die in dem Frame bearbeitet werden soll
     */
    public ContactFrame(Window parent, Contact contact) {
        super(parent, DIALOG_WIDTH, DIALOG_HEIGHT);

        this.mContact = contact;

        lblForename = new JLabel();
        lblSurname = new JLabel();
        lblDisplayname = new JLabel();
        lblNickname = new JLabel();
        lblEmailaddress1 = new JLabel();
        lblEmailaddress2 = new JLabel();
        lblDutyphone = new JLabel();
        lblPrivatephone = new JLabel();
        lblMobilephone = new JLabel();

        tForename = new JTextField();
        tSurname = new JTextField();
        tDisplayname = new JTextField();
        tNickname = new JTextField();
        tEmailaddress1 = new JTextField();
        tEmailaddress2 = new JTextField();
        tDutyphone = new JTextField();
        tPrivatephone = new JTextField();
        tMobilephone = new JTextField();

        btnOK = new JButton();
        btnAbort = new JButton();

        initFrame();
        updateTexts();

        if (mContact != null) {
            String mail1 = mContact.getAddress1AsString();
            String mail2 = mContact.getAddress2AsString();

            tForename.setText(mContact.getForename());
            tSurname.setText(mContact.getSurname());
            tDisplayname.setText(mContact.getDisplayname());
            tNickname.setText(mContact.getNickname());
            tEmailaddress1.setText(mail1);
            tEmailaddress2.setText(mail2);
            tDutyphone.setText(mContact.getDutyphone());
            tPrivatephone.setText(mContact.getPrivatephone());
            tMobilephone.setText(mContact.getMobilephone());
        }
    }

    @Override
    public void updateTexts() {
        refreshTitle();

        lblForename.setText(Localization.getString("Contact_Forename"));
        lblSurname.setText(Localization.getString("Contact_Surname"));
        lblDisplayname.setText(Localization.getString("Account_DisplayName"));
        lblNickname.setText(Localization.getString("Contact_Nickname"));
        lblEmailaddress1.setText(Localization.getString("Account_MailAddress"));
        lblEmailaddress2.setText(Localization.getString("Contact_MailAddress2"));
        lblDutyphone.setText(Localization.getString("Contact_DutyPhone"));
        lblPrivatephone.setText(Localization.getString("Contact_PrivatePhone"));
        lblMobilephone.setText(Localization.getString("Contact_MobilePhone"));

        btnOK.setText(Localization.getString("Button_Ok"));
        btnAbort.setText(Localization.getString("Button_Abort"));
    }

    /**
     * Initialisiert die Komponenten der GUI
     */
    private void initFrame() {
        final DocumentListener nameDocListener = new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                // Aktualisiere den Anzeigenamen beim Entfernen eines Zeichens
                refreshDisplayname();
            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                // Aktualisiere den Anzeigenamen beim Einfügen eines Zeichens
                refreshDisplayname();
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                // Aktualisiere den Anzeigenamen beim Verändern eines Zeichens
                refreshDisplayname();
            }
        };

        tForename.setColumns(TEXTFIELD_COLUMNS);
        tForename.getDocument().addDocumentListener(nameDocListener);

        tSurname.setColumns(TEXTFIELD_COLUMNS);
        tSurname.getDocument().addDocumentListener(nameDocListener);

        tDisplayname.setColumns(TEXTFIELD_COLUMNS);
        tDisplayname.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                // Aktualisiere den FrameTitel beim Entfernen eines Zeichens
                refreshTitle();
            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                // Aktualisiere den FrameTitel beim Einfügen eines Zeichens
                refreshTitle();
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                // Aktualisiere den FrameTitel beim Verändern eines Zeichens
                refreshTitle();
            }
        });

        tNickname.setColumns(TEXTFIELD_COLUMNS);
        tEmailaddress1.setColumns(TEXTFIELD_COLUMNS);
        tEmailaddress2.setColumns(TEXTFIELD_COLUMNS);
        tDutyphone.setColumns(TEXTFIELD_COLUMNS);
        tPrivatephone.setColumns(TEXTFIELD_COLUMNS);
        tMobilephone.setColumns(TEXTFIELD_COLUMNS);

        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                finalizeFrame();
            }
        });

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
                                .addComponent(lblEmailaddress1, Alignment.TRAILING)
                                .addComponent(lblEmailaddress2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
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
                                        .addComponent(tNickname).addComponent(tEmailaddress1)
                                        .addComponent(tEmailaddress2))
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
                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblEmailaddress1)
                                .addComponent(tEmailaddress1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE))
                        .addGap(18)
                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblEmailaddress2)
                                .addComponent(tEmailaddress2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
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
        tabOrder.add(tEmailaddress1);
        tabOrder.add(tEmailaddress2);
        tabOrder.add(tDutyphone);
        tabOrder.add(tPrivatephone);
        tabOrder.add(tMobilephone);
        tabOrder.add(btnOK);
        tabOrder.add(btnAbort);

        setFocusTraversalPolicy(new ListFocusTraversalPolicy(tabOrder));
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
            if (name != null && !name.trim().isEmpty()) {
                title = String.format(Localization.getString("ContactFrame_NewContactFormat"), name);
            } else {
                title = Localization.getString("AddressBookFrame_Menu_NewContact");
            }
        } else if (name != null && !name.trim().isEmpty()) {
            title = String.format(Localization.getString("ContactFrame_EditContactFormat"), name);
        } else {
            title = Localization.getString("ContactFrame_EditContact");
        }

        setTitle(title);
    }

    /**
     * Wird beim Klick auf den OK-Buttom aufgerufen, um den Dialog auf die
     * Rückgabe des finalen Contact-Objekts vorzubereiten.
     */
    private void finalizeFrame() {
        Pair<Address, Address> addresses;
        try {
            addresses = getAddresses();
        } catch (AddressException ex) {
            LOGGER.error(Localization.getString("ContactFrame_ParseMailAddressError"), ex);
            Dialogs.showErrorDialog(this, Localization.getString("ContactFrame_ParseMailAddressError") + "\n" + ex.getMessage());

            return;
        }

        Address mail1 = addresses.getLeft();
        Address mail2 = addresses.getRight();

        if (!hasEnoughInformation(mail1)) {
            JOptionPane.showMessageDialog(this,
                    Localization.getString("ContactFrame_NotEnoughInformation"),
                    Localization.getString("ContactFrame_MissingInfomation"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        setContactData(mail1, mail2);

        close();
    }

    private void setContactData(Address mail1, Address mail2) {
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
    }

    private Pair<Address, Address> getAddresses() throws AddressException {
        String strMail1 = tEmailaddress1.getText().trim();
        String strMail2 = tEmailaddress2.getText().trim();

        Address mail1 = strMail1.isEmpty() ? null : new InternetAddress(tEmailaddress1.getText(), true);
        Address mail2 = strMail2.isEmpty() ? null : new InternetAddress(tEmailaddress2.getText(), true);

        if (mail1 == null && mail2 != null) {
            mail1 = mail2;
            mail2 = null;
        }

        return Pair.of(mail1, mail2);
    }

    private boolean hasEnoughInformation(Address mail1) {
        return mail1 != null
                || !tForename.getText().trim().isEmpty()
                || !tSurname.getText().trim().isEmpty()
                || !tDisplayname.getText().trim().isEmpty();
    }

    @Override
    protected Contact getDialogResult() {
        return mContact;
    }
}
