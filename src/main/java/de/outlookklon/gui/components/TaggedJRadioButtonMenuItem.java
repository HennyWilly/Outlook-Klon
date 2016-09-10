package de.outlookklon.gui.components;

import javax.swing.JRadioButtonMenuItem;
import lombok.Getter;
import lombok.Setter;

/**
 * Diese Klasse stellt ein JRadioButtonMenuItem mit internem Objekt dar.
 *
 * @author Hendrik Karwanni
 */
public class TaggedJRadioButtonMenuItem extends JRadioButtonMenuItem {

    @Setter
    @Getter
    protected Object tag;

    /**
     * Erstellt ein neues TaggedJRadioButtonMenuItem mit dem übergebenen Text
     * und Selektionsstatus.
     *
     * @param text der Text des TaggedJRadioButtonMenuItems
     * @param selected der Selektionsstatus des TaggedJRadioButtonMenuItems
     */
    public TaggedJRadioButtonMenuItem(String text, boolean selected) {
        super(text, selected);
    }
}
