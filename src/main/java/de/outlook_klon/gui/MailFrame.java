package de.outlook_klon.gui;

import de.outlook_klon.dao.DAOException;
import de.outlook_klon.logik.User;
import de.outlook_klon.logik.User.MailChecker;
import de.outlook_klon.logik.kontakte.Contact;
import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.mailclient.MailContent;
import de.outlook_klon.logik.mailclient.MailInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.ParseException;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dieses Frame dient zum Erstellen und Anzeigen von E-Mails.
 *
 * @author Hendrik Karwanni
 */
public class MailFrame extends ExtendedFrame {

    private static final long serialVersionUID = 5976953616015664148L;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailFrame.class);

    /**
     * Interne Aufzählung, welche die verschiedenen Arten definiert, in welchem
     * Kontext das Frame geöffnet werden kann
     */
    private enum MailMode {
        /**
         * Es wird eine neue Mail geschieben
         */
        NEW,
        /**
         * Es wird eine existierende Mail geöffnet
         */
        OPEN,
        /**
         * Es wird auf eine existierende Mail geantwortet
         */
        ANSWER,
        /**
         * Es wird eine existierende Mail weitergeleitet
         */
        FORWARD
    }

    private MailMode mailMode;
    private MailInfo mailInfo;
    private String relPath;

    private JComboBox<MailAccount> cBSender;
    private JTextField tSender;

    private JTextField tTo;
    private JTextField tCC;
    private JTextField tSubject;
    private HtmlEditorPane tpMailtext;

    private JButton btnSend;
    private JButton btnAttachment;

    private JMenuItem mntmFileAttach;
    private JMenuItem mntmFileClose;

    private JRadioButtonMenuItem rdBtnMntmPlaintext;
    private JRadioButtonMenuItem rdBtnMntmHtml;

    private JList<File> lstAttachment;

    private String charset;

    /**
     * Initialisiert das Menü des Frames
     */
    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("Datei");
        menuBar.add(mnFile);

        JMenu mnAttach = new JMenu("Anh\u00E4ngen");
        mnAttach.setVisible(mailMode == MailMode.NEW);
        mnFile.add(mnAttach);

        mntmFileAttach = new JMenuItem("Datei anh\u00E4ngen");
        mntmFileAttach.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAttachment();
            }
        });
        mnAttach.add(mntmFileAttach);

        mntmFileClose = new JMenuItem("Schlie\u00DFen");
        mntmFileClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        mnFile.add(mntmFileClose);

        JMenu mnOptions = new JMenu("Optionen");
        menuBar.add(mnOptions);

        JMenu mnEmailFormat = new JMenu("E-Mail Format");
        mnOptions.add(mnEmailFormat);

        ItemListener radioMenu = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                JRadioButtonMenuItem sender = (JRadioButtonMenuItem) arg0.getSource();
                if (arg0.getStateChange() == ItemEvent.SELECTED) {
                    boolean editable = tpMailtext.isEditable();

                    String text = tpMailtext.getText();
                    String contentType = tpMailtext.getContentType();

                    if (sender == rdBtnMntmPlaintext) {
                        contentType = "TEXT/plain; " + charset;
                    } else if (sender == rdBtnMntmHtml) {
                        contentType = "TEXT/html; " + charset;
                    }

                    tpMailtext.setEditable(true);
                    tpMailtext.setText(text, contentType, false);
                    tpMailtext.setEditable(editable);
                    tpMailtext.setCaretPosition(0);
                }
            }
        };

        rdBtnMntmPlaintext = new JRadioButtonMenuItem("Reintext");
        rdBtnMntmPlaintext.setSelected(true);
        rdBtnMntmPlaintext.addItemListener(radioMenu);
        mnEmailFormat.add(rdBtnMntmPlaintext);

        rdBtnMntmHtml = new JRadioButtonMenuItem("Html");
        rdBtnMntmHtml.addItemListener(radioMenu);
        mnEmailFormat.add(rdBtnMntmHtml);

        ButtonGroup group = new ButtonGroup();
        group.add(rdBtnMntmPlaintext);
        group.add(rdBtnMntmHtml);
    }

    /**
     * Initialisiert die Liste für die Anhänge
     *
     * @param splitHead JSplitPane in die die Liste eingefügt werden soll
     */
    private void initList(JSplitPane splitHead) {
        lstAttachment = new JList<>(new DefaultListModel<File>());

        JScrollPane attachmentScroller = new JScrollPane(lstAttachment);
        splitHead.setRightComponent(attachmentScroller);

        if (mailMode == MailMode.OPEN) {
            lstAttachment.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        File selected = lstAttachment.getSelectedValue();
                        saveAttachment(selected.getName());
                    }
                }
            });
        }
    }

    /**
     * Initialisiert die Elemente der GUI
     */
    private void initGui() {
        JToolBar toolBar = new JToolBar();

        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

        JPanel panel = new JPanel();
        splitPane.setLeftComponent(panel);

        JSplitPane splitHead = new JSplitPane();

        final GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addComponent(splitHead,
                Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE));
        gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addComponent(splitHead,
                GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE));

        final JPanel panel_1 = new JPanel();
        splitHead.setLeftComponent(panel_1);

        JLabel lSender = new JLabel("Von:");
        JLabel lTo = new JLabel("An:");
        JLabel lCC = new JLabel("CC:");
        JLabel lSubject = new JLabel("Betreff:");

        tTo = new JTextField();
        tTo.setColumns(10);

        tCC = new JTextField();
        tCC.setColumns(10);

        tSubject = new JTextField();
        tSubject.setColumns(10);
        tSubject.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent arg0) {
                updateCaption();
            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                updateCaption();
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
                updateCaption();
            }
        });

        cBSender = new JComboBox<>();
        tSender = new JTextField();

        final GroupLayout gl_panel_1 = new GroupLayout(panel_1);
        gl_panel_1
                .setHorizontalGroup(
                        gl_panel_1.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_1.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
                                .addComponent(lSender, GroupLayout.PREFERRED_SIZE, 46,
                                        GroupLayout.PREFERRED_SIZE)
                                .addComponent(lTo, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lCC, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lSubject, GroupLayout.PREFERRED_SIZE, 46,
                                        GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
                                .addComponent(mailMode != MailMode.OPEN ? cBSender : tSender,
                                        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tTo, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 6,
                                        Short.MAX_VALUE)
                                .addComponent(tCC, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 6,
                                        Short.MAX_VALUE)
                                .addComponent(tSubject, GroupLayout.DEFAULT_SIZE, 6, Short.MAX_VALUE))
                        .addContainerGap()));
        gl_panel_1.setVerticalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_1
                .createSequentialGroup().addContainerGap()
                .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE).addComponent(lSender).addComponent(
                        mailMode != MailMode.OPEN ? cBSender : tSender, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE).addComponent(lTo).addComponent(tTo,
                        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(5)
                .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE).addComponent(lCC).addComponent(tCC,
                        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(3)
                .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE).addComponent(lSubject).addComponent(
                        tSubject, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap()));
        panel_1.setLayout(gl_panel_1);

        initList(splitHead);

        panel.setLayout(gl_panel);

        tpMailtext = new HtmlEditorPane();

        JScrollPane textScroller = new JScrollPane(tpMailtext);
        splitPane.setRightComponent(textScroller);

        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING,
                groupLayout.createSequentialGroup()
                .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addComponent(toolBar, GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                        .addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE))
                .addGap(0)));
        groupLayout
                .setVerticalGroup(
                        groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING,
                        groupLayout.createSequentialGroup()
                        .addComponent(toolBar, GroupLayout.PREFERRED_SIZE, 35,
                                GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)));

        btnSend = new JButton("Senden");
        toolBar.add(btnSend);
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                sendMail();
            }
        });

        btnAttachment = new JButton("Anhang");
        btnAttachment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAttachment();
            }
        });
        toolBar.add(btnAttachment);
        toolBar.setVisible(mailMode != MailMode.OPEN);

        getContentPane().setLayout(groupLayout);

        initMenu();
    }

    /**
     * Fügt das übergebene Adress-Array zu einem String mit ';'-Seperator
     * zusammen
     *
     * @param addresses Umzuwandelnde Adressen
     * @return String, der die Aufzählung der übergebenen Adressen enthält
     */
    private String appendAddresses(Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < addresses.length; i++) {
            InternetAddress inet = (InternetAddress) addresses[i];

            sb.append(inet.toUnicodeString());

            if (i < addresses.length - 1) {
                sb.append("; ");
            }
        }

        return sb.toString();
    }

    /**
     * Erstellt eine neue Instanz der Klasse zum Schreiben einer neuen Mail
     */
    public MailFrame() {
        mailMode = MailMode.NEW;
        charset = "";

        setTitle("<Kein Betreff>");
        initGui();

        addMailAccounts();
    }

    /**
     * Erstellt eine neue Instanz der Klasse zum Schreiben einer neuen Mail an
     * die übergebenen Kontakte.
     *
     * @param contacts Kontakte, deren Mailadressen standardmäßig als Empfänger
     * eingetragen werden.
     */
    public MailFrame(Contact[] contacts) {
        mailMode = MailMode.NEW;
        charset = "";

        setTitle("<Kein Betreff>");
        initGui();

        addMailAccounts();

        List<Address> addresses = new ArrayList<>();
        for (Contact contact : contacts) {
            if (contact.getAddress1() == null) {
                continue;
            }
            addresses.add(contact.getAddress1());
        }

        String addressString = appendAddresses(addresses.toArray(new Address[addresses.size()]));
        tTo.setText(addressString);
    }

    /**
     * Erstellt eine neue Instanz der Klasse zum Anzeigen einer Mail.
     *
     * @param mail Info-Objekt, das die Informationen zum Anzeigen der Mail
     * enthält.
     * @param path Ordnerpfad, in dem die Mail liegt.
     * @param parent MailAccount, aus dem die Mail stammt.
     * @throws MessagingException Tritt auf, wenn die Daten der Mail nicht
     * abgefragt werden konnten.
     * @throws de.outlook_klon.dao.DAOException Tritt auf, wenn die Daten der
     * Mail nicht geladen werden konnten.
     */
    public MailFrame(MailInfo mail, String path, MailAccount parent)
            throws MessagingException, DAOException {
        mailMode = MailMode.OPEN;

        initGui();

        mailInfo = mail;
        relPath = path;

        addMailAccount(parent);
        cBSender.setSelectedItem(parent);

        parent.loadMessageData(path, mail, EnumSet.allOf(MailContent.class));
        charset = mail.getContentType().split("; ")[1];

        tSender.setText(((InternetAddress) mail.getSender()).toUnicodeString());
        tSender.setEditable(false);

        tSubject.setText(mail.getSubject());
        tSubject.setEditable(false);

        tTo.setText(appendAddresses(mail.getTo()));
        tTo.setEditable(false);

        tCC.setText(appendAddresses(mail.getCc()));
        tCC.setEditable(false);

        String text = mailInfo.getText();
        String contentType = mailInfo.getContentType();

        // Automatisches Umstellen des Anzeigetyps
        if (HtmlEditorPane.istHtml(text)) {
            contentType = contentType.replace("plain", "html");
        }

        if (contentType.toLowerCase().startsWith("text/plain")) {
            rdBtnMntmPlaintext.setSelected(true);
            tpMailtext.setText(text);
        } else {
            tpMailtext.setText(text);
            rdBtnMntmHtml.setSelected(true);
        }

        tpMailtext.setEditable(false);

        DefaultListModel<File> model = (DefaultListModel<File>) lstAttachment.getModel();

        // Füge Anhänge in die JList ein
        String[] attachments = mail.getAttachment();
        for (String attachment : attachments) {
            model.addElement(new File(attachment));
        }
    }

    /**
     * Erstellt eine neue Instanz der Klasse zum Antworten oder Weiterleiten
     * einer Mail.
     *
     * @param mail Info-Objekt, das die Informationen zum Anzeigen der Mail
     * enthält.
     * @param path Ordnerpfad, in dem die Mail liegt.
     * @param parent MailAccount, aus dem die Mail stammt.
     * @param forward Wenn true soll die Mail weitergeleitet werden. Sonst wird
     * auf die Mail geantwortet.
     * @throws MessagingException Tritt auf, wenn die Daten der Mail nicht
     * abgefragt werden konnten.
     * @throws de.outlook_klon.dao.DAOException Tritt auf, wenn die Daten der
     * Mail nicht geladen werden konnten.
     */
    public MailFrame(MailInfo mail, String path, MailAccount parent, boolean forward)
            throws MessagingException, DAOException {
        mailMode = forward ? MailMode.FORWARD : MailMode.ANSWER;

        initGui();

        addMailAccounts();
        cBSender.setSelectedItem(parent);

        mailInfo = mail;
        relPath = path;

        parent.loadMessageData(path, mail, EnumSet.allOf(MailContent.class));
        charset = mail.getContentType().split("; ")[1];

        String subject = (forward ? "Fwd: " : "Re: ") + mail.getSubject();

        tSubject.setText(subject);

        if (forward == false) {
            tTo.setText(((InternetAddress) mail.getSender()).toUnicodeString());
        }
        tCC.setText(appendAddresses(mail.getCc()));

        String text = mailInfo.getText();
        String contentType = mailInfo.getContentType();

        if (HtmlEditorPane.istHtml(text)) {
            contentType = contentType.replace("plain", "html");
        }

        if (contentType.startsWith("TEXT/plain")) {
            rdBtnMntmPlaintext.setSelected(true);
            tpMailtext.setText(text);
        } else {
            tpMailtext.setText(text);
            rdBtnMntmHtml.setSelected(true);
        }
    }

    /**
     * Fügt alle registrierten MailAccounts der entsprechenden ComboBox hinzu
     */
    private void addMailAccounts() {
        for (MailChecker checker : User.getInstance()) {
            MailAccount acc = checker.getAccount();
            addMailAccount(acc);
        }
    }

    /**
     * Fügt den übergebenen MailAccount der entsprechenden ComboBox hinzu
     *
     * @param ac Hinzuzufügender MailAccount
     */
    private void addMailAccount(MailAccount ac) {
        DefaultComboBoxModel<MailAccount> model = (DefaultComboBoxModel<MailAccount>) cBSender.getModel();

        if (model.getIndexOf(ac) == -1) {
            cBSender.addItem(ac);
        }

        if (cBSender.getSelectedIndex() == -1) {
            cBSender.setSelectedIndex(0);
        }
    }

    /**
     * Gibt den übergebenen Adress-String als Array von
     * InternetAddress-Instanzen im Unicode-Format zurück.
     *
     * @param addresses Zu parsende Adressen.
     * @return Entsprechende Adressen des übergebenen Strings.
     * @throws ParseException Tritt auf, wenn die Adressen nicht geparst werden
     * konnten.
     */
    private static Address[] unicodifyAddresses(String addresses) throws ParseException {
        addresses = addresses.replace(';', ',');

        InternetAddress[] recips = InternetAddress.parse(addresses, true);

        for (int i = 0; i < recips.length; i++) {
            try {
                recips[i] = new InternetAddress(recips[i].getAddress(), recips[i].getPersonal(), "utf-8");
            } catch (UnsupportedEncodingException uee) {
                LOGGER.error("Could not parse internet address", uee);
            }
        }

        return recips;
    }

    /**
     * Sendet die Mail
     */
    private void sendMail() {
        String subject = tSubject.getText();
        String text = tpMailtext.getText();
        MailAccount acc = (MailAccount) cBSender.getSelectedItem();

        if (acc == null) {
            JOptionPane.showMessageDialog(this,
                    "Es wurde keine Mailadresse angegeben, über die die Mail gesendet werden soll", "Fehler",
                    JOptionPane.OK_OPTION);
            return;
        }

        Address[] to;
        Address[] cc;
        try {
            // Erstelle aus den entsprechenden String InternetAddress-Instanzen
            to = unicodifyAddresses(tTo.getText());
            cc = unicodifyAddresses(tCC.getText());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultListModel<File> model = (DefaultListModel<File>) lstAttachment.getModel();
        File[] attachments = new File[model.getSize()];
        for (int i = 0; i < attachments.length; i++) {
            attachments[i] = model.get(i);
        }

        try {
            // Eingentliches Sender der Mail
            acc.sendMail(to, cc, subject, text, tpMailtext.getContentType(), attachments);
            close();
        } catch (MessagingException ex) {
            JOptionPane.showMessageDialog(this,
                    "Es ist ein Fehler beim Senden der Mail aufgetreten:\n" + ex.getMessage(), "Fehler",
                    JOptionPane.OK_OPTION);
        }
    }

    /**
     * Fügt eine per JFileChooser ausgewählte Datei der JList hinzu
     */
    private void addAttachment() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            DefaultListModel<File> model = (DefaultListModel<File>) lstAttachment.getModel();

            for (File file : files) {
                if (file.exists()) {
                    model.addElement(file);
                }
            }
        }
    }

    /**
     * Speichert den Anhang mit dem übergebenen Namen in eine per JFileChooser
     * ausgewählte Datei
     *
     * @param name Name des zu speicherndern Anhangs
     */
    private void saveAttachment(String name) {
        JFileChooser fileChooser = new JFileChooser();
        // Setzt den standardmäßig ausgewählten Dateinamen
        fileChooser.setSelectedFile(new File(name));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            MailAccount acc = (MailAccount) cBSender.getSelectedItem();

            try {
                // Führt das eigentliche Abspeichern aus
                acc.saveAttachment(mailInfo, relPath, name, path);
            } catch (IOException | MessagingException ex) {
                JOptionPane.showMessageDialog(this,
                        "Es ist ein Fehler beim Speichern des Anhangs aufgetreten: \n" + ex.getMessage(), "Fehler",
                        JOptionPane.OK_OPTION);
            }
        }
    }

    /**
     * Aktualisiert den Titel des Frames in Abhängigkeit des Betreffs der Mail
     */
    private void updateCaption() {
        String text = tSubject.getText();

        if (text.trim().isEmpty()) {
            this.setTitle("<Kein Betreff>");
        } else {
            this.setTitle(text);
        }
    }
}
