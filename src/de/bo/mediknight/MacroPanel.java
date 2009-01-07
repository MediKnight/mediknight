package de.bo.mediknight;

import java.awt.*;
import java.awt.event.*;
import javax.swing.ListSelectionModel;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import de.bo.mediknight.widgets.*;
import de.bo.mediknight.domain.*;
import de.bo.mediknight.util.*;

public class MacroPanel extends JPanel implements ChangeListener,
        ListSelectionListener {
	private static final long serialVersionUID = -6057708429859738892L;

	MacroPresenter presenter;

    BorderLayout borderLayout1 = new BorderLayout();

    JSplitPane jSplitPane1 = new JSplitPane();

    JScrollPane macroSP = new JScrollPane();

    JList keyList = new JList();

    JScrollPane itemTableSP = new JScrollPane();

    JTable itemTable = new JTable();

    JPanel jPanel1 = new JPanel();

    BorderLayout borderLayout2 = new BorderLayout();

    GridLayout gridLayout2 = new GridLayout();

    JPanel jPanel2 = new JPanel();

    JButton applyBtn = new JButton();

    FlowLayout flowLayout1 = new FlowLayout();

    JPanel buttonPanel = new JPanel();

    JPanel jPanel3 = new JPanel();

    JButton deleteBtn = new JButton();

    FlowLayout flowLayout2 = new FlowLayout();

    public MacroPanel() {
        jbInit();
        boInit();
    }

    public void setPresenter(MacroPresenter presenter) {
        if (this.presenter != null) {
            this.presenter.getModel().removeChangeListener(this);
        }

        this.presenter = presenter;
        presenter.getModel().addChangeListener(this);
        update();
    }

    public void stateChanged(ChangeEvent e) {
        update();
    }

    protected void update() {
        keyList.setListData(presenter.getModel().getComponentList().toArray());
        itemTable.setModel(new MacroTableModel());
        deleteBtn.setEnabled(false);
        applyBtn.setEnabled(false);
        if (!presenter.getModel().hasContent())
            //createBtn.setEnabled(false);
            sizingTable();
    }

    private void sizingTable() {
        TableColumn column = itemTable.getColumnModel().getColumn(3);
        column.setCellRenderer(MediknightUtilities.getTCRRight());

        column = itemTable.getColumnModel().getColumn(4);
        column.setCellRenderer(MediknightUtilities.getTCRRight());

        itemTable.getColumnModel().getColumn(2).setPreferredWidth(
                itemTable.getPreferredSize().width);
    }

    public RechnungsGruppe getSelectedRechnungsGruppe() {
        return (RechnungsGruppe) keyList.getSelectedValue();
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting())
            return;

        if (getSelectedRechnungsGruppe() == null) {
            itemTable.setModel(new MacroTableModel());
            deleteBtn.setEnabled(false);
            applyBtn.setEnabled(false);
        } else {
            MacroTableModel model = new MacroTableModel(
                    getSelectedRechnungsGruppe());
            itemTable.setModel(model);
            deleteBtn.setEnabled(true);
            applyBtn.setEnabled(true);
        }
        sizingTable();
    }

    void boInit() {
        /*
         * createBtn.addActionListener( new ActionListener() { public void
         * actionPerformed( ActionEvent e ) { presenter.createMacro(); } });
         */

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.deleteMacro();
            }
        });
        deleteBtn.setPreferredSize(applyBtn.getPreferredSize());

        applyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.addMacro();
            }
        });

        keyList.addListSelectionListener(this);

    }

    public void setFocusOnList() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (!presenter.getModel().getComponentList().isEmpty())
                    keyList.setSelectedIndex(0);
                getRootPane().setDefaultButton(applyBtn);
            }
        });
    }

    private void jbInit() {
        this.setLayout(borderLayout1);
        itemTableSP
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jSplitPane1.setOneTouchExpandable(true);
        keyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setOpaque(false);
        jPanel1.setLayout(borderLayout2);
        jPanel1.setOpaque(false);
        gridLayout2.setHgap(10);
        jPanel2.setOpaque(false);
        jPanel2.setLayout(gridLayout2);
        applyBtn.setText("Übernehmen");
        flowLayout1.setAlignment(FlowLayout.RIGHT);
        flowLayout1.setHgap(0);
        buttonPanel.setLayout(flowLayout1);
        buttonPanel.setOpaque(false);
        jPanel3.setOpaque(false);
        jPanel3.setLayout(flowLayout2);
        deleteBtn.setText("Löschen");
        flowLayout2.setAlignment(FlowLayout.LEFT);
        flowLayout2.setHgap(0);
        macroSP.getViewport().add(keyList, null);
        this.add(jSplitPane1, BorderLayout.CENTER);
        jSplitPane1.add(macroSP, JSplitPane.LEFT);
        jSplitPane1.add(itemTableSP, JSplitPane.RIGHT);
        this.add(jPanel1, BorderLayout.SOUTH);
        jPanel1.add(buttonPanel, BorderLayout.EAST);
        buttonPanel.add(jPanel2, null);
        jPanel2.add(applyBtn, null);
        jPanel1.add(jPanel3, BorderLayout.WEST);
        jPanel3.add(deleteBtn, null);
        itemTableSP.getViewport().add(itemTable, null);
        jSplitPane1.setDividerLocation(115);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.getContentPane().setLayout(new FlowLayout());
        //  f.getContentPane().add(new MacroPanel());
        f.setVisible(true);
        f.pack();
    }

    class MacroTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -974915365204385546L;

		BillEntry[] entries;

        final String[] columnNames = { "GebüH", "GoÄ", "Spezifikation",
                "Einzelpreis", "Anzahl" };

        public MacroTableModel() {
            entries = new BillEntry[0];
        }

        public MacroTableModel(RechnungsGruppe macro) {
            entries = BillEntry.loadEntries(macro);
        }

        public int getColumnCount() {
            return 5;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public int getRowCount() {
            return entries.length;
        }

        public Object getValueAt(int row, int column) {
            java.text.NumberFormat nf = MediknightUtilities.getNumberFormat();
            RechnungsPosten entry = entries[row].getItem();

            switch (column) {
            case 0:
                return entry.getGebueH();
            case 1:
                return entry.getGOAE();
            case 2:
                return entry.getText();
            case 3:
                return new CurrencyNumber(entry.getPreis(), CurrencyNumber.DM)
                        .toCurrency(MainFrame.getApplication().getCurrency())
                        .toString();
            case 4:
                //                    try {
                //                        return
                // MediknightUtilities.getNumberFormat().parse(String.valueOf(
                // entries[row].getCount()));
                //			return ((NewAppWindow)
                // AppWindow.getApplication()).getNumberFormat().parse(String.valueOf(
                // entries[row].getCount()));
                //                    } catch (java.text.ParseException e) {
                //                        return new String("0"); /** @todo Better exception */
                //                    }
                return nf.format(entries[row].getCount());
            //return );
            }

            return null;
        }
    }
}