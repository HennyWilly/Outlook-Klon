package de.outlookklon.localization;

/**
 * Dieses Interface spezifiziert eine Schnittstelle zum Update der lokalisierten
 * Texte einer Klasse.
 *
 * @deprecated Use ObservableResourceFactory instead
 */
@Deprecated
public interface ILocalizable {

    /**
     * Aktualisiert die Texte mit der neuen Lokalisierung.
     */
    void updateTexts();
}
