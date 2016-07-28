package de.outlookklon.logik.calendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import lombok.NonNull;
import org.joda.time.DateTime;

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
    public void addAppointment(@NonNull Appointment appointment) {
        mAppointments.add(appointment);
    }

    /**
     * Löscht den übergebenen Appointment aus der Verwaltung
     *
     * @param appointment Zu löschender Appointment
     */
    public void deleteAppointment(@NonNull Appointment appointment) {
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
    public boolean isOverlapping(@NonNull Appointment a) {
        DateTime startA = a.getStart();
        DateTime endA = a.getEnd();

        for (Appointment b : mAppointments) {
            DateTime startB = b.getStart();
            DateTime endB = b.getEnd();
            // IF-Abfrage des Todes
            if (isDateBetween(startB, startA, endA) || isDateBetween(endB, startA, endA)
                    || isDateBetween(startA, startB, endB) || isDateBetween(endA, startB, endB)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDateBetween(DateTime toTest, DateTime start, DateTime end) {
        return start.isBefore(toTest) && end.isAfter(toTest);
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
            if (appointment.getStart().isBefore(oldest.getStart())) {
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
    public Appointment[] getAppointments(@NonNull DateTime start, @NonNull DateTime end) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Der Startzeitpunkt darf nicht hinter dem Endzeitpunkt liegen");
        }

        List<Appointment> list = new ArrayList<>();
        for (Appointment appointment : mAppointments) {
            DateTime startZeit = appointment.getStart();
            if (appointment.getState() != AppointmentState.REJECTED
                    && (start.equals(startZeit) || (startZeit.isAfter(start) && startZeit.isBefore(end)))) {
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
        DateTime now = new DateTime(new Date());
        DateTime start = now.withTimeAtStartOfDay();
        DateTime end = start.plusDays(1);

        return getAppointments(start, end);
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
