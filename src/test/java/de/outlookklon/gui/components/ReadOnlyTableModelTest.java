package de.outlookklon.gui.components;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

public class ReadOnlyTableModelTest {

    @Test
    public void shouldNotAlterCell_InModel() throws Exception {
        AbstractTableModel model = new ReadOnlyTableModel(1, 1);
        assertThat(model.getValueAt(0, 0), is(nullValue()));
        model.setValueAt(new Object(), 0, 0);
        assertThat(model.getValueAt(0, 0), is(nullValue()));
    }

    @Test
    public void shouldNotAlterCell_InTable() throws Exception {
        JTable table = new JTable(new ReadOnlyTableModel(1, 1));
        assertThat(table.getValueAt(0, 0), is(nullValue()));
        table.setValueAt(new Object(), 0, 0);
        assertThat(table.getValueAt(0, 0), is(nullValue()));
    }
}
