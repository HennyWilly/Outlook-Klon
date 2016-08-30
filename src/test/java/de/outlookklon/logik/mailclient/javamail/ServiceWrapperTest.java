package de.outlookklon.logik.mailclient.javamail;

import java.util.Properties;
import javax.mail.Service;
import javax.mail.Session;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Session.class)
public class ServiceWrapperTest {

    @Test
    public void shouldCreateServiceWrapper() throws Exception {
        Session session = mock(Session.class);
        when(session.getProperties()).thenReturn(new Properties());

        Service service = new ServiceImpl(session);
        ServiceWrapper<Service> wrapper = new ServiceWrapper<>(service);

        assertThat(wrapper.getService(), is(service));
        assertThat(wrapper.getSession(), is(session));
        assertThat(wrapper.getServiceClassName(), is("ServiceImpl"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateServiceWrapper_ServiceNull() throws Exception {
        new ServiceWrapper<>(null).toString();
    }

    private class ServiceImpl extends Service {

        public ServiceImpl(Session session) {
            super(session, null);
        }
    }
}
