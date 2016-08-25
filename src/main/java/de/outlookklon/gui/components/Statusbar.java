package de.outlookklon.gui.components;

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
        setPreferredSize(new Dimension(10, 23));

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
        g.setColor(new Color(156, 154, 140));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(196, 194, 183));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(218, 215, 201));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(233, 231, 217));
        g.drawLine(0, y, getWidth(), y);

        y = getHeight() - 3;
        g.setColor(new Color(233, 232, 218));
        g.drawLine(0, y, getWidth(), y);
        y++;
        g.setColor(new Color(233, 231, 216));
        g.drawLine(0, y, getWidth(), y);
        y = getHeight() - 1;
        g.setColor(new Color(221, 221, 220));
        g.drawLine(0, y, getWidth(), y);
    }

    class AngledLinesWindowsCornerIcon implements Icon {

        private static final int WIDTH = 13;
        private static final int HEIGHT = 13;

        private final Color WHITE_LINE_COLOR = new Color(255, 255, 255);
        private final Color GRAY_LINE_COLOR = new Color(172, 168, 153);

        @Override
        public int getIconHeight() {
            return HEIGHT;
        }

        @Override
        public int getIconWidth() {
            return WIDTH;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            int lastX = getIconWidth() - 1;
            int lastY = getIconHeight() - 1;

            g.setColor(WHITE_LINE_COLOR);
            g.drawLine(0, lastY, lastX, 0);
            g.drawLine(5, lastY, lastX, 5);
            g.drawLine(10, lastY, lastX, 10);

            g.setColor(GRAY_LINE_COLOR);
            g.drawLine(1, lastY, lastX, 1);
            g.drawLine(2, lastY, lastX, 2);
            g.drawLine(3, lastY, lastX, 3);

            g.drawLine(6, lastY, lastX, 6);
            g.drawLine(7, lastY, lastX, 7);
            g.drawLine(8, lastY, lastX, 8);

            g.drawLine(lastX - 1, lastY, lastX, lastY - 1);
            g.drawLine(lastX, lastY, lastX, lastY);
        }
    }
}
