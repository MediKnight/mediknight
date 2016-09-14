/*
 * @(#)$Id$
 * Copyright (C) 2000-2001 Baltic Online Computer GmbH.  All rights reserved.
 */
package main.java.de.baltic_online.mediknight;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.metal.MetalComboBoxUI;


/**
 * A new LAF for the <code>JComboBox</code>. The arrow button is smaller and editable combo boxes have the same height as other <code>JTextField</code>
 * components. Editable components also have the same background color as text fields.
 *
 * @author sma@baltic-online.de
 */
public class MediknightComboBoxUI extends MetalComboBoxUI {

    /**
     * The new default renderer for combo boxes. This one makes the background transparent so that the combo box's background is visible.
     */
    static class MediknightComboBoxRenderer extends BasicComboBoxRenderer {

	static class UIResource extends MediknightComboBoxRenderer implements javax.swing.plaf.UIResource {

	    private static final long serialVersionUID = 1L;
	}

	private static final long serialVersionUID = 1L;


	@Override
	public Component getListCellRendererComponent( @SuppressWarnings( "rawtypes" ) final JList list, // This is
													 // defined like
													 // that in
													 // BasicComboBoxRenderer
						       final Object value, final int index, final boolean isSelected, final boolean cellHasFocus ) {
	    final JComponent c = (JComponent) super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
	    c.setOpaque( index != -1 || isSelected );
	    return c;
	}
    }

    public class MyPropertyChangeListener extends MetalComboBoxUI.MetalPropertyChangeListener {

	@Override
	public void propertyChange( final PropertyChangeEvent e ) {
	    super.propertyChange( e );
	    final Color normalColor = comboBox.getBackground();
	    final Color color = UIManager.getColor( comboBox.isEditable() ? "TextField.background" : "ComboBox.background" );
	    comboBox.setBackground( color );
	    listBox.setBackground( color );
	    arrowButton.setBackground( normalColor );
	    comboBox.invalidate();
	}
    }


    /**
     * Constructs a new UI class for combo boxes.
     */
    public static ComponentUI createUI( final JComponent c ) {
	return new MediknightComboBoxUI();
    }


    /**
     * Returns a layout manager to layout the combo box's editor and arrow button. Derived and changed from <code>BasicComboBoxUI.ComboBoxLayoutManager</code>.
     */
    @Override
    protected LayoutManager createLayoutManager() {
	return new ComboBoxLayoutManager() {

	    @Override
	    public void layoutContainer( final Container parent ) {
		if( arrowButton != null ) {
		    final JComboBox< ? > cb = (JComboBox< ? >) parent;
		    final int width = cb.getWidth();
		    final int height = cb.getHeight();
		    final Insets insets = getInsets();
		    final int buttonH = height - (insets.top + insets.bottom);
		    if( comboBox.isEditable() ) {
			final int buttonW = Math.min( buttonH, ((Integer) UIManager.get( "ScrollBar.width" )).intValue() );

			arrowButton.setBounds( width - (insets.right + buttonW), insets.top, buttonW, buttonH );
		    } else {
			arrowButton.setBounds( insets.left, insets.top, width - (insets.left + insets.right), buttonH );
		    }
		}
		if( editor != null ) {
		    editor.setBounds( rectangleForCurrentValue() );
		}
	    }
	};
    }


    @Override
    public PropertyChangeListener createPropertyChangeListener() {
	return new MyPropertyChangeListener();
    }


    /**
     * Create the combo box's default renderer. Overwritten to make the renderer transparent so that the combo box's background is visible.
     */
    @SuppressWarnings( "rawtypes" )
    // Defined like this in BasicComboBoxUI
	    @Override
	    protected
	    ListCellRenderer createRenderer() {
	return new MediknightComboBoxRenderer.UIResource();
    }


    /**
     * Returns the components minimal size. Undo the height increment which is probably done because of the component's focus rectangle. I didn't found the
     * place where the number is calculated, so I guessed the "2". See <code>MediknightTheme</code> for the code to set the height of <code>JTextField</code>
     * fields by setting its margin.
     */
    @Override
    public Dimension getMinimumSize( final JComponent c ) {
	final Dimension d = super.getMinimumSize( c );
	if( comboBox.isEditable() ) {
	    d.height -= 2;
	}
	return d;
    }
}