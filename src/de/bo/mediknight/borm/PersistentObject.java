
package de.bo.mediknight.borm;

public class PersistentObject implements Storable {

    protected Objectstore objectStore() {
        return Objectstore.current;
    }

    public void makePersistent() {
        objectStore().makePersistent(this);
    }

    public void delete() {
        objectStore().delete(this);
    }

    public boolean isPersistent() {
        return objectStore().isPersistent(this);
    }
}