package de.outlookklon;

import static de.outlookklon.matchers.UtilityMatchers.isWellDefinedUtilityClass;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

public class ApplicationTest {

    @Test
    public void shouldCheckIfUtilityClassIsWellCoded() throws Exception {
        assertThat(Application.class, isWellDefinedUtilityClass());
    }

}
