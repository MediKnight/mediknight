package de.baltic_online.mediknight.util;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.baltic_online.mediknight.domain.TagesDiagnose;


public class MediKnightTableModel extends AbstractTableModel {

    /**
     *
     */
    private static final long	   serialVersionUID = 1L;
    final private List< TagesDiagnose > data;
    final private Class< ? >[]	  columnClass;
    final private String[]	      columnNames;


    /**
     * @param data
     */
    public MediKnightTableModel( final List< TagesDiagnose > data ) {
	this.data = data;
	columnClass = new Class[] { Date.class, String.class };
	columnNames = new String[] { "Datum", "Tagesdiagnose" };
    }


    @Override
    public Class< ? > getColumnClass( final int col ) {
	if( col >= 0 && col < columnClass.length ) {
	    return columnClass[col];
	}

	return null;
    }


    @Override
    public int getColumnCount() {
	return 2;
    }


    @Override
    public String getColumnName( final int col ) {
	if( col >= 0 && col < columnNames.length ) {
	    return columnNames[col];
	}

	return null;
    }


    @Override
    public int getRowCount() {
	return data != null ? data.size() : 0;
    }


    public TagesDiagnose getRowObject( final int row ) {
	if( data != null && row >= 0 && row < data.size() ) {
	    return data.get( row );
	}

	return null;
    }


    @Override
    public Object getValueAt( final int row, final int col ) {
	if( data != null && row >= 0 && row < data.size() ) {
	    if( col == 0 ) {
		return data.get( row ).getDatum();
	    } else if( col == 1 ) {
		return data.get( row ).getText();
	    }
	}
	return null;
    }


    @Override
    public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
	return true;
    }


    /**
     * Translate a change in the JTable to a change of the MediKnightTableModel and store this change in the database.
     */
    @Override
    public void setValueAt( final Object value, final int row, final int col ) {
	if( value != null && data != null && row < data.size() ) {
	    if( col >= 0 && col < 2 ) {
		final TagesDiagnose currentDiagnose = data.get( row );

		switch( col ) {
		    case 0: // TODO Date Datentyp im Programm gem. Java-Standards
			    // verwenden und nicht mit dem SQL-DT an Stellen
			    // arbeiten, die deisen niht erfordern!
			final Date dt = (Date) value;
			final LocalDate ldate = Instant.ofEpochMilli( dt.getTime() ).atZone( ZoneId.systemDefault() ).toLocalDate();
			final java.sql.Date sdt = java.sql.Date.valueOf( ldate );

			currentDiagnose.setDatum( sdt );
			break;

		    case 1:
			currentDiagnose.setText( (String) value );
			break;
		}

		try {
		    currentDiagnose.save();
		} catch( final SQLException ex ) {
		    ex.printStackTrace();
		}
	    }
	    fireTableCellUpdated( row, col );
	}
    }

}
