package de.outlookklon.gui.frames;

import de.outlookklon.gui.components.ReadOnlyJTable;
import de.outlookklon.gui.dialogs.AppointmentFrame;
import de.outlookklon.gui.helpers.Events;
import de.outlookklon.localization.Localization;
import de.outlookklon.logik.User;
import de.outlookklon.logik.calendar.Appointment;
import de.outlookklon.logik.calendar.AppointmentCalendar;
import de.outlookklon.logik.mailclient.MailAccount;
import de.outlookklon.logik.mailclient.checker.MailAccountChecker;
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
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Dieses Fenster zeigt den AppointmentCalendar des Benutzers an.
 *
 * @author Hendrik Karwanni
 */
public class AppointmentCalendarFrame extends ExtendedFrame {

    private static final long serialVersionUID = 1L;

    private static final String APPOINTMENT_TABLE_REF_COLUMN_NAME = "Reference";
    private static final int TABLE_COLUMN_INDEX_SUBJECT = 0;
    private static final int TABLE_COLUMN_INDEX_DESCRIPTION = 1;
    private static final int TABLE_COLUMN_INDEX_DATE = 2;

    private static final DateTimeFormatter DATETIMEFORMAT = DateTimeFormat.mediumDateTime();

    private final JTable tblAppointments;
    private final JTextPane textDetails;
    private final AppointmentCalendar calendar;

    private final JPopupMenu appointmentPopup;
    private final JMenu mnFile;
    private final JMenuItem mntmAppointmentAdd;
    private final JMenuItem mntmClose;
    private final JMenuItem popupAppointmentOpen;
    private final JMenuItem popupAppointmentDelete;
    private final JMenuItem popupAppointmentNew;

    private final List<Appointment> hiddenAppointments;
    private final List<Appointment> allAppointments;

    private final JPanel panel;

    private AppointmentCalendarFrame() {
        calendar = User.getInstance().getAppointments();

        tblAppointments = new ReadOnlyJTable();
        textDetails = new JTextPane();

        appointmentPopup = new JPopupMenu();
        popupAppointmentOpen = new JMenuItem();
        popupAppointmentNew = new JMenuItem();
        popupAppointmentDelete = new JMenuItem();
        mnFile = new JMenu();
        mntmAppointmentAdd = new JMenuItem();
        mntmClose = new JMenuItem();

        hiddenAppointments = new ArrayList<>();
        allAppointments = new ArrayList<>();

        panel = new JPanel();
    }

    /**
     * Erstellt ein neues Fenster.
     *
     * @param newAppointment bei {@code true} wird das Fenster zum Erstellen
     * eines neuen Termins mitgestartet
     */
    public AppointmentCalendarFrame(boolean newAppointment) {
        this();

        initGui();
        updateTexts();

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
        this();

        initGui();
        updateTexts();

        newAppointment(start);
    }

    @Override
    public void updateTexts() {
        setTitle(Localization.getString("AppointmentCalendarFrame_Title"));

        popupAppointmentOpen.setText(Localization.getString("Menu_Open"));
        popupAppointmentNew.setText(Localization.getString("AddressBookFrame_Menu_Create"));
        popupAppointmentDelete.setText(Localization.getString("Menu_Delete"));
        mnFile.setText(Localization.getString("Menu_File"));
        mntmAppointmentAdd.setText(Localization.getString("AppointmentCalendarFrame_AddAppointment"));
        mntmClose.setText(Localization.getString("Menu_Close"));

        TableColumnModel tableColumnModel = tblAppointments.getColumnModel();
        tableColumnModel.getColumn(TABLE_COLUMN_INDEX_SUBJECT).setHeaderValue(Localization.getString("Appointment_Subject"));
        tableColumnModel.getColumn(TABLE_COLUMN_INDEX_DESCRIPTION).setHeaderValue(Localization.getString("Appointment_Description"));
        tableColumnModel.getColumn(TABLE_COLUMN_INDEX_DATE).setHeaderValue(Localization.getString("Appointment_Date"));

        int row = tblAppointments.getSelectionModel().getMinSelectionIndex();
        updateTable();
        tblAppointments.getSelectionModel().setSelectionInterval(row, row);

        repaint();
    }

    private TableModel getTableModel() {
        return new DefaultTableModel(new Object[][]{},
                new String[]{
                    APPOINTMENT_TABLE_REF_COLUMN_NAME,
                    StringUtils.EMPTY,
                    StringUtils.EMPTY,
                    StringUtils.EMPTY
                }
        );
    }

    private void initGui() {
        initMenus();

        getContentPane().setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane = new JSplitPane();

        JScrollPane scrollPane = new JScrollPane(panel);
        panel.setLayout(new GridLayout(0, 1, 0, 0));
        splitPane.setLeftComponent(scrollPane);

        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setRightComponent(splitPane1);

        tblAppointments.setModel(getTableModel());
        tblAppointments.removeColumn(tblAppointments.getColumn(APPOINTMENT_TABLE_REF_COLUMN_NAME));
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
                if (Events.isDoubleClick(e)) {
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

        // speichert alle existierenden Termine in mango ab
        for (Appointment appointment : User.getInstance().getAppointments()) {
            allAppointments.add(appointment);
        }

        final JScrollPane scrollPane1 = new JScrollPane(tblAppointments);
        splitPane1.setLeftComponent(scrollPane1);

        splitPane1.setRightComponent(textDetails);
        getContentPane().add(splitPane);

        loadUser();
    }

    private void initMenus() {
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

        popupAppointmentNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newAppointment();
            }
        });
        appointmentPopup.add(popupAppointmentNew);

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

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(mnFile);

        mntmAppointmentAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newAppointment();
            }
        });
        mnFile.add(mntmAppointmentAdd);

        mntmClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        mnFile.add(mntmClose);
    }

    private void updateTable() {
        DefaultTableModel model = (DefaultTableModel) tblAppointments.getModel();
        model.setRowCount(0);

        AppointmentCalendar onewayCalendar = new AppointmentCalendar();

        for (int i = 0; i < allAppointments.size(); i++) {
            if (!hiddenAppointments.isEmpty()) {
                if (!hiddenAppointments.contains(allAppointments.get(i))) {
                    onewayCalendar.addAppointment(allAppointments.get(i));
                }
            } else {
                onewayCalendar.addAppointment(allAppointments.get(i));
            }
        }

        int count = onewayCalendar.getSize();

        DateTimeFormatter localizedFormatter = getLocalizedFormatter();
        for (int i = 0; i < count; i++) {
            Appointment a = onewayCalendar.getOldest();
            model.addRow(new Object[]{a, a.getSubject(), a.getText(), localizedFormatter.print(a.getStart())});
            onewayCalendar.deleteAppointment(a);
        }
    }

    private DateTimeFormatter getLocalizedFormatter() {
        return DATETIMEFORMAT.withLocale(Localization.getLocale());
    }

    private void updateDetails(Appointment appointment) {
        StringBuilder sb = new StringBuilder();

        if (appointment != null) {
            DateTimeFormatter localizedFormatter = getLocalizedFormatter();

            if (!appointment.getSubject().trim().isEmpty()) {
                sb.append(Localization.getString("Appointment_Subject")).append(": ").append(appointment.getSubject()).append('\n');
            }
            if (!appointment.getLocation().trim().isEmpty()) {
                sb.append(Localization.getString("Appointment_Location")).append(": ").append(appointment.getLocation()).append('\n');
            }
            if (!appointment.getStart().toString().trim().isEmpty()) {
                sb.append(Localization.getString("Appointment_Start")).append(": ").append(localizedFormatter.print(appointment.getStart())).append('\n');
            }
            if (!appointment.getEnd().toString().trim().isEmpty()) {
                sb.append(Localization.getString("Appointment_End")).append(": ").append(localizedFormatter.print(appointment.getEnd())).append('\n');
            }
            if (!appointment.getText().trim().isEmpty()) {
                sb.append(Localization.getString("Appointment_Info")).append(": ").append(appointment.getText()).append('\n');
            }
        }

        textDetails.setEditable(true);
        textDetails.setText(sb.toString());
        textDetails.setEditable(false);
    }

    private void newAppointment() {
        AppointmentFrame appointmentFrame = new AppointmentFrame(this);
        Appointment dummy = appointmentFrame.showDialog();

        if (dummy != null) {
            calendar.addAppointment(dummy);
            allAppointments.add(dummy);
            if (calendar.isOverlapping(dummy)) {
                JOptionPane.showMessageDialog(this,
                        Localization.getString("AppointmentCalendarFrame_OverlappingWarning"),
                        Localization.getString("Dialog_Warning"), JOptionPane.WARNING_MESSAGE);
            }
            updateTable();
        }
    }

    private void newAppointment(Date date) {
        AppointmentFrame appointmentFrame = new AppointmentFrame(this, date);
        Appointment dummy = appointmentFrame.showDialog();

        if (dummy != null) {
            calendar.addAppointment(dummy);
            allAppointments.add(dummy);
            if (calendar.isOverlapping(dummy)) {
                JOptionPane.showMessageDialog(this,
                        Localization.getString("AppointmentCalendarFrame_OverlappingWarning"),
                        Localization.getString("Dialog_Warning"), JOptionPane.WARNING_MESSAGE);
            }
            updateTable();
        }
    }

    private void editAppointment(Appointment appointment) {
        AppointmentFrame appointmentFrame = new AppointmentFrame(this, appointment);
        appointmentFrame.showDialog();

        int row = tblAppointments.convertRowIndexToModel(tblAppointments.getSelectedRow());
        updateTable();
        int rowView = tblAppointments.convertRowIndexToView(row);
        tblAppointments.setRowSelectionInterval(rowView, rowView);

    }

    private void loadUser() {
        // speichert alle Konten in apfel
        List<String> usernames = new ArrayList<>();
        for (MailAccountChecker checker : User.getInstance()) {
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
