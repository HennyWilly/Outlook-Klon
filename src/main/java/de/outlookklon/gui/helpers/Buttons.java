package de.outlookklon.gui.helpers;

import de.outlookklon.Program;
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
        return getButton(Program.STRINGS.getString("Button_Done"));
    }

    public static JButton getAbortButton() {
        return getButton(Program.STRINGS.getString("Button_Abort"));
    }

    public static JButton getOkButton() {
        return getButton(Program.STRINGS.getString("Button_Ok"));
    }
}
