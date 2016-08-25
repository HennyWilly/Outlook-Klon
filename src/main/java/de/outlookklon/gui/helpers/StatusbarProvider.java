package de.outlookklon.gui.helpers;

import de.outlookklon.gui.components.Statusbar;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingUtilities;
import lombok.NonNull;

public final class StatusbarProvider {

    private static final Set<Statusbar> STATUSBARS = new HashSet<>();

    private StatusbarProvider() {
    }

    public static void addStatusbar(@NonNull Statusbar statusbar) {
        synchronized (STATUSBARS) {
            STATUSBARS.add(statusbar);
        }
    }

    public static void removeStatusbar(@NonNull Statusbar statusbar) {
        synchronized (STATUSBARS) {
            STATUSBARS.remove(statusbar);
        }
    }

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
