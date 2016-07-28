package de.outlookklon.localization;

import static de.outlookklon.matchers.UtilityMatchers.isWellDefinedUtilityClass;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

public class LocalizationTest {

    @Test
    public void shouldCheckIfUtilityClassIsWellCoded() throws Exception {
        assertThat(Localization.class, isWellDefinedUtilityClass());
    }

    // TODO Test other methods
}
