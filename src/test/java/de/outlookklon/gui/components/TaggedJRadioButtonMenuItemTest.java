package de.outlookklon.gui.components;

import de.outlookklon.gui.components.TaggedJRadioButtonMenuItem;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

public class TaggedJRadioButtonMenuItemTest {

    @Test
    public void shouldSetAndGetTagData() throws Exception {
        TaggedJRadioButtonMenuItem item = new TaggedJRadioButtonMenuItem("TestLabel", true);

        Object tagObject = new Object();
        item.setTag(tagObject);
        assertThat(item.getTag(), is(equalTo(tagObject)));
    }
}
