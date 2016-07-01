package de.outlookklon.gui;

import de.outlookklon.Program;
import de.outlookklon.gui.helpers.Buttons;
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
import javax.swing.JOptionPane;
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
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In diesem Frame werden alle Kontaktlisten der Verwaltung und deren Kontakte
 * angezeigt. Bietet zudem Funktionalit�ten zum Erstellen, Bearbeiten und
 * L�schen von Listen und Kontakten an
 *
 * @author Hendrik Karwanni
 */
public class AddressBookFrame extends ExtendedFrame {

    private static final long serialVersionUID = 2142631007771154882L;

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressBookFrame.class);

    private JPopupMenu tablePopup;
    private JMenuItem tablePopupOpen;
    private JMenuItem tablePopupDelete;
    private JMenuItem tablePopupCreate;
    private JMenu tablePopupAddList;

    private JPopupMenu listPopup;
    private JMenuItem listPopupRename;
    private JMenuItem listPopupDelete;
    private JMenuItem listPopupCreate;

    private JTable tableContacts;
    private JTextPane txtDetails;
    private JList<String> lstLists;

    private JMenuItem mntFileNewContact;
    private JMenuItem mntFileNewList;
    private JMenuItem mntFileClose;

    private MainFrame parent;
    private ContactManagement management;

    /**
     * Erstellt eine neue AdressbuchFrame-Instanz
     *
     * @param parent Referenz auf das Vater-Fenster, um darauf ggf. die
     * newMail-Methode aufzurufen
     * @param contacts Die Referenz auf die ContactManagement
     * @param newContact Wenn true, wird sofort ein neues ContactFrame ge�ffnet;
     * sonst nicht
     */
    public AddressBookFrame(@NonNull MainFrame parent, @NonNull ContactManagement contacts, boolean newContact) {
        setTitle(Program.STRINGS.getString("AddressBookFrame_Title"));
        this.parent = parent;
        this.management = contacts;

        initGUI();

        refreshContactLists();
        lstLists.setSelectedIndex(0);

        if (newContact) {
            newContact();
        }
    }

    /**
     * Initialisiert die JTable des Frames
     *
     * @param verticalSplit JSplitPane in die die JTable eingef�gt werden soll
     */
    private void initTable(JSplitPane verticalSplit) {
        tablePopup = new JPopupMenu();

        tablePopupOpen = new JMenuItem(Program.STRINGS.getString("Menu_Open"));
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

        tablePopupCreate = new JMenuItem(Program.STRINGS.getString("AddressBookFrame_Menu_Create"));
        tablePopupCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writeMail(selectedContacts());
            }
        });
        tablePopup.add(tablePopupCreate);

        tablePopupDelete = new JMenuItem(Program.STRINGS.getString("Menu_Delete"));
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

        String refName = Program.STRINGS.getString("Table_Ref");
        tableContacts.setModel(
                new DefaultTableModel(
                        new Object[][]{
                            {
                                null, null, null
                            },}, new String[]{
                            refName,
                            Program.STRINGS.getString("AddressBookFrame_Table_Name"),
                            Program.STRINGS.getString("AddressBookFrame_Table_Mail"),
                            Program.STRINGS.getString("AddressBookFrame_Table_DutyPhone")
                        }) {
            private static final long serialVersionUID = 1L;
            Class<?>[] columnTypes = new Class<?>[]{Contact.class, String.class, String.class, String.class};

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        });
        tableContacts.removeColumn(tableContacts.getColumn(refName));

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
                if (e.getClickCount() == 2) {
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
     * @param horizontalSplit JSplitPane in die die JList eingef�gt werden soll
     */
    private void initLists(JSplitPane horizontalSplit) {
        listPopup = new JPopupMenu();

        listPopupRename = new JMenuItem(Program.STRINGS.getString("Menu_Rename"));
        listPopupRename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renameList(currentList());
            }
        });
        listPopup.add(listPopupRename);

        listPopupCreate = new JMenuItem(Program.STRINGS.getString("AddressBookFrame_Menu_Create"));
        listPopupCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Contact[] contacts = management.getContacts(currentList());
                writeMail(contacts);
            }
        });
        listPopup.add(listPopupCreate);

        listPopupDelete = new JMenuItem(Program.STRINGS.getString("Menu_Delete"));
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
                if (e.getClickCount() == 2) {
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

        JMenu mnFile = new JMenu(Program.STRINGS.getString("Menu_File"));
        menuBar.add(mnFile);

        JMenu mnNewMenu = new JMenu(Program.STRINGS.getString("Menu_New"));
        mnFile.add(mnNewMenu);

        mntFileNewContact = new JMenuItem(Program.STRINGS.getString("AddressBookFrame_Menu_NewContact"));
        mntFileNewContact.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                newContact();
            }
        });
        mnNewMenu.add(mntFileNewContact);

        mntFileNewList = new JMenuItem(Program.STRINGS.getString("AddressBookFrame_Menu_NewContactList"));
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

        mntFileClose = new JMenuItem(Program.STRINGS.getString("Menu_Close"));
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
     * Aktualisiert die JTable mit den Kontakten der �bergebenen Kontaktliste.
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
     * Gibt den aktuell ausgew�hlten Listennamen der JList zur�ck
     *
     * @return ausgew�hlter Listenname
     */
    private String currentList() {
        return (String) lstLists.getSelectedValue();
    }

    /**
     * Aktualisiert den Inhalt der JTextPane mit den Daten des �bergebenen
     * Kontakts
     *
     * @param contact Contact, dessen Daten zum F�llen der Details verwendet
     * werden
     */
    private void refreshDetails(Contact contact) {
        StringBuilder sb = new StringBuilder();

        if (contact != null) {
            if (!contact.getForename().trim().isEmpty()) {
                sb.append(Program.STRINGS.getString("Contact_Forename")).append(" ").append(contact.getForename()).append('\n');
            }
            if (!contact.getSurname().trim().isEmpty()) {
                sb.append(Program.STRINGS.getString("Contact_Surname")).append(" ").append(contact.getSurname()).append('\n');
            }
            if (!contact.getDisplayname().trim().isEmpty()) {
                sb.append(Program.STRINGS.getString("Account_DisplayName")).append(" ").append(contact.getDisplayname()).append('\n');
            }
            if (!contact.getNickname().trim().isEmpty()) {
                sb.append(Program.STRINGS.getString("Contact_Nickname")).append(" ").append(contact.getNickname()).append('\n');
            }
            if (contact.getAddress1() != null) {
                sb.append(Program.STRINGS.getString("Account_MailAddress")).append(" ").append(contact.getAddress1AsString()).append('\n');
            }
            if (contact.getAddress2() != null) {
                sb.append(Program.STRINGS.getString("Contact_MailAddress2")).append(" ").append(contact.getAddress2AsString()).append('\n');
            }
            if (!contact.getDutyphone().trim().isEmpty()) {
                sb.append(Program.STRINGS.getString("Contact_DutyPhone")).append(" ").append(contact.getDutyphone()).append('\n');
            }
            if (!contact.getPrivatephone().trim().isEmpty()) {
                sb.append(Program.STRINGS.getString("Contact_PrivatePhone")).append(" ").append(contact.getPrivatephone()).append('\n');
            }
            if (!contact.getMobilephone().trim().isEmpty()) {
                sb.append(Program.STRINGS.getString("Contact_MobilePhone")).append(" ").append(contact.getMobilephone()).append('\n');
            }
        }

        txtDetails.setEditable(true);
        txtDetails.setText(sb.toString());
        txtDetails.setEditable(false);
    }

    /**
     * �ffnet ein neues ContactFrame zum Erstellen eines neuen Kontaks
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
     * �ffnet ein neues ContactFrame zum bearbeiten des �bergebenen Kontaks
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
     * �ffnet einen neuen Listendialog zum Umbennen der �bergebenen Liste
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
                LOGGER.warn(Program.STRINGS.getString("AddressBookFrame_CouldNotRenameList"), ex);

                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        Program.STRINGS.getString("Dialog_Error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Gibt ein Array der im JTable ausgew�hlten Kontakte zur�ck
     *
     * @return Array der ausgew�hlten Kontakte
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
     * �ffnet ein neues MailFrame �ber die Instanz des Vaterfensters
     *
     * @param contacts Kontakte deren 1. Mailadresse automatisch als Empf�nger
     * eingetragen werden
     */
    private void writeMail(Contact[] contacts) {
        parent.newMail(contacts);
    }

    /**
     * �ffnet das Popup-Men� der JTable
     *
     * @param e Enth�lt Daten bez�glich des Klicks
     */
    private void openPopupTable(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int row = tableContacts.rowAtPoint(e.getPoint());
            int column = tableContacts.columnAtPoint(e.getPoint());

            if (row >= 0 && column >= 0) {
                // TODO Kontektmen� bei Mehrfachauswahl

                tableContacts.setRowSelectionInterval(row, row);

                tablePopup.show(tableContacts, e.getX(), e.getY());
            }
        }
    }

    /**
     * �ffnet das Popup-Men� der JList
     *
     * @param e Enth�lt Daten bez�glich des Klicks
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
     * Erstellt ein neues JMenu, das als Elemente die Namen aller Eintr�ge der
     * JList ausgenommen des Standardadressbuchs enth�lt
     *
     * @return Neues JMenu mit den Kontaktlisten
     */
    private JMenu createListMenu() {
        JMenu menu = new JMenu(Program.STRINGS.getString("AddressBookFrame_Menu_AssignToList"));
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
     * F�gt die �bergebenen Kontakte in die �bergebene Liste der
     * ContactManagement ein
     *
     * @param contacts Einzuf�gende Kontakte
     * @param list Liste, in die die Kontakte eingef�gt werden sollen
     */
    private void addList(Contact[] contacts, String list) {
        for (Contact k : contacts) {
            try {
                management.addToContactList(k, list);
            } catch (IllegalArgumentException ex) {
                // Ignoriere Fehler
                LOGGER.warn(Program.STRINGS.getString("AddressBookFrame_CouldNotAddContactToList"), ex);
            }
        }
    }

    /**
     * Dialogfenster zum Erstellen und Bearbeiten von Kontaktlisten
     */
    private final class ListDialog extends ExtendedDialog<String> {

        private static final long serialVersionUID = 1L;

        private String mList;
        private JTextField txtList;

        /**
         * Erzeugt eine neue Instanz des Dialogs zum Erstellen einer Liste
         */
        public ListDialog() {
            super(355, 130);

            initGUI();

            setTitle(Program.STRINGS.getString("AddressBookFrame_CreateNewList"));
        }

        /**
         * Erzeugt eine neue Instanz des Dialogs zum Bearbeiten einer Liste
         *
         * @param list
         */
        public ListDialog(String list) {
            super(355, 130);

            initGUI();

            txtList.setText(list);
            setTitle(Program.STRINGS.getString("AddressBookFrame_EditList"));
        }

        /**
         * Initalisiert die Komponenten des Dialogs
         */
        private void initGUI() {
            txtList = new JTextField();
            txtList.setColumns(10);

            JButton btnOK = Buttons.getOkButton();
            btnOK.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    mList = txtList.getText();
                    close();
                }
            });

            JButton btnAbort = Buttons.getAbortButton();
            btnAbort.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    close();
                }
            });

            JLabel lblNameOfList = new JLabel(Program.STRINGS.getString("AddressBookFrame_ListName"));
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
