package de.outlookklon.localization;

import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import lombok.NonNull;

/**
 * Diese statische Klasse dient dem Zugriff auf lokalisierte Strings.
 */
public final class Localization {

    private static final String BUNDLE_NAME = "OutlookKlon";
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    private static final Set<ILocalizable> LOCALIZABLES = new HashSet<>();

    private static ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME);

    private Localization() {
    }

    /**
     * Fügt das übergebene lokalisierbare Objekt dem Localizer hinzu.
     *
     * @param localizable Hinzuzufügendes lokalisierbares Objekt
     */
    public static void addLocalizable(@NonNull ILocalizable localizable) {
        LOCALIZABLES.add(localizable);
    }

    /**
     * Entfernt das übergebene lokalisierbare Objekt aus dem Localizer.
     *
     * @param localizable Zu entfernendes lokalisierbares Objekt
     */
    public static void removeLocalizable(@NonNull ILocalizable localizable) {
        LOCALIZABLES.remove(localizable);
    }

    /**
     * Gibt den lokalisierten String mit dem übergebenen Schlüssel zur�ck.
     *
     * @param key Schlüssel des lokalisierten Strings.
     * @return Der lokalisierte String
     */
    public static String getString(String key) {
        return bundle.getString(key);
    }

    /**
     * Setzt die Sprache der Klasse.
     *
     * @param locale Locale für die Sprache der Klasse.
     */
    public static void setLocale(Locale locale) {
        if (!bundle.getLocale().equals(locale)) {
            ResourceBundle.clearCache();
            bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale,
                    ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));

            for (ILocalizable localizable : LOCALIZABLES) {
                localizable.updateTexts();
            }
        }
    }

    /**
     * Gibt die aktuell gesetzte Sprache der Klasse zurück.
     *
     * @return Aktuell gesetzte Sprache der Klasse
     */
    public static Locale getLocale() {
        Locale bundleLocale = bundle.getLocale();
        if (bundleLocale.getLanguage().isEmpty()) {
            return DEFAULT_LOCALE;
        }
        return bundle.getLocale();
    }

    /**
     * Gibt alle Sprachen zurück, für die eine Lokalisierung vorliegt.
     *
     * @return Alle Sprachen, für die eine Lokalisierung vorliegt
     */
    public static Locale[] getLocalizedLocales() {
        Set<Locale> resourceLocales = new HashSet<>();

        for (Locale locale : Locale.getAvailableLocales()) {
            ResourceBundle bundleForLocale = ResourceBundle.getBundle(BUNDLE_NAME, locale);
            if (bundleForLocale.getLocale().equals(locale)) {
                if (locale.getLanguage().isEmpty()) {
                    resourceLocales.add(DEFAULT_LOCALE);
                } else {
                    resourceLocales.add(locale);
                }

            }
        }

        return resourceLocales.toArray(new Locale[resourceLocales.size()]);
    }
}
