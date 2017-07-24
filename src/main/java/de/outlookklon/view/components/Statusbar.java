package de.outlookklon.view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Diese Klasse stellt eine Statusbar als JPanel dar.
 */
public class Statusbar extends JPanel {

    /**
     * Diese Konstante legt fest, das ein gesetzter Text nach einem beliebigen
     * Zeitraum nicht verschwindet, solange er nicht ersetzt wird.
     */
    public static final int NO_FADEOUT = -1;

    /**
     * Diese Konstante legt fest, das ein gesetzter Text nach 5000ms
     * verschwindet, solange er nicht vorher ersetzt wird.
     */
    public static final int DEFAULT_FADEOUT_TIME_MS = 5000;

    private static final int PREFERED_WIDTH = 10;
    private static final int PREFERED_HEIGHT = 23;

    private static final Color COLOR1 = new Color(156, 154, 140);
    private static final Color COLOR2 = new Color(196, 194, 183);
    private static final Color COLOR3 = new Color(218, 215, 201);
    private static final Color COLOR4 = new Color(233, 231, 217);
    private static final Color COLOR5 = new Color(233, 232, 218);
    private static final Color COLOR6 = new Color(233, 231, 216);
    private static final Color COLOR7 = new Color(221, 221, 220);

    private final JLabel label;

    private final Object timerLock;
    private int fadeOutTimeMS;
    private final Timer fadeOutTimer;

    /**
     * Erstellt eine neue Instanz einer Statusbar.
     */
    public Statusbar() {
        this.label = new JLabel();
        this.timerLock = new Object();
        this.fadeOutTimeMS = DEFAULT_FADEOUT_TIME_MS;
        this.fadeOutTimer = new Timer(fadeOutTimeMS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label.setText(null);
            }
        });
        this.fadeOutTimer.setRepeats(false);

        initStatusbar();
    }

    private void initStatusbar() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(PREFERED_WIDTH, PREFERED_HEIGHT));

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel(new AngledLinesWindowsCornerIcon()), BorderLayout.SOUTH);
        rightPanel.setOpaque(false);
        add(rightPanel, BorderLayout.EAST);

        add(label, BorderLayout.WEST);
        setBackground(SystemColor.control);
    }

    private void initTimer() {
        synchronized (timerLock) {
            fadeOutTimer.stop();

            if (fadeOutTimeMS > NO_FADEOUT) {
                fadeOutTimer.setDelay(fadeOutTimeMS);
                fadeOutTimer.start();
            }
        }
    }

    /**
     * Setzt den Text der Statusbar.
     *
     * @param text
     */
    public void setText(String text) {
        label.setText(text);

        initTimer();
    }

    /**
     * Gibt den Text der Statusbar zurück.
     *
     * @return Text der Statusbar
     */
    public String getText() {
        return label.getText();
    }

    /**
     * Setzt die Zeit in Millisekunden, nach der der Text auf der Statusbar
     * automatisch verschwinden soll.
     *
     * @param timeInMs Zeit in ms, nach der der Text auf der Statusbar
     * automatisch verschwinden soll. Der Text verschwindet nicht, wenn
     * {@code -1} gesetzt wird.
     */
    public void setFadeOutTime(int timeInMs) {
        if (timeInMs < NO_FADEOUT) {
            throw new IllegalArgumentException("Invalid time");
        }
        this.fadeOutTimeMS = timeInMs;
        initTimer();
    }

    /**
     * Gitb die Zeit in Millisekunden zurück, nach der der Text auf der
     * Statusbar automatisch verschwindet.
     *
     * @return Zeit in Millisekunden, nach der der Text auf der Statusbar
     * automatisch verschwindet; oder {@code -1} wenn er nicht verschwindet.
     */
    public int getFadeOutTime() {
        return fadeOutTimeMS;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int y = 0;
        drawLine(g, COLOR1, y);
        drawLine(g, COLOR2, ++y);
        drawLine(g, COLOR3, ++y);
        drawLine(g, COLOR4, ++y);

        y = getHeight() - 1;
        drawLine(g, COLOR7, y);
        drawLine(g, COLOR6, --y);
        drawLine(g, COLOR5, --y);
    }

    private void drawLine(Graphics g, Color color, int y) {
        g.setColor(color);
        g.drawLine(0, y, getWidth(), y);
    }

    private static class AngledLinesWindowsCornerIcon implements Icon {

        private static final int WIDTH_AND_HEIGHT = 13;
        private static final int LINE_DIFF = 5;

        private static final int GRAY_R = 172;
        private static final int GRAY_G = 168;
        private static final int GRAY_B = 153;

        private final Color grayLineColor = new Color(GRAY_R, GRAY_G, GRAY_B);
        private final Color whiteLineColor = Color.WHITE;

        @Override
        public int getIconHeight() {
            return WIDTH_AND_HEIGHT;
        }

        @Override
        public int getIconWidth() {
            return WIDTH_AND_HEIGHT;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            int lastX = getIconWidth() - 1;
            int lastY = getIconHeight() - 1;

            for (int i = 0; i <= WIDTH_AND_HEIGHT; i += LINE_DIFF) {
                g.setColor(whiteLineColor);
                g.drawLine(i, lastY, lastX, i);

                g.setColor(grayLineColor);
                for (int j = 1; j < LINE_DIFF - 1 && j + i < WIDTH_AND_HEIGHT; j++) {
                    g.drawLine(j + i, lastY, lastX, j + i);
                }
            }
        }
    }
}
