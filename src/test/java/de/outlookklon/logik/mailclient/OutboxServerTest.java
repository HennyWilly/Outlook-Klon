package de.outlookklon.logik.mailclient;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
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

    private class OutboxServerImpl extends OutboxServer {

        public OutboxServerImpl(ServerSettings settings, String serverType) {
            super(settings, serverType);
        }

        @Override
        public Message sendMail(String user, String password, MailInfo mailToSend) throws MessagingException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean checkLogin(String userName, String password) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected Properties getProperties() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
