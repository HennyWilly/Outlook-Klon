package de.outlookklon.gui;

import de.outlookklon.logik.kontakte.Contact;
import de.outlookklon.logik.kontakte.ContactManagement;
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

/**
 * In diesem Frame werden alle Kontaktlisten der Verwaltung und deren Kontakte
 * angezeigt. Bietet zudem Funktionalitäten zum Erstellen, Bearbeiten und
 * Löschen von Listen und Kontakten an
 *
 * @author Hendrik Karwanni
 */
public class AddressBookFrame extends ExtendedFrame {

    private static final long serialVersionUID = 2142631007771154882L;

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
     * Dialogfenster zum Erstellen und Bearbeiten von Kontaktlisten
     */
    private final class ListDialog extends ExtendedDialog<String> {

        private static final long serialVersionUID = 1L;

        private String mList;
        private JTextField txtList;

        /**
         * Initalisiert die Komponenten des Dialogs
         */
        private void initGUI() {
            txtList = new JTextField();
            txtList.setColumns(10);

            JButton btnOK = new JButton("OK");
            btnOK.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    mList = txtList.getText();
                    close();
                }
            });

            JButton btnAbort = new JButton("Abbruch");
            btnAbort.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    close();
                }
            });

            JLabel lblNameOfList = new JLabel("Name der Liste:");
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

        /**
         * Erzeugt eine neue Instanz des Dialogs zum Erstellen einer Liste
         */
        public ListDialog() {
            super(355, 130);

            initGUI();

            setTitle("Neue Liste erstellen");
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
            setTitle("Liste bearbeiten");
        }

        @Override
        protected String getDialogResult() {
            return mList;
        }
    }

    /**
     * Initialisiert die JTable des Frames
     *
     * @param verticalSplit JSplitPane in die die JTable eingefügt werden soll
     */
    private void initTable(JSplitPane verticalSplit) {
        tablePopup = new JPopupMenu();

        tablePopupOpen = new JMenuItem("Öffnen");
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

        tablePopupCreate = new JMenuItem("Verfassen");
        tablePopupCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writeMail(selectedContacts());
            }
        });
        tablePopup.add(tablePopupCreate);

        tablePopupDelete = new JMenuItem("Löschen");
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
        tableContacts.setModel(new DefaultTableModel(new Object[][]{{null, null, null},},
                new String[]{"Referenz", "Name", "E-Mail-Adresse", "Tel. dienstlich"}) {
            private static final long serialVersionUID = 1L;
            Class<?>[] columnTypes = new Class<?>[]{Contact.class, String.class, String.class, String.class};

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        });
        tableContacts.removeColumn(tableContacts.getColumn("Referenz"));

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
     * @param horizontalSplit JSplitPane in die die JList eingefügt werden soll
     */
    private void initLists(JSplitPane horizontalSplit) {
        listPopup = new JPopupMenu();

        listPopupRename = new JMenuItem("Umbennen");
        listPopupRename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renameList(currentList());
            }
        });
        listPopup.add(listPopupRename);

        listPopupCreate = new JMenuItem("Verfassen");
        listPopupCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Contact[] contacts = management.getContacts(currentList());
                writeMail(contacts);
            }
        });
        listPopup.add(listPopupCreate);

        listPopupDelete = new JMenuItem("Löschen");
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

        JMenu mnFile = new JMenu("Datei");
        menuBar.add(mnFile);

        JMenu mnNewMenu = new JMenu("Neu");
        mnFile.add(mnNewMenu);

        mntFileNewContact = new JMenuItem("Neuer Kontakt");
        mntFileNewContact.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                newContact();
            }
        });
        mnNewMenu.add(mntFileNewContact);

        mntFileNewList = new JMenuItem("Neue Kontaktliste");
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

        mntFileClose = new JMenuItem("Beenden");
        mntFileClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        mnFile.add(mntFileClose);
    }

    /**
     * Erstellt eine neue AdressbuchFrame-Instanz
     *
     * @param parent Referenz auf das Vater-Fenster, um darauf ggf. die
 newMail-Methode aufzurufen
     * @param contacts Die Referenz auf die ContactManagement
     * @param newContact Wenn true, wird sofort ein neues ContactFrame geöffnet;
 sonst nicht
     */
    public AddressBookFrame(MainFrame parent, ContactManagement contacts, boolean newContact) {
        if (contacts == null) {
            throw new NullPointerException("contacts instace is null");
        }

        setTitle("Adressbuch");
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
 geladen werden sollen.
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
        return (String) lstLists.getSelectedValue();
    }

    /**
     * Aktualisiert den Inhalt der JTextPane mit den Daten des übergebenen
     * Kontakts
     *
     * @param contact Contact, dessen Daten zum Füllen der Details verwendet
 werden
     */
    private void refreshDetails(Contact contact) {
        StringBuilder sb = new StringBuilder();

        if (contact != null) {
            if (!contact.getForename().trim().isEmpty()) {
                sb.append("Vorname: ").append(contact.getForename()).append('\n');
            }
            if (!contact.getSurname().trim().isEmpty()) {
                sb.append("Nachname: ").append(contact.getSurname()).append('\n');
            }
            if (!contact.getDisplayname().trim().isEmpty()) {
                sb.append("Anzeigename: ").append(contact.getDisplayname()).append('\n');
            }
            if (!contact.getNickname().trim().isEmpty()) {
                sb.append("Spitzname: ").append(contact.getNickname()).append('\n');
            }
            if (contact.getAddress1() != null) {
                sb.append("E-Mail-Adresse: ").append(contact.getAddress1AsString()).append('\n');
            }
            if (contact.getAddress2() != null) {
                sb.append("2. E-Mail-Adresse: ").append(contact.getAddress2AsString()).append('\n');
            }
            if (!contact.getDutyphone().trim().isEmpty()) {
                sb.append("Telefonnummer (dienstlich): ").append(contact.getDutyphone()).append('\n');
            }
            if (!contact.getPrivatephone().trim().isEmpty()) {
                sb.append("Telefonnummer (privat): ").append(contact.getPrivatephone()).append('\n');
            }
            if (!contact.getMobilephone().trim().isEmpty()) {
                sb.append("Telefonnummer (mobil): ").append(contact.getMobilephone()).append('\n');
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
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
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
        parent.newMail(contacts);
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
        JMenu menu = new JMenu("Zu Liste zuordnen");
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
     * Fügt die übergebenen Kontakte in die übergebene Liste der
 ContactManagement ein
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
            }
        }
    }
}
