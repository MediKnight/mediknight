/*
 * @(#)$Id$
 *
 * (C)2001 Baltic Online Computer GmbH
 */
package de.bo.mediknight.util;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import de.bo.mediknight.MainFrame;
import de.baltic_online.borm.TraceConstants;

public class ErrorDisplay {
    JFrame errorFrame;
    JFrame traceFrame;
    JButton details;
    Throwable throwable;

    public ErrorDisplay(Throwable throwable,String displayMessage) {
        this(throwable,displayMessage,"Fehler",null);
    }

    public ErrorDisplay(Throwable throwable,String displayMessage,Container parent) {
        this(throwable,displayMessage,"Fehler",parent);
    }

    public ErrorDisplay(Throwable throwable,String displayMessage,String title) {
        this(throwable,displayMessage,title,null);
    }

    public ErrorDisplay(Throwable throwable,String displayMessage,String title,Container parent) {
        this.throwable = throwable;

        MainFrame.getTracer().trace(TraceConstants.ERROR,throwable);

        if ( parent == null ) {
            parent = JOptionPane.getRootFrame();
            if ( parent instanceof RootPaneContainer ) {
                parent = ((RootPaneContainer)parent).getContentPane();
            }
        }

        errorFrame = new JFrame(title);
        JPanel pane=(JPanel)errorFrame.getContentPane();
        pane.setLayout(new BorderLayout());
        details = new JButton("Details >>");
        details.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                detailsClicked();
            }
        });
        JButton ok = new JButton("Ok");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okClicked();
            }
        });
        JPanel buttonBar = new JPanel(new BorderLayout());
        buttonBar.add(ok, BorderLayout.SOUTH);
        buttonBar.add(details, BorderLayout.NORTH);
        pane.add(
            new de.bo.mediknight.widgets.JTextArea(displayMessage + "\n" + throwable.getMessage()),
            BorderLayout.CENTER);
        pane.add(buttonBar, BorderLayout.EAST);
        errorFrame.pack();
        positionErrorFrame(parent);
        errorFrame.setVisible(true);
    }

    private void positionErrorFrame(Container parent) {
        Dimension mySize = errorFrame.getSize();
        Dimension parentSize = parent.getSize();
        Point position = parent.getLocation();
        if(mySize.width>parentSize.width)
            mySize.width=parentSize.width;
        if(mySize.height>parentSize.height)
            mySize.height=parentSize.height;
        position.translate((parentSize.width-mySize.width)/2, (parentSize.height-mySize.height)/4);
        errorFrame.setLocation(position);
    }

    private void okClicked() {
        if(traceFrame!=null)
            traceFrame.dispose();
        errorFrame.dispose();
    }

    private void detailsClicked() {
        if(traceFrame==null) {
            details.setText("Details <<");
            traceFrame = new JFrame();
            StringWriter trace=new StringWriter();
            throwable.printStackTrace(new PrintWriter(trace));
            de.bo.mediknight.widgets.JTextArea details =
                new de.bo.mediknight.widgets.JTextArea(trace.toString());
            JScrollPane pane = new JScrollPane(details);
            traceFrame.getContentPane().add(pane);
            traceFrame.pack();
            positionAndResizeTraceFrame();
            traceFrame.setVisible(true);
        } else {
            traceFrame.dispose();
            traceFrame=null;
            details.setText("Details >>");
        }
    }

    private void positionAndResizeTraceFrame() {
        Point pos = errorFrame.getLocation();
        int errorHeight = errorFrame.getHeight();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = traceFrame.getSize();

        // if there isn't enough space below the error frame, put it above
        if(screenSize.height-(pos.y+errorHeight) < 150) {
            size.height=Math.min(pos.y, size.height);
            pos.translate(0, -size.height);
        }
        else {
            pos.translate(0, errorHeight);
            if(pos.y + size.height > screenSize.height) {
                size.height=screenSize.height-pos.y;
            }
        }
        traceFrame.setSize(size);
        traceFrame.setLocation(pos);
    }
}
