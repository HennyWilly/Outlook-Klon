package de.outlookklon.gui.helpers;

import static de.outlookklon.matchers.UtilityMatchers.isWellDefinedUtilityClass;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

public class DialogsTest {

    @Test
    public void shouldCheckIfUtilityClassIsWellCoded() throws Exception {
        assertThat(Dialogs.class, isWellDefinedUtilityClass());
    }
}
