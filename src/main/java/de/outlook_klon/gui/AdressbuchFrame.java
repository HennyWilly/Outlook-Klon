package de.outlook_klon.gui;

import de.outlook_klon.logik.Benutzer;
import de.outlook_klon.logik.kontakte.Kontakt;
import de.outlook_klon.logik.kontakte.Kontaktverwaltung;
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
public class AdressbuchFrame extends ExtendedFrame {

    private static final long serialVersionUID = 2142631007771154882L;

    private JPopupMenu tablePopup;
    private JMenuItem popupTabelleOeffnen;
    private JMenuItem popupTabelleLoeschen;
    private JMenuItem popupTabelleVerfassen;
    private JMenu popupTabelleListeHinzufügen;

    private JPopupMenu listenPopup;
    private JMenuItem popupListenUmbennen;
    private JMenuItem popupListenLoeschen;
    private JMenuItem popupListenVerfassen;

    private JTable tblKontakte;
    private JTextPane txtDetails;
    private JList<String> lstListen;

    private JMenuItem mntDateiNeuKontakt;
    private JMenuItem mntDateiNeuListe;
    private JMenuItem mntDateiBeenden;

    private MainFrame parent;
    private Kontaktverwaltung verwaltung;

    /**
     * Dialogfenster zum Erstellen und Bearbeiten von Kontaktlisten
     */
    private final class ListenDialog extends ExtendedDialog<String> {

        private static final long serialVersionUID = 1L;

        private String mListe;
        private JTextField txtListe;

        /**
         * Initalisiert die Komponenten des Dialogs
         */
        private void initGUI() {
            txtListe = new JTextField();
            txtListe.setColumns(10);

            JButton btnOK = new JButton("OK");
            btnOK.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    mListe = txtListe.getText();
                    close();
                }
            });

            JButton btnAbbruch = new JButton("Abbruch");
            btnAbbruch.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    close();
                }
            });

            JLabel lblNameDerListe = new JLabel("Name der Liste:");
            GroupLayout groupLayout = new GroupLayout(getContentPane());
            groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                    .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(groupLayout.createSequentialGroup()
                                            .addComponent(btnOK, GroupLayout.PREFERRED_SIZE,
                                                    72, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnAbbruch))
                                    .addComponent(lblNameDerListe)
                                    .addComponent(txtListe, GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE))
                            .addContainerGap()));
            groupLayout
                    .setVerticalGroup(
                            groupLayout.createParallelGroup(Alignment.LEADING)
                            .addGroup(
                                    groupLayout.createSequentialGroup().addGap(10).addComponent(lblNameDerListe)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(txtListe, GroupLayout.PREFERRED_SIZE,
                                            GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addGap(18)
                                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                            .addComponent(btnOK).addComponent(btnAbbruch))
                                    .addContainerGap()));
            getContentPane().setLayout(groupLayout);
        }

        /**
         * Erzeugt eine neue Instanz des Dialogs zum Erstellen einer Liste
         */
        public ListenDialog() {
            super(355, 130);

            initGUI();

            setTitle("Neue Liste erstellen");
        }

        /**
         * Erzeugt eine neue Instanz des Dialogs zum Bearbeiten einer Liste
         *
         * @param liste
         */
        public ListenDialog(String liste) {
            super(355, 130);

            initGUI();

            txtListe.setText(liste);
            setTitle("Liste bearbeiten");
        }

        @Override
        protected String getDialogResult() {
            return mListe;
        }
    }

    /**
     * Initialisiert die JTable des Frames
     *
     * @param verticalSplit JSplitPane in die die JTable eingefügt werden soll
     */
    private void initTabelle(JSplitPane verticalSplit) {
        tablePopup = new JPopupMenu();

        popupTabelleOeffnen = new JMenuItem("Öffnen");
        popupTabelleOeffnen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) tblKontakte.getModel();

                int viewZeile = tblKontakte.getSelectedRow();
                if (viewZeile < 0) {
                    return;
                }

                int row = tblKontakte.convertRowIndexToModel(viewZeile);
                Kontakt referenz = (Kontakt) model.getValueAt(row, 0);

                bearbeiteKontakt(referenz);
            }
        });
        tablePopup.add(popupTabelleOeffnen);

        popupTabelleVerfassen = new JMenuItem("Verfassen");
        popupTabelleVerfassen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verfassen(ausgewaehlteKontakte());
            }
        });
        tablePopup.add(popupTabelleVerfassen);

        popupTabelleLoeschen = new JMenuItem("Löschen");
        popupTabelleLoeschen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Kontakt[] kontakte = ausgewaehlteKontakte();
                String liste = aktuelleListe();

                for (Kontakt k : kontakte) {
                    verwaltung.löscheKontakt(k, liste);
                }

                aktualisiereTabelle(liste);
            }
        });
        tablePopup.add(popupTabelleLoeschen);

        popupTabelleListeHinzufügen = new JMenu();
        tablePopup.add(popupTabelleListeHinzufügen);

        tblKontakte = new JTable() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        ;
        };
		tblKontakte.setModel(new DefaultTableModel(new Object[][]{{null, null, null},},
                new String[]{"Referenz", "Name", "E-Mail-Adresse", "Tel. dienstlich"}) {
            private static final long serialVersionUID = 1L;
            Class<?>[] columnTypes = new Class<?>[]{Kontakt.class, String.class, String.class, String.class};

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        });
        tblKontakte.removeColumn(tblKontakte.getColumn("Referenz"));

        tblKontakte.getColumnModel().getColumn(1).setPreferredWidth(91);
        tblKontakte.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblKontakte.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    DefaultListSelectionModel sender = (DefaultListSelectionModel) e.getSource();
                    int row = sender.getMinSelectionIndex();
                    if (row == -1) {
                        return;
                    }

                    DefaultTableModel model = (DefaultTableModel) tblKontakte.getModel();
                    int length = model.getDataVector().size();

                    if (length > 0) {
                        int zeileModel = tblKontakte.convertRowIndexToModel(row);

                        Kontakt referenz = (Kontakt) model.getValueAt(zeileModel, 0);
                        aktualisiereDetails(referenz);
                    } else {
                        txtDetails.setEditable(true);
                        txtDetails.setText(null);
                        txtDetails.setEditable(false);
                    }
                }
            }
        });

        tblKontakte.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultTableModel model = (DefaultTableModel) tblKontakte.getModel();

                    int viewZeile = tblKontakte.getSelectedRow();
                    if (viewZeile < 0) {
                        return;
                    }

                    int row = tblKontakte.convertRowIndexToModel(viewZeile);
                    Kontakt referenz = (Kontakt) model.getValueAt(row, 0);

                    bearbeiteKontakt(referenz);
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

        JScrollPane kontakteScroller = new JScrollPane(tblKontakte);
        verticalSplit.setLeftComponent(kontakteScroller);
    }

    /**
     * Initialisiert die JList des Frames
     *
     * @param horizontalSplit JSplitPane in die die JList eingefügt werden soll
     */
    private void initListen(JSplitPane horizontalSplit) {
        listenPopup = new JPopupMenu();

        popupListenUmbennen = new JMenuItem("Umbennen");
        popupListenUmbennen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listeUmbenennen(aktuelleListe());
            }
        });
        listenPopup.add(popupListenUmbennen);

        popupListenVerfassen = new JMenuItem("Verfassen");
        popupListenVerfassen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Kontakt[] kontakte = verwaltung.getKontakte(aktuelleListe());
                verfassen(kontakte);
            }
        });
        listenPopup.add(popupListenVerfassen);

        popupListenLoeschen = new JMenuItem("Löschen");
        popupListenLoeschen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel<String> model = (DefaultListModel<String>) lstListen.getModel();
                String liste = aktuelleListe();

                verwaltung.löscheListe(liste);
                model.removeElement(liste);

                aktualisiereTabelle(liste);
            }
        });
        listenPopup.add(popupListenLoeschen);

        lstListen = new JList<>(new DefaultListModel<String>());
        lstListen.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                aktualisiereTabelle(aktuelleListe());
            }
        });

        lstListen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String liste = aktuelleListe();

                    if (!Kontaktverwaltung.DEFAULT.equals(liste)) {
                        listeUmbenennen(aktuelleListe());
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                oeffnePopupListen(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                oeffnePopupListen(e);
            }
        });

        JScrollPane listerScroller = new JScrollPane(lstListen);
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

        initTabelle(verticalSplit);

        txtDetails = new JTextPane();
        JScrollPane detailsScroller = new JScrollPane(txtDetails);
        verticalSplit.setRightComponent(detailsScroller);

        initListen(horizontalSplit);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnDatei = new JMenu("Datei");
        menuBar.add(mnDatei);

        JMenu mnNewMenu = new JMenu("Neu");
        mnDatei.add(mnNewMenu);

        mntDateiNeuKontakt = new JMenuItem("Neuer Kontakt");
        mntDateiNeuKontakt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                neuerKontakt();
            }
        });
        mnNewMenu.add(mntDateiNeuKontakt);

        mntDateiNeuListe = new JMenuItem("Neue Kontaktliste");
        mntDateiNeuListe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ListenDialog ld = new ListenDialog();
                String liste = ld.showDialog();

                if (liste != null) {
                    verwaltung.addListe(liste);
                    aktualisiereKontaktlisten();
                }
            }
        });
        mnNewMenu.add(mntDateiNeuListe);

        mntDateiBeenden = new JMenuItem("Beenden");
        mntDateiBeenden.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        mnDatei.add(mntDateiBeenden);
    }

    /**
     * Erstellt eine neue AdressbuchFrame-Instanz
     *
     * @param parent Referenz auf das Vater-Fenster, um darauf ggf. die
     * neueMail-Methode aufzurufen
     * @param neu Wenn true, wird sofort ein neues KontaktFrame geöffnet; sonst
     * nicht
     */
    public AdressbuchFrame(MainFrame parent, boolean neu) {
        setTitle("Adressbuch");
        this.parent = parent;
        verwaltung = Benutzer.getInstanz().getKontakte();

        initGUI();

        aktualisiereKontaktlisten();
        lstListen.setSelectedIndex(0);

        if (neu) {
            neuerKontakt();
        }
    }

    /**
     * Aktualisiert die JList mit den aktuellen Kontaktlisten.
     */
    private void aktualisiereKontaktlisten() {
        String selected = lstListen.getSelectedValue();

        DefaultListModel<String> model = (DefaultListModel<String>) lstListen.getModel();
        model.clear();

        String[] listen = verwaltung.getListen();
        for (String liste : listen) {
            model.addElement(liste);
        }

        lstListen.setSelectedValue(selected, true);

        tablePopup.remove(popupTabelleListeHinzufügen);
        popupTabelleListeHinzufügen = generiereListenmenü();
        if (popupTabelleListeHinzufügen.getMenuComponentCount() > 0) {
            tablePopup.add(popupTabelleListeHinzufügen);
        }
    }

    /**
     * Aktualisiert die JTable mit den Kontakten der übergebenen Kontaktliste.
     *
     * @param liste Gibt an, aus welcher Liste der Kontaktverwaltung die
     * Kontakte geladen werden sollen.
     */
    private void aktualisiereTabelle(String liste) {
        if (liste == null) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblKontakte.getModel();
        model.setRowCount(0);

        Kontakt[] kontakte = verwaltung.getKontakte(liste);
        if (kontakte == null) {
            return;
        }

        for (Kontakt k : kontakte) {
            model.addRow(new Object[]{k, k.getDisplayname(), k.getAddress1(), k.getDutyphone()});
        }

        aktualisiereDetails(null);
    }

    /**
     * Gibt den aktuell ausgewählten Listennamen der JList zurück
     *
     * @return ausgewählter Listenname
     */
    private String aktuelleListe() {
        return (String) lstListen.getSelectedValue();
    }

    /**
     * Aktualisiert den Inhalt der JTextPane mit den Daten des übergebenen
     * Kontakts
     *
     * @param k Kontakt, dessen Daten zum Füllen der Details verwendet werden
     */
    private void aktualisiereDetails(Kontakt k) {
        StringBuilder sb = new StringBuilder();

        if (k != null) {
            if (!k.getForename().trim().isEmpty()) {
                sb.append("Vorname: ").append(k.getForename()).append('\n');
            }
            if (!k.getSurname().trim().isEmpty()) {
                sb.append("Nachname: ").append(k.getSurname()).append('\n');
            }
            if (!k.getDisplayname().trim().isEmpty()) {
                sb.append("Anzeigename: ").append(k.getDisplayname()).append('\n');
            }
            if (!k.getNickname().trim().isEmpty()) {
                sb.append("Spitzname: ").append(k.getNickname()).append('\n');
            }
            if (k.getAddress1() != null) {
                sb.append("E-Mail-Adresse: ").append(k.getAddress1AsString()).append('\n');
            }
            if (k.getAddress2() != null) {
                sb.append("2. E-Mail-Adresse: ").append(k.getAddress2AsString()).append('\n');
            }
            if (!k.getDutyphone().trim().isEmpty()) {
                sb.append("Telefonnummer (dienstlich): ").append(k.getDutyphone()).append('\n');
            }
            if (!k.getPrivatephone().trim().isEmpty()) {
                sb.append("Telefonnummer (privat): ").append(k.getPrivatephone()).append('\n');
            }
            if (!k.getMobilephone().trim().isEmpty()) {
                sb.append("Telefonnummer (mobil): ").append(k.getMobilephone()).append('\n');
            }
        }

        txtDetails.setEditable(true);
        txtDetails.setText(sb.toString());
        txtDetails.setEditable(false);
    }

    /**
     * Öffnet ein neues KontaktFrame zum Erstellen eines neuen Kontaks
     */
    private void neuerKontakt() {
        KontaktFrame kf = new KontaktFrame();
        Kontakt k = kf.showDialog();

        if (k != null) {
            verwaltung.addKontakt(k);
            aktualisiereTabelle(aktuelleListe());
        }
    }

    /**
     * Öffnet ein neues KontaktFrame zum bearbeiten des übergebenen Kontaks
     *
     * @param k Kontakt-Objekt, das im KontaktFrame bearbeitet werden soll.
     */
    private void bearbeiteKontakt(Kontakt k) {
        KontaktFrame kf = new KontaktFrame(k);
        kf.showDialog();

        if (k != null) {
            int zeile = tblKontakte.convertRowIndexToModel(tblKontakte.getSelectedRow());
            aktualisiereTabelle(aktuelleListe());
            int zeileView = tblKontakte.convertRowIndexToView(zeile);
            tblKontakte.setRowSelectionInterval(zeileView, zeileView);
        }
    }

    /**
     * Öffnet einen neuen Listendialog zum Umbennen der übergebenen Liste
     *
     * @param liste Listenname, der umbenannt werden soll
     */
    private void listeUmbenennen(String liste) {
        ListenDialog ld = new ListenDialog(liste);
        String neuerName = ld.showDialog();

        if (neuerName != null) {
            try {
                verwaltung.renameListe(liste, neuerName);
                aktualisiereKontaktlisten();
                lstListen.setSelectedValue(neuerName, true);
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
    private Kontakt[] ausgewaehlteKontakte() {
        Kontakt[] kontakte = new Kontakt[tblKontakte.getSelectedRowCount()];
        int[] indizes = tblKontakte.getSelectedRows();
        DefaultTableModel model = (DefaultTableModel) tblKontakte.getModel();

        for (int i = 0; i < kontakte.length; i++) {
            kontakte[i] = (Kontakt) model.getValueAt(indizes[i], 0);
        }

        return kontakte;
    }

    /**
     * Öffnet ein neues MailFrame über die Instanz des Vaterfensters
     *
     * @param kontakte Kontakte deren 1. Mailadresse automatisch als Empfänger
     * eingetragen werden
     */
    private void verfassen(Kontakt[] kontakte) {
        parent.neueMail(kontakte);
    }

    /**
     * Öffnet das Popup-Menü der JTable
     *
     * @param e Enthält Daten bezüglich des Klicks
     */
    private void oeffnePopupTabelle(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int zeile = tblKontakte.rowAtPoint(e.getPoint());
            int spalte = tblKontakte.columnAtPoint(e.getPoint());

            if (zeile >= 0 && spalte >= 0) {
                // TODO Kontektmenü bei Mehrfachauswahl

                tblKontakte.setRowSelectionInterval(zeile, zeile);

                tablePopup.show(tblKontakte, e.getX(), e.getY());
            }
        }
    }

    /**
     * Öffnet das Popup-Menü der JList
     *
     * @param e Enthält Daten bezüglich des Klicks
     */
    private void oeffnePopupListen(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int zeile = lstListen.locationToIndex(e.getPoint());

            if (zeile >= 0) {
                lstListen.setSelectedIndex(zeile);

                String liste = lstListen.getSelectedValue();
                boolean istAdressbuch = liste.equals(Kontaktverwaltung.DEFAULT);

                popupListenLoeschen.setEnabled(!istAdressbuch);
                popupListenUmbennen.setEnabled(!istAdressbuch);

                listenPopup.show(lstListen, e.getX(), e.getY());
            }
        }
    }

    /**
     * Erstellt ein neues JMenu, das als Elemente die Namen aller Einträge der
     * JList ausgenommen des Standardadressbuchs enthält
     *
     * @return Neues JMenu mit den Kontaktlisten
     */
    private JMenu generiereListenmenü() {
        JMenu menu = new JMenu("Zu Liste zuordnen");
        DefaultListModel<String> model = (DefaultListModel<String>) lstListen.getModel();

        for (int i = 0; i < model.getSize(); i++) {
            String item = model.get(i);
            if (!Kontaktverwaltung.DEFAULT.equals(item)) {
                JMenuItem menuItem = new JMenuItem(item);
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        String titel = ((JMenuItem) arg0.getSource()).getText();
                        listeHinzufuegen(ausgewaehlteKontakte(), titel);
                    }
                });
                menu.add(menuItem);
            }
        }

        return menu;
    }

    /**
     * Fügt die übergebenen Kontakte in die übergebene Liste der
     * Kontaktverwaltung ein
     *
     * @param kontakte Einzufügende Kontakte
     * @param liste Liste, in die die Kontakte eingefügt werden sollen
     */
    private void listeHinzufuegen(Kontakt[] kontakte, String liste) {
        for (Kontakt k : kontakte) {
            try {
                verwaltung.addKontaktZuListe(k, liste);
            } catch (IllegalArgumentException ex) {
                // Ignoriere Fehler
            }
        }
    }
}
