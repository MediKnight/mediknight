package de.bo.mediknight.tools;

import java.util.*;
import javax.swing.event.*;
import de.bo.mediknight.domain.*;

public class UserAdministrationModel {

    Set changeListeners = new HashSet();
    User[] users;
    String[] userNames;

    public UserAdministrationModel() {
	initUserList();
	initUserNames();
    }

    private void initUserList() {
	try {
	    users = (User[])User.retrieve().toArray( new User[0] );
	} catch (java.sql.SQLException e) {
	    e.printStackTrace();
	}
    }

    private void initUserNames() {

	if (users != null && users.length > 0) {
	    userNames = new String[ users.length ];
	    for (int i = 0; i < users.length; i++)
		userNames[i] = users[i].getId() + ". " + users[i].getName();
	    Arrays.sort( userNames );
	}
    }

    public User[] getUsers() {
	return users;
    }

    public User getUser(int i) {
	return users[i];
    }

    public void saveUser( User user ) {
	try {
	    user.save();
	    initUserNames();
	    fireChangeEvent();
	} catch (java.sql.SQLException e) {
	    e.printStackTrace();
	}
    }

    public void saveNewUser( User user ) {
	try {
	    user.save();
	    initUserList();
	    initUserNames();
	    fireChangeEvent();
	} catch (java.sql.SQLException e) {
	    e.printStackTrace();
	}
    }

    public void deleteUser( User user ) {
	try {
	    user.delete();
	    initUserList();
	    initUserNames();
	} catch (java.sql.SQLException e) {
	    e.printStackTrace();
	}
    }


    public void addChangeListener( ChangeListener l ) {
        changeListeners.add( l );
    }

    public void removeChangeListener( ChangeListener l ) {
        changeListeners.remove( l );
    }

    void fireChangeEvent() {
        Iterator it = changeListeners.iterator();
        ChangeEvent e = new ChangeEvent( this );

        while( it.hasNext() ) {
            ((ChangeListener) it.next()).stateChanged(e);
        }
    }
}