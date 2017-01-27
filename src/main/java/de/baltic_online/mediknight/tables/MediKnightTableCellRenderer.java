package main.java.de.baltic_online.mediknight.tables;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public interface MediKnightTableCellRenderer extends TableCellRenderer {

    public int getPreferredRowHeight( final JTable table, final int row, final int column );


    public int getPreferredRowWidth( final JTable table, final int row, final int column );

}
