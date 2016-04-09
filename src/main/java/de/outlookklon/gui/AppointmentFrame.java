package de.outlookklon.gui;

import de.outlookklon.logik.User;
import de.outlookklon.logik.User.MailChecker;
import de.outlookklon.logik.calendar.Appointment;
import de.outlookklon.logik.contacts.Contact;
import de.outlookklon.logik.mailclient.MailAccount;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerDateModel;

/**
 * Dieses Fenster dient der Eingabe und Anzeige von Terminen.
 *
 * @author Hendrik Karwanni
 */
public class AppointmentFrame extends ExtendedDialog<Appointment> {

    private static final long serialVersionUID = 8451017422297429822L;

    private JTextField textSubject;
    private JTextField textDescription;
    private JTextField textLocation;

    private JSpinner dateStart;
    private JSpinner dateEnd;

    private JComboBox<String> comboAccount;
    private JComboBox<String> comboContact;
    private Appointment mAppointment;

    private void initFrame() {
        setTitle("Termin");

        JLabel lblSubject = new JLabel("Betreff:");
        JLabel lblPlace = new JLabel("Ort:");
        JLabel lblStart = new JLabel("Startzeit:");
        JLabel lblEnd = new JLabel("Endzeit:");
        JLabel lblDescription = new JLabel("Beschreibung:");
        JLabel lblAccount = new JLabel("Benutzerkonto:");
        JLabel lblContact = new JLabel("Kontakt:");

        textSubject = new JTextField();
        textSubject.setColumns(10);

        textDescription = new JTextField();
        textDescription.setColumns(10);

        textLocation = new JTextField();
        textLocation.setColumns(10);

        dateStart = new JSpinner();
        dateStart.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_YEAR));

        dateEnd = new JSpinner();
        dateEnd.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_YEAR));

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                SpinnerDateModel model1 = (SpinnerDateModel) dateStart.getModel();
                SpinnerDateModel model2 = (SpinnerDateModel) dateEnd.getModel();

                try {
                    if (mAppointment == null) {
                        mAppointment = new Appointment(textSubject.getText(), textLocation.getText(), model1.getDate(),
                                model2.getDate(), textDescription.getText(), comboAccount.getSelectedItem().toString(),
                                comboContact.getSelectedItem().toString());
                    } else {

                        mAppointment.setSubject(textSubject.getText());
                        mAppointment.setLocation(textLocation.getText());
                        mAppointment.setText(textDescription.getText());
                        mAppointment.setTimes(model1.getDate(), model2.getDate());
                        mAppointment.setUser(comboAccount.getSelectedItem().toString());
                        mAppointment.setContact(comboContact.getSelectedItem().toString());

                    }

                    close();
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(null, "Es ist ein Fehler aufgetreten:\n" + ex.getMessage(), "Fehler",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnAbort = new JButton("Abbrechen");
        btnAbort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        List<String> userAccounts = new ArrayList<>();
        for (MailChecker checker : User.getInstance()) {
            MailAccount ma = checker.getAccount();
            userAccounts.add(ma.getAddress().getAddress());
        }

        int comboSize1 = userAccounts.size() + 1;
        String[] selectableAccounts = new String[comboSize1];
        selectableAccounts[0] = "";
        for (int i = 1; i < comboSize1; i++) {
            selectableAccounts[i] = userAccounts.get(i - 1);
        }

        List<String> allContacts = new ArrayList<>();
        for (Contact k : User.getInstance().getContacts()) {
            allContacts.add(k.getDisplayname());
        }

        int comboSize2 = allContacts.size() + 1;
        String[] selectableContact = new String[comboSize2];
        selectableContact[0] = "";
        for (int i = 1; i < comboSize2; i++) {
            selectableContact[i] = allContacts.get(i - 1);
        }

        comboAccount = new JComboBox<>();
        comboAccount.setModel(new DefaultComboBoxModel<>(selectableAccounts));

        comboContact = new JComboBox<>();
        comboContact.setModel(new DefaultComboBoxModel<>(selectableContact));

        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout
                .setHorizontalGroup(
                        groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGroup(
                                        groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(
                                                groupLayout.createSequentialGroup().addGap(23)
                                                .addComponent(
                                                        btnOk)
                                                .addGap(102).addComponent(btnAbort))
                                        .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                                                .addGroup(groupLayout
                                                        .createParallelGroup(Alignment.LEADING, false)
                                                        .addGroup(groupLayout.createSequentialGroup()
                                                                .addGroup(groupLayout
                                                                        .createParallelGroup(
                                                                                Alignment.LEADING)
                                                                        .addComponent(lblStart)
                                                                        .addComponent(lblEnd,
                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                45,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(lblPlace,
                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                53,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(lblSubject,
                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                53,
                                                                                GroupLayout.PREFERRED_SIZE))
                                                                .addGap(77)
                                                                .addGroup(groupLayout
                                                                        .createParallelGroup(
                                                                                Alignment.LEADING,
                                                                                false)
                                                                        .addComponent(dateStart,
                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(dateEnd,
                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(textSubject,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                286, Short.MAX_VALUE)
                                                                        .addComponent(textLocation,
                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.PREFERRED_SIZE)))
                                                        .addGroup(groupLayout.createSequentialGroup()
                                                                .addComponent(lblDescription,
                                                                        GroupLayout.PREFERRED_SIZE, 89,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addGap(41)
                                                                .addGroup(groupLayout
                                                                        .createParallelGroup(
                                                                                Alignment.LEADING)
                                                                        .addComponent(textDescription)
                                                                        .addComponent(comboAccount, 0,
                                                                                286, Short.MAX_VALUE)
                                                                        .addComponent(comboContact, 0,
                                                                                286, Short.MAX_VALUE)))))
                                        .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                                                .addComponent(lblAccount))
                                        .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                                                .addComponent(lblContact)))
                                .addContainerGap(54, Short.MAX_VALUE)));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(groupLayout
                .createSequentialGroup().addGap(27)
                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblSubject).addComponent(
                        textSubject, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblPlace, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
                        .addComponent(textLocation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup().addComponent(lblStart).addGap(18)
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblEnd)
                                        .addComponent(dateEnd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)))
                        .addComponent(dateStart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblDescription)
                        .addComponent(textDescription, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE))
                .addGap(18)
                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblAccount)
                        .addComponent(comboAccount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE))
                .addGap(18)
                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblContact).addComponent(
                        comboContact, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED, 37, Short.MAX_VALUE).addGroup(groupLayout
                .createParallelGroup(Alignment.BASELINE).addComponent(btnOk).addComponent(btnAbort))
                .addContainerGap()));
        getContentPane().setLayout(groupLayout);
    }

    /**
     * Erstellt ein neues leeres Fenster.
     */
    public AppointmentFrame() {
        super(485, 344);

        mAppointment = null;
        initFrame();
        this.setTitle("Neuer Termin");
    }

    /**
     * Erstellt ein neues Fenster, das initial den übergebenen Appointment anzeigt.
     *
     * @param appointment anzuzeigender Appointment
     */
    public AppointmentFrame(Appointment appointment) {
        super(485, 344);

        mAppointment = appointment;
        initFrame();
        this.setTitle("Termin bearbeiten");
        textSubject.setText(appointment.getSubject());
        textLocation.setText(appointment.getLocation());
        textDescription.setText(appointment.getText());
        dateStart.setValue(appointment.getStart());
        dateEnd.setValue(appointment.getEnd());
        comboAccount.setSelectedItem(appointment.getUser());
        comboContact.setSelectedItem(appointment.getContact());
    }

    /**
     * Erstellt ein neues Fenster, das initial das übergebe Datum anzeigt.
     *
     * @param date initiales Datum
     */
    public AppointmentFrame(Date date) {
        this();

        SpinnerDateModel m1 = (SpinnerDateModel) dateStart.getModel();
        m1.setValue(date);

        SpinnerDateModel m2 = (SpinnerDateModel) dateEnd.getModel();
        m2.setValue(date);
    }

    @Override
    protected Appointment getDialogResult() {
        return mAppointment;
    }
}
