package de.outlook_klon.gui;

import de.outlook_klon.logik.Benutzer;
import de.outlook_klon.logik.Benutzer.MailChecker;
import de.outlook_klon.logik.NewMailEvent;
import de.outlook_klon.logik.NewMailListener;
import de.outlook_klon.logik.kontakte.Kontakt;
import de.outlook_klon.logik.mailclient.MailAccount;
import de.outlook_klon.logik.mailclient.MailInfo;
import de.outlook_klon.logik.mailclient.OrdnerInfo;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.mail.FolderNotFoundException;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
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

    private static final DateFormat DATEFORMAT = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM,
            Locale.getDefault());

    private JPopupMenu tablePopup;
    private JMenuItem popupOeffnen;
    private JMenuItem popupLoeschen;
    private JMenuItem popupAntworten;
    private JMenuItem popupWeiterleiten;
    private JMenu popupKopieren;
    private JMenu popupVerschieben;

    private JTable tblMails;
    private JMenuItem mntmEmail;
    private JMenuItem mntmKontakt;
    private JMenuItem mntmTermin;
    private JMenuItem mntmBeenden;
    private JButton btnAbrufen;
    private JTree tree;
    private HtmlEditorPane tpPreview;

    private Benutzer benutzer;
    private JMenuItem mntmKonteneinstellungen;
    private JMenuItem mntmAdressbuch;
    private JMenuItem mntmKalendar;

    private boolean laden;

    /**
     * Initialisiert das Menü des Frames
     */
    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnDatei = new JMenu("Datei");
        menuBar.add(mnDatei);

        JMenu mnNewMenu = new JMenu("Neu");
        mnDatei.add(mnNewMenu);

        mntmEmail = new JMenuItem("E-Mail");
        mntmEmail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                neueMail();
            }
        });
        mnNewMenu.add(mntmEmail);

        mntmKontakt = new JMenuItem("Kontakt");
        mntmKontakt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oeffneAdressbuchFrame(true);
            }
        });
        mnNewMenu.add(mntmKontakt);

        mntmTermin = new JMenuItem("Termin");
        mntmTermin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oeffneKalenderFrame(true);
            }
        });
        mnNewMenu.add(mntmTermin);

        mntmBeenden = new JMenuItem("Beenden");
        mntmBeenden.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        mnDatei.add(mntmBeenden);

        JMenu mnMeldungen = new JMenu("Meldungen");
        menuBar.add(mnMeldungen);

        JMenuItem mntDateiNeuKrankmeldung = new JMenuItem("Krankmeldung");
        mntDateiNeuKrankmeldung.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bearbeiteKrankheitsmeldung();
            }
        });
        mnMeldungen.add(mntDateiNeuKrankmeldung);

        JMenuItem mntDateiNeuAbwesenheitmeldung = new JMenuItem("Abwesenheitsmeldung");
        mntDateiNeuAbwesenheitmeldung.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                bearbeiteAbwesenheitsmeldung();
            }
        });
        mnMeldungen.add(mntDateiNeuAbwesenheitmeldung);

        mnMeldungen.add(new JSeparator());

        JCheckBoxMenuItem mnMeldungenAbwesenheit = new JCheckBoxMenuItem("Abwesend");
        mnMeldungenAbwesenheit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem sender = (JCheckBoxMenuItem) e.getSource();

                boolean selected = sender.isSelected();
                benutzer.setAnwesend(!selected);
            }
        });
        mnMeldungen.add(mnMeldungenAbwesenheit);

        JMenu mnExtras = new JMenu("Extras");
        menuBar.add(mnExtras);

        mntmKonteneinstellungen = new JMenuItem("Konteneinstellungen");
        mntmKonteneinstellungen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oeffneKontoverwaltungFrame();
            }
        });

        mntmAdressbuch = new JMenuItem("Adressbuch");
        mntmAdressbuch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oeffneAdressbuchFrame(false);
            }
        });
        mnExtras.add(mntmAdressbuch);

        mntmKalendar = new JMenuItem("Kalendar");
        mntmKalendar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                oeffneKalenderFrame(false);
            }
        });
        mnExtras.add(mntmKalendar);

        mnExtras.add(new JSeparator());
        mnExtras.add(mntmKonteneinstellungen);
    }

    /**
     * Initialisiert das Popup-Menü der Mailtabelle
     */
    private void initTabellePopup() {
        popupOeffnen = new JMenuItem("Öffnen");
        popupOeffnen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) tblMails.getModel();

                int viewZeile = tblMails.getSelectedRow();
                if (viewZeile < 0) {
                    return;
                }

                int row = tblMails.convertRowIndexToModel(viewZeile);
                MailInfo mailID = (MailInfo) model.getValueAt(row, 0);

                oeffneMail(mailID);
            }
        });

        popupLoeschen = new JMenuItem("Löschen");
        popupLoeschen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loescheMail();
            }
        });

        popupAntworten = new JMenuItem("Antworten");
        popupAntworten.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) tblMails.getModel();

                int viewZeile = tblMails.getSelectedRow();
                if (viewZeile < 0) {
                    return;
                }

                int row = tblMails.convertRowIndexToModel(viewZeile);
                MailInfo mailID = (MailInfo) model.getValueAt(row, 0);

                antworten(mailID);
            }
        });

        popupWeiterleiten = new JMenuItem("Weiterleiten");
        popupWeiterleiten.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) tblMails.getModel();

                int viewZeile = tblMails.getSelectedRow();
                if (viewZeile < 0) {
                    return;
                }

                int row = tblMails.convertRowIndexToModel(viewZeile);
                MailInfo mailID = (MailInfo) model.getValueAt(row, 0);

                weiterleiten(mailID);
            }
        });

        popupKopieren = new JMenu("Kopieren");
        popupVerschieben = new JMenu("Verschieben");

        tablePopup = new JPopupMenu();
        tablePopup.add(popupOeffnen);
        tablePopup.add(popupLoeschen);
        tablePopup.add(popupAntworten);
        tablePopup.add(popupWeiterleiten);
        tablePopup.add(popupKopieren);
        tablePopup.add(popupVerschieben);
    }

    /**
     * Initialisiert die Mailtabelle
     *
     * @param verticalSplitPane JSplitPane, in die die Tabelle eingefügt werden
     * soll
     */
    private void initTabelle(JSplitPane verticalSplitPane) {
        tblMails = new JTable() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        ;
        };
		tblMails.setModel(
                new DefaultTableModel(new Object[][]{}, new String[]{"MailInfo", "Betreff", "Von", "Datum"}) {
            private static final long serialVersionUID = 1L;
            Class<?>[] columnTypes = new Class<?>[]{MailInfo.class, String.class, InternetAddress.class,
                Date.class};

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        });

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 6837957351164997131L;

            @Override
            public Component getTableCellRendererComponent(final JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                if (value instanceof Date) {
                    value = DATEFORMAT.format(value);
                } else if (value instanceof InternetAddress) {
                    InternetAddress data = (InternetAddress) value;
                    String personal = data.getPersonal();
                    String address = data.getAddress();

                    if (personal != null && !personal.trim().isEmpty()) {
                        value = personal;
                    } else {
                        value = address;
                    }
                }

                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                int modelRow = table.convertRowIndexToModel(row);
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                Object obj = model.getValueAt(modelRow, 0);
                if (obj instanceof MailInfo) {
                    MailInfo info = (MailInfo) obj;
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

        tblMails.removeColumn(tblMails.getColumn("MailInfo"));
        tblMails.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    DefaultTableModel model = (DefaultTableModel) tblMails.getModel();
                    int viewZeile = tblMails.getSelectedRow();

                    MailInfo info = null;

                    if (viewZeile >= 0) {
                        int zeile = tblMails.convertRowIndexToModel(viewZeile);
                        info = (MailInfo) model.getValueAt(zeile, 0);
                    }

                    previewAnzeigen(info);
                }
            }
        });

        tblMails.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultTableModel model = (DefaultTableModel) tblMails.getModel();

                    int viewZeile = tblMails.getSelectedRow();
                    if (viewZeile < 0) {
                        return;
                    }

                    int row = tblMails.convertRowIndexToModel(viewZeile);
                    MailInfo mailID = (MailInfo) model.getValueAt(row, 0);

                    oeffneMail(mailID);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                oeffnePopupTabelle(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                oeffnePopupTabelle(e);
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
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("[root]");
        tree = new JTree() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isEditable() {
                return false;
            }
        };
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
                    if (userObject instanceof OrdnerInfo) {
                        OrdnerInfo ordner = (OrdnerInfo) userObject;
                        if (ordner.getAnzahlUngelesen() > 0) {
                            label = String.format("<html><b>%s (%d)</b></html>", ordner.getName(),
                                    ordner.getAnzahlUngelesen());
                        }

                    }
                }

                Component c = super.getTreeCellRendererComponent(tree, label, selected, expanded, isLeaf, row, focused);

                if (value instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                    if (userObject instanceof MailChecker) {
                        setIcon(mailIcon);
                    } else if (userObject instanceof OrdnerInfo) {
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
        tree.setRootVisible(false);
        tree.setEditable(true);
        tree.setModel(new DefaultTreeModel(root));
        tree.expandPath(new TreePath(root.getPath()));
        ladeOrdner();
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode == null) {
                    return;
                }

                Object userObject = selectedNode.getUserObject();

                MailChecker checker;
                if (!(userObject instanceof MailChecker)) {
                    checker = ausgewaehlterChecker();
                    MailAccount account = checker.getAccount();

                    String ordnerName = selectedNode.toString();

                    setTitle(ordnerName + " - " + account.getAddress().getAddress());
                } else {
                    checker = (MailChecker) userObject;

                    setTitle(checker.getAccount().getAddress().getAddress());
                }

                ladeMails(checker, selectedNode);
            }
        });

        JScrollPane treeScroller = new JScrollPane(tree);
        splitPane.setLeftComponent(treeScroller);
    }

    /**
     * Erstellt eine neue Instanz des Hauptfensters
     */
    public MainFrame() {
        setTitle("MailClient");

        benutzer = Benutzer.getInstanz();
        if (benutzer == null) {
            System.exit(1);
        }

        JSplitPane horizontalSplitPane = new JSplitPane();

        initTabellePopup();
        initTree(horizontalSplitPane);

        JSplitPane verticalSplitPane = new JSplitPane();
        verticalSplitPane.setContinuousLayout(true);
        verticalSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        horizontalSplitPane.setRightComponent(verticalSplitPane);

        initTabelle(verticalSplitPane);

        tpPreview = new HtmlEditorPane();
        tpPreview.setEditable(false);

        JScrollPane previewScroller = new JScrollPane(tpPreview);
        verticalSplitPane.setRightComponent(previewScroller);

        JToolBar toolBar = new JToolBar();

        btnAbrufen = new JButton("Abrufen");
        btnAbrufen.addActionListener(new ActionListener() {
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

                benutzer.stoppeChecker();
                ladeOrdner();
            }
        });
        toolBar.add(btnAbrufen);
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(toolBar, GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
                .addComponent(horizontalSplitPane, GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(toolBar, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(horizontalSplitPane, GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE).addGap(0)));
        getContentPane().setLayout(groupLayout);

        initMenu();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    benutzer.speichern();
                } catch (IOException e) {
                    Component component = windowEvent.getComponent();
                    JOptionPane.showMessageDialog(component, "Die Einstellungen konnten nicht gespeichert werden!",
                            "Fehler", JOptionPane.ERROR_MESSAGE);
                }
                System.exit(0);
            }
        });

        laden = false;
    }

    /**
     * Öffnet ein MailFrame zum Antworten auf die übergebene MailInfo
     *
     * @param info Info-Objekt der Mail, auf die geantwortet werden soll
     */
    private void antworten(MailInfo info) {
        MailAccount acc = ausgewaehlterAccount();
        OrdnerInfo pfad = nodeZuOrdner((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());

        MailFrame mf;
        try {
            mf = new MailFrame(info, pfad.getPfad(), acc, false);

            mf.setSize(this.getSize());
            mf.setExtendedState(this.getExtendedState());
            mf.setVisible(true);
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(this, "Antworten fehlgeschlagen: \n" + e.getMessage(), "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Öffnet ein MailFrame zum Weiterleiten der übergebenen Mail
     *
     * @param info Info-Objekt der Mail, die weitergeleitet werden soll
     */
    private void weiterleiten(MailInfo info) {
        MailAccount acc = ausgewaehlterAccount();
        OrdnerInfo pfad = nodeZuOrdner((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());

        MailFrame mf;
        try {
            mf = new MailFrame(info, pfad.getPfad(), acc, true);

            mf.setSize(this.getSize());
            mf.setExtendedState(this.getExtendedState());
            mf.setVisible(true);
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(this, "Weiterleiten fehlgeschlagen: \n" + e.getMessage(), "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Öffnet ein MailFrame zum Schreiben einer neuen Mail
     */
    private void neueMail() {
        if (benutzer.getAnzahlKonten() == 0) {
            keinMailAccount();
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
     * @param kontakte Kontakte, für die die Mail verfasst werden soll
     */
    public void neueMail(Kontakt[] kontakte) {
        if (benutzer.getAnzahlKonten() == 0) {
            keinMailAccount();
            return;
        }

        MailFrame mf = new MailFrame(kontakte);

        mf.setSize(this.getSize());
        mf.setExtendedState(this.getExtendedState());
        mf.setVisible(true);
    }

    /**
     * Öffnet den Kalender zum Verwanten der Termine
     *
     * @param neu Wenn true, wird vor dem Öffnen ein neues TerminFrame geöffnet.
     * Wenn false dann nicht.
     */
    private void oeffneKalenderFrame(boolean neu) {
        TerminkalenderFrame tkf = new TerminkalenderFrame(neu);

        tkf.setSize(this.getSize());
        tkf.setExtendedState(this.getExtendedState());
        tkf.setVisible(true);
    }

    /**
     * Öffnet das Adressbuch zum Verwanten der Kontakte
     *
     * @param neu Wenn true, wird vor dem Öffnen ein neues KontaktFrame
     * geöffnet. Wenn false dann nicht.
     */
    private void oeffneAdressbuchFrame(boolean neu) {
        AdressbuchFrame af = new AdressbuchFrame(this, benutzer.getKontakte(), neu);

        af.setSize(this.getSize());
        af.setExtendedState(this.getExtendedState());
        af.setVisible(true);
    }

    /**
     * Öffnet die Kontenverwaltung zum Erstellen, Entfernen und Ändern von
     * MailAccount-Instanzen
     */
    private void oeffneKontoverwaltungFrame() {
        KontoverwaltungFrame vf = new KontoverwaltungFrame();

        MailAccount[] accounts = vf.showDialog();
        if (accounts != null) {
            // Flag, die angibt, ob der Baum neugezeichnet werden soll
            boolean refresh = false;

            DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();

            List<MailAccount> loeschbar = new ArrayList<>();

            // Entfernt nicht mehr verwendete Knoten für MailAccounts aus dem
            // Baum
            outer:
            for (int i = 0; i < rootNode.getChildCount(); i++) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) rootNode.getChildAt(i);
                MailChecker treeChecker = (MailChecker) node.getUserObject();
                MailAccount treeAccount = treeChecker.getAccount();

                for (MailAccount acc : accounts) {
                    if (acc == treeAccount) {
                        continue outer;
                    }
                }

                loeschbar.add(treeAccount);
                rootNode.remove(node);
                treeModel.reload();
                refresh = true;
            }

            // Füge neue MailAccounts dem Baum hinzu
            for (MailAccount acc : accounts) {
                Iterator<MailAccount> iterator = loeschbar.iterator();

                while (iterator.hasNext()) {
                    MailAccount acc2 = iterator.next();
                    if (acc2.getAddress().equals(acc.getAddress())) {
                        loeschbar.remove(acc2);
                    }
                }

                if (benutzer.addMailAccount(acc)) {
                    // MailChecker accChecker = benutzer.getCheckerOf(acc);
                    // accChecker.addNewMessageListener(getMailListener());
                    refresh = true;
                }
            }

            for (MailAccount acc : loeschbar) {
                try {
                    benutzer.entferneMailAccount(acc, true);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this,
                            "Es ist ein Fehler beim Löschen der vorhandenen Einstellungen aufgetreten!", "Fehler",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            if (refresh) {
                ladeOrdner();
            }
        }
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
     * Extrahiert das OrdnerInfo-Objekt aus dem übergebenen Knoten
     *
     * @param knoten Knoten, aus dem das OrdnerInfo-Objekt ausgelesen werden
     * soll
     * @return OrdnerInfo-Instanz, wenn der Knoten aus UserObject eine
     * OrdnerInfo enthält, sonst null
     */
    private OrdnerInfo nodeZuOrdner(DefaultMutableTreeNode knoten) {
        OrdnerInfo ret = null;

        Object userObject = knoten.getUserObject();
        if (userObject instanceof OrdnerInfo) {
            ret = (OrdnerInfo) userObject;
        }

        return ret;
    }

    /**
     * Erstellt rekursiv aus dem Pfad des übergebenen OrdnerInfo-Objekts die
     * Struktur der Baumknoten.
     *
     * @param ordner OrdnerInfo-Objekt zu dem die Baumstruktur erstellt werden
     * soll
     * @param parent Vaterknoten, in dem die Baumstruktur erstellt werden soll
     * @param tiefe Tiefe des Pfads des Ordners, die sich aus dem Trennen des
     * Pfads nach dem '/'-Zeichen ergibt
     */
    private void ordnerZuNode(OrdnerInfo ordner, DefaultMutableTreeNode parent, int tiefe) {
        String pfad = ordner.getPfad();
        String[] parts = ordner.getPfad().split("/");

        if (pfad.contains("/") && tiefe < parts.length - 1) {
            DefaultMutableTreeNode pfadKnoten = null;
            String pfadParent = parts[tiefe];

            // Sucht, ob bereits ein Knoten im Vaterknoten mit dem Pfad für
            // tiefe-1 existiert
            for (int j = 0; j < parent.getChildCount(); j++) {

                DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(j);
                Object childObject = child.getUserObject();

                if (childObject instanceof OrdnerInfo) {
                    OrdnerInfo childOrdner = (OrdnerInfo) childObject;
                    if (childOrdner.getName().equals(pfadParent)) {
                        pfadKnoten = child;
                        break;
                    }
                }
            }

            if (pfadKnoten == null) {
                pfadKnoten = new DefaultMutableTreeNode(ordner);
            }

            ordnerZuNode(ordner, pfadKnoten, tiefe + 1);
        } else {
            // Fügt dem Vaterknoten den neuen Knoten mit dem Ordner-Objekt ein
            parent.add(new DefaultMutableTreeNode(ordner));
        }
    }

    /**
     * Lädt alle Ordner der MailAccounts in die neue Baumstruktur
     */
    private void ladeOrdner() {
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();

        int i = 0;
        outer:
        for (MailChecker checker : benutzer) {
            Enumeration<?> e = rootNode.children();

            DefaultMutableTreeNode node;
            while (e.hasMoreElements()) {
                node = (DefaultMutableTreeNode) e.nextElement();

                // Prüfe, ob sich der Checker des MailAccounts bereits im Baum
                // befindet
                if (checker.equals(node.getUserObject())) {
                    // Wenn ja wird der Checker übersprungen
                    continue outer;
                }
            }

            DefaultMutableTreeNode accNode = new DefaultMutableTreeNode(checker);
            checker.addNewMessageListener(getMailListener());
            checker.start();

            // Lade die Ordnerstruktur aus dem Account
            OrdnerInfo[] ordner;
            try {
                ordner = checker.getAccount().getOrdnerstruktur();
            } catch (MessagingException ex) {
                LOGGER.error("Could not get folder structure", ex);
                ordner = new OrdnerInfo[0];
            }

            // Erstelle für jeden Ordner die passende Baumstruktur
            for (OrdnerInfo ordner1 : ordner) {
                ordnerZuNode(ordner1, accNode, 0);
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

    /**
     * Lädt die Mails des übergebenen Knotens in die Mailtabelle
     *
     * @param checker Checker-Objekt über das die Mails abgefragt werden sollen
     * @param node Baumknoten der den auszulesenden Ordner enthält
     */
    private void ladeMails(MailChecker checker, DefaultMutableTreeNode node) {
        DefaultTableModel model = (DefaultTableModel) tblMails.getModel();
        // Leere Tabelle
        model.setRowCount(0);

        Object userObject = node.getUserObject();
        if (userObject instanceof OrdnerInfo) {
            OrdnerInfo ordner = (OrdnerInfo) userObject;
            laden = true;

            try {
                MailInfo[] messages = checker.getMessages(ordner.getPfad());
                int ungelesen = 0;

                // Füge jede Mail der Tabelle hinzu
                for (MailInfo info : messages) {
                    if (!info.isRead()) {
                        ungelesen++;
                    }

                    model.addRow(new Object[]{info, info.getSubject(), info.getSender(), info.getDate()});
                }

                if (ordner.getAnzahlUngelesen() != ungelesen) {
                    // Aktualisiere den Zähler für ungelesene Nachrichten
                    ordner.setAnzahlUngelesen(ungelesen);
                    aktualisiereNodeAnsicht(node);
                }

                sortTable();
            } catch (FolderNotFoundException e) {
                // Wurde der Ordner nicht gefunden, wird dieser aus dem Baum
                // entfernt
                DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
                treeModel.removeNodeFromParent(node);
            } catch (MessagingException ex) {
                LOGGER.error("Could not get messages", ex);
            }

            laden = false;
        }
    }

    /**
     * Gibt die ausgewählten MailInfo-Objekte der Tabelle zurück.
     *
     * @return Ausgewählte MailInfos
     */
    private MailInfo[] ausgewaehlteMailInfo() {
        DefaultTableModel model = (DefaultTableModel) tblMails.getModel();

        MailInfo[] infos = new MailInfo[tblMails.getSelectedRowCount()];

        // Lese Index der ausgewählten Spalten der View aus
        int[] indizes = tblMails.getSelectedRows();

        for (int i = 0; i < infos.length; i++) {
            // Konvertiere Index, da das Auslesen aus dem Model erfolgt
            int modelIndex = tblMails.convertRowIndexToModel(indizes[i]);

            infos[i] = (MailInfo) model.getValueAt(modelIndex, 0);
        }

        return infos;
    }

    /**
     * Gibt den MailAccount zurück, dessen Knoten, bzw. dessen Ordnerknoten
     * momentan im Baum ausgewählt sind.
     *
     * @return Ausgewählter MailAccount; oder null, falls nicht selektiert wurde
     */
    private MailAccount ausgewaehlterAccount() {
        MailChecker checker = ausgewaehlterChecker();
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
    private MailChecker ausgewaehlterChecker() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode == null) {
            return null;
        }

        Object userObject = null;

        do {
            userObject = selectedNode.getUserObject();
            if (userObject instanceof MailChecker) {
                break;
            }
            selectedNode = (DefaultMutableTreeNode) selectedNode.getParent();
        } while (true);

        return (MailChecker) userObject;
    }

    /**
     * Öffnet ein neues MailFrame für die übergebene MailInfo
     *
     * @param info Die im MailFrame anzuzeigende Mail
     */
    private void oeffneMail(MailInfo info) {
        MailAccount acc = ausgewaehlterAccount();
        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        OrdnerInfo pfad = nodeZuOrdner(selected);

        MailFrame mf;
        try {
            mf = new MailFrame(info, pfad.getPfad(), acc);

            mf.setSize(this.getSize());
            mf.setExtendedState(this.getExtendedState());
            mf.setVisible(true);
        } catch (MessagingException e1) {
            JOptionPane.showMessageDialog(this,
                    "Es ist ein Fehler beim Öffnen der Mail aufgetreten:\n" + e1.getMessage(), "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Öffnet das Popup-Menü der Mailtabelle
     *
     * @param e Daten zur ausgeführen Aktion mit der Maus
     */
    private void oeffnePopupTabelle(MouseEvent e) {
        if (e != null && e.isPopupTrigger()) {
            int zeile = tblMails.rowAtPoint(e.getPoint());
            int spalte = tblMails.columnAtPoint(e.getPoint());

            // Öffnet das Popup, wenn mindestens ein Eintrag gewählt wurde
            if (zeile >= 0 && spalte >= 0) {
                tblMails.setRowSelectionInterval(zeile, zeile);

                tablePopup.remove(popupKopieren);
                popupKopieren = generiereOrdnerMenu(popupKopieren.getText(), "In Ordner kopieren");
                tablePopup.add(popupKopieren);

                tablePopup.remove(popupVerschieben);
                popupVerschieben = generiereOrdnerMenu(popupVerschieben.getText(), "In Ordner verschieben");
                tablePopup.add(popupVerschieben);

                // Öffnet das Popup an der Mausposition relativ zur Tabelle
                tablePopup.show(tblMails, e.getX(), e.getY());
            }
        }
    }

    /**
     * Gibt ein MenüItem/Menü zurück, das die Ordner und eventuellen Unterordner
     * des übergebenen Knotens darstellt.
     *
     * @param pfad Startpfad der Auswertung. Ist der Pfad leer, wird der Knoten
     * vom Beginn an ausgewertet
     * @param operation Name des Obermenüs(Kopieren oder Verschieben)
     * @param node Knoten, dessen Daten in das Menü eingefügt werden sollen
     * @param itemTitel Titel der Menüitems, die den aktuell im Menü angezeigten
     * Ordner selektieren
     * @return MenüItem/Menü, das den übergebenen Knoten darstellt
     */
    private JMenuItem generiereOrdnerMenu(String pfad, String operation, DefaultMutableTreeNode node,
            String itemTitel) {
        ActionListener menuListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JMenuItem item = (JMenuItem) e.getSource();

                // Lese die gespeicherten Properties über den Pfad und Typ des
                // Eintrags aus
                String pfad = (String) item.getClientProperty("PFAD");
                String typ = (String) item.getClientProperty("TYP");

                switch (typ) {
                    case "Kopieren":
                        kopiereMail(pfad);
                        break;
                    case "Verschieben":
                        verschiebeMail(pfad);
                        break;
                    default:
                        throw new IllegalArgumentException("Typ \'" + typ + "\' ungültig");
                }
            }
        };

        Object userObject = node.getUserObject();

        String menuTitel;
        // Bestimme den Titel des MenuItems
        if (userObject instanceof MailChecker) {
            MailChecker checker = (MailChecker) userObject;
            MailAccount acc = checker.getAccount();
            menuTitel = acc.getAddress().getAddress();
        } else {
            menuTitel = node.getUserObject().toString();
        }

        // Pfad des aktuellen Knotens
        pfad += menuTitel;

        // Zurückzugebendes neues MenüItem
        int childCount = node.getChildCount();

        JMenuItem untermenu;
        // Wenn Unterordner vorhanden sind
        if (childCount > 0) {
            // "untermenu" ist ein Menü
            untermenu = new JMenu(menuTitel);

            // MenuItem, das den aktuell ausgewählten Ordner selektiert
            JMenuItem item = new JMenuItem(itemTitel);
            item.putClientProperty("TYP", operation);
            item.putClientProperty("PFAD", pfad);
            item.addActionListener(menuListener);

            untermenu.add(item);
            untermenu.add(new JSeparator());

            // Rekursiver Aufruf auf alle Unterordner
            for (int i = 0; i < childCount; i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
                untermenu.add(generiereOrdnerMenu(pfad + "/", operation, child, itemTitel));
            }
        } else {
            // "untermenu" ist ein MenuItem
            untermenu = new JMenuItem(menuTitel);
            untermenu.putClientProperty("TYP", operation);
            untermenu.putClientProperty("PFAD", pfad);
            untermenu.addActionListener(menuListener);
        }

        return untermenu;
    }

    /**
     * Erstellt ein JMenu der Ordner des ausgewählten MailAccounts
     *
     * @param titel Titel des Menüs
     * @param itemTitel Titel der Menüitems, die den aktuell im Menü angezeigten
     * Ordner selektieren
     * @return Vollständiges Menü der Ordner des ausgewählten MailAccounts
     */
    private JMenu generiereOrdnerMenu(String titel, String itemTitel) {
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();

        MailChecker selectedChecker = ausgewaehlterChecker();

        // Iteriere über alle Knoten, die MailChecker enthalten
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) rootNode.getChildAt(i);

            // Prüfe, ob der Checker des Knotens der ausgewählte Checker ist
            if (child.getUserObject() == selectedChecker) {
                // Zurückzugebendes Menü
                JMenu neu = new JMenu(titel);

                // Iteriere über die Kind-Knoten des Checker-Knotens
                for (int j = 0; j < child.getChildCount(); j++) {
                    DefaultMutableTreeNode subchild = (DefaultMutableTreeNode) child.getChildAt(j);

                    // Füge ein neues Menü für den Knoten ein
                    neu.add(generiereOrdnerMenu("", titel, subchild, itemTitel));
                }

                return neu;
            }
        }

        return null;
    }

    /**
     * Kopiere die ausgewählten Mails in den übergebenen Pfad
     *
     * @param ziel Pfad zum Zielordner
     */
    private void kopiereMail(String ziel) {
        MailAccount acc = ausgewaehlterAccount();
        MailInfo[] infos = ausgewaehlteMailInfo();
        OrdnerInfo quelle = nodeZuOrdner((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());

        try {
            // Das eigendliche Kopieren der Mails
            acc.kopiereMails(infos, quelle.getPfad(), ziel);
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(this, "Kopieren der Mail fehlgeschlagen: \n" + e.getMessage(), "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Verschiebe die ausgewählten Mails in den übergebenen Pfad
     *
     * @param ziel Pfad zum Zielordner
     */
    private void verschiebeMail(String ziel) {
        MailChecker checker = ausgewaehlterChecker();
        MailAccount acc = checker.getAccount();
        MailInfo[] infos = ausgewaehlteMailInfo();
        OrdnerInfo quelle = nodeZuOrdner((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());

        try {
            // Das eigendliche Verschieben der Mails
            acc.verschiebeMails(infos, quelle.getPfad(), ziel);
            checker.removeMailInfos(infos);

            // Entferne alle ausgewählten Mails aus der Tabelle
            DefaultTableModel model = (DefaultTableModel) tblMails.getModel();
            int row = tblMails.getSelectedRow();
            while (row != -1) {
                int mapped = tblMails.convertRowIndexToModel(row);
                model.removeRow(mapped);

                row = tblMails.getSelectedRow();
            }
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(this, "Verschieben der Mail fehlgeschlagen: \n" + e.getMessage(), "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lösche die ausgewählten Mails
     */
    private void loescheMail() {
        MailInfo[] infos = ausgewaehlteMailInfo();
        OrdnerInfo pfad = nodeZuOrdner((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());
        MailChecker checker = ausgewaehlterChecker();
        MailAccount acc = checker.getAccount();

        try {
            // Das eigendliche Löschen der Mails
            if (acc.loescheMails(infos, pfad.getPfad())) {
                checker.removeMailInfos(infos);

                // Entferne bei Erfolg alle ausgewählten Mails aus der Tabelle
                DefaultTableModel model = (DefaultTableModel) tblMails.getModel();
                int row = tblMails.getSelectedRow();
                while (row != -1) {
                    int mapped = tblMails.convertRowIndexToModel(row);
                    model.removeRow(mapped);

                    row = tblMails.getSelectedRow();
                }
            }
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(this, "Löschen fehlgeschlagen: \n" + e.getMessage(), "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Zeige die übergebene MailInfo in dem Vorschau-Feld an
     *
     * @param info Info-Objekt, das angezeigt werden soll
     */
    private void previewAnzeigen(MailInfo info) {
        if (info == null) {
            tpPreview.setEditable(true);
            tpPreview.setText("");
            tpPreview.setEditable(false);

            return;
        }

        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        OrdnerInfo pfad = nodeZuOrdner(selectedNode);

        MailAccount account = ausgewaehlterAccount();

        try {
            boolean gelesen = info.isRead();
            // Lese den Mailtext aus
            account.getMessageText(pfad.getPfad(), info);

            if (!gelesen) {
                // Dekrementiere den Zähler des Ordners für ungelesene Mails
                OrdnerInfo ordner = (OrdnerInfo) selectedNode.getUserObject();
                ordner.setAnzahlUngelesen(ordner.getAnzahlUngelesen() - 1);

                aktualisiereNodeAnsicht(selectedNode);
            }

        } catch (MessagingException ex) {
            JOptionPane.showMessageDialog(this,
                    "Es ist ein Fehler beim Auslesen des Mail-Textes aufgetreten:\n" + ex.getMessage(), "Fehler",
                    JOptionPane.ERROR_MESSAGE);
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
    private void keinMailAccount() {
        JOptionPane.showMessageDialog(this,
                "Es wurde noch kein Mail-Account hinzugefügt!\n"
                + "Unter dem Menü \"Extras\" -> \"Konteneinstellungen\" können Sie Konten hinzufügen",
                "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Aktualisiert die Ansicht des übergebenen Knotens
     *
     * @param node Zu aktualisierender Knoten
     */
    private void aktualisiereNodeAnsicht(DefaultMutableTreeNode node) {
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        treeModel.nodeChanged(node);
    }

    /**
     * Öffnet ein Fenster zum Bearbeiten der Abwesenheitmeldung des Benutzers
     */
    private void bearbeiteAbwesenheitsmeldung() {
        MeldungsFrame mf = new MeldungsFrame(benutzer.getAbwesenheitsmeldung(), "Abwesenheitsmeldung");
        String meldung = mf.showDialog();

        if (meldung != null) {
            benutzer.setAbwesenheitsmeldung(meldung);
        }
    }

    /**
     * Öffnet ein Fenster zum Bearbeiten der Krankmeldung des Benutzers
     */
    private void bearbeiteKrankheitsmeldung() {
        MeldungsFrame mf = new MeldungsFrame(benutzer.getKrankmeldung(), "Krankheitsmeldung");
        String meldung = mf.showDialog();

        if (meldung != null) {
            benutzer.setKrankmeldung(meldung);
        }
    }

    private NewMailListener getMailListener() {
        return new NewMailListener() {
            @Override
            public void newMessage(final NewMailEvent e) {
                // Falls der Aufruf aus einem anderen als dem Thread kommt, in
                // dem die GUI ausgeführt wird
                if (!SwingUtilities.isEventDispatchThread()) {
                    if (e.getSource() == ausgewaehlterChecker()) {
                        synchronized (this) {
                            if (laden) {
                                return;
                            }
                        }
                    }

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            newMessage(e);
                        }
                    });
                    return;
                }

                // Spätestens hier ist der ausführende Thread der GUI-Thread
                MailChecker sender = (MailChecker) e.getSource();

                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

                // Iteriere über alle Knoten, die MailChecker enthalten
                outer:
                for (int i = 0; i < root.getChildCount(); i++) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
                    // Wenn der Sender des Events dem UserObject des
                    // ausgewählten Knotens entspricht
                    if (sender == child.getUserObject()) {
                        // Iteriere über alle direkten Kind-Knoten des
                        // MailChecker-Knotens
                        for (int j = 0; j < child.getChildCount(); j++) {
                            DefaultMutableTreeNode ordnerNode = (DefaultMutableTreeNode) child.getChildAt(j);
                            OrdnerInfo ordner = (OrdnerInfo) ordnerNode.getUserObject();

                            if (ordner.getName().equalsIgnoreCase("inbox")) {
                                // Inkrementiere Anzahl ungelesener Mails im
                                // Ordner
                                ordner.setAnzahlUngelesen(ordner.getAnzahlUngelesen() + 1);
                                // Aktualisiere Ansicht im Baum
                                aktualisiereNodeAnsicht(ordnerNode);

                                DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree
                                        .getLastSelectedPathComponent();
                                // Wenn der aktualisierte Knoten der momentan
                                // ausgewählte Knoten ist
                                if (selected == ordnerNode) {
                                    // Füge Zeile für die neue Mail in die
                                    // Tabelle ein

                                    MailInfo info = e.getInfo();
                                    DefaultTableModel tableModel = (DefaultTableModel) tblMails.getModel();
                                    tableModel.addRow(
                                            new Object[]{info, info.getSubject(), info.getSender(), info.getDate()});
                                }

                                break outer;
                            }
                        }
                    }
                }
            }
        };
    }

    /**
     * Hier wird das MainFrame erzeugt und angezeigt
     *
     * @param args Komandozeilenparamenter
     */
    public static void main(final String[] args) {
        final MainFrame mainFrame = new MainFrame();

        mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }
}
