package de.outlookklon.application.mailclient;

import de.outlookklon.model.mails.ServerSettings;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Store;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class InboxServerTest {

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateInboxServer_SettingsNull() throws Exception {
        new InboxServerImpl(null, "testType").toString();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateInboxServer_TypeNull() throws Exception {
        new InboxServerImpl(mock(ServerSettings.class), null).toString();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateInboxServer_TypeEmpty() throws Exception {
        new InboxServerImpl(mock(ServerSettings.class), " \t ").toString();
    }

    @Test
    public void shouldCreateInboxServer() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);

        InboxServer server = new InboxServerImpl(serverSettings, "Test Type");

        assertThat(server.getServerType(), is("Test Type"));
        assertThat(server.getSettings(), is(serverSettings));
        assertThat(server.toString(), is("TestHost:1234"));
    }

    @Test
    public void shouldLogin_Successful() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);

        Store store = mock(Store.class);
        when(store.isConnected()).thenReturn(true);

        InboxServer server = spy(new InboxServerImpl(serverSettings, "Test Type"));
        doReturn(store).when(server).getMailStore(any(String.class), any(String.class));

        assertThat(server.checkLogin("TestUser", "TestPW"), is(true));
    }

    @Test
    public void shouldLogin_SuccessfulButFailedToClose() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);

        Store store = mock(Store.class);
        when(store.isConnected()).thenReturn(true);
        doThrow(new MessagingException()).when(store).close();

        InboxServer server = spy(new InboxServerImpl(serverSettings, "Test Type"));
        doReturn(store).when(server).getMailStore(any(String.class), any(String.class));

        assertThat(server.checkLogin("TestUser", "TestPW"), is(true));
    }

    @Test
    public void shouldNotLogin_NoStoreProvider() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);

        InboxServer server = spy(new InboxServerImpl(serverSettings, "Test Type"));
        doThrow(new NoSuchProviderException()).when(server).getMailStore(any(String.class), any(String.class));

        assertThat(server.checkLogin("TestUser", "TestPW"), is(false));
    }

    @Test
    public void shouldNotLogin_ConnectionFailed() throws Exception {
        ServerSettings serverSettings = mock(ServerSettings.class);
        when(serverSettings.getHost()).thenReturn("TestHost");
        when(serverSettings.getPort()).thenReturn(1234);

        Store store = mock(Store.class);
        doThrow(new MessagingException()).when(store).connect(any(String.class), any(Integer.class), any(String.class), any(String.class));

        InboxServer server = spy(new InboxServerImpl(serverSettings, "Test Type"));
        doReturn(store).when(server).getMailStore(any(String.class), any(String.class));

        assertThat(server.checkLogin("TestUser", "TestPW"), is(false));
    }

    private class InboxServerImpl extends InboxServer {

        public InboxServerImpl(ServerSettings settings, String serverType) {
            super(settings, serverType);
        }

        @Override
        public Store getMailStore(String user, String password) throws NoSuchProviderException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean supportsMultipleFolders() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected Properties getProperties() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
