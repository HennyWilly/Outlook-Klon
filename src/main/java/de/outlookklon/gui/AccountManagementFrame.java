package de.outlookklon.gui;

import de.outlookklon.Program;
import de.outlookklon.gui.helpers.Buttons;
import de.outlookklon.logik.User;
import de.outlookklon.logik.mailclient.InboxServer;
import de.outlookklon.logik.mailclient.MailAccount;
import de.outlookklon.logik.mailclient.OutboxServer;
import de.outlookklon.logik.mailclient.ServerSettings;
import de.outlookklon.logik.mailclient.checker.MailAccountChecker;
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

/**
 * In diesem Frame werden alle registrierten MailAccount-Instanzen verwaltet. Es
 * können neue Instanzen erstellt, bearbeitet und entfernt werden.
 *
 * @author Hendrik Karwanni
 */
public class AccountManagementFrame extends ExtendedDialog<MailAccount[]> {

    private static final long serialVersionUID = -5036893845172118794L;

    private MailAccount[] myAccounts;

    private final JButton btnNewAccount;
    private final JList<MailAccount> lstAccounts;
    private final JButton btnAbort;
    private final JButton btnOK;
    private final JButton btnRemoveAccount;

    private final JTextField txtUser;
    private final JTextField txtMail;
    private final JTextField txtName;

    private final JTextField txtInboxTyp;
    private final JTextField txtInboxServer;
    private final JTextField txtInboxPort;
    private final JTextField txtInboxConnectionSecurity;
    private final JTextField txtInboxAuthentification;

    private final JTextField txtOutboxTyp;
    private final JTextField txtOutboxServer;
    private final JTextField txtOutboxPort;
    private final JTextField txtOutboxConnectionSecurity;
    private final JTextField txtOutboxAuthentification;

    /**
     * Erstellt eine neue Instanz der Klasse zum Verwalten aller bestehenden
     * MailAccounts
     */
    public AccountManagementFrame() {
        super(711, 695);

        myAccounts = null;

        setTitle(Program.STRINGS.getString("AccountManagementFrame_Title"));
        getContentPane().setLayout(null);

        DefaultListModel<MailAccount> listModel = new DefaultListModel<>();
        for (MailAccountChecker checker : User.getInstance()) {
            MailAccount acc = checker.getAccount();
            listModel.addElement(acc);
        }

        lstAccounts = new JList<>(listModel);
        lstAccounts.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                MailAccount acc = (MailAccount) value;

                return super.getListCellRendererComponent(list, acc.getAddress().getAddress(), index, isSelected,
                        cellHasFocus);
            }
        });
        lstAccounts.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    MailAccount acc = lstAccounts.getSelectedValue();

                    btnRemoveAccount.setEnabled(acc != null);
                    refreshDetails(acc);
                }
            }
        });
        lstAccounts.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    MailAccount acc = lstAccounts.getSelectedValue();
                    MailAccount edit = editAccount(acc);

                    if (acc != edit) {
                        lstAccounts.setSelectedValue(edit, true);
                    }
                }
            }
        });
        lstAccounts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstAccounts.setBounds(10, 11, 239, 570);

        JScrollPane accountScroller = new JScrollPane(lstAccounts);
        accountScroller.setBounds(lstAccounts.getBounds());
        getContentPane().add(accountScroller);

        btnNewAccount = new JButton(Program.STRINGS.getString("AccountManagementFrame_NewAccount"));
        btnNewAccount.setBounds(10, 592, 106, 23);
        btnNewAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                createAccount();
            }
        });
        getContentPane().add(btnNewAccount);

        btnAbort = Buttons.getAbortButton();
        btnAbort.setBounds(583, 633, 112, 23);
        btnAbort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myAccounts = null;
                close();
            }
        });
        getContentPane().add(btnAbort);

        btnOK = Buttons.getOkButton();
        btnOK.setBounds(484, 633, 89, 23);
        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel<MailAccount> model = (DefaultListModel<MailAccount>) lstAccounts.getModel();
                myAccounts = new MailAccount[model.getSize()];
                for (int i = 0; i < myAccounts.length; i++) {
                    myAccounts[i] = model.get(i);
                }
                close();
            }
        });
        getContentPane().add(btnOK);

        btnRemoveAccount = new JButton(Program.STRINGS.getString("AccountManagementFrame_DeleteAccount"));
        btnRemoveAccount.setEnabled(false);
        btnRemoveAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel<MailAccount> model = (DefaultListModel<MailAccount>) lstAccounts.getModel();
                MailAccount acc = lstAccounts.getSelectedValue();

                deleteAccount(acc);
                if (!model.isEmpty() && model.indexOf(acc) == -1) {
                    lstAccounts.setSelectedIndex(0);
                }

            }
        });
        btnRemoveAccount.setBounds(123, 592, 126, 23);
        getContentPane().add(btnRemoveAccount);

        JLabel lblName = new JLabel(Program.STRINGS.getString("AccountManagementFrame_Name"));
        lblName.setBounds(259, 13, 99, 14);
        getContentPane().add(lblName);

        JLabel lblEmailaddress = new JLabel(Program.STRINGS.getString("Account_MailAddress"));
        lblEmailaddress.setBounds(259, 38, 99, 14);
        getContentPane().add(lblEmailaddress);

        JLabel lblInboxUsername = new JLabel(Program.STRINGS.getString("Account_Username"));
        lblInboxUsername.setBounds(259, 63, 99, 14);
        getContentPane().add(lblInboxUsername);

        JPanel panelInbox = new JPanel();
        panelInbox.setBounds(259, 88, 436, 126);
        panelInbox.setBorder(BorderFactory.createTitledBorder(Program.STRINGS.getString("Account_Inbox")));
        getContentPane().add(panelInbox);
        panelInbox.setLayout(null);

        JLabel lblInboxServer = new JLabel("Server: ");
        lblInboxServer.setBounds(10, 52, 46, 14);
        panelInbox.add(lblInboxServer);

        JLabel lblInboxPort = new JLabel("Port: ");
        lblInboxPort.setBounds(285, 52, 46, 14);
        panelInbox.add(lblInboxPort);

        JLabel lblInboxConnectionSecurity = new JLabel(Program.STRINGS.getString("Account_ConnectionSecurity"));
        lblInboxConnectionSecurity.setBounds(10, 77, 143, 14);
        panelInbox.add(lblInboxConnectionSecurity);

        JLabel lblInboxAuthentificationType = new JLabel(Program.STRINGS.getString("Account_Authentification"));
        lblInboxAuthentificationType.setBounds(10, 102, 143, 14);
        panelInbox.add(lblInboxAuthentificationType);

        JLabel lblServerType = new JLabel(Program.STRINGS.getString("Account_ServerType"));
        lblServerType.setBounds(10, 27, 67, 14);
        panelInbox.add(lblServerType);

        txtInboxTyp = new JTextField();
        txtInboxTyp.setBackground(Color.WHITE);
        txtInboxTyp.setEditable(false);
        txtInboxTyp.setBounds(75, 24, 351, 20);
        panelInbox.add(txtInboxTyp);
        txtInboxTyp.setColumns(10);

        txtInboxServer = new JTextField();
        txtInboxServer.setBackground(Color.WHITE);
        txtInboxServer.setEditable(false);
        txtInboxServer.setBounds(75, 49, 200, 20);
        panelInbox.add(txtInboxServer);
        txtInboxServer.setColumns(10);

        txtInboxPort = new JTextField();
        txtInboxPort.setBackground(Color.WHITE);
        txtInboxPort.setEditable(false);
        txtInboxPort.setBounds(314, 49, 112, 20);
        panelInbox.add(txtInboxPort);
        txtInboxPort.setColumns(10);

        txtInboxConnectionSecurity = new JTextField();
        txtInboxConnectionSecurity.setBackground(Color.WHITE);
        txtInboxConnectionSecurity.setEditable(false);
        txtInboxConnectionSecurity.setBounds(153, 74, 273, 20);
        panelInbox.add(txtInboxConnectionSecurity);
        txtInboxConnectionSecurity.setColumns(10);

        txtInboxAuthentification = new JTextField();
        txtInboxAuthentification.setBackground(Color.WHITE);
        txtInboxAuthentification.setEditable(false);
        txtInboxAuthentification.setColumns(10);
        txtInboxAuthentification.setBounds(153, 99, 273, 20);
        panelInbox.add(txtInboxAuthentification);

        JPanel panelOutbox = new JPanel();
        panelOutbox.setLayout(null);
        panelOutbox.setBorder(BorderFactory.createTitledBorder(Program.STRINGS.getString("Account_Outbox")));
        panelOutbox.setBounds(259, 225, 436, 126);
        getContentPane().add(panelOutbox);

        JLabel lblOutboxServer = new JLabel("Server: ");
        lblOutboxServer.setBounds(10, 48, 46, 14);
        panelOutbox.add(lblOutboxServer);

        JLabel lblOutboxPort = new JLabel("Port: ");
        lblOutboxPort.setBounds(285, 48, 46, 14);
        panelOutbox.add(lblOutboxPort);

        JLabel lblOutboxConnectionSecurity = new JLabel(Program.STRINGS.getString("Account_ConnectionSecurity"));
        lblOutboxConnectionSecurity.setBounds(10, 73, 143, 14);
        panelOutbox.add(lblOutboxConnectionSecurity);

        JLabel lblOutboxAuthentificationType = new JLabel(Program.STRINGS.getString("Account_Authentification"));
        lblOutboxAuthentificationType.setBounds(10, 98, 143, 14);
        panelOutbox.add(lblOutboxAuthentificationType);

        txtOutboxServer = new JTextField();
        txtOutboxServer.setBackground(Color.WHITE);
        txtOutboxServer.setEditable(false);
        txtOutboxServer.setColumns(10);
        txtOutboxServer.setBounds(75, 45, 200, 20);
        panelOutbox.add(txtOutboxServer);

        txtOutboxPort = new JTextField();
        txtOutboxPort.setBackground(Color.WHITE);
        txtOutboxPort.setEditable(false);
        txtOutboxPort.setColumns(10);
        txtOutboxPort.setBounds(314, 45, 112, 20);
        panelOutbox.add(txtOutboxPort);

        txtOutboxConnectionSecurity = new JTextField();
        txtOutboxConnectionSecurity.setBackground(Color.WHITE);
        txtOutboxConnectionSecurity.setEditable(false);
        txtOutboxConnectionSecurity.setColumns(10);
        txtOutboxConnectionSecurity.setBounds(153, 70, 273, 20);
        panelOutbox.add(txtOutboxConnectionSecurity);

        txtOutboxAuthentification = new JTextField();
        txtOutboxAuthentification.setBackground(Color.WHITE);
        txtOutboxAuthentification.setEditable(false);
        txtOutboxAuthentification.setColumns(10);
        txtOutboxAuthentification.setBounds(153, 95, 273, 20);
        panelOutbox.add(txtOutboxAuthentification);

        JLabel label = new JLabel(Program.STRINGS.getString("Account_ServerType"));
        label.setBounds(10, 23, 65, 14);
        panelOutbox.add(label);

        txtOutboxTyp = new JTextField();
        txtOutboxTyp.setBackground(Color.WHITE);
        txtOutboxTyp.setEditable(false);
        txtOutboxTyp.setColumns(10);
        txtOutboxTyp.setBounds(75, 20, 351, 20);
        panelOutbox.add(txtOutboxTyp);

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
     * @param acc MailAccount-Instanz aus der die Daten ausgelesen werden sollen
     */
    private void refreshDetails(MailAccount acc) {
        if (acc == null) {
            txtUser.setText(null);
            txtMail.setText(null);
            txtName.setText(null);
            txtInboxTyp.setText(null);
            txtInboxServer.setText(null);
            txtInboxPort.setText(null);
            txtInboxConnectionSecurity.setText(null);
            txtInboxAuthentification.setText(null);
            txtOutboxTyp.setText(null);
            txtOutboxServer.setText(null);
            txtOutboxPort.setText(null);
            txtOutboxConnectionSecurity.setText(null);
            txtOutboxAuthentification.setText(null);
        } else {
            txtUser.setText(acc.getUser());
            txtMail.setText(acc.getAddress().getAddress());
            txtName.setText(acc.getAddress().getPersonal());

            InboxServer inboxServer = acc.getInboxMailServer();
            ServerSettings inboxSettings = inboxServer.getSettings();

            txtInboxTyp.setText(inboxServer.getServerType());
            txtInboxServer.setText(inboxSettings.getHost());
            txtInboxPort.setText(Integer.toString(inboxSettings.getPort()));
            txtInboxConnectionSecurity.setText(inboxSettings.getConnectionSecurity().toString());
            txtInboxAuthentification.setText(inboxSettings.getAuthentificationType().toString());

            OutboxServer outboxServer = acc.getOutboxMailServer();
            ServerSettings outboxSettings = outboxServer.getSettings();

            txtOutboxTyp.setText(outboxServer.getServerType());
            txtOutboxServer.setText(outboxSettings.getHost());
            txtOutboxPort.setText(Integer.toString(outboxSettings.getPort()));
            txtOutboxConnectionSecurity.setText(outboxSettings.getConnectionSecurity().toString());
            txtOutboxAuthentification.setText(outboxSettings.getAuthentificationType().toString());
        }
    }

    /**
     * Öffnet ein neues Konto-Frame zum Erstellen einer neuen
     * MailAccount-Instanz
     */
    private void createAccount() {
        AccountFrame accountFrame = new AccountFrame();
        MailAccount acc = accountFrame.showDialog();
        if (acc != null) {
            DefaultListModel<MailAccount> model = (DefaultListModel<MailAccount>) lstAccounts.getModel();
            // Füge die neue Instanz der JList hinzu
            model.addElement(acc);
        }
    }

    /**
     * Öffnet ein neues Konto-Frame zum Bearbeiten einer bestehenden
     * MailAccount-Instanz
     *
     * @param acc Zu bearbeitender MailAccount
     * @return Instanz des veränderten MailAccounts
     */
    private MailAccount editAccount(MailAccount acc) {
        AccountFrame accountFrame = new AccountFrame(acc);

        MailAccount result = accountFrame.showDialog();
        if (result != null) {
            DefaultListModel<MailAccount> model = (DefaultListModel<MailAccount>) lstAccounts.getModel();
            int index = model.indexOf(acc);

            // Entfernt die alte Instanz aus der JList
            model.remove(index);
            // Füge die neue Instanz der JList hinzu
            model.add(index, result);

            acc = result;
        }

        return acc;
    }

    /**
     * Löscht die übergebene MailAccount-Instanz aus der Liste
     *
     * @param acc Zu löschender MailAccount
     */
    private void deleteAccount(MailAccount acc) {
        if (acc != null) {
            String message = String.format(
                    Program.STRINGS.getString("AccountManagementFrame_DeletionMessageFormat"),
                    acc.getAddress().getAddress());

            int result = JOptionPane.showConfirmDialog(this, message,
                    Program.STRINGS.getString("AccountManagementFrame_DeletionConfirmation"),
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                DefaultListModel<MailAccount> model = (DefaultListModel<MailAccount>) lstAccounts.getModel();

                // Entfernt die Instanz aus der JList
                model.removeElement(acc);
            }
        }
    }

    @Override
    protected MailAccount[] getDialogResult() {
        return myAccounts;
    }
}
