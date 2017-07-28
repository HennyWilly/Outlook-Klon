package de.outlookklon.localization;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Dieser WindowAdapter aktualisiert bei lokalisierbaren Fenstern und Dialogen
 * die Texte bei einem Wechsel der Sprache.
 *
 * @author Hendrik Karwanni
 * @param <T> Typparameter, damit intern nicht so viel gecastet werden muss...
 *
 * @deprecated Use ObservableResourceFactory instead
 */
@Deprecated
public class WindowLocalizer<T extends Window & ILocalizable> extends WindowAdapter {

    @Override
    public void windowOpened(WindowEvent e) {
        T localizableWindow = getLocalizableWindow(e);

        if (localizableWindow != null) {
            Localization.addLocalizable(localizableWindow);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        T localizableWindow = getLocalizableWindow(e);

        if (localizableWindow != null) {
            Localization.removeLocalizable(localizableWindow);
        }
    }

    private T getLocalizableWindow(WindowEvent event) {
        Window sender = event.getWindow();
        if (ILocalizable.class.isAssignableFrom(sender.getClass())) {
            return (T) sender;
        }
        return null;
    }
}
