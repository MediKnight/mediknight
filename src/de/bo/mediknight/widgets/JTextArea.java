/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * This subclass of <code>JTextArea</code> supports an easy to use
 * TextListener. Also implements the <code>MutableTextComponent</code>
 * interface and adds a popup-menu for easy cut'n'paste etc.
 *
 * @author sma@baltic-online.de
 * @author chs@baltic-online.de
 * @author es@baltic-online.de
 * @version 1.6
 * @see MutableTextComponent
 */

public class JTextArea extends javax.swing.JTextArea implements MutableTextComponent, DocumentListener {

    private TextListenerPlugin plugin;
    private String originalText = null;
    private JPopupMenu popupMenu = new JPopupMenu();
    private UndoHandler undoHandler = null;
    private String undoHandlerName = null;

    /**
     * a list of those interested in receiving <code>MutableChangeEvent</code>s
     * from this instance.
     */
    EventListenerList listenerList = new EventListenerList();

    /**
     * Construct a new <code>JTextArea</code> with an empty initial text.
     *
     * @since 1.0
     */
    public JTextArea() {
        this("");
    }

    /**
     * Construct a new <code>JTextArea</code> using the given initial text.
     *
     * @param initialText the initial text of the <code>JTextArea</code>
     *
     * @since 1.0
     */
    public JTextArea(String initialText) {
        super(initialText);

        plugin = new TextListenerPlugin(this);
        if(!initialText.equals(""))
          originalText = initialText;

        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                processMouseClick(e);
            }
        });

        // construct a popup-menu for the sake of user convenience.
        JMenuItem itemCut = new JMenuItem("Ausschneiden");
        JMenuItem itemCopy = new JMenuItem("Kopieren");
        JMenuItem itemPaste = new JMenuItem("Einfügen");
        JMenuItem itemMarkAll = new JMenuItem("Alles markieren");
        JMenuItem itemRevert = new JMenuItem("Wiederherstellen");

        itemCut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cut();
            }
        });

        itemCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copy();
            }
        });

        itemPaste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paste();
            }
        });

        itemMarkAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectAll();
            }
        });

        itemRevert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                revert();
            }
        });

        popupMenu.add(itemCut);
        popupMenu.add(itemCopy);
        popupMenu.add(itemPaste);
        popupMenu.add(itemMarkAll);
        popupMenu.addSeparator();
        popupMenu.add(itemRevert);

        setBorder(new UnderlineableBorder(getBorder(), getSelectionColor()));
        getDocument().addDocumentListener(this);

    }

    /**
     * Set the text of this <code>JTextArea</code>. Also sets the original
     * text if necessary (i.e., if it is <code>null</code>)
     *
     * @param newText the new text of the <code>JTextArea</code>
     *
     * @since 1.1
     */
    public void setText(String newText) {
        if(newText == null)
            newText = "";

        super.setText(newText);
        originalText = newText;
    }

    /**
     * update the <code>JTextArea</code>'s border, if it is an instance of
     * <code>UnderlineableBorder</code>
     *
     * @see UnderlineableBorder
     * @since 1.1
     */
    public void updateBorder() {
        Border border = getBorder();
        if(border instanceof UnderlineableBorder) {
            ((UnderlineableBorder) border).setUnderlined(
                (getOriginalText() == null) ? false : isChanged());
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                repaint();
            }
        });
    }

    /**
     * get the original text.
     *
     * @since 1.1
     */
    public String getOriginalText() {
        return originalText;
    }

    /**
     * set the original text.
     *
     * @param originalText the new original text of the <code>JTextArea</code>.
     *
     * @since 1.1
     */
    public void setOriginalText(String originalText) {
        if(originalText == null)
            originalText = "";

        this.originalText = originalText;
    }

    /**
     * Forget the original text.
     *
     * @since 1.2
     */
    public void forgetOriginalValue() {
        setOriginalText(null);
        updateBorder();
    }

    /**
     * check whether the text has been changed.
     *
     * @since 1.1
     */
    public boolean isChanged() {
        if(getOriginalText() == null)
            return false;

        return(this.isEditable() && !this.getText().equals(originalText));
    }

    /**
     * revert the text to the original text.
     *
     * @since 1.1
     */
    public void revert() {
        this.setText(originalText);
        updateBorder();
        fireMutableChanged();
    }

    /**
     * add a text listener to this <code>JTextArea</code>, using the
     * <code>TextListener</code> plugin.
     *
     * @param l the <code>TextListener</code> to add
     *
     * @since 1.0
     */
    public void addTextListener(TextListener l) {
        plugin.addTextListener(l);
    }

    /**
     * remove a text listener from this <code>JTextArea</code>, using the
     * <code>TextListener</code> plugin.
     *
     * @param l the <code>TextListener</code> to remove
     *
     * @since 1.0
     */
    public void removeTextListener(TextListener l) {
        plugin.removeTextListener(l);
    }

    /**
     * process a mouse click: show a pop-up menu on right button-click (and
     * consume that click in the process), and hide it on any click if is
     * showing (and don't consume that click in the process).
     *
     * @param e the <code>MouseEvent</code> to process
     *
     * @since 1.1
     */
    public void processMouseClick(MouseEvent e) {
        if((e.getID() == MouseEvent.MOUSE_CLICKED) && (e.getModifiers() == MouseEvent.BUTTON3_MASK)) {
          popupMenu.show(this, e.getX(), e.getY());
          popupMenu.setVisible(true);
          e.consume();
        } else if((e.getID() == MouseEvent.MOUSE_CLICKED) && popupMenu.isVisible())
          popupMenu.setVisible(false);
    }


    /**
     * Register an object to receive <code>MutableChangeEvent</code>s from us
     * in the future.
     *
     * @since 1.3
     */
    public void addMutableChangeListener(MutableChangeListener l) {
       listenerList.add(MutableChangeListener.class, l);
    }

    /**
     * Deregister an object to receive <code>MutableChangeEvent</code>s from us
     * in the future.
     *
     * @since 1.3
     */
    public void removeMutableChangeListener(MutableChangeListener l) {
        listenerList.remove(MutableChangeListener.class, l);
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.
     *
     * @since 1.3
     */
    public void fireMutableChanged() {
        if(undoHandler == null) {
            undoHandler = UndoUtilities.findUndoHandler(this);
            addMutableChangeListener(undoHandler);
        }
        MutableChangeEvent fooEvent = new MutableChangeEvent(this, isChanged());
        UndoUtilities.dispatchMutableChangeEvent(fooEvent, listenerList);
    }

    /**
     * Return the name of the <code>UndoHandler</code> object responsible for
     * handling Undo for this widget.
     *
     * @since 1.4
     */
    public String getResponsibleUndoHandler() {
        return undoHandlerName;
    }

    /**
     * Set the name of the <code>UndoHandler</code> widget responsible for
     * handling Undo for this widget.
     *
     * @param s the name of the new <code>UndoHandler</code>
     *
     * @since 1.4
     */
    public void setResponsibleUndoHandler(String s) {
        if(!isChanged()) {
            if(undoHandler != null)
                removeMutableChangeListener(undoHandler);

            undoHandlerName = s;
            undoHandler = null;
        }
    }

    /**
     * Do what's necessary when the text changes.
     *
     * @param fireAllowed should be true if firing
     * <code>MutableChangeEvent</code>s is allowed.
     *
     * @since 1.5
     */
    public void textUpdate(boolean fireAllowed) {
        if(originalText == null)
            originalText = getText();

        if(!fireAllowed)
            return;

        updateBorder();
        fireMutableChanged();
    }

    /**
     * Do what's necessary when the text changes, allowing
     * <code>MutableChangeEvents</code> to be fired.
     *
     * @since 1.5
     */
    public void textUpdate() {
        textUpdate(true);
    }

    /**
     * Commit changes made.
     *
     * @since 1.6
     */
    public void commit() {
        setOriginalText(getText());
    }

    // --- implementation of DocumentListener ---

    /**
     * this method is called whenever the text is changed by the user.
     *
     * @since 1.5
     */
    public void changedUpdate(DocumentEvent e) {
        textUpdate();
    }

    /**
     * this method is called whenever text is inserted by the user.
     *
     * @since 1.5
     */
    public void insertUpdate(DocumentEvent e) {
        textUpdate();
    }

    /**
     * this method is called whenever text is removed by the user.
     *
     * @since 1.5
     */
    public void removeUpdate(DocumentEvent e) {
        textUpdate();
    }

}