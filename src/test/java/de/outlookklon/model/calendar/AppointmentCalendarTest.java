package de.outlookklon.model.calendar;

import de.outlookklon.model.calendar.Appointment;
import de.outlookklon.model.calendar.AppointmentCalendar;
import de.outlookklon.application.User;
import de.outlookklon.model.contacts.ContactManagement;
import java.util.Iterator;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(User.class)
public class AppointmentCalendarTest {

    private AppointmentCalendar calendar;

    @Before
    public void setUp() throws Exception {
        ContactManagement contacts = new ContactManagement();

        User user = PowerMockito.mock(User.class);
        PowerMockito.mockStatic(User.class);
        PowerMockito.when(User.getInstance()).thenReturn(user);
        when(user.getContacts()).thenReturn(contacts);

        calendar = new AppointmentCalendar();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAddAppointment_IsNull() {
        calendar.addAppointment(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotDeleteAppointment_IsNull() {
        calendar.deleteAppointment(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotFindOverlappingAppointments_IsNull() {
        calendar.isOverlapping(null);
    }

    @Test
    public void shouldFindOverlappingAppointments_EndABetweenB() {
        DateTime startA = new DateTime();
        DateTime endA = startA.plusHours(2);
        DateTime startB = startA.plusHours(1);
        DateTime endB = startA.plusHours(3);

        Appointment appointmentA = new Appointment(null, null, startA,
                endA, null, null, null);
        Appointment appointmentB = new Appointment(null, null, startB,
                endB, null, null, null);

        calendar.addAppointment(appointmentA);
        assertThat(calendar.isOverlapping(appointmentB), is(true));
    }

    @Test
    public void shouldFindOverlappingAppointments_StartABetweenB() {
        DateTime startA = new DateTime();
        DateTime endA = startA.plusHours(2);
        DateTime startB = startA.minusHours(1);
        DateTime endB = startA.plusHours(1);

        Appointment appointmentA = new Appointment(null, null, startA,
                endA, null, null, null);
        Appointment appointmentB = new Appointment(null, null, startB,
                endB, null, null, null);

        calendar.addAppointment(appointmentA);
        assertThat(calendar.isOverlapping(appointmentB), is(true));
    }

    @Test
    public void shouldFindOverlappingAppointments_EndBBetweenA() {
        DateTime startB = new DateTime();
        DateTime endB = startB.plusHours(2);
        DateTime startA = startB.plusHours(1);
        DateTime endA = startB.plusHours(3);

        Appointment appointmentA = new Appointment(null, null, startA,
                endA, null, null, null);
        Appointment appointmentB = new Appointment(null, null, startB,
                endB, null, null, null);

        calendar.addAppointment(appointmentA);
        assertThat(calendar.isOverlapping(appointmentB), is(true));
    }

    @Test
    public void shouldFindOverlappingAppointments_StartBBetweenA() {
        DateTime startB = new DateTime();
        DateTime endB = startB.plusHours(2);
        DateTime startA = startB.minusHours(1);
        DateTime endA = startB.plusHours(1);

        Appointment appointmentA = new Appointment(null, null, startA,
                endA, null, null, null);
        Appointment appointmentB = new Appointment(null, null, startB,
                endB, null, null, null);

        calendar.addAppointment(appointmentA);
        assertThat(calendar.isOverlapping(appointmentB), is(true));
    }

    @Test
    public void shouldNotFindOverlappingAppointments_ABeforeB() {
        DateTime startA = new DateTime();
        DateTime endA = startA.plusHours(1);
        DateTime startB = startA.plusHours(2);
        DateTime endB = startA.plusHours(3);

        Appointment appointmentA = new Appointment(null, null, startA,
                endA, null, null, null);
        Appointment appointmentB = new Appointment(null, null, startB,
                endB, null, null, null);

        calendar.addAppointment(appointmentA);
        assertThat(calendar.isOverlapping(appointmentB), is(false));
    }

    @Test
    public void shouldNotFindOverlappingAppointments_AAfterB() {
        DateTime startB = new DateTime();
        DateTime endB = startB.plusHours(1);
        DateTime startA = startB.plusHours(2);
        DateTime endA = startB.plusHours(3);

        Appointment appointmentA = new Appointment(null, null, startA,
                endA, null, null, null);
        Appointment appointmentB = new Appointment(null, null, startB,
                endB, null, null, null);

        calendar.addAppointment(appointmentA);
        assertThat(calendar.isOverlapping(appointmentB), is(false));
    }

    @Test
    public void shouldGetOldestAppointment() {

        Appointment appointmentA = new Appointment(null, null, new DateTime(2014, 10, 4, 20, 15, 00),
                new DateTime(2014, 10, 10, 20, 15, 00), null, null, null);
        Appointment appointmentB = new Appointment(null, null, new DateTime(2014, 10, 5, 10, 00, 00),
                new DateTime(2014, 10, 10, 10, 00, 00), null, null, null);
        Appointment appointmentC = new Appointment(null, null, new DateTime(2014, 10, 6, 20, 15, 00),
                new DateTime(2014, 10, 10, 20, 15, 00), null, null, null);

        calendar.addAppointment(appointmentC);
        calendar.addAppointment(appointmentA);
        calendar.addAppointment(appointmentB);

        assertThat(calendar.getOldest(), is(appointmentA));
    }

    @Test
    public void shouldNotGetOldestAppointment_NoAppointments() {
        assertThat(calendar.getOldest(), is(nullValue()));
    }

    @Test
    public void shouldGetAppointments() {
        Appointment appointmentA = new Appointment(null, null, new DateTime(2014, 10, 4, 20, 15, 00),
                new DateTime(2014, 10, 10, 20, 15, 00), null, null, null);
        Appointment appointmentB = new Appointment(null, null, new DateTime(2014, 10, 5, 10, 00, 00),
                new DateTime(2014, 10, 10, 10, 00, 00), null, null, null);
        Appointment appointmentC = new Appointment(null, null, new DateTime(2014, 10, 6, 20, 15, 00),
                new DateTime(2014, 10, 10, 20, 15, 00), null, null, null);
        Appointment appointmentD = new Appointment(null, null, new DateTime(2014, 10, 7, 20, 15, 00),
                new DateTime(2014, 10, 10, 20, 15, 00), null, null, null);
        Appointment appointmentE = new Appointment(null, null, new DateTime(2014, 10, 8, 10, 00, 00),
                new DateTime(2014, 10, 10, 10, 00, 00), null, null, null);
        Appointment appointmentF = new Appointment(null, null, new DateTime(2014, 10, 9, 20, 15, 00),
                new DateTime(2014, 10, 10, 20, 15, 00), null, null, null);

        calendar.addAppointment(appointmentA);
        calendar.addAppointment(appointmentB);
        calendar.addAppointment(appointmentC);
        calendar.addAppointment(appointmentD);
        calendar.addAppointment(appointmentE);
        calendar.addAppointment(appointmentF);

        Appointment[] expected = new Appointment[]{appointmentB, appointmentC, appointmentD};
        Appointment[] actual = calendar.getAppointments(new DateTime(2014, 10, 5, 1, 00, 00),
                new DateTime(2014, 10, 7, 23, 59, 59));

        assertThat(actual, is(expected));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotGetAppointments_StartDateIsNull() {
        calendar.getAppointments(null, new DateTime());
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotGetAppointments_EndDateIsNull() {
        calendar.getAppointments(new DateTime(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGetAppointments_StartAfterEnd() {
        calendar.getAppointments(new DateTime(2014, 10, 7, 23, 59, 59), new DateTime(2014, 10, 5, 1, 00, 00));
    }

    @Test
    public void shouldIterateAppointments() {
        Appointment appointmentA = new Appointment(null, null, new DateTime(2014, 10, 4, 20, 15, 00),
                new DateTime(2014, 10, 10, 20, 15, 00), null, null, null);
        Appointment appointmentB = new Appointment(null, null, new DateTime(2014, 10, 5, 10, 00, 00),
                new DateTime(2014, 10, 10, 10, 00, 00), null, null, null);
        Appointment appointmentC = new Appointment(null, null, new DateTime(2014, 10, 6, 20, 15, 00),
                new DateTime(2014, 10, 10, 20, 15, 00), null, null, null);

        calendar.addAppointment(appointmentA);
        calendar.addAppointment(appointmentB);
        calendar.addAppointment(appointmentC);

        assertThat(calendar.getSize(), is(3));

        Iterator<Appointment> iterator = calendar.iterator();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(appointmentA));
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(appointmentB));
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(appointmentC));
        assertThat(iterator.hasNext(), is(false));
    }

    @Test
    public void shouldDeleteAppointment() {
        Appointment appointmentA = new Appointment(null, null, new DateTime(2014, 10, 4, 20, 15, 00),
                new DateTime(2014, 10, 10, 20, 15, 00), null, null, null);
        Appointment appointmentB = new Appointment(null, null, new DateTime(2014, 10, 5, 10, 00, 00),
                new DateTime(2014, 10, 10, 10, 00, 00), null, null, null);
        Appointment appointmentC = new Appointment(null, null, new DateTime(2014, 10, 6, 20, 15, 00),
                new DateTime(2014, 10, 10, 20, 15, 00), null, null, null);

        calendar.addAppointment(appointmentA);
        calendar.addAppointment(appointmentB);
        calendar.addAppointment(appointmentC);

        assertThat(calendar.getSize(), is(3));
        calendar.deleteAppointment(appointmentB);
        assertThat(calendar.getSize(), is(2));

        Appointment[] expected = new Appointment[]{appointmentA, appointmentC};
        Appointment[] actual = calendar.getAppointments(new DateTime(2014, 10, 4, 20, 15, 00),
                new DateTime(2014, 10, 10, 20, 15, 00));

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldCancelAppointments() {
        DateTime today = new DateTime();
        Appointment appointmentA = new Appointment(null, null, new DateTime(2014, 10, 4, 20, 15, 00),
                new DateTime(2014, 10, 10, 20, 15, 00), null, null, null);
        Appointment appointmentB = new Appointment(null, null, today, today, null, null, null);
        Appointment appointmentC = new Appointment(null, null, today, today, null, null, null);
        Appointment appointmentD = new Appointment(null, null, new DateTime(2014, 10, 7, 20, 15, 00),
                new DateTime(2014, 10, 10, 20, 15, 00), null, null, null);

        calendar.addAppointment(appointmentA);
        calendar.addAppointment(appointmentB);
        calendar.addAppointment(appointmentC);
        calendar.addAppointment(appointmentD);

        calendar.cancel();

        Appointment[] expected = new Appointment[]{appointmentA, appointmentD};
        Appointment[] actual = calendar.getAppointments(new DateTime(2014, 10, 4, 20, 15, 00),
                today);

        assertThat(actual, is(expected));
    }
}
