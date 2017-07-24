package de.outlookklon.view.helpers;

import java.awt.Component;
import java.awt.FocusTraversalPolicy;
import java.util.Arrays;
import java.util.Collections;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;

public class ListFocusTraversalPolicyTest {

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateInstance_NullPointer() throws Exception {
        new ListFocusTraversalPolicy(null).toString();
    }

    @Test
    public void shouldCreateInstance_EmptyList() throws Exception {
        new ListFocusTraversalPolicy(Collections.<Component>emptyList()).toString();
    }

    @Test
    public void shouldGetComponentsAfter() throws Exception {
        Component c1 = mock(Component.class);
        Component c2 = mock(Component.class);
        Component c3 = mock(Component.class);

        FocusTraversalPolicy policy = new ListFocusTraversalPolicy(Arrays.asList(c1, c2, c3));
        assertThat(policy.getComponentAfter(null, c1), is(c2));
        assertThat(policy.getComponentAfter(null, c2), is(c3));
        assertThat(policy.getComponentAfter(null, c3), is(c1));
    }

    @Test
    public void shouldGetComponentsBefore() throws Exception {
        Component c1 = mock(Component.class);
        Component c2 = mock(Component.class);
        Component c3 = mock(Component.class);

        FocusTraversalPolicy policy = new ListFocusTraversalPolicy(Arrays.asList(c1, c2, c3));
        assertThat(policy.getComponentBefore(null, c1), is(c3));
        assertThat(policy.getComponentBefore(null, c2), is(c1));
        assertThat(policy.getComponentBefore(null, c3), is(c2));
    }

    @Test
    public void shouldGetDifferentComponents_NonEmptyList() throws Exception {
        Component c1 = mock(Component.class);
        Component c2 = mock(Component.class);
        Component c3 = mock(Component.class);

        FocusTraversalPolicy policy = new ListFocusTraversalPolicy(Arrays.asList(c1, c2, c3));
        assertThat(policy.getFirstComponent(null), is(c1));
        assertThat(policy.getDefaultComponent(null), is(c1));
        assertThat(policy.getLastComponent(null), is(c3));
    }

    @Test
    public void shouldNotGetDifferentComponents_EmptyList() throws Exception {
        FocusTraversalPolicy policy = new ListFocusTraversalPolicy(Collections.<Component>emptyList());
        assertThat(policy.getFirstComponent(null), is(nullValue()));
        assertThat(policy.getDefaultComponent(null), is(nullValue()));
        assertThat(policy.getLastComponent(null), is(nullValue()));
    }
}
