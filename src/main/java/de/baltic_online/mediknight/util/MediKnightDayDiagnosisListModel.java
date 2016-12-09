package main.java.de.baltic_online.mediknight.util;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractListModel;

import main.java.de.baltic_online.mediknight.domain.TagesDiagnose;


public class MediKnightDayDiagnosisListModel extends AbstractListModel< String > {

    /**
     *
     */
    private static final long		serialVersionUID = 1L;
    final private List< TagesDiagnose >	data;


    /**
     * @param data
     */
    public MediKnightDayDiagnosisListModel( final List< TagesDiagnose > data ) {
	this.data = data;
    }


    @Override
    public int getSize() {
	return data != null ? data.size() : 0;
    }


    public TagesDiagnose getRowObject( final int row ) {
	if( data != null && row >= 0 && row < data.size() ) {
	    return data.get( row );
	}

	return null;
    }


    @Override
    public String getElementAt( final int index ) {
	if( data != null && index >= 0 && index < data.size() ) {
	    return data.get( index ).getDatumAsString();
	}
	return null;
    }


    public TagesDiagnose getObjectAt( final int index ) {
	if( data != null && index >= 0 && index < data.size() ) {
	    return data.get( index );
	}
	return null;
    }

}
