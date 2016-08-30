package de.outlookklon.logik.mailclient;

import de.outlookklon.serializers.Serializer;
import java.lang.reflect.Method;
import java.util.Properties;
import javax.mail.Transport;
import javax.mail.URLName;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SmtpServerTest {

    private static final String EXAMPLE_JSON
            = "{"
            + "    \"@class\" : \"de.outlookklon.logik.mailclient.SmtpServer\","
            + "    \"settings\" : {"
            + "      \"host\" : \"smtp.xyz.com\","
            + "      \"port\" : 587,"
            + "      \"connectionSecurity\" : \"STARTTLS\","
            + "      \"authentificationType\" : \"NORMAL\""
            + "    },"
            + "    \"serverType\" : \"SMTP\""
            + "  }";

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateSmtpServer_SettingsNull() throws Exception {
        new SmtpServer(null).toString();
    }

    @Test
    public void shouldCreateSmtpServer_WithSSL() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);
        when(serverSettings.getConnectionSecurity()).thenReturn(ConnectionSecurity.SSL_TLS);

        SmtpServer server = new SmtpServer(serverSettings);

        assertThat(server.getServerType(), is("SMTP"));
        assertThat(server.getSettings(), is(serverSettings));
        assertThat(server.toString(), is("TestHost:1234"));

        Properties props = getProperties(server);
        assertThat(props.getProperty("mail.smtp.socketFactory.port"), is("1234"));
        assertThat(props.getProperty("mail.smtp.socketFactory.class"), is("javax.net.ssl.SSLSocketFactory"));
        assertThat(props.getProperty("mail.smtp.socketFactory.fallback"), is("false"));
    }

    @Test
    public void shouldCreateSmtpServer_WithStartTLS() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);
        when(serverSettings.getConnectionSecurity()).thenReturn(ConnectionSecurity.STARTTLS);

        SmtpServer server = new SmtpServer(serverSettings);

        assertThat(server.getServerType(), is("SMTP"));
        assertThat(server.getSettings(), is(serverSettings));
        assertThat(server.toString(), is("TestHost:1234"));

        Properties props = getProperties(server);
        assertThat(props.getProperty("mail.smtp.starttls.enable"), is("true"));
    }

    private Properties getProperties(SmtpServer server) throws Exception {
        Method method = SmtpServer.class.getDeclaredMethod("getProperties");
        method.setAccessible(true);

        try {
            return (Properties) method.invoke(server);
        } finally {
            method.setAccessible(false);
        }
    }

    @Test
    public void shouldSerializeServer() throws Exception {
        ServerSettings settings = new ServerSettings("smtp.xyz.com", 587, ConnectionSecurity.STARTTLS, AuthentificationType.NORMAL);
        MailServer server = new SmtpServer(settings);

        String json = Serializer.serializeObjectToJson(server);
        assertThat(json, jsonEquals(EXAMPLE_JSON));
    }

    @Test
    public void shouldDeserializeServer() throws Exception {
        ServerSettings settings = new ServerSettings("smtp.xyz.com", 587, ConnectionSecurity.STARTTLS, AuthentificationType.NORMAL);
        MailServer expected = new SmtpServer(settings);

        MailServer actual = Serializer.deserializeJson(EXAMPLE_JSON, MailServer.class);
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void shouldGetTransport_SSL() throws Exception {
        ServerSettings settings = new ServerSettings("mail.xyz.com", 993, ConnectionSecurity.SSL_TLS, AuthentificationType.NORMAL);
        OutboxServer expected = new SmtpServer(settings);

        Transport transport = expected.getTransport("TestUser", "TestPW");
        URLName name = transport.getURLName();
        assertThat(name.getProtocol(), is("smtps"));
    }

    @Test
    public void shouldGetTransport_NoSSL() throws Exception {
        ServerSettings settings = new ServerSettings("mail.xyz.com", 993, ConnectionSecurity.NONE, AuthentificationType.NORMAL);
        OutboxServer expected = new SmtpServer(settings);

        Transport transport = expected.getTransport("TestUser", "TestPW");
        URLName name = transport.getURLName();
        assertThat(name.getProtocol(), is("smtp"));
    }
}
