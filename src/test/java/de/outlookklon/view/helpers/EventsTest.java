package de.outlookklon.view.helpers;

import static de.outlookklon.matchers.UtilityMatchers.isWellDefinedUtilityClass;
import java.awt.event.MouseEvent;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventsTest {

    @Test
    public void shouldCheckIfUtilityClassIsWellCoded() throws Exception {
        assertThat(Events.class, isWellDefinedUtilityClass());
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotGetIsDoubleClick_NullPointer() throws Exception {
        Events.isDoubleClick(null);
    }

    @Test
    public void shouldGetIsDoubleClick_DoubleClick() throws Exception {
        MouseEvent event = mock(MouseEvent.class);
        when(event.getClickCount()).thenReturn(2);

        assertThat(Events.isDoubleClick(event), is(true));
    }

    @Test
    public void shouldGetIsDoubleClick_SimpleClick() throws Exception {
        MouseEvent event = mock(MouseEvent.class);
        when(event.getClickCount()).thenReturn(1);

        assertThat(Events.isDoubleClick(event), is(false));
    }
}
