package de.outlookklon.gui.helpers;

import de.outlookklon.gui.components.Statusbar;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;
import lombok.NonNull;

/**
 * Diese Klasse gibt den gesetzten Text an alle registrierten Statusbars weiter.
 */
public final class StatusbarProvider {

    private static final Set<Statusbar> STATUSBARS = new HashSet<>();

    private StatusbarProvider() {
    }

    /**
     * Fügt die übergebene Statusbar dem Provider hinzu.
     *
     * @param statusbar Hinzuzufügende Statusbar
     */
    public static void addStatusbar(@NonNull Statusbar statusbar) {
        synchronized (STATUSBARS) {
            STATUSBARS.add(statusbar);
        }
    }

    /**
     * Entfernt die übergebene Statusbar aus dem Provider.
     *
     * @param statusbar Zu entfernende Statusbar
     */
    public static void removeStatusbar(@NonNull Statusbar statusbar) {
        synchronized (STATUSBARS) {
            STATUSBARS.remove(statusbar);
        }
    }

    /**
     * Gibt den übergebenen Text an alle registrierten Statusbars weiter.
     *
     * @param text Zu setzender Text
     */
    public static void setText(@NonNull String text) {
        synchronized (STATUSBARS) {
            for (Statusbar statusbar : STATUSBARS) {
                setTextInEDT(statusbar, text);
            }
        }
    }

    private static void setTextInEDT(final Statusbar statusbar, final String text) {
        if (SwingUtilities.isEventDispatchThread()) {
            statusbar.setText(text);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setTextInEDT(statusbar, text);
                }
            });
        }
    }
}
