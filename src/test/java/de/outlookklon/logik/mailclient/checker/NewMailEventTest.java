package de.outlookklon.logik.mailclient.checker;

import de.outlookklon.logik.mailclient.StoredMailInfo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;

public class NewMailEventTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateNewMailEvent_SourceNull() throws Exception {
        new NewMailEvent(null, "folder", mock(StoredMailInfo.class)).toString();
    }

    @Test
    public void shouldCreateNewMailEvent() throws Exception {
        MailAccountChecker sender = mock(MailAccountChecker.class);
        StoredMailInfo info = mock(StoredMailInfo.class);
        NewMailEvent event = new NewMailEvent(sender, "folder", info);

        assertThat(event.getSource(), is((Object) sender));
        assertThat(event.getFolder(), is("folder"));
        assertThat(event.getInfo(), is(info));
    }
}
