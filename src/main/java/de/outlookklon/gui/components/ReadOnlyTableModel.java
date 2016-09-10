package de.outlookklon.gui.components;

import javax.swing.table.DefaultTableModel;

/**
 * Diese Klasse stellt ein TableModel dar, deren Zellen sich nicht editieren
 * lassen.
 */
public class ReadOnlyTableModel extends DefaultTableModel {

    /**
     * Erstellt ein neues ReadOnlyTableModel ohne Zeilen und Spalten.
     */
    public ReadOnlyTableModel() {
        super();
    }

    /**
     * Erstellt ein neues ReadOnlyTableModel mit den gegebenen Zeilen und
     * Spalten.
     *
     * @param rowCount Zeilenanzahl
     * @param columnCount Spaltenanzahl
     */
    public ReadOnlyTableModel(int rowCount, int columnCount) {
        super(rowCount, columnCount);
    }

    /**
     * Erstellt ein ReadOnlyTableModel mit so vielen Spalten wie es Elemente in
     * <code>columnNames</code> und so vielen Zeilen wie in
     * <code>rowCount</code> angegeben.
     *
     * @param columnNames Namen der Spalten
     * @param rowCount Zeilenanzahl
     */
    public ReadOnlyTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    /**
     * Erstellt ein ReadOnlyTableModel mit den gegebenen Daten und den gegebenen
     * Spaltennamen.
     *
     * @param data Daten der Tabelle
     * @param columnNames Namen der Spalten
     */
    public ReadOnlyTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // Nothing to do here
    }
}
