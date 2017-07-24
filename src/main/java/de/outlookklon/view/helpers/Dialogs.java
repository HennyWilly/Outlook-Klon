package de.outlookklon.view.helpers;

import de.outlookklon.localization.Localization;
import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Statische Hilfklasse zur Vermeidung von Redundanzen bei Dialogen.
 */
public final class Dialogs {

    private Dialogs() {
    }

    /**
     * Erstellt einen neuen Fehlerdialog mit dem gegebenen Text.
     *
     * @param text Anzuzeigender Text des Dialogs
     */
    public static void showErrorDialog(String text) {
        showErrorDialog(null, text);
    }

    /**
     * Erstellt einen neuen Fehlerdialog in der gegebenen Komponente mit dem
     * gegebenen Text.
     *
     * @param parent Vaterkomponente des Dialogs
     * @param text Anzuzeigender Text des Dialogs
     */
    public static void showErrorDialog(Component parent, String text) {
        JOptionPane.showMessageDialog(parent,
                text,
                Localization.getString("Dialog_Error"),
                JOptionPane.ERROR_MESSAGE);
    }
}
