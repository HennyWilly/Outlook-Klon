package de.outlookklon.localization;

import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import lombok.NonNull;

/**
 * Diese statische Klasse dient dem Zugriff auf lokalisierte Strings.
 */
public class Localization {

    private static final String BUNDLE_NAME = "OutlookKlon";
    private static ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private static final Set<ILocalizable> LOCALIZABLES = new HashSet<>();

    /**
     * F�gt das �bergebene lokalisierbare Objekt dem Localizer hinzu.
     *
     * @param localizable Hinzuzuf�gendes lokalisierbares Objekt
     */
    public static void addLocalizable(@NonNull ILocalizable localizable) {
        LOCALIZABLES.add(localizable);
    }

    /**
     * Entfernt das �bergebene lokalisierbare Objekt aus dem Localizer.
     *
     * @param localizable Zu entfernendes lokalisierbares Objekt
     */
    public static void removeLocalizable(@NonNull ILocalizable localizable) {
        LOCALIZABLES.remove(localizable);
    }

    /**
     * Gibt den lokalisierten String mit dem �bergebenen Schl�ssel zur�ck.
     *
     * @param key Schl�ssel des lokalisierten Strings.
     * @return Der lokalisierte String
     */
    public static String getString(String key) {
        return BUNDLE.getString(key);
    }

    /**
     * Setzt die Sprache der Klasse.
     *
     * @param locale Locale f�r die Sprache der Klasse.
     */
    public static void setLocale(Locale locale) {
        if (!BUNDLE.getLocale().equals(locale)) {
            ResourceBundle.clearCache();
            BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, locale,
                    ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));

            for (ILocalizable localizable : LOCALIZABLES) {
                localizable.updateTexts();
            }
        }
    }

    /**
     * Gibt die aktuell gesetzte Sprache der Klasse zur�ck.
     *
     * @return Aktuell gesetzte Sprache der Klasse
     */
    public static Locale getLocale() {
        Locale bundleLocale = BUNDLE.getLocale();
        if (bundleLocale.getLanguage().isEmpty()) {
            return DEFAULT_LOCALE;
        }
        return BUNDLE.getLocale();
    }

    /**
     * Gibt alle Sprachen zur�ck, f�r die eine Lokalisierung vorliegt.
     *
     * @return Alle Sprachen, f�r die eine Lokalisierung vorliegt
     */
    public static Locale[] getLocalizedLocales() {
        Set<Locale> resourceLocales = new HashSet<>();

        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
                if (bundle.getLocale().equals(locale)) {
                    if (locale.getLanguage().isEmpty()) {
                        resourceLocales.add(DEFAULT_LOCALE);
                    } else {
                        resourceLocales.add(locale);
                    }

                }
            } catch (MissingResourceException ex) {
                // Ignore
            }
        }

        return resourceLocales.toArray(new Locale[resourceLocales.size()]);
    }
}
