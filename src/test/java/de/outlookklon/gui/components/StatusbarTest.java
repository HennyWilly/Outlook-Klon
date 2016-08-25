package de.outlookklon.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.commons.lang3.StringUtils;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;

public class StatusbarTest {

    private Statusbar statusbar;

    @Before
    public void init() {
        statusbar = new Statusbar();
    }

    @Test
    public void shouldSetAndGetText() throws Exception {
        statusbar.setText("This is a test text");
        assertThat(statusbar.getText(), is(equalTo("This is a test text")));
    }

    @Test
    public void shouldSetAndGetFadeOutTime() throws Exception {
        statusbar.setFadeOutTime(-1);
        assertThat(statusbar.getFadeOutTime(), is(equalTo(-1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotSetFadeOutTime_IllegalValue() throws Exception {
        statusbar.setFadeOutTime(-2);
    }

    @Test
    public void shouldFadeOutText_Immediately() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                statusbar.setFadeOutTime(0);
                statusbar.setText("TestText");
            }
        }).join();

        assertThat(statusbar.getText(), is(StringUtils.EMPTY));
    }

    @Test
    public void shouldPaintMainComponent() throws Exception {
        int width = 500;
        int height = 23;

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        statusbar.setSize(width, height);
        statusbar.paint(g2);
        g2.dispose();

        Color lineColor1 = new Color(bi.getRGB(0, 0));
        Color lineColor2 = new Color(bi.getRGB(0, 1));
        Color lineColor3 = new Color(bi.getRGB(0, 2));
        Color lineColor4 = new Color(bi.getRGB(0, 3));
        Color lineColorYMinus3 = new Color(bi.getRGB(0, bi.getHeight() - 3));
        Color lineColorYMinus2 = new Color(bi.getRGB(0, bi.getHeight() - 2));
        Color lineColorYMinus1 = new Color(bi.getRGB(0, bi.getHeight() - 1));

        assertThat(lineColor1, is(equalTo(new Color(156, 154, 140))));
        assertThat(lineColor2, is(equalTo(new Color(196, 194, 183))));
        assertThat(lineColor3, is(equalTo(new Color(218, 215, 201))));
        assertThat(lineColor4, is(equalTo(new Color(233, 231, 217))));
        assertThat(lineColorYMinus3, is(equalTo(new Color(233, 232, 218))));
        assertThat(lineColorYMinus2, is(equalTo(new Color(233, 231, 216))));
        assertThat(lineColorYMinus1, is(equalTo(new Color(221, 221, 220))));
    }

    @Test
    public void shouldPaintInnerIcon() throws Exception {
        Icon innerIcon = getStatusbarIcon();

        assertThat(innerIcon, is(not(nullValue())));
        assertThat(innerIcon.getIconWidth(), is(13));
        assertThat(innerIcon.getIconHeight(), is(13));

        BufferedImage bi = new BufferedImage(innerIcon.getIconWidth(), innerIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        innerIcon.paintIcon(null, g2, 0, 0);
        g2.dispose();

        Color white = new Color(255, 255, 255);
        Color gray = new Color(172, 168, 153);

        Color lineColorW1 = new Color(bi.getRGB(0, 12));
        Color lineColorW2 = new Color(bi.getRGB(5, 12));
        Color lineColorW3 = new Color(bi.getRGB(10, 12));
        Color lineColorG1 = new Color(bi.getRGB(1, 12));
        Color lineColorG2 = new Color(bi.getRGB(2, 12));
        Color lineColorG3 = new Color(bi.getRGB(3, 12));
        Color lineColorG4 = new Color(bi.getRGB(6, 12));
        Color lineColorG5 = new Color(bi.getRGB(7, 12));
        Color lineColorG6 = new Color(bi.getRGB(8, 12));
        Color lineColorG7 = new Color(bi.getRGB(11, 12));
        Color lineColorG8 = new Color(bi.getRGB(12, 12));

        assertThat(lineColorW1, is(equalTo(white)));
        assertThat(lineColorW2, is(equalTo(white)));
        assertThat(lineColorW3, is(equalTo(white)));
        assertThat(lineColorG1, is(equalTo(gray)));
        assertThat(lineColorG2, is(equalTo(gray)));
        assertThat(lineColorG3, is(equalTo(gray)));
        assertThat(lineColorG4, is(equalTo(gray)));
        assertThat(lineColorG5, is(equalTo(gray)));
        assertThat(lineColorG6, is(equalTo(gray)));
        assertThat(lineColorG7, is(equalTo(gray)));
        assertThat(lineColorG8, is(equalTo(gray)));
    }

    private Icon getStatusbarIcon() {
        for (Component component : statusbar.getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                for (Component panelComponent : panel.getComponents()) {
                    if (panelComponent instanceof JLabel) {
                        JLabel label = (JLabel) panelComponent;
                        Icon icon = (Icon) label.getIcon();
                        if (icon != null) {
                            return icon;
                        }
                    }
                }
            }
        }

        return null;
    }
}
