package de.outlookklon.logik.calendar;

import de.outlookklon.logik.User;
import de.outlookklon.logik.contacts.ContactManagement;
import static org.hamcrest.CoreMatchers.is;
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

    @Test
    public void shouldFindOverlappingAppointments() {
        Appointment appointmentA = new Appointment(null, null, new DateTime(2014, 10, 5, 20, 15, 00),
                new DateTime(2014, 10, 10, 20, 15, 00), null, null, null);
        Appointment appointmentB = new Appointment(null, null, new DateTime(2014, 10, 4, 10, 00, 00),
                new DateTime(2014, 10, 6, 10, 00, 00), null, null, null);

        calendar.addAppointment(appointmentA);
        assertThat(calendar.isOverlapping(appointmentB), is(true));
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

}
