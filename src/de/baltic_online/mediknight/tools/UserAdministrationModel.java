package de.baltic_online.mediknight.tools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.baltic_online.mediknight.domain.User;


public class UserAdministrationModel {

    Set< ChangeListener > changeListeners = new HashSet< ChangeListener >();
    User[]		users;
    String[]	      userNames;


    public UserAdministrationModel() {
	initUserList();
	initUserNames();
    }


    public void addChangeListener( final ChangeListener l ) {
	changeListeners.add( l );
    }


    public void deleteUser( final User user ) {
	try {
	    user.delete();
	    initUserList();
	    initUserNames();
	} catch( final java.sql.SQLException e ) {
	    e.printStackTrace();
	}
    }


    void fireChangeEvent() {
	final Iterator< ChangeListener > it = changeListeners.iterator();
	final ChangeEvent e = new ChangeEvent( this );

	while( it.hasNext() ) {
	    it.next().stateChanged( e );
	}
    }


    public User getUser( final int i ) {
	return users[i];
    }


    public User[] getUsers() {
	return users;
    }


    private void initUserList() {
	try {
	    users = User.retrieve().toArray( new User[0] );
	} catch( final java.sql.SQLException e ) {
	    e.printStackTrace();
	}
    }


    private void initUserNames() {

	if( users != null && users.length > 0 ) {
	    userNames = new String[users.length];
	    for( int i = 0; i < users.length; i++ ) {
		userNames[i] = users[i].getId() + ". " + users[i].getName();
	    }
	    Arrays.sort( userNames );
	}
    }


    public void removeChangeListener( final ChangeListener l ) {
	changeListeners.remove( l );
    }


    public void saveNewUser( final User user ) {
	try {
	    user.save();
	    initUserList();
	    initUserNames();
	    fireChangeEvent();
	} catch( final java.sql.SQLException e ) {
	    e.printStackTrace();
	}
    }


    public void saveUser( final User user ) {
	try {
	    user.save();
	    initUserNames();
	    fireChangeEvent();
	} catch( final java.sql.SQLException e ) {
	    e.printStackTrace();
	}
    }
}