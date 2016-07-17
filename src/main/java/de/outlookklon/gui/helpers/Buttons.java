package de.outlookklon.gui.helpers;

import de.outlookklon.localization.Localization;
import javax.swing.JButton;

/**
 *
 */
public abstract class Buttons {

    private Buttons() {
    }

    private static JButton getButton(String text) {
        return new JButton(text);
    }

    public static JButton getDoneButton() {
        return getButton(Localization.getString("Button_Done"));
    }

    public static JButton getAbortButton() {
        return getButton(Localization.getString("Button_Abort"));
    }

    public static JButton getOkButton() {
        return getButton(Localization.getString("Button_Ok"));
    }
}
