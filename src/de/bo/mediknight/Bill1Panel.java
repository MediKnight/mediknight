package de.bo.mediknight;

//import de.bo.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import de.bo.mediknight.domain.Rechnung;
import de.bo.mediknight.domain.RechnungsPosten;
import de.bo.mediknight.util.CurrencyNumber;
import de.bo.mediknight.util.ErrorDisplay;
import de.bo.mediknight.util.MediknightUtilities;
import de.bo.mediknight.widgets.JPanel;
import de.bo.mediknight.widgets.JButton;
import de.bo.mediknight.widgets.JScrollPane;
import de.bo.mediknight.widgets.JTable;
import de.bo.mediknight.widgets.JRadioButton;


public class Bill1Panel
    extends JPanel
    implements ChangeListener, ListSelectionListener {

    public static final int ITEM_PRICE_COLUMN = 2;
    public static final int ITEM_AMOUNT_COLUMN = 3;

    BillPresenter presenter;
    Rechnung rechnung;
    BillEntry[] entries;
    BillTableModel billModel = new BillTableModel();

    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JPanel headerPanel = new JPanel();
    JLabel headerLbl = new JLabel();
    FlowLayout flowLayout1 = new FlowLayout();
    JPanel splitPanePanel = new JPanel();
    JPanel footerPanel = new JPanel();
    JSplitPane mainSP = new JSplitPane();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    JPanel spTopPanel = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel spTopBtnPanel = new JPanel();
    JButton addBtn = new JButton();
    FlowLayout flowLayout3 = new FlowLayout();
    JPanel spBottomPanel = new JPanel();
    JScrollPane billTableSP = new JScrollPane();
    BorderLayout borderLayout2 = new BorderLayout();
    JPanel spBottomSouthPanel = new JPanel();
    JButton deleteBtn = new JButton();
    JButton createBtn = new JButton();
    JScrollPane entriesTableSP = new JScrollPane();
    JTable entriesTable = new JTable();
    JTable billTable = new JTable();
    BorderLayout borderLayout3 = new BorderLayout();
    JLabel sumLbl = new JLabel();
    JPanel southPanel = new JPanel();
    JPanel dummyPanel = new JPanel();
    JButton macroBtn = new JButton();
    JButton page2Btn = new JButton();
    JPanel buttonPanel = new JPanel();
    GridLayout gridLayout2 = new GridLayout();
    FlowLayout flowLayout2 = new FlowLayout();
    JPanel spBottomSouthBtnPanel = new JPanel();
    BorderLayout borderLayout4 = new BorderLayout();
    JPanel spBottomSouthFeePanel = new JPanel();
    FlowLayout flowLayout4 = new FlowLayout();
    JRadioButton gebuehBtn = new JRadioButton();
    JRadioButton goaeBtn = new JRadioButton();
    ButtonGroup gebBg = new ButtonGroup();
    FlowLayout flowLayout5 = new FlowLayout();
    Component component1;

    public Bill1Panel(Rechnung rechnung) {
        this.rechnung = rechnung;
        jbInit();
        billTable.setModel(billModel);
        // korrigiert Fehler beim Ausdehnen der SplitPane, da Scroller überdimensional groß
        mainSP.updateUI();
        boInit();

    }

    public void activate() {
        getRootPane().setDefaultButton(page2Btn);
    }

    public boolean isGOAE() {
        return goaeBtn.isSelected();
    }

    public void setPresenter(BillPresenter presenter) {
        if (this.presenter != null) {
            this.presenter.getModel().removeChangeListener(this);
        }

        this.presenter = presenter;
        presenter.getModel().addChangeListener(this);
        update();
    }

    /**
     * Called when the entriesTable's selection changes
     */
    public void valueChanged(ListSelectionEvent e) {
        if (entriesTable.getSelectedRow() == -1) { // no row is selected
            addBtn.setEnabled(false);
        } else {
            addBtn.setEnabled(true);
        }

        if (billTable.getSelectedRow() == -1) { // no row is selected
            deleteBtn.setEnabled(false);
            createBtn.setEnabled(false);
        } else {
            deleteBtn.setEnabled(true);
            createBtn.setEnabled(true);
        }
    }

    public void stateChanged(ChangeEvent e) {
        update();
    }

    private void calculateTotal() {
        CurrencyNumber sum = new CurrencyNumber(0.0, CurrencyNumber.EUR);
        for (int i = 0; i < entries.length; i++) {
            RechnungsPosten rp = entries[i].getItem();
            int currency = rp.isEuro() ? CurrencyNumber.EUR : CurrencyNumber.DM;
            /*            CurrencyNumber cn = new
                            CurrencyNumber(rp.getPreis()*entries[i].getCount(),currency).toEuro(); */

            CurrencyNumber cn =
                new CurrencyNumber(
                    rp.getPreis() * entries[i].getCount(),
                    currency).toCurrency(
                    MainFrame.getApplication().getCurrency()).round(2);

            sum.add(cn);
        }

        sumLbl.setText("Gesamtsumme: " + sum);
    }

    protected void update() {

        entries = BillEntry.loadEntries(presenter.getModel().getRechnung());
        setColumnView();

        billTable.getSelectionModel().addListSelectionListener(this);

        ItemTableModel itemModel;
        try {
            itemModel =
                new ItemTableModel(presenter.getModel().getRechnungsPosten());
        } catch (java.sql.SQLException e) {
            new ErrorDisplay(
                e,
                "Fehler beim Einlesen der Rechnungsposten!",
                "Fehler!",
                this);
            itemModel = new ItemTableModel(new RechnungsPosten[0]);
        }

        entriesTable.setModel(itemModel);
        entriesTable.getSelectionModel().addListSelectionListener(this);

        TableColumn column = entriesTable.getColumnModel().getColumn(3);
        column.setCellRenderer(MediknightUtilities.getTCRRight());

        column = billTable.getColumnModel().getColumn(ITEM_PRICE_COLUMN);
        column.setCellRenderer(MediknightUtilities.getTCRRight());

        column = billTable.getColumnModel().getColumn(ITEM_AMOUNT_COLUMN);
        column.setCellRenderer(MediknightUtilities.getTCRRight());

        billTable.getColumnModel().getColumn(1).setPreferredWidth(
            billTable.getPreferredSize().width);
        entriesTable.getColumnModel().getColumn(2).setPreferredWidth(
            entriesTable.getPreferredSize().width);

        gebuehBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //update();
                setColumnView();
            }
        });

        goaeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //  update();
                setColumnView();
            }
        });

        calculateTotal();
        //	sumLbl.setText( presenter.getModel().getRechnung());
    }

    private void setColumnView() {
        //        billModel = new BillTableModel(  );
        if (gebuehBtn.isSelected()) {
            billModel.columnsVisible[0] = true;
            billModel.columnsVisible[1] = false;
        } else {
            billModel.columnsVisible[0] = false;
            billModel.columnsVisible[1] = true;
        }
        //        billTable.setModel( billModel );
        billModel.fireTableDataChanged();
    }

    void boInit() {
        entriesTable.setShowHorizontalLines(false);

        entriesTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    presenter.addItem();
                }
            }
        });

        page2Btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.showLetter();
            }
        });

        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.addItem();
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.deleteEntry();
            }
        });

        createBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.createMacro();
            }
        });

        macroBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presenter.showMacro();
            }
        });

        if (rechnung.isGoae())
            goaeBtn.setSelected(true);
        else
            gebuehBtn.setSelected(true);

    }

    public RechnungsPosten getSelectedItem() {
        try {
            if (entriesTable.getSelectedRowCount() > 0)
                return presenter
                    .getModel()
                    .getRechnungsPosten()[entriesTable
                    .getSelectedRow()];
            else
                return null;
        } catch (java.sql.SQLException e) {
            new ErrorDisplay(
                e,
                "Fehler beim Kopieren der Stammdaten!",
                "Fehler!",
                this);
            return null;
        }

    }

    public RechnungsPosten[] getSelectedRechnungsPostenItems() {
        RechnungsPosten[] posten;
        try {
            int[] rows = entriesTable.getSelectedRows();
            posten = new RechnungsPosten[rows.length];
            for (int i = 0; i < rows.length; ++i) {
                posten[i] = presenter.getModel().getRechnungsPosten()[rows[i]];
            }
            return posten;
        } catch (java.sql.SQLException e) {
            new ErrorDisplay(
                e,
                "Fehler beim Einlesen der Rechnungsposten!",
                "Fehler!",
                this);
            return posten = null;
        }

    }

    public BillEntry[] getSelectedItems() {
        int[] rows = billTable.getSelectedRows();
        BillEntry[] posten = new BillEntry[rows.length];
        for (int i = 0; i < rows.length; i++) {
            posten[i] =
                ((BillTableModel) billTable.getModel()).getRowObject(rows[i]);
        }
        return posten;
    }

    public int[] getSelectedBillRows() {
        return billTable.getSelectedRows();
    }

    public int getSelectedBillRow() {
        return billTable.getSelectedRow();
    }

    JTable getBillTable() {
        return billTable;
    }

    private void jbInit() {
        component1 = Box.createHorizontalStrut(5);
        this.setLayout(gridBagLayout1);
        headerLbl.setText("Seite 1/2: Rechnungsposten");
        headerPanel.setLayout(flowLayout1);
        flowLayout1.setAlignment(FlowLayout.LEFT);
        flowLayout1.setHgap(0);
        footerPanel.setLayout(borderLayout3);
        splitPanePanel.setLayout(gridBagLayout2);
        mainSP.setOrientation(JSplitPane.VERTICAL_SPLIT);
        mainSP.setMinimumSize(new Dimension(218, 20));
        mainSP.setOpaque(false);
        mainSP.setOneTouchExpandable(true);
        spTopPanel.setLayout(borderLayout1);
        spTopPanel.setMinimumSize(new Dimension(100, 100));
        spTopPanel.setOpaque(false);
        addBtn.setEnabled(false);
        addBtn.setHorizontalAlignment(SwingConstants.RIGHT);
        addBtn.setText("Hinzuf\u00FCgen");
        spTopBtnPanel.setLayout(flowLayout3);
        flowLayout3.setAlignment(FlowLayout.RIGHT);
        flowLayout3.setHgap(0);
        billTableSP.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        billTableSP.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        billTableSP.setOpaque(false);
        spBottomPanel.setLayout(borderLayout2);
        deleteBtn.setEnabled(false);
        deleteBtn.setText("Entfernen");
        createBtn.setText("Als Baustein...");
        createBtn.setEnabled(false);
        spBottomSouthPanel.setLayout(borderLayout4);
        entriesTableSP.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        entriesTableSP.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sumLbl.setFont(new java.awt.Font("Dialog", 1, 18));
        sumLbl.setHorizontalAlignment(SwingConstants.CENTER);
        sumLbl.setText("<summe>");
        southPanel.setLayout(flowLayout2);
        macroBtn.setText("Baustein");
        page2Btn.setText("Seite 2 & Drucken");
        buttonPanel.setLayout(gridLayout2);
        flowLayout2.setAlignment(FlowLayout.RIGHT);
        flowLayout2.setHgap(0);
        flowLayout2.setVgap(0);
        gridLayout2.setHgap(10);
        spBottomSouthBtnPanel.setLayout(flowLayout5);
        spBottomSouthFeePanel.setLayout(flowLayout4);
        gebuehBtn.setOpaque(false);
        gebuehBtn.setText("GebüH");
        gebuehBtn.setActionCommand("gebuehBtn");
        goaeBtn.setOpaque(false);
        goaeBtn.setText("GOÄ");
        goaeBtn.setActionCommand("goaeBtn");
        this.setOpaque(false);
        headerPanel.setOpaque(false);
        splitPanePanel.setOpaque(false);
        spTopBtnPanel.setOpaque(false);
        spBottomPanel.setOpaque(false);
        spBottomSouthPanel.setOpaque(false);
        spBottomSouthBtnPanel.setOpaque(false);
        spBottomSouthFeePanel.setOpaque(false);
        footerPanel.setOpaque(false);
        southPanel.setOpaque(false);
        buttonPanel.setOpaque(false);
        dummyPanel.setOpaque(false);
        billTable.setOpaque(false);
        flowLayout5.setHgap(0);
        this.add(
            headerPanel,
            new GridBagConstraints(
                0,
                0,
                3,
                1,
                1.0,
                0.0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0),
                0,
                0));
        headerPanel.add(headerLbl, null);
        this.add(
            splitPanePanel,
            new GridBagConstraints(
                0,
                1,
                2,
                1,
                1.0,
                1.0,
                GridBagConstraints.WEST,
                GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0),
                0,
                0));
        splitPanePanel.add(
            mainSP,
            new GridBagConstraints(
                0,
                0,
                1,
                1,
                1.0,
                1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0),
                0,
                0));
        mainSP.add(spTopPanel, JSplitPane.TOP);
        spTopPanel.add(spTopBtnPanel, BorderLayout.SOUTH);
        spTopBtnPanel.add(addBtn, null);
        spTopPanel.add(entriesTableSP, BorderLayout.CENTER);
        mainSP.add(spBottomPanel, JSplitPane.BOTTOM);
        spBottomPanel.add(billTableSP, BorderLayout.CENTER);
        spBottomPanel.add(spBottomSouthPanel, BorderLayout.SOUTH);
        spBottomSouthPanel.add(spBottomSouthBtnPanel, BorderLayout.EAST);
        spBottomSouthBtnPanel.add(createBtn, null);
        spBottomSouthBtnPanel.add(component1, null);
        spBottomSouthBtnPanel.add(deleteBtn, null);
        spBottomSouthPanel.add(spBottomSouthFeePanel, BorderLayout.WEST);
        spBottomSouthFeePanel.add(gebuehBtn, null);
        spBottomSouthFeePanel.add(goaeBtn, null);
        billTableSP.getViewport().add(billTable, null);
        entriesTableSP.getViewport().add(entriesTable, null);
        this.add(
            footerPanel,
            new GridBagConstraints(
                1,
                2,
                1,
                1,
                1.0,
                0.0,
                GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0),
                0,
                0));
        footerPanel.add(sumLbl, BorderLayout.WEST);
        footerPanel.add(southPanel, BorderLayout.SOUTH);
        southPanel.add(buttonPanel, null);
        buttonPanel.add(macroBtn, null);
        buttonPanel.add(page2Btn, null);
        footerPanel.add(dummyPanel, BorderLayout.CENTER);
        gebBg.add(gebuehBtn);
        gebBg.add(goaeBtn);
        mainSP.setDividerLocation(200);
    }

    class ItemTableModel extends AbstractTableModel {

        final String[] columnNames =
            { "GebüH", "GoÄ", "Spezifikation", "Einzelpreis" };

        RechnungsPosten[] items;

        public ItemTableModel(RechnungsPosten[] items) {
            this.items = items;
        }

        public int getColumnCount() {
            return 4;
        }

        public int getRowCount() {
            return items.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int column) {
            RechnungsPosten entry = items[row];

            switch (column) {
                case 0 :
                    return entry.getGebueH();
                case 1 :
                    return entry.getGOAE();
                case 2 :
                    return entry.getText();
                case 3 :
                    CurrencyNumber price =
                        new CurrencyNumber(entry.getPreis(), CurrencyNumber.DM);

                    if (MainFrame.getApplication().isEuro()) {
                        price = price.toEuro();
                    }
                    return price.toString();
            }

            return null;
        }
    }

    class BillTableModel extends AbstractTableModel {

        final String[] columnNames =
            { "GebüH", "GoÄ", "Spezifikation", "Einzelpreis", "Anzahl" };

        public boolean[] columnsVisible = new boolean[5];

        public BillTableModel() {
            columnsVisible[0] = true;
            columnsVisible[1] = false;
            columnsVisible[2] = true;
            columnsVisible[3] = true;
            columnsVisible[4] = true;
        }

        public int getColumnCount() {
            int n = 0;

            for (int i = 0; i < columnsVisible.length; i++)
                if (columnsVisible[i])
                    n++;
            return n;
        }

        public int getRowCount() {
            if (entries != null)
                return entries.length;
            else
                return 0;
        }

        public String getColumnName(int col) {
            return columnNames[getNumber(col)];
        }

        public BillEntry getRowObject(int row) {
            return entries[row];
        }

        /**
         * This functiun converts a column number in the table
         * to the right number of the datas.
         */
        protected int getNumber(int col) {
            int n = col; // right number to return
            int i = 0;
            do {
                if (!(columnsVisible[i]))
                    n++;
                i++;
            } while (i < n);
            // If we are on an invisible column,
            // we have to go one step further
            while (!(columnsVisible[n]))
                n++;
            return n;
        }

        public boolean isCellEditable(int row, int col) {
            return col >= 1;
        }

        public Object getValueAt(int row, int column) {
            NumberFormat nf = MediknightUtilities.getNumberFormat();
            RechnungsPosten entry = entries[row].getItem();

            switch (column) {
                case 0 :
                    if (gebuehBtn.isSelected())
                        return entry.getGebueH();
                    else
                        return entry.getGOAE();
                case 1 :
                    return entry.getText();
                case 2 :
                    int currency =
                        entry.isEuro() ? CurrencyNumber.EUR : CurrencyNumber.DM;
                    return new CurrencyNumber(entry.getPreis(), currency)
                        .toCurrency(MainFrame.getApplication().getCurrency())
                        .toString();
                case 3 :
                    return nf.format(entries[row].getCount());
            }
            return null;
        }

        public void setValueAt(Object o, int row, int col) {

            NumberFormat nf = MediknightUtilities.getNumberFormat();
            RechnungsPosten rp = entries[row].getItem();
            boolean useEuro = MainFrame.getApplication().isEuro();

            if (col == 1) {
                rp.setText((String) o);
            }
            if (col == 2) {
                try {
                    int defaultCurrency =
                        useEuro ? CurrencyNumber.EUR : CurrencyNumber.DM;
                    CurrencyNumber cn =
                        CurrencyNumber.parse(o.toString(), defaultCurrency);
                    rp.setPreis(cn.doubleValue());
                    rp.setEuro(cn.getCurrency() == CurrencyNumber.EUR);
                } catch (IllegalArgumentException x) {
                    x.printStackTrace();
                    rp.setPreis(0.0);
                    rp.setEuro(useEuro);
                }
            }
            if (col == 3) {
                try {
                    entries[row].setCount(nf.parse(o.toString()).doubleValue());
                } catch (ParseException e) {
                    e.printStackTrace(); /** @todo Exception */
                    entries[row].setCount(1.0);
                }
            }
            BillEntry.saveEntries(rechnung, entries);
            update();
        }
    }
}
