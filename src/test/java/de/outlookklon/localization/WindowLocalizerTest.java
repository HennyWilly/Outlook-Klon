package de.outlookklon.localization;

import de.outlookklon.gui.frames.ExtendedFrame;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @author Hendrik Karwanni
 */
public class WindowLocalizerTest {

    private WindowLocalizer localizer;

    @Before
    public void init() {
        localizer = spy(new WindowLocalizer());
    }

    @Test
    public void shouldCallEvents_LocalizableWindow() throws Exception {
        TestFrame frame = spy(new TestFrame());
        frame.addWindowListener(localizer);

        frame.setVisible(true);
        Thread.sleep(100);
        verify(localizer).windowOpened(any(WindowEvent.class));

        frame.close();
        Thread.sleep(100);
        verify(localizer).windowClosed(any(WindowEvent.class));
    }

    @Test
    public void shouldCallEvents_NonLocalizableWindow() throws Exception {
        JFrame frame = spy(new JFrame());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(localizer);

        frame.setVisible(true);
        Thread.sleep(100);
        verify(localizer).windowOpened(any(WindowEvent.class));

        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        Thread.sleep(100);
        verify(localizer).windowClosed(any(WindowEvent.class));
    }

    private class TestFrame extends ExtendedFrame {

        @Override
        public void updateTexts() {
            // Just an empty implementation
        }
    }
}
