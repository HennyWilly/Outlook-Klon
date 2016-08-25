package de.outlookklon.logik.contacts;

import de.outlookklon.serializers.Serializer;
import java.util.HashMap;
import java.util.Map;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.NewsAddress;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ContactTest {

    private static final String EXAMPLE_JSON
            = "{"
            + "  \"surname\" : \"Willy\","
            + "  \"forename\" : \"Henny\","
            + "  \"displayname\" : \"Henny Willy\","
            + "  \"nickname\" : \"HennyMan\","
            + "  \"address1\" : {"
            + "    \"@class\" : \"javax.mail.internet.InternetAddress\","
            + "    \"address\" : \"henny@willy.de\","
            + "    \"personal\" : null"
            + "  },"
            + "  \"address2\" : {"
            + "    \"@class\" : \"javax.mail.internet.InternetAddress\","
            + "    \"address\" : \"henny@willy.com\","
            + "    \"personal\" : null"
            + "  },"
            + "  \"privatephone\" : \"23456\","
            + "  \"dutyphone\" : \"12345\","
            + "  \"mobilephone\" : \"78901\""
            + "}";

    @Autowired
    private Serializer serializer;

    @Test
    public void shouldSaveAsJson() throws Exception {
        Contact contact = new Contact("Willy", "Henny", "Henny Willy", "HennyMan",
                new InternetAddress("henny@willy.de"),
                new InternetAddress("henny@willy.com"),
                "23456", "12345", "78901");

        String json = serializer.serializeObjectToJson(contact);
        assertThat(json, jsonEquals(EXAMPLE_JSON));
    }

    @Test
    public void shouldLoadFromJson() throws Exception {
        Contact contactExpected = new Contact("Willy", "Henny", "Henny Willy", "HennyMan",
                new InternetAddress("henny@willy.de"),
                new InternetAddress("henny@willy.com"),
                "23456", "12345", "78901");
        Contact contactActual = serializer.deserializeJson(EXAMPLE_JSON, Contact.class);

        assertThat(contactActual, is(equalTo(contactExpected)));
    }

    @Test
    public void shouldUseDisplayNameForToString() throws Exception {
        Contact contact = new Contact("", "", "Henny Willy", "",
                null,
                null,
                "", "", "");

        assertThat(contact.toString(), is(equalTo("Henny Willy")));
    }

    @Test
    public void shouldNotBeEqual_Null() throws Exception {
        Contact contact = new Contact("Willy", "Henny", "Henny Willy", "HennyMan",
                new InternetAddress("henny@willy.de"), new InternetAddress("henny@willy.com"),
                "23456", "12345", "78901");
        assertThat(contact, is(not(equalTo(null))));
    }

    @Test
    public void shouldNotBeEqual_OtherType() throws Exception {
        Contact contact = new Contact("Willy", "Henny", "Henny Willy", "HennyMan",
                new InternetAddress("henny@willy.de"), new InternetAddress("henny@willy.com"),
                "23456", "12345", "78901");
        assertThat(contact, is(not(equalTo(new Object()))));
    }

    @Test
    public void shouldTestHashCodeContract() throws Exception {
        Map<Contact, String> map = new HashMap<>();

        map.put(new Contact("Willy", "Henny", "Henny Willy", "HennyMan",
                new InternetAddress("henny@willy.de"), new InternetAddress("henny@willy.com"),
                "23456", "12345", "78901"),
                "aaaa");
        map.put(new Contact("Willy2", "Henny2", "Henny Willy2", "HennyMan2",
                new InternetAddress("henny@willy.de"), new InternetAddress("henny@willy.com"),
                "23456", "12345", "78901"),
                "bbbb");

        assertThat(map, hasEntry(new Contact("Willy", "Henny", "Henny Willy", "HennyMan",
                new InternetAddress("henny@willy.de"), new InternetAddress("henny@willy.com"),
                "23456", "12345", "78901"), "aaaa"));
        assertThat(map, hasEntry(new Contact("Willy2", "Henny2", "Henny Willy2", "HennyMan2",
                new InternetAddress("henny@willy.de"), new InternetAddress("henny@willy.com"),
                "23456", "12345", "78901"), "bbbb"));
    }

    @Test
    public void shouldGetAddressesAsStrings() throws Exception {
        Contact contact1 = new Contact("Willy", "Henny", "Henny Willy", "HennyMan",
                new InternetAddress("henny@willy.de"), null,
                "23456", "12345", "78901");

        Contact contact2 = new Contact("Willy", "Henny", "Henny Willy", "HennyMan",
                new NewsAddress("TestNewsGroup"), null,
                "23456", "12345", "78901");

        assertThat(contact1.getAddress1AsString(), is("henny@willy.de"));
        assertThat(contact1.getAddress2AsString(), is(""));
        assertThat(contact2.getAddress1AsString(), is("TestNewsGroup"));
    }

    @Configuration
    public static class ContactTestConfiguration {

        @Bean
        public Serializer getSerializer() {
            return spy(new Serializer());
        }
    }
}
