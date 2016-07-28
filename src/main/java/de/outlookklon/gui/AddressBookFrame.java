package de.outlookklon.gui;

import de.outlookklon.gui.helpers.Dialogs;
import de.outlookklon.gui.helpers.Events;
import de.outlookklon.localization.Localization;
import de.outlookklon.logik.contacts.Contact;
import de.outlookklon.logik.contacts.ContactManagement;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In diesem Frame werden alle Kontaktlisten der Verwaltung und deren Kontakte
 * angezeigt. Bietet zudem Funktionalitäten zum Erstellen, Bearbeiten und
 * Löschen von Listen und Kontakten an
 *
 * @author Hendrik Karwanni
 */
public class AddressBookFrame extends ExtendedFrame {

    private static final long serialVersionUID = 2142631007771154882L;

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressBookFrame.class);

    private static final String CONTACT_TABLE_REF_COLUMN_NAME = "Reference";
    private static final int TABLE_COLUMN_INDEX_NAME = 0;
    private static final int TABLE_COLUMN_INDEX_MAIL = 1;
    private static final int TABLE_COLUMN_INDEX_DUTYPHONE = 2;

    private JPopupMenu tablePopup;
    private final JMenuItem tablePopupOpen;
    private final JMenuItem tablePopupDelete;
    private final JMenuItem tablePopupCreate;
    private JMenu tablePopupAddList;

    private JPopupMenu listPopup;
    private final JMenuItem listPopupRename;
    private final JMenuItem listPopupDelete;
    private final JMenuItem listPopupCreate;

    private JTable tableContacts;
    private JTextPane txtDetails;
    private JList<String> lstLists;

    private final JMenu mnNewMenu;
    private final JMenuItem mntFileNewContact;
    private final JMenuItem mntFileNewList;
    private final JMenu mnFile;
    private final JMenuItem mntFileClose;

    private MainFrame parentFrame;
    private ContactManagement management;

    /**
     * Erstellt eine neue AdressbuchFrame-Instanz
     *
     * @param parentFrame Referenz auf das Vater-Fenster, um darauf ggf. die
     * newMail-Methode aufzurufen
     * @param contacts Die Referenz auf die ContactManagement
     * @param newContact Wenn true, wird sofort ein neues ContactFrame geöffnet;
     * sonst nicht
     */
    public AddressBookFrame(@NonNull MainFrame parentFrame, @NonNull ContactManagement contacts, boolean newContact) {
        this.parentFrame = parentFrame;
        this.management = contacts;

        tablePopupOpen = new JMenuItem();
        tablePopupCreate = new JMenuItem();
        tablePopupDelete = new JMenuItem();

        listPopupRename = new JMenuItem();
        listPopupCreate = new JMenuItem();
        listPopupDelete = new JMenuItem();

        mnFile = new JMenu();
        mnNewMenu = new JMenu();
        mntFileNewContact = new JMenuItem();
        mntFileNewList = new JMenuItem();
        mntFileClose = new JMenuItem();

        initGUI();
        updateTexts();

        if (newContact) {
            newContact();
        }
    }

    @Override
    public void updateTexts() {
        setTitle(Localization.getString("AddressBookFrame_Title"));

        tablePopupOpen.setText(Localization.getString("Menu_Open"));
        tablePopupCreate.setText(Localization.getString("AddressBookFrame_Menu_Create"));
        tablePopupDelete.setText(Localization.getString("Menu_Delete"));

        TableColumnModel tableColumnModel = tableContacts.getColumnModel();
        tableColumnModel.getColumn(TABLE_COLUMN_INDEX_NAME).setHeaderValue(Localization.getString("AddressBookFrame_Table_Name"));
        tableColumnModel.getColumn(TABLE_COLUMN_INDEX_MAIL).setHeaderValue(Localization.getString("AddressBookFrame_Table_Mail"));
        tableColumnModel.getColumn(TABLE_COLUMN_INDEX_DUTYPHONE).setHeaderValue(Localization.getString("AddressBookFrame_Table_DutyPhone"));

        listPopupRename.setText(Localization.getString("Menu_Rename"));
        listPopupCreate.setText(Localization.getString("AddressBookFrame_Menu_Create"));
        listPopupDelete.setText(Localization.getString("Menu_Delete"));

        mnFile.setText(Localization.getString("Menu_File"));
        mnNewMenu.setText(Localization.getString("Menu_New"));
        mntFileNewContact.setText(Localization.getString("AddressBookFrame_Menu_NewContact"));
        mntFileNewList.setText(Localization.getString("AddressBookFrame_Menu_NewContactList"));
        mntFileClose.setText(Localization.getString("Menu_Close"));

        int row = tableContacts.getSelectionModel().getMinSelectionIndex();
        DefaultTableModel model = (DefaultTableModel) tableContacts.getModel();
        int length = model.getDataVector().size();

        int lstIndex = lstLists.getSelectedIndex();
        refreshContactLists();
        lstLists.setSelectedIndex(lstIndex == -1 ? 0 : lstIndex);

        if (row >= 0 && length > 0) {
            int rowModel = tableContacts.convertRowIndexToModel(row);

            Contact reference = (Contact) model.getValueAt(rowModel, 0);
            refreshDetails(reference);
            tableContacts.setRowSelectionInterval(row, row);
        }

        repaint();
    }

    private TableModel getTableModel() {
        return new DefaultTableModel(
                new Object[][]{
                    {
                        null, null, null
                    },}, new String[]{
                    CONTACT_TABLE_REF_COLUMN_NAME,
                    StringUtils.EMPTY,
                    StringUtils.EMPTY,
                    StringUtils.EMPTY
                }) {
            private static final long serialVersionUID = 1L;
            Class<?>[] columnTypes = new Class<?>[]{Contact.class, String.class, String.class, String.class};

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        };
    }

    /**
     * Initialisiert die JTable des Frames
     *
     * @param verticalSplit JSplitPane in die die JTable eingefügt werden soll
     */
    private void initTable(JSplitPane verticalSplit) {
        tablePopup = new JPopupMenu();

        tablePopupOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) tableContacts.getModel();

                int viewRow = tableContacts.getSelectedRow();
                if (viewRow < 0) {
                    return;
                }

                int row = tableContacts.convertRowIndexToModel(viewRow);
                Contact referenz = (Contact) model.getValueAt(row, 0);

                editContact(referenz);
            }
        });
        tablePopup.add(tablePopupOpen);

        tablePopupCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writeMail(selectedContacts());
            }
        });
        tablePopup.add(tablePopupCreate);

        tablePopupDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Contact[] contacts = selectedContacts();
                String list = currentList();

                for (Contact k : contacts) {
                    management.deleteContact(k, list);
                }

                refreshTable(list);
            }
        });
        tablePopup.add(tablePopupDelete);

        tablePopupAddList = new JMenu();
        tablePopup.add(tablePopupAddList);

        tableContacts = new JTable() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableContacts.setModel(getTableModel());
        tableContacts.removeColumn(tableContacts.getColumn(CONTACT_TABLE_REF_COLUMN_NAME));

        tableContacts.getColumnModel().getColumn(1).setPreferredWidth(91);
        tableContacts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableContacts.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    DefaultListSelectionModel sender = (DefaultListSelectionModel) e.getSource();
                    int row = sender.getMinSelectionIndex();
                    if (row == -1) {
                        return;
                    }

                    DefaultTableModel model = (DefaultTableModel) tableContacts.getModel();
                    int length = model.getDataVector().size();

                    if (length > 0) {
                        int rowModel = tableContacts.convertRowIndexToModel(row);

                        Contact reference = (Contact) model.getValueAt(rowModel, 0);
                        refreshDetails(reference);
                    } else {
                        txtDetails.setEditable(true);
                        txtDetails.setText(null);
                        txtDetails.setEditable(false);
                    }
                }
            }
        });

        tableContacts.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Events.isDoubleClick(e)) {
                    DefaultTableModel model = (DefaultTableModel) tableContacts.getModel();

                    int viewRow = tableContacts.getSelectedRow();
                    if (viewRow < 0) {
                        return;
                    }

                    int row = tableContacts.convertRowIndexToModel(viewRow);
                    Contact reference = (Contact) model.getValueAt(row, 0);

                    editContact(reference);
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

        JScrollPane contactScroller = new JScrollPane(tableContacts);
        verticalSplit.setLeftComponent(contactScroller);
    }

    /**
     * Initialisiert die JList des Frames
     *
     * @param horizontalSplit JSplitPane in die die JList eingefügt werden soll
     */
    private void initLists(JSplitPane horizontalSplit) {
        listPopup = new JPopupMenu();

        listPopupRename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renameList(currentList());
            }
        });
        listPopup.add(listPopupRename);

        listPopupCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Contact[] contacts = management.getContacts(currentList());
                writeMail(contacts);
            }
        });
        listPopup.add(listPopupCreate);

        listPopupDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel<String> model = (DefaultListModel<String>) lstLists.getModel();
                String list = currentList();

                management.deleteList(list);
                model.removeElement(list);

                refreshTable(list);
            }
        });
        listPopup.add(listPopupDelete);

        lstLists = new JList<>(new DefaultListModel<String>());
        lstLists.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                refreshTable(currentList());
            }
        });

        lstLists.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Events.isDoubleClick(e)) {
                    String list = currentList();

                    if (!ContactManagement.DEFAULT.equals(list)) {
                        renameList(currentList());
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                openPopupLists(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                openPopupLists(e);
            }
        });

        JScrollPane listerScroller = new JScrollPane(lstLists);
        horizontalSplit.setLeftComponent(listerScroller);
    }

    /**
     * Initalisiert die Komponenten des Frames
     */
    private void initGUI() {
        JSplitPane horizontalSplit = new JSplitPane();
        getContentPane().add(horizontalSplit, BorderLayout.CENTER);

        JSplitPane verticalSplit = new JSplitPane();
        verticalSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
        horizontalSplit.setRightComponent(verticalSplit);

        initTable(verticalSplit);

        txtDetails = new JTextPane();
        JScrollPane detailsScroller = new JScrollPane(txtDetails);
        verticalSplit.setRightComponent(detailsScroller);

        initLists(horizontalSplit);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(mnFile);
        mnFile.add(mnNewMenu);

        mntFileNewContact.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                newContact();
            }
        });
        mnNewMenu.add(mntFileNewContact);

        mntFileNewList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ListDialog ld = new ListDialog();
                String list = ld.showDialog();

                if (list != null) {
                    management.addList(list);
                    refreshContactLists();
                }
            }
        });
        mnNewMenu.add(mntFileNewList);

        mntFileClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        mnFile.add(mntFileClose);
    }

    /**
     * Aktualisiert die JList mit den aktuellen Kontaktlisten.
     */
    private void refreshContactLists() {
        String selected = lstLists.getSelectedValue();

        DefaultListModel<String> model = (DefaultListModel<String>) lstLists.getModel();
        model.clear();

        for (String liste : management.getLists()) {
            model.addElement(liste);
        }

        lstLists.setSelectedValue(selected, true);

        tablePopup.remove(tablePopupAddList);
        tablePopupAddList = createListMenu();
        if (tablePopupAddList.getMenuComponentCount() > 0) {
            tablePopup.add(tablePopupAddList);
        }
    }

    /**
     * Aktualisiert die JTable mit den Kontakten der übergebenen Kontaktliste.
     *
     * @param list Gibt an, aus welcher Liste der ContactManagement die Kontakte
     * geladen werden sollen.
     */
    private void refreshTable(String list) {
        if (list == null) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tableContacts.getModel();
        model.setRowCount(0);

        Contact[] contacts = management.getContacts(list);
        if (contacts == null) {
            return;
        }

        for (Contact k : contacts) {
            model.addRow(new Object[]{k, k.getDisplayname(), k.getAddress1(), k.getDutyphone()});
        }

        refreshDetails(null);
    }

    /**
     * Gibt den aktuell ausgewählten Listennamen der JList zurück
     *
     * @return ausgewählter Listenname
     */
    private String currentList() {
        return lstLists.getSelectedValue();
    }

    /**
     * Aktualisiert den Inhalt der JTextPane mit den Daten des übergebenen
     * Kontakts
     *
     * @param contact Contact, dessen Daten zum Füllen der Details verwendet
     * werden
     */
    private void refreshDetails(Contact contact) {
        StringBuilder sb = new StringBuilder();

        if (contact != null) {
            if (!contact.getForename().trim().isEmpty()) {
                sb.append(Localization.getString("Contact_Forename")).append(" ").append(contact.getForename()).append('\n');
            }
            if (!contact.getSurname().trim().isEmpty()) {
                sb.append(Localization.getString("Contact_Surname")).append(" ").append(contact.getSurname()).append('\n');
            }
            if (!contact.getDisplayname().trim().isEmpty()) {
                sb.append(Localization.getString("Account_DisplayName")).append(" ").append(contact.getDisplayname()).append('\n');
            }
            if (!contact.getNickname().trim().isEmpty()) {
                sb.append(Localization.getString("Contact_Nickname")).append(" ").append(contact.getNickname()).append('\n');
            }
            if (contact.getAddress1() != null) {
                sb.append(Localization.getString("Account_MailAddress")).append(" ").append(contact.getAddress1AsString()).append('\n');
            }
            if (contact.getAddress2() != null) {
                sb.append(Localization.getString("Contact_MailAddress2")).append(" ").append(contact.getAddress2AsString()).append('\n');
            }
            if (!contact.getDutyphone().trim().isEmpty()) {
                sb.append(Localization.getString("Contact_DutyPhone")).append(" ").append(contact.getDutyphone()).append('\n');
            }
            if (!contact.getPrivatephone().trim().isEmpty()) {
                sb.append(Localization.getString("Contact_PrivatePhone")).append(" ").append(contact.getPrivatephone()).append('\n');
            }
            if (!contact.getMobilephone().trim().isEmpty()) {
                sb.append(Localization.getString("Contact_MobilePhone")).append(" ").append(contact.getMobilephone()).append('\n');
            }
        }

        txtDetails.setEditable(true);
        txtDetails.setText(sb.toString());
        txtDetails.setEditable(false);
    }

    /**
     * Öffnet ein neues ContactFrame zum Erstellen eines neuen Kontaks
     */
    private void newContact() {
        ContactFrame kf = new ContactFrame();
        Contact k = kf.showDialog();

        if (k != null) {
            management.addContact(k);
            refreshTable(currentList());
        }
    }

    /**
     * Öffnet ein neues ContactFrame zum bearbeiten des übergebenen Kontaks
     *
     * @param k Contact-Objekt, das im ContactFrame bearbeitet werden soll.
     */
    private void editContact(Contact k) {
        ContactFrame kf = new ContactFrame(k);
        kf.showDialog();

        if (k != null) {
            int row = tableContacts.convertRowIndexToModel(tableContacts.getSelectedRow());
            refreshTable(currentList());
            int rowView = tableContacts.convertRowIndexToView(row);
            tableContacts.setRowSelectionInterval(rowView, rowView);
        }
    }

    /**
     * Öffnet einen neuen Listendialog zum Umbennen der übergebenen Liste
     *
     * @param list Listenname, der umbenannt werden soll
     */
    private void renameList(String list) {
        ListDialog ld = new ListDialog(list);
        String newName = ld.showDialog();

        if (newName != null) {
            try {
                management.renameList(list, newName);
                refreshContactLists();
                lstLists.setSelectedValue(newName, true);
            } catch (IllegalArgumentException ex) {
                LOGGER.warn(Localization.getString("AddressBookFrame_CouldNotRenameList"), ex);
                Dialogs.showErrorDialog(this, ex.getLocalizedMessage());
            }
        }
    }

    /**
     * Gibt ein Array der im JTable ausgewählten Kontakte zurück
     *
     * @return Array der ausgewählten Kontakte
     */
    private Contact[] selectedContacts() {
        Contact[] contacts = new Contact[tableContacts.getSelectedRowCount()];
        int[] indices = tableContacts.getSelectedRows();
        DefaultTableModel model = (DefaultTableModel) tableContacts.getModel();

        for (int i = 0; i < contacts.length; i++) {
            contacts[i] = (Contact) model.getValueAt(indices[i], 0);
        }

        return contacts;
    }

    /**
     * Öffnet ein neues MailFrame über die Instanz des Vaterfensters
     *
     * @param contacts Kontakte deren 1. Mailadresse automatisch als Empfänger
     * eingetragen werden
     */
    private void writeMail(Contact[] contacts) {
        parentFrame.newMail(contacts);
    }

    /**
     * Öffnet das Popup-Menü der JTable
     *
     * @param e Enthält Daten bezüglich des Klicks
     */
    private void openPopupTable(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int row = tableContacts.rowAtPoint(e.getPoint());
            int column = tableContacts.columnAtPoint(e.getPoint());

            if (row >= 0 && column >= 0) {
                // TODO Kontektmenü bei Mehrfachauswahl

                tableContacts.setRowSelectionInterval(row, row);

                tablePopup.show(tableContacts, e.getX(), e.getY());
            }
        }
    }

    /**
     * Öffnet das Popup-Menü der JList
     *
     * @param e Enthält Daten bezüglich des Klicks
     */
    private void openPopupLists(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int row = lstLists.locationToIndex(e.getPoint());

            if (row >= 0) {
                lstLists.setSelectedIndex(row);

                String list = lstLists.getSelectedValue();
                boolean isAddressBook = list.equals(ContactManagement.DEFAULT);

                listPopupDelete.setEnabled(!isAddressBook);
                listPopupRename.setEnabled(!isAddressBook);

                listPopup.show(lstLists, e.getX(), e.getY());
            }
        }
    }

    /**
     * Erstellt ein neues JMenu, das als Elemente die Namen aller Einträge der
     * JList ausgenommen des Standardadressbuchs enthält
     *
     * @return Neues JMenu mit den Kontaktlisten
     */
    private JMenu createListMenu() {
        // Kann so bleiben, weil RefreshContentList in updateText() aufgerufen wird.
        JMenu menu = new JMenu(Localization.getString("AddressBookFrame_Menu_AssignToList"));
        DefaultListModel<String> model = (DefaultListModel<String>) lstLists.getModel();

        for (int i = 0; i < model.getSize(); i++) {
            String item = model.get(i);
            if (!ContactManagement.DEFAULT.equals(item)) {
                JMenuItem menuItem = new JMenuItem(item);
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        String title = ((JMenuItem) arg0.getSource()).getText();
                        addList(selectedContacts(), title);
                    }
                });
                menu.add(menuItem);
            }
        }

        return menu;
    }

    /**
     * Fügt die übergebenen Kontakte in die übergebene Liste des
     * Kontaktmanagements ein
     *
     * @param contacts Einzufügende Kontakte
     * @param list Liste, in die die Kontakte eingefügt werden sollen
     */
    private void addList(Contact[] contacts, String list) {
        for (Contact k : contacts) {
            try {
                management.addToContactList(k, list);
            } catch (IllegalArgumentException ex) {
                // Ignoriere Fehler
                LOGGER.warn(Localization.getString("AddressBookFrame_CouldNotAddContactToList"), ex);
            }
        }
    }

    /**
     * Dialogfenster zum Erstellen und Bearbeiten von Kontaktlisten
     */
    private final class ListDialog extends ExtendedDialog<String> {

        private static final long serialVersionUID = 1L;

        private final String captionKey;

        private String mList;

        private final JLabel lblNameOfList;
        private final JTextField txtList;

        private final JButton btnOK;
        private final JButton btnAbort;

        /**
         * Erzeugt eine neue Instanz des Dialogs zum Erstellen einer Liste
         */
        public ListDialog() {
            super(355, 130);

            captionKey = "AddressBookFrame_CreateNewList";

            lblNameOfList = new JLabel();
            txtList = new JTextField();

            btnOK = new JButton();
            btnAbort = new JButton();

            initGUI();
            updateTexts();
        }

        /**
         * Erzeugt eine neue Instanz des Dialogs zum Bearbeiten einer Liste
         *
         * @param list
         */
        public ListDialog(String list) {
            super(355, 130);

            captionKey = "AddressBookFrame_EditList";

            lblNameOfList = new JLabel();
            txtList = new JTextField();

            btnOK = new JButton();
            btnAbort = new JButton();

            initGUI();
            updateTexts();

            txtList.setText(list);
        }

        @Override
        public void updateTexts() {
            setTitle(Localization.getString(captionKey));

            lblNameOfList.setText(Localization.getString("AddressBookFrame_ListName"));
            btnOK.setText(Localization.getString("Button_Ok"));
            btnAbort.setText(Localization.getString("Button_Abort"));
        }

        /**
         * Initalisiert die Komponenten des Dialogs
         */
        private void initGUI() {
            txtList.setColumns(10);

            btnOK.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    mList = txtList.getText();
                    close();
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
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(groupLayout.createSequentialGroup()
                                            .addComponent(btnOK, GroupLayout.PREFERRED_SIZE,
                                                    72, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnAbort))
                                    .addComponent(lblNameOfList)
                                    .addComponent(txtList, GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE))
                            .addContainerGap()));
            groupLayout
                    .setVerticalGroup(
                            groupLayout.createParallelGroup(Alignment.LEADING)
                            .addGroup(
                                    groupLayout.createSequentialGroup().addGap(10).addComponent(lblNameOfList)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(txtList, GroupLayout.PREFERRED_SIZE,
                                            GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18)
                                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                            .addComponent(btnOK).addComponent(btnAbort))
                                    .addContainerGap()));
            getContentPane().setLayout(groupLayout);
        }

        @Override
        protected String getDialogResult() {
            return mList;
        }
    }
}
