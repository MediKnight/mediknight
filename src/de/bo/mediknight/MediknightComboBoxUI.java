/*
 * @(#)$Id$
 * Copyright (C) 2000-2001 Baltic Online Computer GmbH.  All rights reserved.
 */
package de.bo.mediknight;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;

/**
 * A new LAF for the <code>JComboBox</code>. The arrow button is smaller and
 * editable combo boxes have the same height as other <code>JTextField</code>
 * components.  Editable components also have the same background color as
 * text fields.
 *
 * @author sma@baltic-online.de
 */
public class MediknightComboBoxUI extends MetalComboBoxUI {

    /**
     * Constructs a new UI class for combo boxes.
     */
    public static ComponentUI createUI(JComponent c) {
        return new MediknightComboBoxUI();
    }

    public PropertyChangeListener createPropertyChangeListener() {
        return new MyPropertyChangeListener();
    }

    /**
     * Returns a layout manager to layout the combo box's editor and arrow
     * button.  Derived and changed from
     * <code>BasicComboBoxUI.ComboBoxLayoutManager</code>.
     */
    protected LayoutManager createLayoutManager() {
        return new ComboBoxLayoutManager() {
            public void layoutContainer(Container parent) {
                if (arrowButton != null) {
                    JComboBox cb = (JComboBox)parent;
                    int width = cb.getWidth();
                    int height = cb.getHeight();
                    Insets insets = getInsets();
                    int buttonH = height - (insets.top + insets.bottom);
                    if (comboBox.isEditable()) {
                        int buttonW = Math.min(buttonH,
                            ((Integer)UIManager.get("ScrollBar.width")).intValue());

                        arrowButton.setBounds(width - (insets.right + buttonW),
                                              insets.top,
                                              buttonW,
                                              buttonH);
                    } else
                        arrowButton.setBounds(insets.left,
                                              insets.top,
                                              width - (insets.left + insets.right),
                                              buttonH);
                }
                if (editor != null) {
                    editor.setBounds(rectangleForCurrentValue());
                }
            }
        };
    }

    /**
     * Returns the components minimal size.  Undo the height increment which
     * is probably done because of the component's focus rectangle.  I didn't
     * found the place where the number is calculated, so I guessed the "2".
     * See <code>MediknightTheme</code> for the code to set the height of
     * <code>JTextField</code> fields by setting its margin.
     */
    public Dimension getMinimumSize(JComponent c) {
        Dimension d = super.getMinimumSize(c);
        if (comboBox.isEditable())
            d.height -= 2;
        return d;
    }


    public class MyPropertyChangeListener extends MetalComboBoxUI.MetalPropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
               super.propertyChange(e);
            Color normalColor = comboBox.getBackground();
            Color color = UIManager.getColor(comboBox.isEditable() ? "TextField.background" : "ComboBox.background");
            comboBox.setBackground(color);
            listBox.setBackground(color);
            arrowButton.setBackground(normalColor);
            comboBox.invalidate();
        }
    }

    /**
     * Create the combo box's default renderer.  Overwritten to make the
     * renderer transparent so that the combo box's background is visible.
     */
    protected ListCellRenderer createRenderer() {
        return new MediknightComboBoxRenderer.UIResource();
    }

    /**
     * The new default renderer for combo boxes.  This one makes the background
     * transparent so that the combo box's background is visible.
     */
    static class MediknightComboBoxRenderer extends BasicComboBoxRenderer {
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus)
        {
            JComponent c = (JComponent)super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
            c.setOpaque(index != -1 || isSelected);
            return c;
        }

        static class UIResource extends MediknightComboBoxRenderer implements javax.swing.plaf.UIResource {}
    }
}