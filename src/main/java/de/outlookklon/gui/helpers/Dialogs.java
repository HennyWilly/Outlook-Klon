package de.outlookklon.gui.helpers;

import de.outlookklon.localization.Localization;
import java.awt.Component;
import javax.swing.JOptionPane;

public final class Dialogs {

    private Dialogs() {
    }

    public static void showErrorDialog(String text) {
        showErrorDialog(null, text);
    }

    public static void showErrorDialog(Component parent, String text) {
        JOptionPane.showMessageDialog(parent,
                text,
                Localization.getString("Dialog_Error"),
                JOptionPane.ERROR_MESSAGE);
    }
}
