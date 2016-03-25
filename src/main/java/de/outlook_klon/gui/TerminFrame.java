package de.outlook_klon.gui;

import de.outlook_klon.logik.Benutzer;
import de.outlook_klon.logik.Benutzer.MailChecker;
import de.outlook_klon.logik.kalendar.Termin;
import de.outlook_klon.logik.kontakte.Kontakt;
import de.outlook_klon.logik.mailclient.MailAccount;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

public class TerminFrame extends ExtendedDialog<Termin> {

    private static final long serialVersionUID = 8451017422297429822L;

    private JTextField textBetreff;
    private JTextField textBeschreibung;
    private JTextField textOrt;

    private JSpinner dateStart;
    private JSpinner dateEnde;

    private JComboBox<String> comboKonto;
    private JComboBox<String> comboKontakt;
    private Termin mTermin;

    private void initFrame() {
        setTitle("Termin");

        JLabel lblNBetreff = new JLabel("Betreff:");
        JLabel lblOrt = new JLabel("Ort:");
        JLabel lblNewLabel = new JLabel("Startzeit:");
        JLabel lblEndzeit = new JLabel("Endzeit:");
        JLabel lblBeschreibung = new JLabel("Beschreibung:");
        JLabel lblBenutzerkonto = new JLabel("Benutzerkonto:");
        JLabel lblKontakt = new JLabel("Kontakt:");

        textBetreff = new JTextField();
        textBetreff.setColumns(10);

        textBeschreibung = new JTextField();
        textBeschreibung.setColumns(10);

        textOrt = new JTextField();
        textOrt.setColumns(10);

        dateStart = new JSpinner();
        dateStart.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_YEAR));

        dateEnde = new JSpinner();
        dateEnde.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_YEAR));

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                SpinnerDateModel model1 = (SpinnerDateModel) dateStart.getModel();
                SpinnerDateModel model2 = (SpinnerDateModel) dateEnde.getModel();

                try {
                    if (mTermin == null) {
                        mTermin = new Termin(textBetreff.getText(), textOrt.getText(), model1.getDate(),
                                model2.getDate(), textBeschreibung.getText(), comboKonto.getSelectedItem().toString(),
                                comboKontakt.getSelectedItem().toString());
                    } else {

                        mTermin.setSubject(textBetreff.getText());
                        mTermin.setLocation(textOrt.getText());
                        mTermin.setText(textBeschreibung.getText());
                        mTermin.setTimes(model1.getDate(), model2.getDate());
                        mTermin.setUser(comboKonto.getSelectedItem().toString());
                        mTermin.setContact(comboKontakt.getSelectedItem().toString());

                    }

                    close();
                } catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(null, "Es ist ein Fehler aufgetreten:\n" + ex.getMessage(), "Fehler",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnAbbrechen = new JButton("Abbrechen");
        btnAbbrechen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        ArrayList<String> benutzerKonten = new ArrayList<String>();
        for (MailChecker checker : Benutzer.getInstanz()) {
            MailAccount ma = checker.getAccount();
            benutzerKonten.add(ma.getAddress().getAddress());
        }

        int comboSize1 = benutzerKonten.size() + 1;
        String[] selectableBenutzerkonto = new String[comboSize1];
        selectableBenutzerkonto[0] = "";
        for (int i = 1; i < comboSize1; i++) {
            selectableBenutzerkonto[i] = benutzerKonten.get(i - 1);
        }

        ArrayList<String> allKontakte = new ArrayList<String>();
        for (Kontakt k : Benutzer.getInstanz().getKontakte()) {
            allKontakte.add(k.getDisplayname());
        }

        int comboSize2 = allKontakte.size() + 1;
        String[] selectableKontakt = new String[comboSize2];
        selectableKontakt[0] = "";
        for (int i = 1; i < comboSize2; i++) {
            selectableKontakt[i] = allKontakte.get(i - 1);
        }

        comboKonto = new JComboBox<String>();
        comboKonto.setModel(new DefaultComboBoxModel<String>(selectableBenutzerkonto));

        comboKontakt = new JComboBox<String>();
        comboKontakt.setModel(new DefaultComboBoxModel<String>(selectableKontakt));

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
                                                .addGap(102).addComponent(btnAbbrechen))
                                        .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                                                .addGroup(groupLayout
                                                        .createParallelGroup(Alignment.LEADING, false)
                                                        .addGroup(groupLayout.createSequentialGroup()
                                                                .addGroup(groupLayout
                                                                        .createParallelGroup(
                                                                                Alignment.LEADING)
                                                                        .addComponent(lblNewLabel)
                                                                        .addComponent(lblEndzeit,
                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                45,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(lblOrt,
                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                53,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(lblNBetreff,
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
                                                                        .addComponent(dateEnde,
                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(textBetreff,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                286, Short.MAX_VALUE)
                                                                        .addComponent(textOrt,
                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                GroupLayout.PREFERRED_SIZE)))
                                                        .addGroup(groupLayout.createSequentialGroup()
                                                                .addComponent(lblBeschreibung,
                                                                        GroupLayout.PREFERRED_SIZE, 89,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addGap(41)
                                                                .addGroup(groupLayout
                                                                        .createParallelGroup(
                                                                                Alignment.LEADING)
                                                                        .addComponent(textBeschreibung)
                                                                        .addComponent(comboKonto, 0,
                                                                                286, Short.MAX_VALUE)
                                                                        .addComponent(comboKontakt, 0,
                                                                                286, Short.MAX_VALUE)))))
                                        .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                                                .addComponent(lblBenutzerkonto))
                                        .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                                                .addComponent(lblKontakt)))
                                .addContainerGap(54, Short.MAX_VALUE)));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(groupLayout
                .createSequentialGroup().addGap(27)
                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblNBetreff).addComponent(
                        textBetreff, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblOrt, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
                        .addComponent(textOrt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup().addComponent(lblNewLabel).addGap(18)
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblEndzeit)
                                        .addComponent(dateEnde, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)))
                        .addComponent(dateStart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblBeschreibung)
                        .addComponent(textBeschreibung, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE))
                .addGap(18)
                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblBenutzerkonto)
                        .addComponent(comboKonto, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE))
                .addGap(18)
                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblKontakt).addComponent(
                        comboKontakt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED, 37, Short.MAX_VALUE).addGroup(groupLayout
                .createParallelGroup(Alignment.BASELINE).addComponent(btnOk).addComponent(btnAbbrechen))
                .addContainerGap()));
        getContentPane().setLayout(groupLayout);
    }

    public TerminFrame() {
        super(485, 344);

        mTermin = null;
        initFrame();
        this.setTitle("Neuer Termin");
    }

    public TerminFrame(Termin t) {
        super(485, 344);

        mTermin = t;
        initFrame();
        this.setTitle("Termin bearbeiten");
        textBetreff.setText(t.getSubject());
        textOrt.setText(t.getLocation());
        textBeschreibung.setText(t.getText());
        dateStart.setValue(t.getStart());
        dateEnde.setValue(t.getEnd());
        comboKonto.setSelectedItem(t.getUser());
        comboKontakt.setSelectedItem(t.getContact());
    }

    public TerminFrame(Date date) {
        this();

        SpinnerDateModel m1 = (SpinnerDateModel) dateStart.getModel();
        m1.setValue(date);

        SpinnerDateModel m2 = (SpinnerDateModel) dateEnde.getModel();
        m2.setValue(date);
    }

    @Override
    protected Termin getDialogResult() {
        return mTermin;
    }
}
