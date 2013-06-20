package de.bo.mediknight.tools;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import de.bo.mediknight.domain.*;
import de.bo.mediknight.util.*;
import de.bo.swing.*;


public class MasterDataSupportPanel extends JPanel implements ChangeListener, ListSelectionListener {

    private static final long serialVersionUID = 1L;
    MasterDataSupportPresenter presenter;
    RechnungsPosten[] posten;

    private static int ITEM_SPEC_COLUMN = 2;
    private static int ITEM_PRICE_COLUMN = 3;
    private static boolean useEuro = false;


    BorderLayout borderLayout1 = new BorderLayout();
    JLabel headerLbl = new JLabel();
    JPanel southPanel = new JPanel();
    FlowLayout flowLayout1 = new FlowLayout();
    JPanel buttonPanel = new JPanel();
    GridLayout gridLayout1 = new GridLayout();
    JButton deleteBtn = new JButton();
    JScrollPane tableScrollPane = new JScrollPane();
    JTable itemTable = new JTable();
    JButton addBtn = new JButton();

    public MasterDataSupportPanel( RechnungsPosten[] posten ) {
        this.posten = posten;
        jbInit();
        boInit();
    }

    public void setPresenter( MasterDataSupportPresenter presenter ) {
        if( this.presenter != null ) {
            this.presenter.getModel().removeChangeListener( this );
        }

        this.presenter = presenter;
        presenter.getModel().addChangeListener( this );
        update();
    }

    public void stateChanged( ChangeEvent e ) {
        update();
    }

    private void update() {
        posten = presenter.getModel().getRechnungsPosten();
        ItemTableModel model = new ItemTableModel( );
        itemTable.setModel( model );

        itemTable.getSelectionModel().addListSelectionListener( this );

        TableColumn column = itemTable.getColumnModel().getColumn( ITEM_PRICE_COLUMN );
        column.setCellRenderer( MediknightUtilities.getTCRRight() );

        itemTable.getColumnModel().getColumn( ITEM_SPEC_COLUMN ).setPreferredWidth(
                itemTable.getPreferredSize().width);

    }

    public void valueChanged( ListSelectionEvent e) {
        if (itemTable.getSelectedRow() == -1) {
            deleteBtn.setEnabled( false );
        } else {
            deleteBtn.setEnabled( true );
        }
    }

    public int[] getSelectedRows() {
        return itemTable.getSelectedRows();
    }

    private void jbInit() {
        headerLbl.setText("GoÄ / GebüH - Stammdatenpflege");
        this.setLayout(borderLayout1);
        southPanel.setLayout(flowLayout1);
        buttonPanel.setLayout(gridLayout1);
        deleteBtn.setText("Löschen");
        flowLayout1.setAlignment(2);
        flowLayout1.setHgap(0);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        addBtn.setText("Neu");
        gridLayout1.setHgap(5);
        this.setOpaque(false);
        southPanel.setOpaque(false);
        buttonPanel.setOpaque(false);
        this.add(headerLbl, BorderLayout.NORTH);
        this.add(southPanel, BorderLayout.SOUTH);
        southPanel.add(buttonPanel, null);
        buttonPanel.add(deleteBtn, null);
        buttonPanel.add(addBtn, null);
        this.add(tableScrollPane, BorderLayout.CENTER);
        tableScrollPane.getViewport().add(itemTable, null);
    }

    private void boInit() {
        deleteBtn.setEnabled( false );

        addBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e) {
                new AddDialog( MainTool.getFrame() );
            }
        });

        deleteBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e) {
                deleteEntries();
            }
        });
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                getRootPane().setDefaultButton( addBtn );
            }
        });
    }

    private void deleteEntries() {
        int r = JOptionPane.showConfirmDialog( this,
                itemTable.getSelectedRowCount() > 1 ? itemTable.getSelectedRowCount() + " Positionen wirklich löschen ?" :
                    itemTable.getSelectedRowCount() + " Position wirklich löschen ?" ,
                    "Stammdaten löschen",
                    JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);

        if ( r == JOptionPane.YES_OPTION )
            presenter.getModel().deleteEntries( getSelectedRows() );
    }


    class ItemTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        final String[] columnNames = {
                "GebüH", "GoÄ", "Spezifikation","Einzelpreis" };



        public ItemTableModel() {
        }

        public int getColumnCount() {
            return 4;
        }

        public int getRowCount() {
            return posten.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public boolean isCellEditable(int row, int col) {
            return true;
        }

        public Object getValueAt( int row, int column ) {

            switch( column ) {
            case 0:
                return posten[row].getGebueH();
            case 1:
                return posten[row].getGOAE();
            case 2:
                return posten[row].getText();
            case 3:
                return new CurrencyNumber( posten[row].getPreis(), CurrencyNumber.DM ).toString();
            }
            return null;
        }


        public void setValueAt(Object o, int row, int col) {

            //            boolean useEuro = MainFrame.getApplication().isEuro();
            switch (col) {
            case 0:
                posten[row].setGebueH((String) o);
                break;
            case 1:
                posten[row].setGOAE((String) o);
                break;
            case 2:
                posten[row].setText((String) o);
                break;
            case 3:
                try {
                    int defaultCurrency = useEuro ? CurrencyNumber.EUR : CurrencyNumber.DM;
                    CurrencyNumber cn = CurrencyNumber.parse(o.toString(),defaultCurrency);
                    posten[row].setPreis(cn.doubleValue());
                    posten[row].setEuro(cn.getCurrency() == CurrencyNumber.EUR);
                } catch (IllegalArgumentException x) {
                    x.printStackTrace();
                    posten[row].setPreis(0.0);
                    posten[row].setEuro(useEuro);
                }
            }
            try {
                posten[row].save();
            } catch (java.sql.SQLException e ) {
                e.printStackTrace();
            }
            presenter.getModel().setRechnungsPosten( posten );

            update();
        }
    }

    private class AddDialog extends JDialog {
        private static final long serialVersionUID = 1L;
        JPanel topPanel = new JPanel();
        JPanel downPanel = new JPanel();
        JTextField goaTF = new JTextField( 10 );
        JTextField gebuhTF = new JTextField( 10);
        JTextField specTF = new JTextField( 20 );
        JTextField priceTF = new JTextField( 10 );
        JButton cancelBtn = new JButton("Abbrechen");
        JButton okBtn = new JButton("Anlegen");

        RechnungsPosten rp;

        public AddDialog( JFrame frame ) {
            super(frame , "Artikel hinzufügen", true);
            initializeComponents();
            pack();
            setResizable(false);

            setLocationRelativeTo( frame.getContentPane() );
            setVisible(true);

        }

        void initializeComponents() {
            Container pane = getContentPane();
            pane.setLayout(new BorderLayout());

            JPanel inputPanel = new JPanel();

            FlexGridLayout fgl = new FlexGridLayout(4, 2);
            fgl.setHgap( 6 );
            fgl.setVgap( 6 );

            inputPanel.setLayout( fgl );

            inputPanel.add(new JLabel("GoÄ-Bezeichnung:"), new FlexGridConstraints(-1, 0, FlexGridConstraints.W));
            inputPanel.add(goaTF, new FlexGridConstraints(0, 0, FlexGridConstraints.W));
            inputPanel.add(new JLabel("GebüH-Bezeichnung:"), new FlexGridConstraints(-1, 0, FlexGridConstraints.W));
            inputPanel.add(gebuhTF, new FlexGridConstraints(0, 0, FlexGridConstraints.W));
            inputPanel.add(new JLabel("Spezifikation:"), new FlexGridConstraints(-1, 0, FlexGridConstraints.W));
            inputPanel.add(specTF, new FlexGridConstraints(0, 0, FlexGridConstraints.W));
            inputPanel.add(new JLabel("Preis:"), new FlexGridConstraints(-1, 0, FlexGridConstraints.W));
            inputPanel.add(priceTF, new FlexGridConstraints(0, 0, FlexGridConstraints.W));



            topPanel.add(inputPanel);
            downPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
            downPanel.add(okBtn);
            downPanel.add(cancelBtn);

            cancelBtn.addActionListener(new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dispose();
                }
            } );

            okBtn.addActionListener(new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    rp = new RechnungsPosten();
                    String gebuh = gebuhTF.getText();
                    String goa   = goaTF.getText();
                    String spec  = specTF.getText();
                    String price = priceTF.getText();

                    if (gebuh.length() > 0 || goa.length() > 0) {
                        if ( gebuh.length() > 0 )
                            rp.setGebueH( gebuh );
                        if ( goa.length() > 0 )
                            rp.setGOAE( goa );
                        if ( spec.length() > 0 )
                            rp.setText( spec );
                        if ( price.length() > 0 ) {
                            try {
                                int defaultCurrency = useEuro ? CurrencyNumber.EUR : CurrencyNumber.DM;
                                CurrencyNumber cn = CurrencyNumber.parse(price, defaultCurrency);
                                rp.setPreis(cn.doubleValue());
                                rp.setEuro(cn.getCurrency() == CurrencyNumber.EUR);
                            } catch (IllegalArgumentException x) {
                                x.printStackTrace();
                                rp.setPreis(0.0);
                                rp.setEuro(useEuro);
                            }
                        }
                        presenter.getModel().addItem( rp );
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog( null,
                                "Sie haben weder eine GoÄ- noch eine GebüH-Bezeichnung eingegeben!","Ungültiger Eintrag!",
                                JOptionPane.INFORMATION_MESSAGE);

                        return;
                    }
                }
            } );

            pane.add(topPanel, BorderLayout.NORTH);
            pane.add(downPanel, BorderLayout.SOUTH);

            Runnable runnable = new Runnable() {
                public void run() {
                    gebuhTF.requestFocus();
                }
            };
            SwingUtilities.invokeLater(runnable);
        }
    }
}