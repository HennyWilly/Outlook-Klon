package de.outlookklon.gui.components;

import javax.swing.JTable;

/**
 * Diese Klasse stellt eine JTable dar, deren Zellen sich nicht editieren
 * lassen.
 */
public class ReadOnlyJTable extends JTable {

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
