package de.outlookklon.application.mailclient;

import de.outlookklon.model.mails.SendMailInfo;
import de.outlookklon.model.mails.ServerSettings;
import java.util.Arrays;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OutboxServerTest {

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateOutboxServer_SettingsNull() throws Exception {
        new OutboxServerImpl(null, "testType").toString();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateOutboxServer_TypeNull() throws Exception {
        new OutboxServerImpl(mock(ServerSettings.class), null).toString();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateOutboxServer_TypeEmpty() throws Exception {
        new OutboxServerImpl(mock(ServerSettings.class), " \t ").toString();
    }

    @Test
    public void shouldCreateOutboxServer() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);

        OutboxServer server = new OutboxServerImpl(serverSettings, "Test Type");

        assertThat(server.getServerType(), is("Test Type"));
        assertThat(server.getSettings(), is(serverSettings));
        assertThat(server.toString(), is("TestHost:1234"));
    }

    @Test
    public void shouldLogin_Successful() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);

        Transport transport = mock(Transport.class);
        when(transport.isConnected()).thenReturn(true);

        OutboxServer server = spy(new OutboxServerImpl(serverSettings, "Test Type"));
        doReturn(transport).when(server).getTransport(any(String.class), any(String.class));

        assertThat(server.checkLogin("TestUser", "TestPW"), is(true));
    }

    @Test
    public void shouldLogin_SuccessfulButFailedToClose() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);

        Transport transport = mock(Transport.class);
        when(transport.isConnected()).thenReturn(true);
        doThrow(new MessagingException()).when(transport).close();

        OutboxServer server = spy(new OutboxServerImpl(serverSettings, "Test Type"));
        doReturn(transport).when(server).getTransport(any(String.class), any(String.class));

        assertThat(server.checkLogin("TestUser", "TestPW"), is(true));
    }

    @Test
    public void shouldNotLogin_NoStoreProvider() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);

        OutboxServer server = spy(new OutboxServerImpl(serverSettings, "Test Type"));
        doThrow(new NoSuchProviderException()).when(server).getTransport(any(String.class), any(String.class));

        assertThat(server.checkLogin("TestUser", "TestPW"), is(false));
    }

    @Test
    public void shouldNotLogin_ConnectionFailed() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);

        Transport transport = mock(Transport.class);
        doThrow(new MessagingException()).when(transport).connect(any(String.class), any(Integer.class), any(String.class), any(String.class));

        OutboxServer server = spy(new OutboxServerImpl(serverSettings, "Test Type"));
        doReturn(transport).when(server).getTransport(any(String.class), any(String.class));

        assertThat(server.checkLogin("TestUser", "TestPW"), is(false));
    }

    @Test
    public void shouldSendMail() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);

        Transport transport = mock(Transport.class);
        when(transport.isConnected()).thenReturn(true);

        OutboxServer server = spy(new OutboxServerImpl(serverSettings, "Test Type"));
        doReturn(transport).when(server).getTransport(any(String.class), any(String.class));

        SendMailInfo mailToSend = new SendMailInfo("TestSubject", "TestText", "text/plain",
                Arrays.<Address>asList(new InternetAddress("tester1@test.com")),
                Arrays.<Address>asList(new InternetAddress("management@test.com")),
                Arrays.asList("/a/test/path.txt"));
        Message message = server.sendMail("TestUser", "TestPW", mailToSend);
        assertThat(message.isSet(Flags.Flag.SEEN), is(true));

        verify(transport).sendMessage(eq(message), any(Address[].class));
    }

    @Test(expected = NoSuchProviderException.class)
    public void shouldNotSendMail_NoStoreProvider() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);

        OutboxServer server = spy(new OutboxServerImpl(serverSettings, "Test Type"));
        doThrow(new NoSuchProviderException()).when(server).getTransport(any(String.class), any(String.class));

        server.sendMail("TestUser", "TestPW", mock(SendMailInfo.class));
    }

    @Test(expected = MessagingException.class)
    public void shouldNotSendMail_ConnectionFailed() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);

        Transport transport = mock(Transport.class);
        doThrow(new MessagingException()).when(transport).connect(any(String.class), any(Integer.class), any(String.class), any(String.class));

        OutboxServer server = spy(new OutboxServerImpl(serverSettings, "Test Type"));
        doReturn(transport).when(server).getTransport(any(String.class), any(String.class));

        server.sendMail("TestUser", "TestPW", mock(SendMailInfo.class));
    }

    private class OutboxServerImpl extends OutboxServer {

        public OutboxServerImpl(ServerSettings settings, String serverType) {
            super(settings, serverType);
        }

        @Override
        public Transport getTransport(String user, String passwd) throws NoSuchProviderException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected Properties getProperties() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
