package de.outlookklon.gui;

import de.outlookklon.gui.helpers.Dialogs;
import de.outlookklon.localization.Localization;
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
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In diesem Frame können Daten für bestehende Mailkonten eingegeben, bzw.
 * verändert werden.
 *
 * @author Hendrik Karwanni
 */
public class AccountFrame extends ExtendedDialog<MailAccount> {

    private static final long serialVersionUID = -8114432074006047938L;

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountFrame.class);

    private final String titleLocalizationKey;

    private MailAccount mailAccount;
    private MailAccount tmpAccount;

    private final JLabel lblDisplayname;
    private final JTextField txtDisplayname;
    private final JLabel lblMail;
    private final JTextField txtMail;
    private final JLabel lblPassword;
    private final JPasswordField passwordField;
    private final JLabel lblInboxServer;
    private final JTextField txtInServer;
    private final JLabel lblOutboxServer;
    private final JTextField txtOutServer;
    private final JLabel lblUsername;
    private final JTextField txtUsername;

    private final JLabel lblServerAddress;
    private final JLabel lblAuthentification;

    private final JComboBox<String> cbInProtocoll;
    private final JSpinner spInPort;
    private final JComboBox<ConnectionSecurity> cBInConnectionSecurity;
    private final JComboBox<AuthentificationType> cBInAuthentificationType;

    private final JSpinner spOutPort;
    private final JComboBox<ConnectionSecurity> cBOutConnectionSecurity;
    private final JComboBox<AuthentificationType> cBOutAuthentificationType;

    private final JButton btnTest;
    private final JButton btnAbort;
    private final JButton btnFinish;

    private AccountFrame(String titleKey) {
        super(750, 350);
        titleLocalizationKey = titleKey;

        txtMail = new JTextField();
        lblMail = new JLabel();
        lblPassword = new JLabel();
        passwordField = new JPasswordField();
        lblInboxServer = new JLabel();
        cbInProtocoll = new JComboBox<>();
        txtInServer = new JTextField();
        spInPort = new JSpinner();
        cBInConnectionSecurity = new JComboBox<>();
        cBInAuthentificationType = new JComboBox<>();
        lblOutboxServer = new JLabel();
        txtOutServer = new JTextField();
        spOutPort = new JSpinner();
        cBOutConnectionSecurity = new JComboBox<>();
        cBOutAuthentificationType = new JComboBox<>();
        lblUsername = new JLabel();
        txtUsername = new JTextField();
        lblServerAddress = new JLabel();
        lblAuthentification = new JLabel();
        btnAbort = new JButton();
        btnTest = new JButton();
        btnFinish = new JButton();
        lblDisplayname = new JLabel();
        txtDisplayname = new JTextField();

        initFrame();
        updateTexts();
    }

    /**
     * Erstellt eine neue Instanz der Klasse zum Erstellen eines neuen
     * MailAccount-Objekts
     */
    public AccountFrame() {
        this("AccountFrame_DefaultTitle");
    }

    /**
     * Erstellt eine neue Instanz der Klasse zum Bearbeiten eines neuen
     * MailAccount-Objekts
     *
     * @param account Zu bearbeitender Account
     */
    public AccountFrame(MailAccount account) {
        this("AccountFrame_EditTitle");

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

    @Override
    public void updateTexts() {
        setTitle(Localization.getString(titleLocalizationKey));

        lblMail.setText(Localization.getString("Account_MailAddress"));
        lblPassword.setText(Localization.getString("Account_Password"));
        lblInboxServer.setText(Localization.getString("Account_Inbox"));
        lblOutboxServer.setText(Localization.getString("Account_Outbox"));
        lblUsername.setText(Localization.getString("Account_Username"));
        lblServerAddress.setText(Localization.getString("Account_ServerAddress"));
        lblAuthentification.setText(Localization.getString("Account_Authentification"));
        btnAbort.setText(Localization.getString("Button_Abort"));
        btnTest.setText(Localization.getString("Button_Test"));
        btnFinish.setText(Localization.getString("Button_Done"));
        lblDisplayname.setText(Localization.getString("Account_DisplayName"));
    }

    /**
     * Initialisiert die GUI-Elemente des Frames
     */
    private void initFrame() {
        txtMail.setBounds(140, 58, 315, 20);
        txtMail.setColumns(10);

        lblMail.setBounds(10, 61, 120, 14);

        lblPassword.setBounds(37, 92, 93, 14);

        passwordField.setBounds(140, 89, 315, 20);

        JPanel groupBox = new JPanel();
        groupBox.setBounds(10, 120, 724, 137);

        lblInboxServer.setBounds(10, 37, 138, 14);

        cbInProtocoll.setBounds(158, 34, 57, 20);
        cbInProtocoll.setModel(new DefaultComboBoxModel<>(new String[]{"POP3", "IMAP"}));

        txtInServer.setBounds(226, 34, 162, 20);
        txtInServer.setColumns(10);

        spInPort.setBounds(394, 34, 54, 20);

        cBInConnectionSecurity.setBounds(454, 34, 127, 20);
        cBInConnectionSecurity
                .setModel(new DefaultComboBoxModel<>(ConnectionSecurity.values()));

        cBInAuthentificationType.setBounds(587, 34, 127, 20);
        cBInAuthentificationType.setModel(new DefaultComboBoxModel<>(AuthentificationType.values()));

        lblOutboxServer.setBounds(10, 65, 138, 14);

        txtOutServer.setBounds(226, 65, 162, 20);
        txtOutServer.setColumns(10);

        spOutPort.setBounds(394, 65, 54, 20);

        cBOutConnectionSecurity.setBounds(454, 65, 127, 20);
        cBOutConnectionSecurity
                .setModel(new DefaultComboBoxModel<>(ConnectionSecurity.values()));

        cBOutAuthentificationType.setBounds(587, 64, 127, 20);
        cBOutAuthentificationType
                .setModel(new DefaultComboBoxModel<>(AuthentificationType.values()));

        JLabel lblSmtp = new JLabel("SMTP");
        lblSmtp.setBounds(160, 65, 55, 14);

        lblUsername.setBounds(10, 106, 138, 14);

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

        lblServerAddress.setBounds(226, 11, 162, 14);
        groupBox.add(lblServerAddress);

        JLabel lblPort = new JLabel("Port:");
        lblPort.setBounds(394, 11, 54, 14);
        groupBox.add(lblPort);

        JLabel lblSsl = new JLabel("SSL:");
        lblSsl.setBounds(454, 11, 123, 14);
        groupBox.add(lblSsl);

        lblAuthentification.setBounds(587, 11, 127, 14);
        groupBox.add(lblAuthentification);

        btnAbort.setBounds(649, 288, 85, 23);
        btnAbort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mailAccount = null;
                close();
            }
        });
        getContentPane().add(btnAbort);

        btnTest.setBounds(459, 288, 85, 23);
        btnTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAccountObject();
            }
        });
        getContentPane().add(btnTest);

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

        lblDisplayname.setBounds(10, 14, 120, 14);
        getContentPane().add(lblDisplayname);

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
            throw new UnsupportedOperationException(Localization.getString("AccountFrame_UnknownProtocol"));
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
                Dialogs.showErrorDialog(this, Localization.getString("AccountFrame_InvalidData"));
            }
        } catch (IOException e) {
            LOGGER.error(Localization.getString("AccountFrame_CouldNotCreateAccount"), e);
            Dialogs.showErrorDialog(this, e.getLocalizedMessage());
        }
    }

    @Override
    protected MailAccount getDialogResult() {
        return mailAccount;
    }
}
