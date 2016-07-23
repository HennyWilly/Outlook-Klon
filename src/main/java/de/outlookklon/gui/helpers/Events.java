package de.outlookklon.gui.helpers;

import java.awt.event.MouseEvent;

/**
 * Statische Hilfklasse zur Vermeidung von Redundanzen bei Events.
 */
public final class Events {

    private Events() {
    }

    /**
     * Gibt zurück, ob das übergebene MouseEvent einen Doppelklick darstellt.
     *
     * @param event Das zu prüfende Events
     * @return true, wenn das Event einen Doppelklick darstellt; sonst false
     */
    public static boolean isDoubleClick(MouseEvent event) {
        return event.getClickCount() == 2;
    }
}
