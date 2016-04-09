package de.outlookklon.logik.calendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

/**
 * Diese Klasse stellt die Verwaltung für die Termine des Benutzers dar
 *
 * @author Hendrik Karwanni
 */
public class AppointmentCalendar implements Iterable<Appointment> {

    @JsonProperty("appointments")
    private final List<Appointment> mAppointments;

    /**
     * Erstellt eine neue Instanz der Terminverwaltung
     */
    public AppointmentCalendar() {
        this(new ArrayList<Appointment>());
    }

    @JsonCreator
    private AppointmentCalendar(
            @JsonProperty("appointments") List<Appointment> appointments) {
        this.mAppointments = appointments;
    }

    @Override
    public Iterator<Appointment> iterator() {
        return mAppointments.iterator();
    }

    /**
     * Fügt den übergebenen Appointment der Verwaltung hinzu
     *
     * @param appointment Der hinzuzufügende Appointment
     */
    public void addAppointment(Appointment appointment) {
        mAppointments.add(appointment);
    }

    /**
     * Löscht den übergebenen Appointment aus der Verwaltung
     *
     * @param appointment Zu löschender Appointment
     */
    public void deleteAppointment(Appointment appointment) {
        mAppointments.remove(appointment);
    }

    /**
     * Gibt zurück, ob sich die Termine der Verwaltung mit dem übergebenen
     * Appointment überschneiden
     *
     * @param a Zu vergleichender Appointment
     * @return true, wenn sich mindestens ein Appointment überschneidet; sonst
     * false
     */
    public boolean isOverlapping(Appointment a) {
        Date startA = a.getStart();
        Date endA = a.getEnd();

        for (Appointment b : mAppointments) {
            Date startB = b.getStart();
            Date endB = b.getEnd();
            // IF-Abfrage des Todes
            if ((startA.before(startB) && endA.after(startB)) || (startA.before(endB) && endA.after(endB))
                    || (startB.before(startA) && endB.after(startA)) || (startB.before(endA) && endB.after(endA))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt den Appointment zurück, der am ehesten beginnt
     *
     * @return Appointment-Objekt, das zeitlich am ehesten beginnt
     */
    @JsonIgnore
    public Appointment getOldest() {
        if (mAppointments.isEmpty()) {
            return null;
        }

        Appointment oldest = mAppointments.get(0);

        for (Appointment appointment : mAppointments) {
            if (appointment.getStart().before(oldest.getStart())) {
                oldest = appointment;
            }
        }
        return oldest;
    }

    /**
     * Gibt die Anzahl der Termine der Verwaltung zurück
     *
     * @return Anzahl der Termine
     */
    @JsonIgnore
    public int getSize() {
        return mAppointments.size();
    }

    /**
     * Gibt alle Termine in der übergebenen Zeitspanne zurück
     *
     * @param start Startzeit der Auswertung
     * @param end Endzeit der Auswertung
     * @return Termine innerhalb des intervalls
     */
    public Appointment[] getAppointments(Date start, Date end) {
        if (end.before(start)) {
            throw new IllegalArgumentException("Der Startzeitpunkt darf nicht hinter dem Endzeitpunkt liegen");
        }

        List<Appointment> list = new ArrayList<>();
        for (Appointment appointment : mAppointments) {
            Date startZeit = appointment.getStart();
            if (appointment.getState() != AppointmentState.REJECTED
                    && (start.equals(startZeit) || (startZeit.after(start) && startZeit.before(end)))) {
                list.add(appointment);
            }
        }
        return list.toArray(new Appointment[list.size()]);
    }

    /**
     * Gibt alle Termine des aktuellen Tages zurück
     *
     * @return Termine des aktuellen Tages
     */
    @JsonIgnore
    public Appointment[] getAppointments() {
        Date now = new Date();

        GregorianCalendar c = new GregorianCalendar();

        c.setTime(now);

        // Setzt den Eintrag der Stunden auf 0
        c.set(Calendar.HOUR, 0);

        // Setzt den Eintrag der Minuten auf 0
        c.set(Calendar.MINUTE, 0);

        // Setzt den Eintrag der Sekunden auf 0
        c.set(Calendar.SECOND, 0);

        // Übergebener Tag mit der Uhrzeit 00:00:00
        Date start = c.getTime();
        c.add(Calendar.DAY_OF_YEAR, 1);
        Date end = c.getTime(); // Tag um 1 höher als time1

        return AppointmentCalendar.this.getAppointments(start, end);
    }

    /**
     * Entfernt alle Termine aus der Verwaltung, die am aktuellen Tag
     * stattfinden
     */
    public void cancel() {
        for (Appointment appointment : getAppointments()) {
            appointment.setState(AppointmentState.REJECTED);
        }
    }
}
