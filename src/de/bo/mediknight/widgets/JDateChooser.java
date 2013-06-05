/*
 * @(#)$Id$
 *
 * (C)2000 Baltic Online Computer GmbH
 */
package de.bo.mediknight.widgets;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.*;

/**
 * This class implements a date chooser component.
 *
 * @autor sma@baltic-online.de
 * @author chs@baltic-online.de
 * @version 1.1
 */
public class JDateChooser extends JComponent {

    private static final long serialVersionUID = 1L;

    private Calendar calendar;

    private JButton prevYear;
    private JButton nextYear;
    private JButton prevMonth;
    private JButton nextMonth;
    private JLabel year;
    private JPanel days;

    private static final String[] names = {"So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"};
    private JLabel[] headers = new JLabel[7];
    private JLabel[] fillers = new JLabel[14];
    private JToggleButton[] buttons = new JToggleButton[31];

    private ActionListener listeners;

    /**
     * Shows and creates a date chooser dialog.
     *
     * @param parent the parent component
     * @param calendar initial date
     * @returns the selected date or <code>null</code> if nothing was chosen
     *
     * @since 1.0
     */
    public static Calendar showDialog(Component parent, Calendar calendar) {
        final JDateChooser d = new JDateChooser(calendar);
        final JOptionPane op = new JOptionPane(
            d, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[0]);
        d.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    op.setValue(d.getCalendar());
            }
        });
        op.createDialog(JOptionPane.getFrameForComponent(parent), "Datum auswählen").show();
        return (Calendar)op.getValue();
    }

    /**
     * Constructs a new date chooser component based on the current date.
     *
     * @since 1.0
     */
    public JDateChooser() {
        this(Calendar.getInstance());
    }

    /**
     * Constructs a new date chooser component based on the specified date.
     *
     * @param calendar the initial date
     *
     * @since 1.0
     */
    public JDateChooser(Calendar calendar) {
        this.calendar = calendar;
        createUI();
    }

    /**
     * Returns the component's current date value.
     *
     * @since 1.0
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * Sets the component's current date value.  If called while showing the
     * component, it will update the calendar view.
     *
     * @since 1.0
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        if (calendar != null)
            updateCalendar();
    }

    /**
     * Creates the component's UI.
     *
     * @see #createNavigator
     * @see #createDaysPanel
     * @since 1.0
     */
    private void createUI() {
        setLayout(new BorderLayout());
        add(createNavigator(), BorderLayout.NORTH);
        add(createDaysPanel(), BorderLayout.CENTER);
        //add(createButtonPanel(), BorderLayout.SOUTH);
        updateCalendar();
    }

    /**
     * Creates the component's UI, actually the navigator panel which is located
     * above the days panel.  It consists of
     * <ul>
     * <li>a button to go back one year
     * <li>a button to go back one month
     * <li>a label with the currently shown month and year
     * <li>a button to advance on month
     * <li>a button to advance on year
     * </ul>
     *
     * @see #createDaysPanel
     * @since 1.0
     */
    private JPanel createNavigator() {
        JPanel p = new JPanel(new GridBagLayout());

        ActionListener l = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == prevYear)
                    calendar.add(Calendar.YEAR, -1);
                else if (e.getSource() == nextYear)
                    calendar.add(Calendar.YEAR, +1);
                if (e.getSource() == prevMonth)
                    calendar.add(Calendar.MONTH, -1);
                else if (e.getSource() == nextMonth)
                    calendar.add(Calendar.MONTH, +1);
                updateCalendar();
            }
        };

        prevYear = new ArrowButton("<<", l);
        nextYear = new ArrowButton(">>", l);
        prevMonth = new ArrowButton("<", l);
        nextMonth = new ArrowButton(">", l);

        year = new JLabel("", JLabel.CENTER);
        year.setBorder(BorderFactory.createEtchedBorder());
        year.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (!e.isPopupTrigger())
                    return;
                yearPopup(e);
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = c.gridy = c.gridwidth = c.gridheight = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        p.add(prevYear, c);
        c.gridx++;
        p.add(prevMonth, c);
        c.gridx++;
        c.weightx = 1.0;
        p.add(year, c);
        c.gridx++;
        c.weightx = 0.0;
        p.add(nextMonth, c);
        c.gridx++;
        p.add(nextYear, c);
        return p;
    }

    private void yearPopup(MouseEvent e) {
        String yearString = JOptionPane.showInputDialog(this, "Neues Jahr:");
        try {
            calendar.set(Calendar.YEAR, Integer.parseInt(yearString));
            updateYear();
        } catch (NumberFormatException ex) {}
    }

    /**
     * Creates the component's UI, actually the days panel which is located
     * below the navigator panel.  It consists of up to 31 buttons for each
     * day in the month.  The current day is selected.  Clicking a button
     * fires the component's <code>ActionEvent</code>.
     *
     * @see #createNavigator
     * @see #updateCalendar
     * @since 1.0
     */
    private JPanel createDaysPanel() {
        for (int i = 0; i < 7; i++)
            headers[i] = new JLabel(names[i], JLabel.CENTER);
        for (int i = 0; i < 14; i++)
            fillers[i] = new JLabel();

        ButtonGroup group = new ButtonGroup();
        ActionListener l = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AbstractButton b = (AbstractButton)e.getSource();
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(b.getText()));
                fireActionPerformed();
            }
        };
        Insets i0 = new Insets(0, 1, 0, 1);
        Font f0 = UIManager.getFont("ToggleButton.font").deriveFont(Font.PLAIN);
        for (int i = 0; i < 31; i++) {
            buttons[i] = new JToggleButton(String.valueOf(i + 1));
            buttons[i].setMargin(i0);
            buttons[i].setFont(f0);
            buttons[i].addActionListener(l);
            group.add(buttons[i]);
        }
        buttons[calendar.get(Calendar.DAY_OF_MONTH) - 1].setSelected(true);

        days = new JPanel(new GridLayout(7, 7));
        return days;
    }

    /**
     * Creates the buttons panel which is located below the days panel.
     * Actually it includes the OK-button for canceling;
     * Clicking a button fires the component's <code>ActionEvent</code>.
     *
     * @see #createNavigator
     * @see #createDaysPanel
     * @since 1.0
     */
    private JPanel createButtonPanel () {
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                fireActionPerformed();
            }
        });
        JButton cancelButton = new JButton("Abbrechen");
        cancelButton.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                fireActionPerformed();
            }
        });
        JPanel buttonGrid = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonGrid.add(okButton);
        buttonGrid.add(cancelButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(buttonGrid);
        return buttonPanel;
    }

    /**
     * Updates the component after changes to the calendar object.
     *
     * @see #updateYear
     * @see #updateDays
     * @since 1.0
     */
    private void updateCalendar() {
        updateYear();
        updateDays();
        revalidate();
    }

    private static final DateFormat yearFmt = new SimpleDateFormat("MMM, yyyy");

    /**
     * Updates the month and year view in the component's navigator panel. This
     * must be called after a new month and/or year as been set in the calendar
     * object.
     *
     * @since 1.0
     */
    private void updateYear() {
        if (year == null)
            return;
        year.setText(yearFmt.format(calendar.getTime()));
    }

    /**
     * Updates the day button layout in the component's days panel.  This must
     * be called after a new month and/or year as been set in the calendar
     * object.
     *
     * @since 1.0
     */
    private void updateDays() {
        if (days == null)
            return;
        days.removeAll();
        Calendar c = (Calendar)calendar.clone();
        for (int i = 0; i < 7; i++) {
            days.add(headers[(c.getFirstDayOfWeek() + i - 1) % 7]);
        }
        c.set(Calendar.DAY_OF_MONTH, 1);
        int stop = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
        int j = 0;
        int cnt = 0;
        for (int i = 0; i < stop; i++) {
            days.add(fillers[j++]);
            cnt++;
        }
        stop = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < stop; i++) {
            days.add(buttons[i]);
            cnt++;
        }
        while (cnt < 42) {
            days.add(fillers[j++]);
            cnt++;
        }
    }

    /**
     * Adds a new <code>ActionListener</code> to this component.  An
     * <code>ActionEvent</code> is fired every time a day button is clicked.
     *
     * @param l the action listener to add
     *
     * @since 1.0
     */
    public void addActionListener(ActionListener l) {
        listeners = AWTEventMulticaster.add(listeners, l);
    }

    /**
     * Removes an <code>ActionListener</code> from this component.
     *
     * @param l the action listener to remove
     *
     * @since 1.0
     */
    public void removeActionListener(ActionListener l) {
        listeners = AWTEventMulticaster.remove(listeners, l);
    }

    /**
     * Fires an <code>ActionEvent</code> and notifies all registered listeners.
     *
     * @since 1.0
     */
    private void fireActionPerformed() {
        if (listeners != null)
            listeners.actionPerformed(new ActionEvent(this, 0, null));
    }

    /**
     * This class implements the navigator panel's buttons which have the
     * following features:  They have no margin, are always square and are
     * autorepeating, that is, as long as they're pressed, they'll fire
     * action events.
     *
     * @since 1.0
     * @version 1.0
     */
    public static class ArrowButton extends JButton implements ActionListener {
        private static final long serialVersionUID = 1L;
        private static final Insets i0 = new Insets(0, 0, 0, 0);
        private javax.swing.Timer timer;
        private int delay;

        /**
         * Constructs a new button.
         * @param text the button label
         * @param l the action listener this button is connected to
         */
        public ArrowButton(String text, ActionListener l) {
            super(text);
            setMargin(i0);
            this.addActionListener(l);
            timer = new javax.swing.Timer(80, this);
        }

        /**
         * Returns the button's preferred size which is always a square.
         * @return the button's preferred size - a square.
         *
         * @since 1.0
         */
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            if (d.width < d.height)
                d.width = d.height;
            else
                d.height = d.width;
            return d;
        }

        /**
         * Processes a mouse event - overwritten from superclass to enable
         * auto repeat, that is, as long as the button is pressed, it will
         * fire action events.
         *
         * @see #timer
         * @see #actionPerformed
         * @since 1.0
         */
        protected void processMouseEvent(MouseEvent e) {
            // use timer to generate multiple clicks while mouse is pressed
            super.processMouseEvent(e);
            if (e.getID() == MouseEvent.MOUSE_PRESSED) {
                delay = 0;
                timer.start();
            }
            if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                timer.stop();
            }
        }

        /**
         * This method gets triggered by the timer.
         *
         * @see #processMouseEvent
         * @since 1.0
         */
        public void actionPerformed(ActionEvent e) {
            // use a short delay...
            if (delay++ < 4)
                return;
            // ...before generating multiple clicks while mouse is down
            this.fireActionPerformed(new ActionEvent(this, 0, null));
        }
    }


    // ----------------------------------------------------------------------
    /**
     * main() method to test this class.
     *
     * @since 1.0
     */
     public static void main(String[] args) {
        final JFrame f = new JFrame("Test");
        JButton b = new JButton("Click");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Calendar c = showDialog(f, Calendar.getInstance());
                if (c == null)
                    System.out.println("CANCEL");
                else
                    System.out.println(c.getTime());
            }
        });
        f.getContentPane().add(b);
        f.setSize(200, 100);
        f.show();
    }
}