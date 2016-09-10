package de.outlookklon.gui.helpers;

import de.outlookklon.gui.components.Statusbar;
import static de.outlookklon.matchers.UtilityMatchers.isWellDefinedUtilityClass;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class StatusbarProviderTest {

    @Test
    public void shouldCheckIfUtilityClassIsWellCoded() throws Exception {
        assertThat(StatusbarProvider.class, isWellDefinedUtilityClass());
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAddStatusbar_NullPointer() throws Exception {
        StatusbarProvider.addStatusbar(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotRemoveStatusbar_NullPointer() throws Exception {
        StatusbarProvider.removeStatusbar(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotSetText_IsNull() throws Exception {
        StatusbarProvider.setText(null);
    }

    @Test
    public void shouldSetText() throws Exception {
        Statusbar statusbar = mock(Statusbar.class);
        StatusbarProvider.addStatusbar(statusbar);

        final AtomicBoolean invoked = new AtomicBoolean(false);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertThat(SwingUtilities.isEventDispatchThread(), is(true));
                invoked.set(true);
                return null;
            }
        }).when(statusbar).setText(any(String.class));

        StatusbarProvider.setText("TestTestTest");
        while (!invoked.get()) {
            Thread.sleep(1);
        }
        verify(statusbar).setText("TestTestTest");
    }

    @Test
    public void shouldSetTextOnlyOnce_StatusbarRemoved() throws Exception {
        Statusbar statusbar = mock(Statusbar.class);
        StatusbarProvider.addStatusbar(statusbar);

        StatusbarProvider.setText("Test1");
        StatusbarProvider.removeStatusbar(statusbar);
        StatusbarProvider.setText("Test2");

        verify(statusbar, atMost(1)).setText(any(String.class));
    }

    @Test
    public void shouldSetText_InEDT() throws Exception {
        Statusbar statusbar = mock(Statusbar.class);
        StatusbarProvider.addStatusbar(statusbar);

        final AtomicBoolean invoked = new AtomicBoolean(false);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                assertThat(SwingUtilities.isEventDispatchThread(), is(true));
                invoked.set(true);
                return null;
            }
        }).when(statusbar).setText(any(String.class));

        StatusbarProvider.setText("TestTestTest");
        while (!invoked.get()) {
            Thread.sleep(1);
        }
        verify(statusbar).setText("TestTestTest");
    }
}
