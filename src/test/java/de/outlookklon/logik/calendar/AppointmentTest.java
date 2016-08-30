package de.outlookklon.logik.calendar;

import de.outlookklon.logik.User;
import de.outlookklon.logik.contacts.Contact;
import de.outlookklon.logik.contacts.ContactManagement;
import de.outlookklon.serializers.Serializer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import org.joda.time.DateTime;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(User.class)
public class AppointmentTest {

    private static final String EXAMPLE_JSON
            = "{"
            + "    \"subject\" : \"TestAppointment\","
            + "    \"location\" : \"TestPlace\","
            + "    \"start\" : \"2016-08-26T06:00:00.000+02:00[Europe/Berlin]\","
            + "    \"end\" : \"2016-08-26T12:30:00.000+02:00[Europe/Berlin]\","
            + "    \"text\" : \"TestDescription\","
            + "    \"user\" : \"testuser@test.org\","
            + "    \"contact\" : \"TestContact\","
            + "    \"state\" : \"PROMISED\","
            + "    \"addresses\" : [ {"
            + "      \"@class\" : \"javax.mail.internet.InternetAddress\","
            + "      \"address\" : \"tester@test.org\","
            + "      \"personal\" : null"
            + "    } ]"
            + "}";

    @Test
    public void shouldSaveAsJson() throws Exception {
        Appointment appointment = new Appointment("TestAppointment", "TestPlace",
                new DateTime(2016, 8, 26, 6, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription", "testuser@test.org", "TestContact", AppointmentState.PROMISED,
                Collections.singletonList(new InternetAddress("tester@test.org")));

        String json = Serializer.serializeObjectToJson(appointment);
        assertThat(json, jsonEquals(EXAMPLE_JSON));
    }

    @Test
    public void shouldLoadFromJson() throws Exception {
        Appointment expectedAppointment = new Appointment("TestAppointment", "TestPlace",
                new DateTime(2016, 8, 26, 6, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription", "testuser@test.org", "TestContact", AppointmentState.PROMISED,
                Collections.singletonList(new InternetAddress("tester@test.org")));
        Appointment actualAppointment = Serializer.deserializeJson(EXAMPLE_JSON, Appointment.class);

        assertThat(actualAppointment, is(equalTo(expectedAppointment)));
    }

    @Test
    public void shouldCreateAppointment_ContactFromContactManager() throws Exception {
        ContactManagement contacts = initContactManagement();
        contacts.addContact(new Contact("Test", "User", "TestContact", "Testy",
                new InternetAddress("tester@test.org"), new InternetAddress("tester2@test.org"),
                "123", "234", "345"));

        Address[] expectedAddresses = {
            new InternetAddress("tester@test.org"),
            new InternetAddress("tester2@test.org")
        };

        Appointment appointment = new Appointment("TestAppointment", "TestPlace",
                new DateTime(2016, 8, 26, 6, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription", "testuser@test.org", "TestContact");
        assertThat(appointment.getContact(), is(equalTo("TestContact")));
        assertThat(appointment.getAddresses(), containsInAnyOrder(expectedAddresses));
    }

    @Test
    public void shouldCreateAppointment_ContactFromContactManagerNotFound() throws Exception {
        ContactManagement contacts = initContactManagement();
        contacts.addContact(new Contact("Test", "User", "DummyContact", "Testy",
                new InternetAddress("testerDuummy@test.org"), new InternetAddress("testerDummy2@test.org"),
                "123", "234", "345"));

        Appointment appointment = new Appointment("TestAppointment", "TestPlace",
                new DateTime(2016, 8, 26, 6, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription", "testuser@test.org", "TestContact");
        assertThat(appointment.getContact(), is(equalTo("TestContact")));
        assertThat(appointment.getAddresses(), is(empty()));
    }

    private ContactManagement initContactManagement() throws Exception {
        ContactManagement contacts = new ContactManagement();

        User user = PowerMockito.mock(User.class);
        PowerMockito.mockStatic(User.class);
        PowerMockito.when(User.getInstance()).thenReturn(user);
        when(user.getContacts()).thenReturn(contacts);

        return contacts;
    }

    @Test
    public void shouldNotBeEqual_Null() throws Exception {
        Appointment appointment = new Appointment("TestAppointment", "TestPlace",
                new DateTime(2016, 8, 26, 6, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription", "testuser@test.org", "TestContact", AppointmentState.PROMISED,
                Collections.singletonList(new InternetAddress("tester@test.org")));
        assertThat(appointment, is(not(equalTo(null))));
    }

    @Test
    public void shouldNotBeEqual_OtherType() throws Exception {
        Appointment appointment = new Appointment("TestAppointment", "TestPlace",
                new DateTime(2016, 8, 26, 6, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription", "testuser@test.org", "TestContact", AppointmentState.PROMISED,
                Collections.singletonList(new InternetAddress("tester@test.org")));
        assertThat(appointment, is(not(equalTo(new Object()))));
    }

    @Test
    public void shouldTestHashCodeContract() throws Exception {
        Map<Appointment, String> map = new HashMap<>();

        map.put(new Appointment("TestAppointment", "TestPlace", new DateTime(2016, 8, 26, 6, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription", "testuser@test.org", "TestContact", AppointmentState.PROMISED,
                Collections.singletonList(new InternetAddress("tester@test.org"))),
                "aaaa");
        map.put(new Appointment("TestAppointment2", "TestPlace2", new DateTime(2016, 8, 26, 6, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription2", "testuser2@test.org", "TestContact2", AppointmentState.PROMISED,
                Collections.singletonList(new InternetAddress("tester2@test.org"))),
                "bbbb");

        assertThat(map, hasEntry(new Appointment("TestAppointment", "TestPlace", new DateTime(2016, 8, 26, 6, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription", "testuser@test.org", "TestContact", AppointmentState.PROMISED,
                Collections.singletonList(new InternetAddress("tester@test.org"))), "aaaa"));
        assertThat(map, hasEntry(new Appointment("TestAppointment2", "TestPlace2", new DateTime(2016, 8, 26, 6, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription2", "testuser2@test.org", "TestContact2", AppointmentState.PROMISED,
                Collections.singletonList(new InternetAddress("tester2@test.org"))), "bbbb"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotSetTimes_EndBeforeStart() throws Exception {
        Appointment appointment = new Appointment("TestAppointment", "TestPlace",
                new DateTime(2016, 8, 26, 6, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription", "testuser@test.org", "TestContact", AppointmentState.PROMISED,
                Collections.singletonList(new InternetAddress("tester@test.org")));

        appointment.setTimes(new DateTime(2016, 8, 26, 12, 30), new DateTime(2016, 8, 26, 6, 0));
    }

    @Test
    public void shouldCompareAppointments_ABeforeB() throws Exception {
        Appointment a = new Appointment("TestAppointment", "TestPlace",
                new DateTime(2016, 8, 26, 6, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription", "testuser@test.org", "TestContact", AppointmentState.PROMISED,
                Collections.singletonList(new InternetAddress("tester@test.org")));
        Appointment b = new Appointment("TestAppointment", "TestPlace",
                new DateTime(2016, 8, 26, 7, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription", "testuser@test.org", "TestContact", AppointmentState.PROMISED,
                Collections.singletonList(new InternetAddress("tester@test.org")));

        assertThat(a, lessThan(b));
    }

    @Test
    public void shouldCompareAppointments_BBeforeA() throws Exception {
        Appointment a = new Appointment("TestAppointment", "TestPlace",
                new DateTime(2016, 8, 26, 7, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription", "testuser@test.org", "TestContact", AppointmentState.PROMISED,
                Collections.singletonList(new InternetAddress("tester@test.org")));
        Appointment b = new Appointment("TestAppointment", "TestPlace",
                new DateTime(2016, 8, 26, 6, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription", "testuser@test.org", "TestContact", AppointmentState.PROMISED,
                Collections.singletonList(new InternetAddress("tester@test.org")));

        assertThat(a, greaterThan(b));
    }

    @Test
    public void shouldCompareAppointments_AStartsWithB() throws Exception {
        Appointment a = new Appointment("TestAppointment", "TestPlace",
                new DateTime(2016, 8, 26, 6, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription", "testuser@test.org", "TestContact", AppointmentState.PROMISED,
                Collections.singletonList(new InternetAddress("tester@test.org")));
        Appointment b = new Appointment("TestAppointment", "TestPlace",
                new DateTime(2016, 8, 26, 6, 0), new DateTime(2016, 8, 26, 12, 30),
                "TestDescription", "testuser@test.org", "TestContact", AppointmentState.PROMISED,
                Collections.singletonList(new InternetAddress("tester@test.org")));

        assertThat(a.compareTo(b), is(0));
    }
}
