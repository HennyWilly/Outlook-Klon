package de.outlookklon.gui;

import de.outlookklon.logik.mailclient.AuthentificationType;
import de.outlookklon.logik.mailclient.ConnectionSecurity;
import de.outlookklon.logik.mailclient.ImapServer;
import de.outlookklon.logik.mailclient.InboxServer;
import de.outlookklon.logik.mailclient.MailAccount;
import de.outlookklon.logik.mailclient.OutboxServer;
import de.outlookklon.logik.mailclient.Pop3Server;
import de.outlookklon.logik.mailclient.ServerSettings;
import de.outlookklon.logik.mailclient.SmtpServer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
public class AccountFrame extends ExtendedDialog<MailAccount> {

    private static final long serialVersionUID = -8114432074006047938L;

    private MailAccount mailAccount;
    private MailAccount tmpAccount;

    private JTextField txtDisplayname;
    private JTextField txtMail;
    private JPasswordField passwordField;
    private JTextField txtInServer;
    private JTextField txtOutServer;
    private JTextField txtUsername;

    private JComboBox<String> cbInProtocoll;
    private JSpinner spInPort;
    private JComboBox<ConnectionSecurity> cBInConnectionSecurity;
    private JComboBox<AuthentificationType> cBInAuthentificationType;

    private JSpinner spOutPort;
    private JComboBox<ConnectionSecurity> cBOutConnectionSecurity;
    private JComboBox<AuthentificationType> cBOutAuthentificationType;

    private JButton btnTest;
    private JButton btnAbort;
    private JButton btnFinish;

    /**
     * Erstellt eine neue Instanz der Klasse zum Erstellen eines neuen
     * MailAccount-Objekts
     */
    public AccountFrame() {
        super(750, 350);

        setTitle("Neues Konto hinzufügen");

        initFrame();
    }

    /**
     * Erstellt eine neue Instanz der Klasse zum Bearbeiten eines neuen
     * MailAccount-Objekts
     */
    public AccountFrame(MailAccount account) {
        super(750, 350);

        setTitle("Konto bearbeiten");

        initFrame();

        InboxServer inServer = account.getInboxMailServer();
        OutboxServer outServer = account.getOutboxMailServer();

        txtDisplayname.setText(account.getAddress().getPersonal());
        txtMail.setText(account.getAddress().getAddress());
        txtUsername.setText(account.getUser());

        if (inServer != null) {
            ServerSettings settings = inServer.getSettings();

            cbInProtocoll.setSelectedItem(inServer.getServerType());
            txtInServer.setText(settings.getHost());
            spInPort.setValue(settings.getPort());
            cBInConnectionSecurity.setSelectedItem(settings.getConnectionSecurity());
            cBInAuthentificationType.setSelectedItem(settings.getAuthentificationType());
        }
        if (outServer != null) {
            ServerSettings settings = outServer.getSettings();

            txtOutServer.setText(settings.getHost());
            spOutPort.setValue(settings.getPort());
            cBOutConnectionSecurity.setSelectedItem(settings.getConnectionSecurity());
            cBOutAuthentificationType.setSelectedItem(settings.getAuthentificationType());
        }
    }

    /**
     * Initialisiert die GUI-Elemente des Frames
     */
    private void initFrame() {
        txtMail = new JTextField();
        txtMail.setBounds(140, 58, 315, 20);
        txtMail.setColumns(10);

        JLabel lblMail = new JLabel("E-Mail-Adresse:");
        lblMail.setBounds(10, 61, 120, 14);

        JLabel lblPassword = new JLabel("Passwort:");
        lblPassword.setBounds(37, 92, 93, 14);

        passwordField = new JPasswordField();
        passwordField.setBounds(140, 89, 315, 20);

        JPanel groupBox = new JPanel();
        groupBox.setBounds(10, 120, 724, 137);

        JLabel lblInboxServer = new JLabel("Posteingang-Server:");
        lblInboxServer.setBounds(10, 37, 138, 14);

        cbInProtocoll = new JComboBox<>();
        cbInProtocoll.setBounds(158, 34, 57, 20);
        cbInProtocoll.setModel(new DefaultComboBoxModel<>(new String[]{"POP3", "IMAP"}));

        txtInServer = new JTextField();
        txtInServer.setBounds(226, 34, 162, 20);
        txtInServer.setColumns(10);

        spInPort = new JSpinner();
        spInPort.setBounds(394, 34, 54, 20);

        cBInConnectionSecurity = new JComboBox<>();
        cBInConnectionSecurity.setBounds(454, 34, 127, 20);
        cBInConnectionSecurity
                .setModel(new DefaultComboBoxModel<>(ConnectionSecurity.values()));

        cBInAuthentificationType = new JComboBox<>();
        cBInAuthentificationType.setBounds(587, 34, 127, 20);
        cBInAuthentificationType.setModel(new DefaultComboBoxModel<>(AuthentificationType.values()));

        JLabel lblOutboxServer = new JLabel("Postausgang-Server:");
        lblOutboxServer.setBounds(10, 65, 138, 14);

        txtOutServer = new JTextField();
        txtOutServer.setBounds(226, 65, 162, 20);
        txtOutServer.setColumns(10);

        spOutPort = new JSpinner();
        spOutPort.setBounds(394, 65, 54, 20);

        cBOutConnectionSecurity = new JComboBox<>();
        cBOutConnectionSecurity.setBounds(454, 65, 127, 20);
        cBOutConnectionSecurity
                .setModel(new DefaultComboBoxModel<>(ConnectionSecurity.values()));

        cBOutAuthentificationType = new JComboBox<>();
        cBOutAuthentificationType.setBounds(587, 64, 127, 20);
        cBOutAuthentificationType
                .setModel(new DefaultComboBoxModel<>(AuthentificationType.values()));

        JLabel lblSmtp = new JLabel("SMTP");
        lblSmtp.setBounds(160, 65, 55, 14);

        JLabel lblUsername = new JLabel("Benutzername:");
        lblUsername.setBounds(10, 106, 138, 14);

        txtUsername = new JTextField();
        txtUsername.setBounds(226, 103, 162, 20);
        txtUsername.setColumns(10);
        getContentPane().setLayout(null);
        getContentPane().add(lblMail);
        getContentPane().add(lblPassword);
        getContentPane().add(txtMail);
        getContentPane().add(passwordField);
        getContentPane().add(groupBox);
        groupBox.setLayout(null);
        groupBox.add(lblInboxServer);
        groupBox.add(cbInProtocoll);
        groupBox.add(lblUsername);
        groupBox.add(lblOutboxServer);
        groupBox.add(lblSmtp);
        groupBox.add(txtOutServer);
        groupBox.add(txtInServer);
        groupBox.add(spInPort);
        groupBox.add(spOutPort);
        groupBox.add(cBInConnectionSecurity);
        groupBox.add(cBOutConnectionSecurity);
        groupBox.add(cBInAuthentificationType);
        groupBox.add(cBOutAuthentificationType);
        groupBox.add(txtUsername);

        JLabel lblServerAddress = new JLabel("Server-Adresse");
        lblServerAddress.setBounds(226, 11, 162, 14);
        groupBox.add(lblServerAddress);

        JLabel lblPort = new JLabel("Port");
        lblPort.setBounds(394, 11, 54, 14);
        groupBox.add(lblPort);

        JLabel lblSsl = new JLabel("SSL");
        lblSsl.setBounds(454, 11, 123, 14);
        groupBox.add(lblSsl);

        JLabel lblAuthentification = new JLabel("Authentifizierung");
        lblAuthentification.setBounds(587, 11, 127, 14);
        groupBox.add(lblAuthentification);

        btnAbort = new JButton("Abbrechen");
        btnAbort.setBounds(649, 288, 85, 23);
        btnAbort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mailAccount = null;
                close();
            }
        });
        getContentPane().add(btnAbort);

        btnTest = new JButton("Testen");
        btnTest.setBounds(459, 288, 85, 23);
        btnTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAccountObject();
            }
        });
        getContentPane().add(btnTest);

        btnFinish = new JButton("Fertig");
        btnFinish.setEnabled(false);
        btnFinish.setBounds(554, 288, 85, 23);
        btnFinish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mailAccount = tmpAccount;
                close();
            }
        });
        getContentPane().add(btnFinish);

        JLabel lblDisplayname = new JLabel("Anzeigename:");
        lblDisplayname.setBounds(10, 14, 120, 14);
        getContentPane().add(lblDisplayname);

        txtDisplayname = new JTextField();
        txtDisplayname.setColumns(10);
        txtDisplayname.setBounds(140, 11, 315, 20);
        getContentPane().add(txtDisplayname);
    }

    /**
     * Versucht aus den getätigten Eingaben ein neues MailAccount-Objekt zu
     * erstellen
     */
    private void createAccountObject() {
        // Settings-Instanz für den Mailempfang erstellen
        ServerSettings inboxSettings = new ServerSettings(txtInServer.getText(), (Integer) spInPort.getValue(),
                cBInConnectionSecurity.getItemAt(cBInConnectionSecurity.getSelectedIndex()),
                cBInAuthentificationType.getItemAt(cBInAuthentificationType.getSelectedIndex()));

        // Instanz für den Mailempfang erstellen
        InboxServer inbox = null;
        if ("IMAP".equals(cbInProtocoll.getSelectedItem())) {
            inbox = new ImapServer(inboxSettings);
        } else if ("POP3".equals(cbInProtocoll.getSelectedItem())) {
            inbox = new Pop3Server(inboxSettings);
        } else {
            throw new UnsupportedOperationException("Unbekanntes Protokoll ausgewählt");
        }

        // Settings-Instanz für den Mailversandt erstellen
        ServerSettings outboxSettings = new ServerSettings(txtOutServer.getText(), (Integer) spOutPort.getValue(),
                cBOutConnectionSecurity.getItemAt(cBOutConnectionSecurity.getSelectedIndex()),
                cBOutAuthentificationType.getItemAt(cBOutAuthentificationType.getSelectedIndex()));

        // Instanz für den Mailversandt erstellen
        OutboxServer outbox = new SmtpServer(outboxSettings);

        try {
            // MailAccount-Instanz mit Versandt- und Empfangsinstanz erstellen
            tmpAccount = new MailAccount(inbox, outbox,
                    new InternetAddress(txtMail.getText(), txtDisplayname.getText()), txtUsername.getText(),
                    new String(passwordField.getPassword()));

            // Prüfe, ob sich mit den beiden Servern verbunden werden kann
            boolean valid = tmpAccount.validate();

            btnFinish.setEnabled(valid);
            if (!valid) {
                JOptionPane.showMessageDialog(this, "Die übergebenen Daten sind ungültig", "Fehler",
                        JOptionPane.OK_OPTION);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Fehler", JOptionPane.OK_OPTION);
        }
    }

    @Override
    protected MailAccount getDialogResult() {
        return mailAccount;
    }
}
