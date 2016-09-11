package de.outlookklon.gui.frames;

import de.outlookklon.gui.dialogs.AccountManagementFrame;
import de.outlookklon.dao.DAOException;
import de.outlookklon.gui.components.HtmlEditorPane;
import de.outlookklon.gui.components.ReadOnlyTableModel;
import de.outlookklon.gui.components.Statusbar;
import de.outlookklon.gui.components.TaggedJRadioButtonMenuItem;
import de.outlookklon.gui.helpers.Dialogs;
import de.outlookklon.gui.helpers.Events;
import de.outlookklon.gui.helpers.StatusbarProvider;
import de.outlookklon.localization.Localization;
import de.outlookklon.logik.User;
import de.outlookklon.logik.UserException;
import de.outlookklon.logik.contacts.Contact;
import de.outlookklon.logik.mailclient.FolderInfo;
import de.outlookklon.logik.mailclient.MailAccount;
import de.outlookklon.logik.mailclient.MailContent;
import de.outlookklon.logik.mailclient.StoredMailInfo;
import de.outlookklon.logik.mailclient.checker.MailAccountChecker;
import de.outlookklon.logik.mailclient.checker.NewMailEvent;
import de.outlookklon.logik.mailclient.checker.NewMailListener;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.mail.FolderNotFoundException;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Diese Klasse stellt das Hauptfenster der Anwendung dar. Hier werden die
 * Ordnerstrukturen der Mailkonten und deren Mails angezeigt. Von hier aus
 * können alle anderen Fenster der Anwendung aufgerufen werden.
 *
 * @author Hendrik Karwanni
 */
public class MainFrame extends ExtendedFrame {

    private static final long serialVersionUID = 817918826034684858L;

    private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);

    private static final String MAIL_TABLE_REF_COLUMN_NAME = "MailInfo";
    private static final int MAIL_TABLE_COLUMN_INDEX_SUBJECT = 0;
    private static final int MAIL_TABLE_COLUMN_INDEX_FROM = 1;
    private static final int MAIL_TABLE_COLUMN_INDEX_DATE = 2;

    private JPopupMenu tablePopup;
    private final JMenuItem popupOpen;
    private final JMenuItem popupDelete;
    private final JMenuItem popupAnswer;
    private final JMenuItem popupForward;

    private JMenu popupCopy;
    private JMenu popupMove;

    private final JTable tblMails;
    private final JMenu mnFile;
    private final JMenu mnNewMenu;
    private final JMenuItem mntmEmail;
    private final JMenuItem mntmContact;
    private final JMenuItem mntmAppointment;
    private final JMenuItem mntmClose;
    private final JMenu mnExtras;
    private final JMenuItem mntmAddressBook;
    private final JMenuItem mntmCalendar;
    private final JMenu mnLanguages;
    private final JMenuItem mntmAccountSettings;

    private final JButton btnPoll;
    private final JTree tree;
    private final HtmlEditorPane tpPreview;

    private final Statusbar statusbar;

    private User user;

    private boolean load;

    /**
     * Erstellt eine neue Instanz des Hauptfensters
     */
    public MainFrame() throws UserException {
        user = User.getInstance();

        btnPoll = new JButton();
        mnFile = new JMenu();
        mnNewMenu = new JMenu();
        mntmEmail = new JMenuItem();
        mntmContact = new JMenuItem();
        mntmAppointment = new JMenuItem();
        mntmClose = new JMenuItem();
        mnExtras = new JMenu();
        mntmAccountSettings = new JMenuItem();
        mntmAddressBook = new JMenuItem();
        mntmCalendar = new JMenuItem();
        mnLanguages = new JMenu();

        popupOpen = new JMenuItem();
        popupDelete = new JMenuItem();
        popupAnswer = new JMenuItem();
        popupForward = new JMenuItem();
        popupCopy = new JMenu();
        popupMove = new JMenu();

        tpPreview = new HtmlEditorPane();
        statusbar = new Statusbar();

        tblMails = new JTable();

        tree = new JTree() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isEditable() {
                return false;
            }
        };

        initGui();
        updateTexts();
        StatusbarProvider.addStatusbar(statusbar);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    user.save();
                } catch (IOException e) {
                    LOGGER.error(Localization.getString("MainFrame_SaveSettingsError"), e);

                    Component component = windowEvent.getComponent();
                    Dialogs.showErrorDialog(component, Localization.getString("MainFrame_SaveSettingsError"));
                }
            }
        });

        load = false;
    }

    @Override
    public void updateTexts() {
        setTitle(Localization.getString("MainFrame_Title"));

        btnPoll.setText(Localization.getString("MainFrame_Receive"));
        mnFile.setText(Localization.getString("Menu_File"));
        mnNewMenu.setText(Localization.getString("Menu_New"));
        mntmEmail.setText(Localization.getString("MainFrame_EMail"));
        mntmContact.setText(Localization.getString("Contact"));
        mntmAppointment.setText(Localization.getString("Appointment"));
        mntmClose.setText(Localization.getString("Menu_Close"));
        mnExtras.setText(Localization.getString("Menu_Extras"));
        mntmAccountSettings.setText(Localization.getString("AccountManagementFrame_Title"));
        mntmAddressBook.setText(Localization.getString("AddressBookFrame_Title"));
        mntmCalendar.setText(Localization.getString("Calendar"));
        mnLanguages.setText(Localization.getString("Languages"));

        popupOpen.setText(Localization.getString("Menu_Open"));
        popupDelete.setText(Localization.getString("Menu_Delete"));
        popupAnswer.setText(Localization.getString("Answer"));
        popupForward.setText(Localization.getString("Forward"));
        popupCopy.setText(Localization.getString("Menu_Copy"));
        popupMove.setText(Localization.getString("Menu_Move"));

        TableColumnModel columnModel = tblMails.getColumnModel();
        columnModel.getColumn(MAIL_TABLE_COLUMN_INDEX_SUBJECT).setHeaderValue(Localization.getString("MailFrame_Subject"));
        columnModel.getColumn(MAIL_TABLE_COLUMN_INDEX_FROM).setHeaderValue(Localization.getString("MailFrame_From"));
        columnModel.getColumn(MAIL_TABLE_COLUMN_INDEX_DATE).setHeaderValue(Localization.getString("Appointment_Date"));

        updateLanguageMenu();

        StatusbarProvider.setText("Language updated");

        repaint();
    }

    private void updateLanguageMenu() {
        mnLanguages.removeAll();

        Locale currentLocale = Localization.getLocale();
        for (Locale locale : Localization.getLocalizedLocales()) {
            TaggedJRadioButtonMenuItem languageMenuItem = new TaggedJRadioButtonMenuItem(locale.getDisplayLanguage(), locale.equals(currentLocale));
            languageMenuItem.setTag(locale);
            languageMenuItem.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    TaggedJRadioButtonMenuItem sender = (TaggedJRadioButtonMenuItem) e.getSource();

                    if (sender.isSelected()) {
                        Locale locale = (Locale) sender.getTag();
                        Localization.setLocale(locale);
                    }
                }
            });
            mnLanguages.add(languageMenuItem);
        }
    }

    private TableModel getTableModel() {
        return new ReadOnlyTableModel(
                new Object[][]{},
                new String[]{
                    MAIL_TABLE_REF_COLUMN_NAME,
                    StringUtils.EMPTY,
                    StringUtils.EMPTY,
                    StringUtils.EMPTY
                }
        ) {
            private static final long serialVersionUID = 1L;
            Class<?>[] columnTypes = new Class<?>[]{StoredMailInfo.class, String.class, InternetAddress.class,
                Date.class};

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        };
    }

    private void initGui() {
        JSplitPane horizontalSplitPane = new JSplitPane();

        initTablePopup();
        initTree(horizontalSplitPane);

        JSplitPane verticalSplitPane = new JSplitPane();
        verticalSplitPane.setContinuousLayout(true);
        verticalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        horizontalSplitPane.setRightComponent(verticalSplitPane);

        initTable(verticalSplitPane);

        tpPreview.setEditable(false);

        JScrollPane previewScroller = new JScrollPane(tpPreview);
        verticalSplitPane.setRightComponent(previewScroller);

        JToolBar toolBar = new JToolBar();

        btnPoll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel tableModel = (DefaultTableModel) tblMails.getModel();
                tableModel.setRowCount(0);

                DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

                int childs = root.getChildCount();
                for (int i = 0; i < childs; i++) {
                    DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) root.getChildAt(0);

                    treeModel.removeNodeFromParent(childNode);
                }

                tpPreview.setText(null);

                user.stopChecker();
                loadFolder();
            }
        });
        toolBar.add(btnPoll);
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(toolBar, GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
                .addComponent(horizontalSplitPane, GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
                .addComponent(statusbar, GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(toolBar, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(horizontalSplitPane, GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(statusbar, GroupLayout.DEFAULT_SIZE, 16, 16)
                )
        );

        getContentPane().setLayout(groupLayout);

        initMenu();
        initFrameIcons();
    }

    /**
     * Initialisiert das Icon des Frames. Es werdern Icons in verschiedenen
     * Größen geladen, die an verschiedenen Stellen angezeigt werden.
     *
     * @see
     * <a href="https://www.iconfinder.com/icons/34317/email_envelope_mail_icon">iconfinder.com</a>
     */
    private void initFrameIcons() {
        try {
            List<Image> icons = new ArrayList<>();
            icons.add(ImageIO.read(getClass().getResource("mainFrameIcon_16x16.png")));
            icons.add(ImageIO.read(getClass().getResource("mainFrameIcon_24x24.png")));
            icons.add(ImageIO.read(getClass().getResource("mainFrameIcon_32x32.png")));
            icons.add(ImageIO.read(getClass().getResource("mainFrameIcon_64x64.png")));
            icons.add(ImageIO.read(getClass().getResource("mainFrameIcon_128x128.png")));
            setIconImages(icons);
        } catch (IOException ex) {
            // TODO Localize error msg
            LOGGER.error("Could not load icons", ex);
        }
    }

    /**
     * Initialisiert das Menü des Frames
     */
    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(mnFile);
        mnFile.add(mnNewMenu);

        mntmEmail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.newMail();
            }
        });
        mnNewMenu.add(mntmEmail);

        mntmContact.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddressBookFrame(true);
            }
        });
        mnNewMenu.add(mntmContact);

        mntmAppointment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCalendarFrame(true);
            }
        });
        mnNewMenu.add(mntmAppointment);

        mntmClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        mnFile.add(mntmClose);

        menuBar.add(mnExtras);

        mntmAccountSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAccountManagementFrame();
            }
        });

        mntmAddressBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddressBookFrame(false);
            }
        });
        mnExtras.add(mntmAddressBook);

        mntmCalendar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCalendarFrame(false);
            }
        });
        mnExtras.add(mntmCalendar);

        mnExtras.add(new JSeparator());
        mnExtras.add(mnLanguages);
        mnExtras.add(new JSeparator());
        mnExtras.add(mntmAccountSettings);
    }

    /**
     * Initialisiert das Popup-Menü der Mailtabelle
     */
    private void initTablePopup() {
        popupOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) tblMails.getModel();

                int viewRow = tblMails.getSelectedRow();
                if (viewRow < 0) {
                    return;
                }

                int row = tblMails.convertRowIndexToModel(viewRow);
                StoredMailInfo mailID = (StoredMailInfo) model.getValueAt(row, 0);

                openMail(mailID);
            }
        });

        popupDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMail();
            }
        });

        popupAnswer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) tblMails.getModel();

                int viewZeile = tblMails.getSelectedRow();
                if (viewZeile < 0) {
                    return;
                }

                int row = tblMails.convertRowIndexToModel(viewZeile);
                StoredMailInfo mailID = (StoredMailInfo) model.getValueAt(row, 0);

                answer(mailID);
            }
        });

        popupForward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) tblMails.getModel();

                int viewZeile = tblMails.getSelectedRow();
                if (viewZeile < 0) {
                    return;
                }

                int row = tblMails.convertRowIndexToModel(viewZeile);
                StoredMailInfo mailID = (StoredMailInfo) model.getValueAt(row, 0);

                forward(mailID);
            }
        });

        tablePopup = new JPopupMenu();
        tablePopup.add(popupOpen);
        tablePopup.add(popupDelete);
        tablePopup.add(popupAnswer);
        tablePopup.add(popupForward);
        tablePopup.add(popupCopy);
        tablePopup.add(popupMove);
    }

    /**
     * Initialisiert die Mailtabelle
     *
     * @param verticalSplitPane JSplitPane, in die die Tabelle eingefügt werden
     * soll
     */
    private void initTable(JSplitPane verticalSplitPane) {
        tblMails.setModel(getTableModel());

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 6837957351164997131L;

            @Override
            public Component getTableCellRendererComponent(final JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Object cellValue;
                if (value instanceof Date) {
                    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM,
                            Localization.getLocale());
                    cellValue = dateFormat.format(value);
                } else if (value instanceof InternetAddress) {
                    InternetAddress data = (InternetAddress) value;
                    String personal = data.getPersonal();
                    String address = data.getAddress();

                    if (!StringUtils.isBlank(personal)) {
                        cellValue = personal;
                    } else {
                        cellValue = address;
                    }
                } else {
                    cellValue = value;
                }

                Component comp = super.getTableCellRendererComponent(table, cellValue, isSelected, hasFocus, row, column);

                int modelRow = table.convertRowIndexToModel(row);
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                Object obj = model.getValueAt(modelRow, 0);
                if (obj instanceof StoredMailInfo) {
                    StoredMailInfo info = (StoredMailInfo) obj;
                    if (isSelected || info.isRead()) {
                        comp.setFont(comp.getFont().deriveFont(Font.PLAIN));
                    } else {
                        comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                    }
                }

                return comp;
            }
        };

        tblMails.setDefaultRenderer(String.class, cellRenderer);
        tblMails.setDefaultRenderer(Date.class, cellRenderer);
        tblMails.setDefaultRenderer(InternetAddress.class, cellRenderer);

        TableRowSorter<TableModel> myRowSorter = new TableRowSorter<>(tblMails.getModel());
        myRowSorter.setSortsOnUpdates(true);
        tblMails.setRowSorter(myRowSorter);

        tblMails.removeColumn(tblMails.getColumn(MAIL_TABLE_REF_COLUMN_NAME));
        tblMails.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    DefaultTableModel model = (DefaultTableModel) tblMails.getModel();
                    int viewRow = tblMails.getSelectedRow();

                    StoredMailInfo info = null;

                    if (viewRow >= 0) {
                        int row = tblMails.convertRowIndexToModel(viewRow);
                        info = (StoredMailInfo) model.getValueAt(row, 0);
                    }

                    showPreview(info);
                }
            }
        });

        tblMails.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Events.isDoubleClick(e)) {
                    DefaultTableModel model = (DefaultTableModel) tblMails.getModel();

                    int viewRow = tblMails.getSelectedRow();
                    if (viewRow < 0) {
                        return;
                    }

                    int row = tblMails.convertRowIndexToModel(viewRow);
                    StoredMailInfo mailID = (StoredMailInfo) model.getValueAt(row, 0);

                    openMail(mailID);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                openPopupTable(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                openPopupTable(e);
            }
        });

        JScrollPane mailScroller = new JScrollPane(tblMails);
        verticalSplitPane.setLeftComponent(mailScroller);
    }

    /**
     * Initialisiert den Ordnerbaum
     *
     * @param splitPane JSplitPane, in die der Baum eingefügt werden soll
     */
    private void initTree(JSplitPane splitPane) {
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            private static final long serialVersionUID = 3057355870823054419L;

            private final Icon mailIcon = new ImageIcon(getClass().getResource("mail.png"));
            private final Icon openFolderIcon = UIManager.getIcon("Tree.openIcon");
            private final Icon closedFolderIcon = UIManager.getIcon("Tree.closedIcon");

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                    boolean isLeaf, int row, boolean focused) {

                String label = value.toString();
                if (value instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                    if (userObject instanceof FolderInfo) {
                        FolderInfo folder = (FolderInfo) userObject;
                        if (folder.getNumberUnread() > 0) {
                            label = String.format("<html><b>%s (%d)</b></html>", folder.getName(),
                                    folder.getNumberUnread());
                        }

                    }
                }

                Component c = super.getTreeCellRendererComponent(tree, label, selected, expanded, isLeaf, row, focused);

                if (value instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                    if (userObject instanceof MailAccountChecker) {
                        setIcon(mailIcon);
                    } else if (userObject instanceof FolderInfo) {
                        if (expanded) {
                            setIcon(openFolderIcon);
                        } else {
                            setIcon(closedFolderIcon);
                        }
                    }
                }

                return c;
            }
        });
        tree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent arg0) {
                // Not needed
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent e) {
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                TreePath nodePath = new TreePath(model.getPathToRoot(node));
                TreePath path = e.getPath();

                if (path.isDescendant(nodePath)) {
                    tree.setSelectionPath(path);
                }
            }
        });

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("[root]");

        tree.setRootVisible(false);
        tree.setEditable(true);
        tree.setModel(new DefaultTreeModel(root));
        tree.expandPath(new TreePath(root.getPath()));
        loadFolder();
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode == null) {
                    return;
                }

                Object userObject = selectedNode.getUserObject();

                MailAccountChecker checker;
                if (!(userObject instanceof MailAccountChecker)) {
                    checker = selectedChecker();
                    MailAccount account = checker.getAccount();

                    String folderName = selectedNode.toString();

                    setTitle(folderName + " - " + account.getAddress().getAddress());
                } else {
                    checker = (MailAccountChecker) userObject;

                    setTitle(checker.getAccount().getAddress().getAddress());
                }

                loadMails(checker, selectedNode);
            }
        });

        JScrollPane treeScroller = new JScrollPane(tree);
        splitPane.setLeftComponent(treeScroller);
    }

    /**
     * Öffnet ein MailFrame zum Antworten auf die übergebene StoredMailInfo
     *
     * @param info Info-Objekt der Mail, auf die geantwortet werden soll
     */
    private void answer(StoredMailInfo info) {
        MailAccount acc = selectedAccount();
        FolderInfo path = nodeToFolder((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());

        MailFrame mf;
        try {
            mf = new MailFrame(info, path.getPath(), acc, false);

            mf.setSize(this.getSize());
            mf.setExtendedState(this.getExtendedState());
            mf.setVisible(true);
        } catch (MessagingException | DAOException e) {
            LOGGER.error(Localization.getString("MainFrame_CouldNotAnswerMail"), e);

            Dialogs.showErrorDialog(this, Localization.getString("MainFrame_CouldNotAnswerMail") + ": \n" + e.getMessage());
        }
    }

    /**
     * Öffnet ein MailFrame zum Weiterleiten der übergebenen Mail
     *
     * @param info Info-Objekt der Mail, die weitergeleitet werden soll
     */
    private void forward(StoredMailInfo info) {
        MailAccount acc = selectedAccount();
        FolderInfo path = nodeToFolder((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());

        MailFrame mf;
        try {
            mf = new MailFrame(info, path.getPath(), acc, true);

            mf.setSize(this.getSize());
            mf.setExtendedState(this.getExtendedState());
            mf.setVisible(true);
        } catch (MessagingException | DAOException e) {
            LOGGER.error(Localization.getString("MainFrame_CouldNotForwarMail"), e);
            Dialogs.showErrorDialog(this, Localization.getString("MainFrame_CouldNotForwarMail") + ": \n" + e.getMessage());
        }
    }

    /**
     * Öffnet ein MailFrame zum Schreiben einer neuen Mail
     */
    private void newMail() {
        if (user.getAccountCount() == 0) {
            noMailAccount();
            return;
        }

        MailFrame mf = new MailFrame();

        mf.setSize(this.getSize());
        mf.setExtendedState(this.getExtendedState());
        mf.setVisible(true);
    }

    /**
     * Öffnet ein MailFrame zum Schreiben einer neuen Mail an die übergebenen
     * Kontakte
     *
     * @param contacts Kontakte, für die die Mail verfasst werden soll
     */
    public void newMail(Contact[] contacts) {
        if (user.getAccountCount() == 0) {
            noMailAccount();
            return;
        }

        MailFrame mf = new MailFrame(contacts);

        mf.setSize(this.getSize());
        mf.setExtendedState(this.getExtendedState());
        mf.setVisible(true);
    }

    /**
     * Öffnet den Kalender zum Verwanten der Termine
     *
     * @param newAppointment Wenn true, wird vor dem Öffnen ein neues
     * TerminFrame geöffnet. Wenn false dann nicht.
     */
    private void openCalendarFrame(boolean newAppointment) {
        AppointmentCalendarFrame tkf = new AppointmentCalendarFrame(newAppointment);

        tkf.setSize(this.getSize());
        tkf.setExtendedState(this.getExtendedState());
        tkf.setVisible(true);
    }

    /**
     * Öffnet das Adressbuch zum Verwanten der Kontakte
     *
     * @param newContact Wenn true, wird vor dem Öffnen ein neues KontaktFrame
     * geöffnet. Wenn false dann nicht.
     */
    private void openAddressBookFrame(boolean newContact) {
        AddressBookFrame af = new AddressBookFrame(this, user.getContacts(), newContact);

        af.setSize(this.getSize());
        af.setExtendedState(this.getExtendedState());
        af.setVisible(true);
    }

    /**
     * Öffnet die Kontenverwaltung zum Erstellen, Entfernen und Ändern von
     * MailAccount-Instanzen
     */
    private void openAccountManagementFrame() {
        AccountManagementFrame accountManagementFrame = new AccountManagementFrame(this);

        MailAccount[] accounts = accountManagementFrame.showDialog();
        if (accounts != null) {
            List<MailAccount> deleteable = removeDeletedAccountsFromTree(accounts);

            // Flag, die angibt, ob der Baum neugezeichnet werden soll
            boolean refresh = !deleteable.isEmpty();

            // Füge neue MailAccounts dem Baum hinzu
            refresh |= addNewAccountsToTree(accounts, deleteable);

            for (MailAccount acc : deleteable) {
                try {
                    user.removeMailAccount(acc);
                } catch (IOException e) {
                    LOGGER.error(Localization.getString("MainFrame_CouldNotDeleteAccountSettings"), e);
                    Dialogs.showErrorDialog(this, Localization.getString("MainFrame_CouldNotDeleteAccountSettings"));
                }
            }

            if (refresh) {
                loadFolder();
            }
        }
    }

    private List<MailAccount> removeDeletedAccountsFromTree(MailAccount[] accounts) {
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();

        List<MailAccount> deleteable = new ArrayList<>();

        // Entfernt nicht mehr verwendete Knoten für MailAccounts aus dem
        // Baum
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode.getChildAt(i);
            MailAccountChecker treeChecker = (MailAccountChecker) node.getUserObject();
            MailAccount treeAccount = treeChecker.getAccount();

            if (ArrayUtils.contains(accounts, treeAccount)) {
                continue;
            }

            deleteable.add(treeAccount);
            rootNode.remove(node);
        }

        if (!deleteable.isEmpty()) {
            treeModel.reload();
        }

        return deleteable;
    }

    private boolean addNewAccountsToTree(MailAccount[] accounts, List<MailAccount> deleteable) {
        boolean refresh = false;

        // Füge neue MailAccounts dem Baum hinzu
        for (MailAccount acc : accounts) {
            try {
                user.setMailInfoDAO(acc);
            } catch (IOException ex) {
                LOGGER.warn("Could not create directory for MailInfo objects.", ex);
            }

            Iterator<MailAccount> iterator = deleteable.iterator();
            while (iterator.hasNext()) {
                MailAccount acc2 = iterator.next();
                if (acc2.getAddress().equals(acc.getAddress())) {
                    deleteable.remove(acc2);
                }
            }

            if (user.addMailAccount(acc)) {
                // MailChecker accChecker = benutzer.getCheckerOf(acc);
                // accChecker.addNewMessageListener(getMailListener());
                refresh = true;
            }
        }

        return refresh;
    }

    /**
     * Sortiert die Tabelle nach der Referenz-Spalte
     */
    private void sortTable() {
        TableRowSorter<?> sorter = (TableRowSorter<?>) tblMails.getRowSorter();

        List<RowSorter.SortKey> keys = new ArrayList<>();
        RowSorter.SortKey key = new RowSorter.SortKey(0, SortOrder.ASCENDING);
        keys.add(key);
        sorter.setSortKeys(keys);
        sorter.sort();
    }

    /**
     * Extrahiert das FolderInfo-Objekt aus dem übergebenen Knoten
     *
     * @param node Knoten, aus dem das FolderInfo-Objekt ausgelesen werden soll
     * @return FolderInfo-Instanz, wenn der Knoten aus UserObject eine
     * FolderInfo enthält, sonst null
     */
    private FolderInfo nodeToFolder(DefaultMutableTreeNode node) {
        FolderInfo ret = null;

        Object userObject = node.getUserObject();
        if (userObject instanceof FolderInfo) {
            ret = (FolderInfo) userObject;
        }

        return ret;
    }

    /**
     * Erstellt rekursiv aus dem Pfad des übergebenen FolderInfo-Objekts die
     * Struktur der Baumknoten.
     *
     * @param folder FolderInfo-Objekt zu dem die Baumstruktur erstellt werden
     * soll
     * @param parent Vaterknoten, in dem die Baumstruktur erstellt werden soll
     * @param depth Tiefe des Pfads des Ordners, die sich aus dem Trennen des
     * Pfads nach dem '/'-Zeichen ergibt
     */
    private void folderToNode(FolderInfo folder, DefaultMutableTreeNode parent, int depth) {
        String path = folder.getPath();
        String[] parts = folder.getPath().split("/");

        if (path.contains("/") && depth < parts.length - 1) {
            DefaultMutableTreeNode pathNode = findNode(folder, parent, parts[depth]);
            folderToNode(folder, pathNode, depth + 1);
        } else {
            // Fügt dem Vaterknoten den neuen Knoten mit dem Ordner-Objekt ein
            parent.add(new DefaultMutableTreeNode(folder));
        }
    }

    private DefaultMutableTreeNode findNode(FolderInfo folder, DefaultMutableTreeNode parent, String pathParent) {
        // Sucht, ob bereits ein Knoten im Vaterknoten mit dem Pfad für
        // tiefe-1 existiert
        for (int j = 0; j < parent.getChildCount(); j++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(j);
            Object childObject = child.getUserObject();

            if (childObject instanceof FolderInfo) {
                FolderInfo childOrdner = (FolderInfo) childObject;
                if (childOrdner.getName().equals(pathParent)) {
                    return child;
                }
            }
        }

        return new DefaultMutableTreeNode(folder);
    }

    /**
     * Lädt alle Ordner der MailAccounts in die neue Baumstruktur
     */
    private void loadFolder() {
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();

        int i = 0;
        for (MailAccountChecker checker : user) {
            if (treeContainsChecker(rootNode, checker)) {
                continue;
            }

            DefaultMutableTreeNode accNode = new DefaultMutableTreeNode(checker);
            checker.addNewMessageListener(new MailListener());
            checker.start();

            // Lade die Ordnerstruktur aus dem Account
            FolderInfo[] folders;
            try {
                folders = checker.getAccount().getFolderStructure();
            } catch (MessagingException ex) {
                LOGGER.error(Localization.getString("MainFrame_CouldNotGetFolders"), ex);
                folders = new FolderInfo[0];
            }

            // Erstelle für jeden Ordner die passende Baumstruktur
            for (FolderInfo folder : folders) {
                folderToNode(folder, accNode, 0);
            }

            // Füge den neuen Knoten in den Baum ein
            treeModel.insertNodeInto(accNode, rootNode, i);
            i++;
        }

        if (rootNode.getChildCount() != 0) {
            tree.setRootVisible(true);
            tree.expandPath(new TreePath(rootNode));
            tree.setRootVisible(false);
        }
    }

    private boolean treeContainsChecker(DefaultMutableTreeNode rootNode, MailAccountChecker checker) {
        Enumeration<?> e = rootNode.children();

        DefaultMutableTreeNode node;
        while (e.hasMoreElements()) {
            node = (DefaultMutableTreeNode) e.nextElement();

            // Prüfe, ob sich der Checker des MailAccounts bereits im Baum
            // befindet
            if (checker.equals(node.getUserObject())) {
                // Wenn ja wird der Checker übersprungen
                return true;
            }
        }
        return false;
    }

    /**
     * Lädt die Mails des übergebenen Knotens in die Mailtabelle
     *
     * @param checker Checker-Objekt über das die Mails abgefragt werden sollen
     * @param node Baumknoten der den auszulesenden Ordner enthält
     */
    private void loadMails(MailAccountChecker checker, DefaultMutableTreeNode node) {
        DefaultTableModel model = (DefaultTableModel) tblMails.getModel();
        // Leere Tabelle
        model.setRowCount(0);

        Object userObject = node.getUserObject();
        if (userObject instanceof FolderInfo) {
            FolderInfo folder = (FolderInfo) userObject;
            load = true;

            try {
                StoredMailInfo[] messages = checker.getMessages(folder.getPath());
                int unread = addMailsToTable(messages, model);
                updateUnreadCounter(folder, node, unread);
                sortTable();
            } catch (FolderNotFoundException e) {
                // Wurde der Ordner nicht gefunden, wird dieser aus dem Baum
                // entfernt
                DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
                treeModel.removeNodeFromParent(node);

                LOGGER.warn(Localization.getString("MainFrame_CouldNotFindMailFolder"), e);
            } catch (MessagingException | DAOException ex) {
                LOGGER.error(Localization.getString("MainFrame_CouldNotGetMessage"), ex);
            }

            load = false;
        }
    }

    private int addMailsToTable(StoredMailInfo[] messages, DefaultTableModel model) {
        int unread = 0;

        // Füge jede Mail der Tabelle hinzu
        for (StoredMailInfo info : messages) {
            if (!info.isRead()) {
                unread++;
            }

            model.addRow(new Object[]{info, info.getSubject(), info.getSender(), info.getDate()});
        }

        return unread;
    }

    private void updateUnreadCounter(FolderInfo folder, DefaultMutableTreeNode node, int unread) {
        if (folder.getNumberUnread() != unread) {
            // Aktualisiere den Zähler für ungelesene Nachrichten
            folder.setNumberUnread(unread);
            refreshNodeView(node);
        }
    }

    /**
     * Gibt die ausgewählten StoredMailInfo-Objekte der Tabelle zurück.
     *
     * @return Ausgewählte MailInfos
     */
    private StoredMailInfo[] selectedMailInfos() {
        DefaultTableModel model = (DefaultTableModel) tblMails.getModel();

        StoredMailInfo[] infos = new StoredMailInfo[tblMails.getSelectedRowCount()];

        // Lese Index der ausgewählten Spalten der View aus
        int[] indices = tblMails.getSelectedRows();

        for (int i = 0; i < infos.length; i++) {
            // Konvertiere Index, da das Auslesen aus dem Model erfolgt
            int modelIndex = tblMails.convertRowIndexToModel(indices[i]);

            infos[i] = (StoredMailInfo) model.getValueAt(modelIndex, 0);
        }

        return infos;
    }

    /**
     * Gibt den MailAccount zurück, dessen Knoten, bzw. dessen Ordnerknoten
     * momentan im Baum ausgewählt sind.
     *
     * @return Ausgewählter MailAccount; oder null, falls nicht selektiert wurde
     */
    private MailAccount selectedAccount() {
        MailAccountChecker checker = selectedChecker();
        if (checker == null) {
            return null;
        }

        return checker.getAccount();
    }

    /**
     * Gibt den MailChecker zurück, dessen Knoten, bzw. dessen Ordnerknoten
     * momentan im Baum ausgewählt sind.
     *
     * @return Ausgewählter MailChecker; oder null, falls nicht selektiert wurde
     */
    private MailAccountChecker selectedChecker() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode == null) {
            return null;
        }

        Object userObject = null;
        do {
            userObject = selectedNode.getUserObject();
            if (userObject instanceof MailAccountChecker) {
                break;
            }
            selectedNode = (DefaultMutableTreeNode) selectedNode.getParent();
        } while (true);

        return (MailAccountChecker) userObject;
    }

    /**
     * Öffnet ein neues MailFrame für die übergebene StoredMailInfo
     *
     * @param info Die im MailFrame anzuzeigende Mail
     */
    private void openMail(StoredMailInfo info) {
        MailAccount acc = selectedAccount();
        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        FolderInfo path = nodeToFolder(selected);

        MailFrame mailFrame;
        try {
            mailFrame = new MailFrame(info, path.getPath(), acc);

            mailFrame.setSize(this.getSize());
            mailFrame.setExtendedState(this.getExtendedState());
            mailFrame.setVisible(true);
        } catch (MessagingException | DAOException e) {
            LOGGER.error(Localization.getString("MainFrame_CouldNotOpenMail"), e);
            Dialogs.showErrorDialog(this, Localization.getString("MainFrame_CouldNotOpenMail") + ":\n" + e.getMessage());
        }
    }

    /**
     * Öffnet das Popup-Menü der Mailtabelle
     *
     * @param e Daten zur ausgeführen Aktion mit der Maus
     */
    private void openPopupTable(MouseEvent e) {
        if (e != null && e.isPopupTrigger()) {
            int row = tblMails.rowAtPoint(e.getPoint());
            int column = tblMails.columnAtPoint(e.getPoint());

            // Öffnet das Popup, wenn mindestens ein Eintrag gewählt wurde
            if (row >= 0 && column >= 0) {
                tblMails.setRowSelectionInterval(row, row);

                tablePopup.remove(popupCopy);
                popupCopy = generateFolderMenu(popupCopy.getText(), Localization.getString("MainFrame_CopyToFolder"));
                tablePopup.add(popupCopy);

                tablePopup.remove(popupMove);
                popupMove = generateFolderMenu(popupMove.getText(), Localization.getString("MainFrame_MoveToFolder"));
                tablePopup.add(popupMove);

                // Öffnet das Popup an der Mausposition relativ zur Tabelle
                tablePopup.show(tblMails, e.getX(), e.getY());
            }
        }
    }

    /**
     * Gibt ein MenüItem/Menü zurück, das die Ordner und eventuellen Unterordner
     * des übergebenen Knotens darstellt.
     *
     * @param path Startpfad der Auswertung. Ist der Pfad leer, wird der Knoten
     * vom Beginn an ausgewertet
     * @param operation Name des Obermenüs(Kopieren oder Verschieben)
     * @param node Knoten, dessen Daten in das Menü eingefügt werden sollen
     * @param itemTitle Titel der Menüitems, die den aktuell im Menü angezeigten
     * Ordner selektieren
     * @return MenüItem/Menü, das den übergebenen Knoten darstellt
     */
    private JMenuItem generateFolderMenu(String path, String operation, DefaultMutableTreeNode node,
            String itemTitle) {
        ActionListener menuListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JMenuItem item = (JMenuItem) e.getSource();

                // Lese die gespeicherten Properties über den Pfad und Typ des
                // Eintrags aus
                String path = (String) item.getClientProperty("PFAD");
                String type = (String) item.getClientProperty("TYP");

                if (type.equals(Localization.getString("Menu_Copy"))) {
                    copyMail(path);
                } else if (type.equals(Localization.getString("Menu_Move"))) {
                    moveMail(path);
                } else {
                    throw new IllegalArgumentException(
                            String.format(Localization.getString("MainFrame_InvalidTypeFormat"), type));
                }
            }
        };

        Object userObject = node.getUserObject();

        String menuTitle;
        // Bestimme den Titel des MenuItems
        if (userObject instanceof MailAccountChecker) {
            MailAccountChecker checker = (MailAccountChecker) userObject;
            MailAccount acc = checker.getAccount();
            menuTitle = acc.getAddress().getAddress();
        } else {
            menuTitle = node.getUserObject().toString();
        }

        // Pfad des aktuellen Knotens
        String currentNodePath = path + menuTitle;

        // Zurückzugebendes neues MenüItem
        int childCount = node.getChildCount();

        JMenuItem subMenu;
        // Wenn Unterordner vorhanden sind
        if (childCount > 0) {
            // "untermenu" ist ein Menü
            subMenu = new JMenu(menuTitle);

            // MenuItem, das den aktuell ausgewählten Ordner selektiert
            JMenuItem item = new JMenuItem(itemTitle);
            item.putClientProperty("TYP", operation);
            item.putClientProperty("PFAD", currentNodePath);
            item.addActionListener(menuListener);

            subMenu.add(item);
            subMenu.add(new JSeparator());

            // Rekursiver Aufruf auf alle Unterordner
            for (int i = 0; i < childCount; i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
                subMenu.add(generateFolderMenu(currentNodePath + "/", operation, child, itemTitle));
            }
        } else {
            // "untermenu" ist ein MenuItem
            subMenu = new JMenuItem(menuTitle);
            subMenu.putClientProperty("TYP", operation);
            subMenu.putClientProperty("PFAD", currentNodePath);
            subMenu.addActionListener(menuListener);
        }

        return subMenu;
    }

    /**
     * Erstellt ein JMenu der Ordner des ausgewählten MailAccounts
     *
     * @param title Titel des Menüs
     * @param itemTitle Titel der Menüitems, die den aktuell im Menü angezeigten
     * Ordner selektieren
     * @return Vollständiges Menü der Ordner des ausgewählten MailAccounts
     */
    private JMenu generateFolderMenu(String title, String itemTitle) {
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();

        MailAccountChecker selectedChecker = selectedChecker();

        // Iteriere über alle Knoten, die MailChecker enthalten
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) rootNode.getChildAt(i);

            // Prüfe, ob der Checker des Knotens der ausgewählte Checker ist
            if (child.getUserObject() == selectedChecker) {
                // Zurückzugebendes Menü
                JMenu newMenu = new JMenu(title);

                // Iteriere über die Kind-Knoten des Checker-Knotens
                for (int j = 0; j < child.getChildCount(); j++) {
                    DefaultMutableTreeNode subchild = (DefaultMutableTreeNode) child.getChildAt(j);

                    // Füge ein neues Menü für den Knoten ein
                    newMenu.add(generateFolderMenu("", title, subchild, itemTitle));
                }

                return newMenu;
            }
        }

        return null;
    }

    /**
     * Kopiere die ausgewählten Mails in den übergebenen Pfad
     *
     * @param target Pfad zum Zielordner
     */
    private void copyMail(String target) {
        MailAccount acc = selectedAccount();
        StoredMailInfo[] infos = selectedMailInfos();
        FolderInfo source = nodeToFolder((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());

        try {
            // Das eigendliche Kopieren der Mails
            acc.copyMails(infos, source.getPath(), target);
        } catch (MessagingException | DAOException e) {
            LOGGER.error(Localization.getString("MainFrame_CouldNotCopyMail"), e);
            Dialogs.showErrorDialog(this, Localization.getString("MainFrame_CouldNotCopyMail") + ": \n" + e.getMessage());
        }
    }

    /**
     * Verschiebe die ausgewählten Mails in den übergebenen Pfad
     *
     * @param target Pfad zum Zielordner
     */
    private void moveMail(String target) {
        MailAccountChecker checker = selectedChecker();
        MailAccount acc = checker.getAccount();
        StoredMailInfo[] infos = selectedMailInfos();
        FolderInfo source = nodeToFolder((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());

        try {
            // Das eigendliche Verschieben der Mails
            acc.moveMails(infos, source.getPath(), target);
            checker.removeMailInfos(infos);

            // Entferne alle ausgewählten Mails aus der Tabelle
            removeSelectedMailEntries();
        } catch (MessagingException | DAOException e) {
            LOGGER.error(Localization.getString("MainFrame_CouldNotMoveMail"), e);
            Dialogs.showErrorDialog(this, Localization.getString("MainFrame_CouldNotMoveMail") + ": \n" + e.getMessage());
        }
    }

    /**
     * Lösche die ausgewählten Mails
     */
    private void deleteMail() {
        StoredMailInfo[] infos = selectedMailInfos();
        FolderInfo path = nodeToFolder((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());
        MailAccountChecker checker = selectedChecker();
        MailAccount acc = checker.getAccount();

        try {
            // Das eigendliche Löschen der Mails
            acc.deleteMails(infos, path.getPath());
            checker.removeMailInfos(infos);

            // Entferne bei Erfolg alle ausgewählten Mails aus der Tabelle
            removeSelectedMailEntries();
        } catch (MessagingException | DAOException e) {
            LOGGER.error(Localization.getString("MainFrame_CouldNotDeleteMail"), e);
            Dialogs.showErrorDialog(this, Localization.getString("MainFrame_CouldNotDeleteMail") + ": \n" + e.getMessage());
        }
    }

    private void removeSelectedMailEntries() {
        DefaultTableModel model = (DefaultTableModel) tblMails.getModel();
        int row = tblMails.getSelectedRow();
        while (row != -1) {
            int mapped = tblMails.convertRowIndexToModel(row);
            model.removeRow(mapped);

            row = tblMails.getSelectedRow();
        }
    }

    /**
     * Zeige die übergebene StoredMailInfo in dem Vorschau-Feld an
     *
     * @param info Info-Objekt, das angezeigt werden soll
     */
    private void showPreview(StoredMailInfo info) {
        if (info == null) {
            tpPreview.setEditable(true);
            tpPreview.setText("");
            tpPreview.setEditable(false);

            return;
        }

        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        FolderInfo path = nodeToFolder(selectedNode);

        MailAccount account = selectedAccount();
        Set<MailContent> mailContents = EnumSet.of(
                MailContent.TEXT,
                MailContent.CONTENTTYPE,
                MailContent.READ);

        try {
            boolean read = info.isRead();
            // Lese den Mailtext aus
            account.loadMessageData(path.getPath(), info, mailContents);

            if (!read) {
                // Dekrementiere den Zähler des Ordners für ungelesene Mails
                FolderInfo folder = (FolderInfo) selectedNode.getUserObject();
                folder.setNumberUnread(folder.getNumberUnread() - 1);

                refreshNodeView(selectedNode);
            }

        } catch (MessagingException | DAOException ex) {
            LOGGER.error(Localization.getString("MainFrame_CouldNotLoadMailText"), ex);
            Dialogs.showErrorDialog(this, Localization.getString("MainFrame_CouldNotLoadMailText") + ":\n" + ex.getMessage());
        }

        String text = info.getText();
        String contentType = info.getContentType();

        tpPreview.setEditable(true);
        tpPreview.setText(text, contentType, true);
        tpPreview.setEditable(false);

        // Scrolle an den Kopf des Textes
        tpPreview.setCaretPosition(0);
    }

    /**
     * Zeigt eine Fehlermeldung für das Nichtvorhandensein eines MailAccounts an
     */
    private void noMailAccount() {
        Dialogs.showErrorDialog(this, Localization.getString("MainFrame_NoMailAccount"));
    }

    /**
     * Aktualisiert die Ansicht des übergebenen Knotens
     *
     * @param node Zu aktualisierender Knoten
     */
    private void refreshNodeView(DefaultMutableTreeNode node) {
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        treeModel.nodeChanged(node);
    }

    private class MailListener implements NewMailListener {

        @Override
        public void newMessage(final NewMailEvent e) {
            // Falls der Aufruf aus einem anderen als dem Thread kommt, in
            // dem die GUI ausgeführt wird
            if (!SwingUtilities.isEventDispatchThread()) {
                if (e.getSource() == selectedChecker()) {
                    synchronized (MailListener.class) {
                        if (load) {
                            return;
                        }
                    }
                }

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        processMessage(e);
                    }
                });
                return;
            }

            processMessage(e);
        }

        private void processMessage(NewMailEvent e) {
            // Spätestens hier ist der ausführende Thread der GUI-Thread
            MailAccountChecker sender = (MailAccountChecker) e.getSource();

            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

            DefaultMutableTreeNode checkerNode = getMailCheckerNode(root, sender);
            if (checkerNode == null) {
                LOGGER.warn(Localization.getString("MainFrame_NoNodeForMail"));
                return;
            }

            // Iteriere über alle direkten Kind-Knoten des
            // MailChecker-Knotens
            for (int j = 0; j < checkerNode.getChildCount(); j++) {
                DefaultMutableTreeNode folderNode = (DefaultMutableTreeNode) checkerNode.getChildAt(j);
                FolderInfo folder = (FolderInfo) folderNode.getUserObject();

                if ("inbox".equalsIgnoreCase(folder.getName())) {
                    // Inkrementiere Anzahl ungelesener Mails im
                    // Ordner
                    folder.setNumberUnread(folder.getNumberUnread() + 1);
                    // Aktualisiere Ansicht im Baum
                    refreshNodeView(folderNode);

                    DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree
                            .getLastSelectedPathComponent();
                    // Wenn der aktualisierte Knoten der momentan
                    // ausgewählte Knoten ist
                    if (selected == folderNode) {
                        // Füge Zeile für die neue Mail in die
                        // Tabelle ein

                        StoredMailInfo info = e.getInfo();
                        DefaultTableModel tableModel = (DefaultTableModel) tblMails.getModel();
                        tableModel.addRow(
                                new Object[]{info, info.getSubject(), info.getSender(), info.getDate()});
                    }

                    break;
                }
            }
        }

        private DefaultMutableTreeNode getMailCheckerNode(DefaultMutableTreeNode root, MailAccountChecker checker) {
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
                // Wenn der Sender des Events dem UserObject des
                // ausgewählten Knotens entspricht
                if (checker == child.getUserObject()) {
                    return child;
                }
            }

            return null;
        }
    }
}
