package de.bo.mediknight;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import com.borland.jbcl.layout.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JLabel;
import de.bo.mediknight.util.ErrorDisplay;
import de.bo.mediknight.domain.*;
import de.bo.mediknight.widgets.*;

public class MedicationPanel extends JPanel implements ChangeListener, ListSelectionListener {
    MedicationPresenter presenter;

    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JPanel headerPanel = new JPanel();
    JPanel mainPanel = new JPanel();
    JPanel footerBtnPanel = new JPanel();
    JButton printBtn = new JButton();
    JSplitPane mainSP = new JSplitPane();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    JPanel spTopPanel = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel topButtonPanel = new JPanel();
    JButton addBtn = new JButton();
    FlowLayout flowLayout3 = new FlowLayout();
    JPanel bottomPanel = new JPanel();
    JScrollPane medicationSP = new JScrollPane();
    JTextArea medicationTA = new JTextArea();
    Border border1;
    GridBagLayout gridBagLayout3 = new GridBagLayout();
    JPanel jPanel1 = new JPanel();
    BorderLayout borderLayout2 = new BorderLayout();
    JPanel jPanel2 = new JPanel();
    BorderLayout borderLayout3 = new BorderLayout();
    JPanel jPanel3 = new JPanel();
    BorderLayout borderLayout4 = new BorderLayout();
    JPanel jPanel4 = new JPanel();
    BorderLayout borderLayout5 = new BorderLayout();
    BorderLayout borderLayout6 = new BorderLayout();
    JPanel jPanel5 = new JPanel();
    BorderLayout borderLayout7 = new BorderLayout();
    JTextArea textTA = new JTextArea();
    JScrollPane jScrollPane1 = new JScrollPane();
    JScrollPane specificationSP = new JScrollPane();
    JTable medicationTable = new JTable();
    JLabel headerLbl = new JLabel();
    JLabel taLabel = new JLabel();
    BorderLayout borderLayout8 = new BorderLayout();
    JUndoButton undoBtn = new JUndoButton();

    public MedicationPanel() {
    }

    public MedicationPanel( MedicationPresenter presenter ) {
        setPresenter( presenter );
        jbInit();
        boInit();
    }


    void boInit() {
        MedicationTableModel model;
        try {
            model = new MedicationTableModel( presenter.getModel().getVerordnungsposten() );
        } catch( java.sql.SQLException e ) {
            new ErrorDisplay(e, "Fehler beim Einlesen der Rechnungsposten!", "Fehler!", this);
            model = new MedicationTableModel(new VerordnungsPosten[0]);
            e.printStackTrace();
        } catch (NullPointerException ex) {
            model = new MedicationTableModel(new VerordnungsPosten[0]);
            ex.printStackTrace();
        }

        medicationTable.setModel( model );

        int insets = 0;

        for( Enumeration columns = medicationTable.getColumnModel().getColumns(); columns.hasMoreElements(); ) {
            final TableColumn column = (TableColumn) columns.nextElement();

            TableCellRenderer tcr = column.getHeaderRenderer();
            if (tcr == null)
                tcr = medicationTable.getTableHeader().getDefaultRenderer();

            Component c = tcr.getTableCellRendererComponent( medicationTable, column.getHeaderValue(), false, false, -1, 0 );
            if( c instanceof JComponent )
                insets = ((JComponent) c).getInsets().left + ((JComponent) c).getInsets().right;
            else
                insets = 4;

            Dimension d = c.getPreferredSize();
            if (column.getHeaderValue().equals( "Name" ))
                if (column.getPreferredWidth() < 100)
                    d.width = column.getPreferredWidth() * 2;
                else
                    d.width = column.getPreferredWidth();

            column.setPreferredWidth( d.width + insets);
        }
        medicationTable.setPreferredScrollableViewportSize(medicationTable.getPreferredSize());
        medicationTable.getSelectionModel().addListSelectionListener( this );

        medicationTable.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                if( e.getClickCount() == 2 ) {
                    if (textTA.getText().length() > 0)
//                        presenter.changeMedication( textTA.getText() );
			changeTA();
                }
            }
        });

        textTA.setEditable( false );
        textTA.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                if( e.getClickCount() == 2 ) {
                    if (textTA.getText().length() > 0)
//                        presenter.changeMedication( textTA.getText() );
			changeTA();
                }
            }
        });


        addBtn.setEnabled( false );
        undoBtn.setName("medicationUndoBtn");

        printBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                presenter.printMedication();
            }
        });

        addBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if (textTA.getText().length() > 0)
                  // presenter.changeMedication( textTA.getText() );
		  changeTA();
            }
        });

    }

    public void valueChanged( ListSelectionEvent e) {
        if (medicationTable.getSelectedRow() != -1) {
            textTA.setText( ((VerordnungsPosten) ((MedicationTableModel) medicationTable.getModel())
                .getRowObject(medicationTable.getSelectedRow())).getText() );
	    addBtn.setEnabled( true );
        } else
	    addBtn.setEnabled( false );
    }

    public void setFocusOnMedication() {
	SwingUtilities.invokeLater( new Runnable() {
	    public void run() {
		medicationTA.requestFocus();
		getRootPane().setDefaultButton( printBtn );
	    }
	});
    }

    public void setPresenter( MedicationPresenter presenter ) {
        if( this.presenter != null ) {
            this.presenter.getModel().removeChangeListener( this );
        }

        this.presenter = presenter;
        presenter.getModel().addChangeListener( this );
        update();
    }

    public String getVerordnungstext() {
        return medicationTA.getText();
    }

    public void stateChanged( ChangeEvent e ) {
        update();
    }

    protected void update() {
        updateTA();
    }

    void updateTA() {
        MedicationEntry[] entries = MedicationEntry.loadEntries( presenter.getModel().getVerordnung() );
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);

        for ( int i = 0; i < entries.length; i++ ) {
            writer.println(entries[i].getItem());
        }
        medicationTA.setText(sw.getBuffer().toString());
    }

    public void changeTA() {
	medicationTA.append("\n" + textTA.getText());
    }


    public String getMedication() {
        return medicationTA.getText();
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.getContentPane().add(new MedicationPanel());
        f.show();
    }

    private void jbInit() {
        border1 = BorderFactory.createEmptyBorder();
        this.setLayout(gridBagLayout1);
        headerPanel.setLayout(borderLayout6);
        printBtn.setText("Drucken");
        footerBtnPanel.setLayout(borderLayout8);
        mainPanel.setLayout(gridBagLayout2);
        mainSP.setOrientation(JSplitPane.VERTICAL_SPLIT);
        mainSP.setBorder(border1);
        mainSP.setOpaque(false);
        mainSP.setOneTouchExpandable(true);
        spTopPanel.setLayout(borderLayout1);
        spTopPanel.setMinimumSize(new Dimension(100, 100));
        spTopPanel.setOpaque(false);
        spTopPanel.setPreferredSize(new Dimension(100, 100));
        addBtn.setActionCommand("Hinzufügen");
        addBtn.setHorizontalAlignment(SwingConstants.RIGHT);
        addBtn.setText("Hinzufügen");
        topButtonPanel.setLayout(flowLayout3);
        flowLayout3.setAlignment(FlowLayout.RIGHT);
        flowLayout3.setHgap(0);
        medicationSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        medicationSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        medicationSP.setOpaque(false);
        bottomPanel.setLayout(gridBagLayout3);
        this.setOpaque(false);
        headerPanel.setOpaque(false);
        mainPanel.setOpaque(false);
        topButtonPanel.setOpaque(false);
        bottomPanel.setOpaque(false);
        footerBtnPanel.setOpaque(false);
        jPanel1.setOpaque(false);
        jPanel1.setLayout(borderLayout2);
        jPanel2.setOpaque(false);
        jPanel2.setLayout(borderLayout3);
        medicationTA.setResponsibleUndoHandler("medicationUndoBtn");
        textTA.setResponsibleUndoHandler("medicationUndoBtn");
        jPanel3.setLayout(borderLayout4);
        jPanel4.setLayout(borderLayout5);
        borderLayout4.setHgap(6);
        jPanel3.setOpaque(false);
        jPanel4.setOpaque(false);
        jPanel5.setLayout(borderLayout7);
      //  textTA.setOriginalText("");
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        specificationSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        specificationSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        specificationSP.setOpaque(false);
        jPanel5.setOpaque(false);
        textTA.setColumns(40);
        headerLbl.setToolTipText("");
        headerLbl.setHorizontalAlignment(SwingConstants.LEFT);
        headerLbl.setText("Verordnungsbaustein:");
        taLabel.setText("Verordnungstext:");
        undoBtn.setText("Rückgängig");
        this.add(headerPanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0
            ,GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        this.add(mainPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(mainSP, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 84, 20));
        mainSP.add(spTopPanel, JSplitPane.TOP);
        spTopPanel.add(topButtonPanel, BorderLayout.SOUTH);
        topButtonPanel.add(addBtn, null);
        spTopPanel.add(jPanel3, BorderLayout.CENTER);
        jPanel3.add(jPanel4, BorderLayout.EAST);
        jPanel4.add(jScrollPane1, BorderLayout.CENTER);
        jPanel4.add(taLabel, BorderLayout.NORTH);
        jScrollPane1.getViewport().add(textTA, null);
        jPanel3.add(jPanel5, BorderLayout.CENTER);
        jPanel5.add(specificationSP, BorderLayout.CENTER);
        jPanel5.add(headerLbl, BorderLayout.NORTH);
        specificationSP.getViewport().add(medicationTable, null);
        mainSP.add(bottomPanel, JSplitPane.BOTTOM);
        bottomPanel.add(medicationSP, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), -1000, -1000));
        bottomPanel.add(jPanel1, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 10, 0), 0, 0));
        jPanel1.add(jPanel2, BorderLayout.WEST);
        medicationSP.getViewport().add(medicationTA, null);
        this.add(footerBtnPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        footerBtnPanel.add(printBtn, BorderLayout.EAST);
        footerBtnPanel.add(undoBtn, BorderLayout.WEST);
        mainSP.setDividerLocation(200);
    }


    class MedicationTableModel extends AbstractTableModel {

        final String[] columnNames = {
            "Gruppe", "Nummer", "Name" };


        VerordnungsPosten[] items;

        public MedicationTableModel( VerordnungsPosten[] items ) {
            this.items = items;
        }

        public int getColumnCount() {
            return 3;
        }

        public int getRowCount() {
            return items.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public VerordnungsPosten getRowObject(int row) {
            return items[row];
        }

        public Object getValueAt( int row, int column ) {
            VerordnungsPosten entry = items[row];

            switch( column ) {
                case 0:
                    return new Integer(entry.getGruppe());
                case 1:
                    return new Integer(entry.getNummer());
                case 2:
                    return entry.getName();
            }

            return null;
        }
    }

}