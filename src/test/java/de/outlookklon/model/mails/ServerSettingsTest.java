package de.outlookklon.model.mails;

import de.outlookklon.serializers.Serializer;
import java.util.HashMap;
import java.util.Map;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasToString;
import org.junit.Test;

public class ServerSettingsTest {

    private static final String EXAMPLE_JSON
            = "{"
            + "  \"host\" : \"mail.xyz.com\","
            + "  \"port\" : 993,"
            + "  \"connectionSecurity\" : \"SSL_TLS\","
            + "  \"authentificationType\" : \"NORMAL\""
            + "}";

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateServerSettings_HostNull() throws Exception {
        new ServerSettings(null, 993, ConnectionSecurity.SSL_TLS, AuthentificationType.NORMAL).toString();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateServerSettings_PortNumberTooLow() throws Exception {
        new ServerSettings("mail.xyz.com", 0, ConnectionSecurity.SSL_TLS, AuthentificationType.NORMAL).toString();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateServerSettings_PortNumberTooHigh() throws Exception {
        new ServerSettings("mail.xyz.com", 65536, ConnectionSecurity.SSL_TLS, AuthentificationType.NORMAL).toString();
    }

    @Test
    public void shouldCreateServerSettings() throws Exception {
        ServerSettings settings = new ServerSettings("mail.xyz.com", 993, ConnectionSecurity.SSL_TLS, AuthentificationType.NORMAL);
        assertThat(settings, hasToString("mail.xyz.com:993"));
    }

    @Test
    public void shouldSerializeServerSettings() throws Exception {
        ServerSettings settings = new ServerSettings("mail.xyz.com", 993, ConnectionSecurity.SSL_TLS, AuthentificationType.NORMAL);

        String json = Serializer.serializeObjectToJson(settings);
        assertThat(json, jsonEquals(EXAMPLE_JSON));
    }

    @Test
    public void shouldDeserializeServerSettings() throws Exception {
        ServerSettings expected = new ServerSettings("mail.xyz.com", 993, ConnectionSecurity.SSL_TLS, AuthentificationType.NORMAL);

        ServerSettings actual = Serializer.deserializeJson(EXAMPLE_JSON, ServerSettings.class);
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void shouldBeEqual() throws Exception {
        ServerSettings settings1 = new ServerSettings("testHost", 1234, ConnectionSecurity.STARTTLS, AuthentificationType.KERBEROS);
        ServerSettings settings2 = new ServerSettings("testHost", 1234, ConnectionSecurity.STARTTLS, AuthentificationType.KERBEROS);

        assertThat(settings1, is(equalTo(settings2)));
    }

    @Test
    public void shouldBeEqual_SameInstance() throws Exception {
        ServerSettings settings = new ServerSettings("testHost", 1234, ConnectionSecurity.STARTTLS, AuthentificationType.KERBEROS);

        assertThat(settings, is(equalTo(settings)));
    }

    @Test
    public void shouldNotBeEqual_Null() throws Exception {
        ServerSettings settings = new ServerSettings("testHost", 1234, ConnectionSecurity.STARTTLS, AuthentificationType.KERBEROS);

        assertThat(settings, is(not(equalTo(null))));
    }

    @Test
    public void shouldNotBeEqual_OtherClass() throws Exception {
        ServerSettings settings = new ServerSettings("testHost", 1234, ConnectionSecurity.STARTTLS, AuthentificationType.KERBEROS);

        assertThat(settings, is(not(equalTo(new Object()))));
    }

    @Test
    public void shouldTestHashCodeContract() throws Exception {
        Map<ServerSettings, String> map = new HashMap<>();

        map.put(new ServerSettings("testHost1", 1234, ConnectionSecurity.STARTTLS, AuthentificationType.KERBEROS), "aaaa");
        map.put(new ServerSettings("testHost2", 5678, ConnectionSecurity.SSL_TLS, AuthentificationType.NTLM), "bbbb");

        assertThat(map, hasEntry(new ServerSettings("testHost1", 1234, ConnectionSecurity.STARTTLS, AuthentificationType.KERBEROS), "aaaa"));
        assertThat(map, hasEntry(new ServerSettings("testHost2", 5678, ConnectionSecurity.SSL_TLS, AuthentificationType.NTLM), "bbbb"));
    }

}
