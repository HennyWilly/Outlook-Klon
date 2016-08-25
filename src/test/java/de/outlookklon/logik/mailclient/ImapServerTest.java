package de.outlookklon.logik.mailclient;

import de.outlookklon.serializers.Serializer;
import java.lang.reflect.Method;
import java.util.Properties;
import javax.mail.Store;
import javax.mail.URLName;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ImapServerTest {

    private static final String EXAMPLE_JSON
            = "{"
            + "  \"@class\" : \"de.outlookklon.logik.mailclient.ImapServer\","
            + "  \"settings\" : {"
            + "    \"host\" : \"mail.xyz.com\","
            + "    \"port\" : 993,"
            + "    \"connectionSecurity\" : \"SSL_TLS\","
            + "    \"authentificationType\" : \"NORMAL\""
            + "  },"
            + "  \"serverType\" : \"IMAP\""
            + "}";

    @Autowired
    private Serializer serializer;

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateImapServer_SettingsNull() throws Exception {
        new ImapServer(null).toString();
    }

    @Test
    public void shouldCreateImapServer() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);
        when(serverSettings.getConnectionSecurity()).thenReturn(ConnectionSecurity.NONE);

        ImapServer server = new ImapServer(serverSettings);

        assertThat(server.getServerType(), is("IMAP"));
        assertThat(server.getSettings(), is(serverSettings));
        assertThat(server.toString(), is("TestHost:1234"));
        assertThat(server.supportsMultipleFolders(), is(true));

        Properties props = getProperties(server);
        assertThat(props.getProperty("mail.imap.host"), is("TestHost"));
        assertThat(props.getProperty("mail.imap.port"), is("1234"));
        assertThat(props.getProperty("mail.imap.auth"), is("true"));
        assertThat(props.getProperty("mail.imap.ssl.enable", null), is(nullValue()));
    }

    @Test
    public void shouldCreateImapServer_WithSSL() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);
        when(serverSettings.getConnectionSecurity()).thenReturn(ConnectionSecurity.SSL_TLS);

        ImapServer server = new ImapServer(serverSettings);

        assertThat(server.getServerType(), is("IMAP"));
        assertThat(server.getSettings(), is(serverSettings));
        assertThat(server.toString(), is("TestHost:1234"));
        assertThat(server.supportsMultipleFolders(), is(true));

        Properties props = getProperties(server);
        assertThat(props.getProperty("mail.imap.host"), is("TestHost"));
        assertThat(props.getProperty("mail.imap.port"), is("1234"));
        assertThat(props.getProperty("mail.imap.auth"), is("true"));
        assertThat(props.getProperty("mail.imap.ssl.enable"), is("true"));
    }

    private Properties getProperties(ImapServer server) throws Exception {
        Method method = ImapServer.class.getDeclaredMethod("getProperties");
        method.setAccessible(true);

        try {
            return (Properties) method.invoke(server);
        } finally {
            method.setAccessible(false);
        }
    }

    @Test
    public void shouldSerializeServer() throws Exception {
        ServerSettings settings = new ServerSettings("mail.xyz.com", 993, ConnectionSecurity.SSL_TLS, AuthentificationType.NORMAL);
        MailServer server = new ImapServer(settings);

        String json = serializer.serializeObjectToJson(server);
        assertThat(json, jsonEquals(EXAMPLE_JSON));
    }

    @Test
    public void shouldDeserializeServer() throws Exception {
        ServerSettings settings = new ServerSettings("mail.xyz.com", 993, ConnectionSecurity.SSL_TLS, AuthentificationType.NORMAL);
        MailServer expected = new ImapServer(settings);

        MailServer actual = serializer.deserializeJson(EXAMPLE_JSON, MailServer.class);
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void shouldGetMailStore_SSL() throws Exception {
        ServerSettings settings = new ServerSettings("mail.xyz.com", 993, ConnectionSecurity.SSL_TLS, AuthentificationType.NORMAL);
        InboxServer expected = new ImapServer(settings);

        Store store = expected.getMailStore("TestUser", "TestPW");
        URLName name = store.getURLName();
        assertThat(name.getProtocol(), is("imaps"));
    }

    @Test
    public void shouldGetMailStore_NoSSL() throws Exception {
        ServerSettings settings = new ServerSettings("mail.xyz.com", 993, ConnectionSecurity.NONE, AuthentificationType.NORMAL);
        InboxServer expected = new ImapServer(settings);

        Store store = expected.getMailStore("TestUser", "TestPW");
        URLName name = store.getURLName();
        assertThat(name.getProtocol(), is("imap"));
    }

    @Configuration
    public static class ImapServerTestConfiguration {

        @Bean
        public Serializer getSerializer() {
            return spy(new Serializer());
        }
    }
}
