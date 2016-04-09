package de.outlookklon.gui;

import de.outlookklon.logik.User;
import de.outlookklon.logik.User.MailChecker;
import de.outlookklon.logik.calendar.Appointment;
import de.outlookklon.logik.calendar.AppointmentCalendar;
import de.outlookklon.logik.mailclient.MailAccount;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 * Dieses Fenster zeigt den AppointmentCalendar des Benutzers an.
 *
 * @author Hendrik Karwanni
 */
public class AppointmentCalendarFrame extends ExtendedFrame {

    private static final long serialVersionUID = 1L;

    private JTable tblAppointments;
    private JTextPane textDetails;
    private AppointmentCalendar calendar;

    private JPopupMenu appointmentPopup;
    private JMenuItem popupAppointmentOpen;
    private JMenuItem popupAppointmentDelete;
    private JMenuItem popupAppointmentNew;

    private List<Appointment> hiddenAppointments;
    private List<Appointment> allAppointments;

    private JPanel panel;

    private void initGui() {
        setTitle("Termine");
        appointmentPopup = new JPopupMenu();

        popupAppointmentOpen = new JMenuItem("Öffnen");
        popupAppointmentOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) tblAppointments.getModel();

                int viewrow = tblAppointments.getSelectedRow();
                if (viewrow < 0) {
                    return;
                }

                int row = tblAppointments.convertRowIndexToModel(viewrow);
                Appointment reference = (Appointment) model.getValueAt(row, 0);

                editAppointment(reference);
            }
        });
        appointmentPopup.add(popupAppointmentOpen);

        popupAppointmentNew = new JMenuItem("Verfassen");
        popupAppointmentNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newAppointment();
            }
        });
        appointmentPopup.add(popupAppointmentNew);

        popupAppointmentDelete = new JMenuItem("Löschen");
        popupAppointmentDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Appointment[] appointments = selectedAppointments();

                for (Appointment appointment : appointments) {
                    calendar.deleteAppointment(appointment);
                    allAppointments.remove(appointment);
                }
                updateTable();
            }
        });

        appointmentPopup.add(popupAppointmentDelete);

        calendar = User.getInstance().getAppointments();

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu mnFile = new JMenu("Datei");
        menuBar.add(mnFile);

        JMenuItem mntmAppointmentAdd = new JMenuItem("Termin hinzufügen");
        mntmAppointmentAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newAppointment();
            }
        });
        mnFile.add(mntmAppointmentAdd);

        JMenuItem mntmClose = new JMenuItem("Beenden");
        mntmClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        mnFile.add(mntmClose);
        getContentPane().setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane = new JSplitPane();

        panel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(panel);
        panel.setLayout(new GridLayout(0, 1, 0, 0));
        splitPane.setLeftComponent(scrollPane);

        final JSplitPane splitPane_1 = new JSplitPane();
        splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setRightComponent(splitPane_1);

        tblAppointments = new JTable() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblAppointments.setModel(new DefaultTableModel(new Object[][]{},
                new String[]{"Referenz", "Betreff", "Beschreibung", "Datum"}));

        tblAppointments.removeColumn(tblAppointments.getColumn("Referenz"));
        tblAppointments.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    DefaultListSelectionModel sender = (DefaultListSelectionModel) e.getSource();
                    int row = sender.getMinSelectionIndex();
                    if (row == -1) {
                        return;
                    }

                    DefaultTableModel model = (DefaultTableModel) tblAppointments.getModel();
                    int length = model.getDataVector().size();

                    if (length > 0) {
                        int rowModel = tblAppointments.convertRowIndexToModel(row);

                        Appointment reference = (Appointment) model.getValueAt(rowModel, 0);
                        updateDetails(reference);
                    } else {
                        textDetails.setEditable(true);
                        textDetails.setText(null);
                        textDetails.setEditable(false);
                    }
                }

            }

        });

        tblAppointments.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultTableModel model = (DefaultTableModel) tblAppointments.getModel();

                    int viewRow = tblAppointments.getSelectedRow();
                    if (viewRow < 0) {
                        return;
                    }

                    int row = tblAppointments.convertRowIndexToModel(viewRow);
                    Appointment reference = (Appointment) model.getValueAt(row, 0);

                    editAppointment(reference);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                openAppointmentPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                openAppointmentPopup(e);
            }

        });

        hiddenAppointments = new ArrayList<>();

        allAppointments = new ArrayList<>(); // speichert alle existierenden
        // Termine in mango ab
        for (Appointment appointment : User.getInstance().getAppointments()) {
            allAppointments.add(appointment);
        }

        final JScrollPane scrollPane_1 = new JScrollPane(tblAppointments);
        splitPane_1.setLeftComponent(scrollPane_1);

        textDetails = new JTextPane();
        splitPane_1.setRightComponent(textDetails);
        getContentPane().add(splitPane);

        updateTable();
        loadUser();
    }

    /**
     * Erstellt ein neues Fenster.
     *
     * @param newAppointment bei {@code true} wird das Fenster zum Erstellen
     * eines neuen Termins mitgestartet
     */
    public AppointmentCalendarFrame(boolean newAppointment) {
        initGui();

        if (newAppointment) {
            newAppointment();
        }
    }

    /**
     * Erstellt ein neues Fenster.
     *
     * @param start das Datum, das auf dem automatisch startenden Fenster zum
     * Erstellen eines neuen Termins angezeigt wird
     */
    public AppointmentCalendarFrame(Date start) {
        this(false);
        newAppointment(start);
    }

    private void updateTable() {
        DefaultTableModel model = (DefaultTableModel) tblAppointments.getModel();
        model.setRowCount(0);

        AppointmentCalendar onewayCalendar = new AppointmentCalendar();

        for (int i = 0; i < allAppointments.size(); i++) {
            if (hiddenAppointments.size() > 0) {
                if (!hiddenAppointments.contains(allAppointments.get(i))) {
                    onewayCalendar.addAppointment(allAppointments.get(i));
                }
            } else {
                onewayCalendar.addAppointment(allAppointments.get(i));
            }
        }

        int count = onewayCalendar.getSize();
        for (int i = 0; i < count; i++) {
            Appointment a = onewayCalendar.getOldest();
            model.addRow(new Object[]{a, a.getSubject(), a.getText(), a.getStart().toString()});
            onewayCalendar.deleteAppointment(a);
        }
    }

    private void updateDetails(Appointment appointment) {
        StringBuilder sb = new StringBuilder();

        if (appointment != null) {

            if (!appointment.getSubject().trim().isEmpty()) {
                sb.append("Betreff: ").append(appointment.getSubject()).append('\n');
            }
            if (!appointment.getLocation().trim().isEmpty()) {
                sb.append("Ort: ").append(appointment.getLocation()).append('\n');
            }
            if (!appointment.getStart().toString().trim().isEmpty()) {
                sb.append("Startzeit: ").append(appointment.getStart().toString()).append('\n');
            }
            if (!appointment.getEnd().toString().trim().isEmpty()) {
                sb.append("Ende: ").append(appointment.getEnd().toString()).append('\n');
            }
            if (!appointment.getText().trim().isEmpty()) {
                sb.append("Info: ").append(appointment.getText()).append('\n');
            }
        }

        textDetails.setEditable(true);
        textDetails.setText(sb.toString());
        textDetails.setEditable(false);
    }

    private void newAppointment() {
        AppointmentFrame appointmentFrame = new AppointmentFrame();
        Appointment dummy = appointmentFrame.showDialog();

        if (dummy != null) {
            calendar.addAppointment(dummy);
            allAppointments.add(dummy);
            if (calendar.isOverlapping(dummy)) {
                JOptionPane.showMessageDialog(this,
                        "ACHTUNG! Überschneidung mit bereits vorhandenem Termin. Evtl. Sollten Sie ihre Termine überprüfen.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
            updateTable();
        }
    }

    private void newAppointment(Date date) {
        AppointmentFrame appointmentFrame = new AppointmentFrame(date);
        Appointment dummy = appointmentFrame.showDialog();

        if (dummy != null) {
            calendar.addAppointment(dummy);
            allAppointments.add(dummy);
            if (calendar.isOverlapping(dummy)) {
                JOptionPane.showMessageDialog(this,
                        "ACHTUNG! Überschneidung mit bereits vorhandenem Termin. Evtl. Sollten Sie ihre Termine überprüfen.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
            updateTable();
        }
    }

    private void editAppointment(Appointment appointment) {
        AppointmentFrame appointmentFrame = new AppointmentFrame(appointment);
        appointmentFrame.showDialog();

        int row = tblAppointments.convertRowIndexToModel(tblAppointments.getSelectedRow());
        updateTable();
        int rowView = tblAppointments.convertRowIndexToView(row);
        tblAppointments.setRowSelectionInterval(rowView, rowView);

    }

    private void loadUser() {
        // speichert alle Konten in apfel
        List<String> usernames = new ArrayList<>();
        for (MailChecker checker : User.getInstance()) {
            MailAccount ma = checker.getAccount();
            usernames.add(ma.getUser());
        }

        for (String username : usernames) {
            JCheckBox cb = new JCheckBox(username);

            cb.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent arg0) {
                    JCheckBox cb = (JCheckBox) arg0.getSource();
                    String text = cb.getText();

                    if (arg0.getStateChange() == ItemEvent.SELECTED) {
                        List<Appointment> temp = new ArrayList<>();
                        for (Appointment appointment : hiddenAppointments) {
                            String user = appointment.getUser();
                            if (text.equals(user)) {
                                temp.add(appointment);
                            }
                        }
                        for (Appointment appointment : temp) {
                            hiddenAppointments.remove(appointment);
                        }

                    } else {
                        for (Appointment appointment : allAppointments) {
                            String user = appointment.getUser();
                            if (text.equals(user)) {
                                hiddenAppointments.add(appointment);
                            }
                        }

                    }
                    updateTable();
                }
            });

            cb.setSelected(true);
            panel.add(cb);
            panel.revalidate();
            panel.repaint();
        }
    }

    private Appointment[] selectedAppointments() {
        Appointment[] appointments = new Appointment[tblAppointments.getSelectedRowCount()];
        int[] indices = tblAppointments.getSelectedRows();
        DefaultTableModel model = (DefaultTableModel) tblAppointments.getModel();

        for (int i = 0; i < appointments.length; i++) {
            appointments[i] = (Appointment) model.getValueAt(indices[i], 0);
        }

        return appointments;
    }

    private void openAppointmentPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int row = tblAppointments.rowAtPoint(e.getPoint());
            int column = tblAppointments.columnAtPoint(e.getPoint());

            if (row >= 0 && column >= 0) {
                tblAppointments.setRowSelectionInterval(row, row);

                appointmentPopup.show(tblAppointments, e.getX(), e.getY());
            }
        }
    }
}
