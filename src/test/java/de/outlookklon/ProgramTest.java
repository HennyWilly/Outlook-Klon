package de.outlookklon;

import static de.outlookklon.matchers.UtilityMatchers.isWellDefinedUtilityClass;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

public class ProgramTest {

    @Test
    public void shouldCheckIfUtilityClassIsWellCoded() throws Exception {
        assertThat(Program.class, isWellDefinedUtilityClass());
    }

}
