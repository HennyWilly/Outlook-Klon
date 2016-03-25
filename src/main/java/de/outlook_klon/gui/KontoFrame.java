package de.outlook_klon.gui;

import de.outlook_klon.logik.mailclient.Authentifizierungsart;
import de.outlook_klon.logik.mailclient.EmpfangsServer;
import de.outlook_klon.logik.mailclient.ImapServer;
import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.mailclient.Pop3Server;
import de.outlook_klon.logik.mailclient.SendServer;
import de.outlook_klon.logik.mailclient.ServerSettings;
import de.outlook_klon.logik.mailclient.SmtpServer;
import de.outlook_klon.logik.mailclient.Verbindungssicherheit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import javax.mail.internet.InternetAddress;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 * In diesem Frame können Daten für bestehende Mailkonten eingegeben, bzw.
 * verändert werden.
 *
 * @author Hendrik Karwanni
 */
public class KontoFrame extends ExtendedDialog<MailAccount> {

    private static final long serialVersionUID = -8114432074006047938L;

    private MailAccount mailAccount;
    private MailAccount tmpAccount;

    private JTextField txtAnzeigename;
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

    private JButton btnTesten;
    private JButton btnAbbrechen;
    private JButton btnFertig;

    /**
     * Initialisiert die GUI-Elemente des Frames
     */
    private void initFrame() {
        txtMail = new JTextField();
        txtMail.setBounds(140, 58, 315, 20);
        txtMail.setColumns(10);

        JLabel lblMail = new JLabel("E-Mail-Adresse:");
        lblMail.setBounds(10, 61, 120, 14);

        JLabel lblPasswort = new JLabel("Passwort:");
        lblPasswort.setBounds(37, 92, 93, 14);

        passwordField = new JPasswordField();
        passwordField.setBounds(140, 89, 315, 20);

        JPanel groupBox = new JPanel();
        groupBox.setBounds(10, 120, 724, 137);

        JLabel lblPosteingangserver = new JLabel("Posteingang-Server:");
        lblPosteingangserver.setBounds(10, 37, 138, 14);

        cbInProtokoll = new JComboBox<>();
        cbInProtokoll.setBounds(158, 34, 57, 20);
        cbInProtokoll.setModel(new DefaultComboBoxModel<>(new String[]{"POP3", "IMAP"}));

        txtInServer = new JTextField();
        txtInServer.setBounds(226, 34, 162, 20);
        txtInServer.setColumns(10);

        spInPort = new JSpinner();
        spInPort.setBounds(394, 34, 54, 20);

        cBInVerbindungssicherheit = new JComboBox<>();
        cBInVerbindungssicherheit.setBounds(454, 34, 127, 20);
        cBInVerbindungssicherheit
                .setModel(new DefaultComboBoxModel<>(Verbindungssicherheit.values()));

        cBInAuthentifizierung = new JComboBox<>();
        cBInAuthentifizierung.setBounds(587, 34, 127, 20);
        cBInAuthentifizierung.setModel(new DefaultComboBoxModel<>(Authentifizierungsart.values()));

        JLabel lblPostausgangsserver = new JLabel("Postausgang-Server:");
        lblPostausgangsserver.setBounds(10, 65, 138, 14);

        txtOutServer = new JTextField();
        txtOutServer.setBounds(226, 65, 162, 20);
        txtOutServer.setColumns(10);

        spOutPort = new JSpinner();
        spOutPort.setBounds(394, 65, 54, 20);

        cBOutVerbindungssicherheit = new JComboBox<>();
        cBOutVerbindungssicherheit.setBounds(454, 65, 127, 20);
        cBOutVerbindungssicherheit
                .setModel(new DefaultComboBoxModel<>(Verbindungssicherheit.values()));

        cBOutAuthentifizierung = new JComboBox<>();
        cBOutAuthentifizierung.setBounds(587, 64, 127, 20);
        cBOutAuthentifizierung
                .setModel(new DefaultComboBoxModel<>(Authentifizierungsart.values()));

        JLabel lblSmtp = new JLabel("SMTP");
        lblSmtp.setBounds(160, 65, 55, 14);

        JLabel lblBenutzername = new JLabel("Benutzername:");
        lblBenutzername.setBounds(10, 106, 138, 14);

        txtBenutzername = new JTextField();
        txtBenutzername.setBounds(226, 103, 162, 20);
        txtBenutzername.setColumns(10);
        getContentPane().setLayout(null);
        getContentPane().add(lblMail);
        getContentPane().add(lblPasswort);
        getContentPane().add(txtMail);
        getContentPane().add(passwordField);
        getContentPane().add(groupBox);
        groupBox.setLayout(null);
        groupBox.add(lblPosteingangserver);
        groupBox.add(cbInProtokoll);
        groupBox.add(lblBenutzername);
        groupBox.add(lblPostausgangsserver);
        groupBox.add(lblSmtp);
        groupBox.add(txtOutServer);
        groupBox.add(txtInServer);
        groupBox.add(spInPort);
        groupBox.add(spOutPort);
        groupBox.add(cBInVerbindungssicherheit);
        groupBox.add(cBOutVerbindungssicherheit);
        groupBox.add(cBInAuthentifizierung);
        groupBox.add(cBOutAuthentifizierung);
        groupBox.add(txtBenutzername);

        JLabel lblServeradresse = new JLabel("Server-Adresse");
        lblServeradresse.setBounds(226, 11, 162, 14);
        groupBox.add(lblServeradresse);

        JLabel lblPort = new JLabel("Port");
        lblPort.setBounds(394, 11, 54, 14);
        groupBox.add(lblPort);

        JLabel lblSsl = new JLabel("SSL");
        lblSsl.setBounds(454, 11, 123, 14);
        groupBox.add(lblSsl);

        JLabel lblAuthentifizierung = new JLabel("Authentifizierung");
        lblAuthentifizierung.setBounds(587, 11, 127, 14);
        groupBox.add(lblAuthentifizierung);

        btnAbbrechen = new JButton("Abbrechen");
        btnAbbrechen.setBounds(649, 288, 85, 23);
        btnAbbrechen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mailAccount = null;
                close();
            }
        });
        getContentPane().add(btnAbbrechen);

        btnTesten = new JButton("Testen");
        btnTesten.setBounds(459, 288, 85, 23);
        btnTesten.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                kontoObjektErzeugen();
            }
        });
        getContentPane().add(btnTesten);

        btnFertig = new JButton("Fertig");
        btnFertig.setEnabled(false);
        btnFertig.setBounds(554, 288, 85, 23);
        btnFertig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mailAccount = tmpAccount;
                close();
            }
        });
        getContentPane().add(btnFertig);

        JLabel lblAnzeigename = new JLabel("Anzeigename:");
        lblAnzeigename.setBounds(10, 14, 120, 14);
        getContentPane().add(lblAnzeigename);

        txtAnzeigename = new JTextField();
        txtAnzeigename.setColumns(10);
        txtAnzeigename.setBounds(140, 11, 315, 20);
        getContentPane().add(txtAnzeigename);
    }

    /**
     * Erstellt eine neue Instanz der Klasse zum Erstellen eines neuen
     * MailAccount-Objekts
     */
    public KontoFrame() {
        super(750, 350);

        setTitle("Neues Konto hinzufügen");

        initFrame();
    }

    /**
     * Erstellt eine neue Instanz der Klasse zum Bearbeiten eines neuen
     * MailAccount-Objekts
     */
    public KontoFrame(MailAccount acc) {
        super(750, 350);

        setTitle("Konto bearbeiten");

        initFrame();

        EmpfangsServer inServer = acc.getIncomingMailServer();
        SendServer outServer = acc.getOutgoingMailServer();

        txtAnzeigename.setText(acc.getAddress().getPersonal());
        txtMail.setText(acc.getAddress().getAddress());
        txtBenutzername.setText(acc.getUser());

        if (inServer != null) {
            ServerSettings settings = inServer.getSettings();

            cbInProtokoll.setSelectedItem(inServer.getServerType());
            txtInServer.setText(settings.getHost());
            spInPort.setValue(settings.getPort());
            cBInVerbindungssicherheit.setSelectedItem(settings.getConnectionSecurity());
            cBInAuthentifizierung.setSelectedItem(settings.getAuthentificationType());
        }
        if (outServer != null) {
            ServerSettings settings = outServer.getSettings();

            txtOutServer.setText(settings.getHost());
            spOutPort.setValue(settings.getPort());
            cBOutVerbindungssicherheit.setSelectedItem(settings.getConnectionSecurity());
            cBOutAuthentifizierung.setSelectedItem(settings.getAuthentificationType());
        }
    }

    /**
     * Versucht aus den getätigten Eingaben ein neues MailAccount-Objekt zu
     * erstellen
     */
    private void kontoObjektErzeugen() {
        // Settings-Instanz für den Mailempfang erstellen
        ServerSettings empfangsSettings = new ServerSettings(txtInServer.getText(), (Integer) spInPort.getValue(),
                cBInVerbindungssicherheit.getItemAt(cBInVerbindungssicherheit.getSelectedIndex()),
                cBInAuthentifizierung.getItemAt(cBInAuthentifizierung.getSelectedIndex()));

        // Instanz für den Mailempfang erstellen
        EmpfangsServer empfang = null;
        if (cbInProtokoll.getSelectedItem().equals("IMAP")) {
            empfang = new ImapServer(empfangsSettings);
        } else if (cbInProtokoll.getSelectedItem().equals("POP3")) {
            empfang = new Pop3Server(empfangsSettings);
        } else {
            throw new UnsupportedOperationException("Unbekanntes Protokoll ausgewählt");
        }

        // Settings-Instanz für den Mailversandt erstellen
        ServerSettings sendeSettings = new ServerSettings(txtOutServer.getText(), (Integer) spOutPort.getValue(),
                cBOutVerbindungssicherheit.getItemAt(cBOutVerbindungssicherheit.getSelectedIndex()),
                cBOutAuthentifizierung.getItemAt(cBOutAuthentifizierung.getSelectedIndex()));

        // Instanz für den Mailversandt erstellen
        SendServer senden = new SmtpServer(sendeSettings);

        try {
            // MailAccount-Instanz mit Versandt- und Empfangsinstanz erstellen
            tmpAccount = new MailAccount(empfang, senden,
                    new InternetAddress(txtMail.getText(), txtAnzeigename.getText()), txtBenutzername.getText(),
                    new String(passwordField.getPassword()));

            // Prüfe, ob sich mit den beiden Servern verbunden werden kann
            boolean gueltig = tmpAccount.validieren();

            btnFertig.setEnabled(gueltig);
            if (!gueltig) {
                JOptionPane.showMessageDialog(this, "Die übergebenen Daten sind ungültig", "Fehler",
                        JOptionPane.OK_OPTION);
            }
        } catch (UnsupportedEncodingException e) {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Fehler", JOptionPane.OK_OPTION);
        }
    }

    @Override
    protected MailAccount getDialogResult() {
        return mailAccount;
    }
}
