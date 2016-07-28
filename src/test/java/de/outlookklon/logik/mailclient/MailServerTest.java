package de.outlookklon.logik.mailclient;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MailServerTest {

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateMailServer_SettingsNull() throws Exception {
        new MailServerImpl(null, "testType").toString();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateMailServer_TypeNull() throws Exception {
        new MailServerImpl(mock(ServerSettings.class), null).toString();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateMailServer_TypeEmpty() throws Exception {
        new MailServerImpl(mock(ServerSettings.class), " \t ").toString();
    }

    @Test
    public void shouldCreateMailServer() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);

        MailServer server = new MailServerImpl(serverSettings, "Test Type");

        assertThat(server.getServerType(), is("Test Type"));
        assertThat(server.getSettings(), is(serverSettings));
        assertThat(server.toString(), is("TestHost:1234"));
    }

    @Test
    public void shouldCreateStandardAuthentificator() throws Exception {
        PasswordAuthentication auth = getPasswordAuthentication("TestUser", "TestPW");
        assertThat(auth.getUserName(), is("TestUser"));
        assertThat(auth.getPassword(), is("TestPW"));
    }

    private PasswordAuthentication getPasswordAuthentication(String user, String pw) throws Exception {
        Authenticator auth = new MailServerImpl(mock(ServerSettings.class), "ServerType")
                .getInnerAuthentificator(user, pw);

        Method getPWMethod = Authenticator.class.getDeclaredMethod("getPasswordAuthentication");
        getPWMethod.setAccessible(true);

        try {
            return (PasswordAuthentication) getPWMethod.invoke(auth);
        } finally {
            getPWMethod.setAccessible(false);
        }
    }

    @Test
    public void shouldGetSession() throws Exception {
        MailServerImpl server = spy(new MailServerImpl(mock(ServerSettings.class), "Test Type"));
        Session session = server.getSession("TestUser", "TestPW");
        assertThat(session, is(not(nullValue())));

        PasswordAuthentication auth = session.requestPasswordAuthentication(mock(InetAddress.class), 0, "", "", "");
        assertThat(auth.getUserName(), is("TestUser"));
        assertThat(auth.getPassword(), is("TestPW"));

        verify(server).getProperties();
    }

    @Test
    public void shouldBeEqual() throws Exception {
        ServerSettings settings1 = new ServerSettings("testHost", 1234, ConnectionSecurity.STARTTLS, AuthentificationType.KERBEROS);
        MailServer server1 = new MailServerImpl(settings1, "testType");

        ServerSettings settings2 = new ServerSettings("testHost", 1234, ConnectionSecurity.STARTTLS, AuthentificationType.KERBEROS);
        MailServer server2 = new MailServerImpl(settings2, "testType");

        assertThat(server1, is(equalTo(server2)));
    }

    @Test
    public void shouldBeEqual_SameInstance() throws Exception {
        ServerSettings settings = new ServerSettings("testHost", 1234, ConnectionSecurity.STARTTLS, AuthentificationType.KERBEROS);
        MailServer server = new MailServerImpl(settings, "testType");

        assertThat(server, is(equalTo(server)));
    }

    @Test
    public void shouldNotBeEqual_Null() throws Exception {
        ServerSettings settings = new ServerSettings("testHost", 1234, ConnectionSecurity.STARTTLS, AuthentificationType.KERBEROS);
        MailServer server = new MailServerImpl(settings, "testType");

        assertThat(server, is(not(equalTo(null))));
    }

    @Test
    public void shouldNotBeEqual_OtherSubclass() throws Exception {
        ServerSettings settings = new ServerSettings("testHost", 1234, ConnectionSecurity.STARTTLS, AuthentificationType.KERBEROS);
        MailServer server1 = new MailServerImpl(settings, "testType");
        MailServer server2 = new ImapServer(settings);

        assertThat(server1, is(not(equalTo(server2))));
    }

    @Test
    public void shouldTestHashCodeContract() throws Exception {
        ServerSettings settings = new ServerSettings("testHost", 1234, ConnectionSecurity.STARTTLS, AuthentificationType.KERBEROS);

        Map<MailServer, String> map = new HashMap<>();

        map.put(new ImapServer(settings), "aaaa");
        map.put(new Pop3Server(settings), "bbbb");

        assertThat(map, hasEntry((MailServer) new ImapServer(settings), "aaaa"));
        assertThat(map, hasEntry((MailServer) new Pop3Server(settings), "bbbb"));
    }

    private class MailServerImpl extends MailServer {

        public MailServerImpl(ServerSettings settings, String serverType) {
            super(settings, serverType);
        }

        @Override
        public boolean checkLogin(String userName, String password) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Properties getProperties() {
            Properties props = mock(Properties.class);

            when(props.get("mail.event.executor")).thenReturn(mock(Executor.class));
            when(props.getProperty("mail.debug")).thenReturn("true");

            return props;
        }

        public Session getSession(String user, String pw) {
            return super.getSession(new StandardAuthenticator(user, pw));
        }

        public Authenticator getInnerAuthentificator(String user, String pw) {
            return new StandardAuthenticator(user, pw);
        }
    }
}
