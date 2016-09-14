package main.java.de.baltic_online.mediknight.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.baltic_online.swing.FlexGridConstraints;
import de.baltic_online.swing.FlexGridLayout;


public class ColorPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    Color		      primary1;
    Color		      primary2;
    Color		      primary3;
    Color		      secondary1;
    Color		      secondary2;
    Color		      secondary3;

    BorderLayout	      borderLayout1    = new BorderLayout();
    JPanel		      centerPanel      = new JPanel();
    FlexGridLayout	      flexGridLayout1  = new FlexGridLayout();
    JLabel		      lbl1	       = new JLabel();
    JLabel		      lbl2	       = new JLabel();
    JTextField		      primColor1       = new JTextField();
    JLabel		      lbl3	       = new JLabel();
    JLabel		      lbl5	       = new JLabel();
    JLabel		      lbl6	       = new JLabel();
    JButton		      btn5	       = new JButton();
    JButton		      btn3	       = new JButton();
    JButton		      btn4	       = new JButton();
    JLabel		      lbl4	       = new JLabel();
    JTextField		      primColor2       = new JTextField();
    JTextField		      primColor3       = new JTextField();
    JTextField		      secColor3	       = new JTextField();
    JTextField		      secColor1	       = new JTextField();
    JTextField		      secColor2	       = new JTextField();
    JButton		      btn6	       = new JButton();
    JButton		      btn1	       = new JButton();
    JButton		      btn2	       = new JButton();

    ColorPresenter	      presenter;
    Properties		      prop;

    JPanel		      jPanel1	       = new JPanel();
    FlowLayout		      flowLayout1      = new FlowLayout();
    JLabel		      legend	       = new JLabel();


    public ColorPanel( final Properties prop ) {
	this.prop = prop;
	jbInit();
	boInit();
    }


    private void boInit() {

	legend.setText( "<html><b>Legende:</b><br>" + "Farbe 1: Rollbalken<br>" + "Farbe 2: Haupthintergrundfarbe<br>" + "Farbe 3: Aktivierte Schaltfläche<br>"
		+ "Farbe 4: Rand jeder Komponente<br>" + "Farbe 5: Farbe beim Drücken einer Schaltfläche<br>"
		+ "Farbe 6: Farbe einer inaktiven Schaltfläche und zweite Hintergrundfarbe<br><p>"
		+ "<i><font size=\"2\">Um die veränderten Farbwerte zu aktivieren, muss die Anwendung neu gestartet werden!</i></p></html>" );

	centerPanel.setBorder( BorderFactory.createEmptyBorder( 20, 20, 0, 0 ) );

	try {
	    primary1 = new Color( Integer.parseInt( prop.getProperty( "primary1.r" ) ), Integer.parseInt( prop.getProperty( "primary1.g" ) ),
		    Integer.parseInt( prop.getProperty( "primary1.b" ) ) );

	    primary2 = new Color( Integer.parseInt( prop.getProperty( "primary2.r" ) ), Integer.parseInt( prop.getProperty( "primary2.g" ) ),
		    Integer.parseInt( prop.getProperty( "primary2.b" ) ) );

	    primary3 = new Color( Integer.parseInt( prop.getProperty( "primary3.r" ) ), Integer.parseInt( prop.getProperty( "primary3.g" ) ),
		    Integer.parseInt( prop.getProperty( "primary3.b" ) ) );

	    secondary1 = new Color( Integer.parseInt( prop.getProperty( "secondary1.r" ) ), Integer.parseInt( prop.getProperty( "secondary1.g" ) ),
		    Integer.parseInt( prop.getProperty( "secondary1.b" ) ) );

	    secondary2 = new Color( Integer.parseInt( prop.getProperty( "secondary2.r" ) ), Integer.parseInt( prop.getProperty( "secondary2.g" ) ),
		    Integer.parseInt( prop.getProperty( "secondary2.b" ) ) );

	    secondary3 = new Color( Integer.parseInt( prop.getProperty( "secondary3.r" ) ), Integer.parseInt( prop.getProperty( "secondary3.g" ) ),
		    Integer.parseInt( prop.getProperty( "secondary3.b" ) ) );
	} catch( final NumberFormatException e ) {
	    e.printStackTrace();
	    primary1 = new Color( 87, 87, 47 );
	    primary2 = new Color( 159, 151, 111 );
	    primary3 = new Color( 199, 183, 143 );
	    secondary1 = new Color( 111, 111, 111 );
	    secondary2 = new Color( 159, 159, 159 );
	    secondary3 = new Color( 231, 215, 183 );
	}

	primColor1.setBackground( primary1 );
	primColor2.setBackground( primary2 );
	primColor3.setBackground( primary3 );

	secColor1.setBackground( secondary1 );
	secColor2.setBackground( secondary2 );
	secColor3.setBackground( secondary3 );

	btn1.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		primary1 = changeColor( primary1, 1 );
		update();
	    }
	} );
	btn2.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		primary2 = changeColor( primary2, 2 );
		update();
	    }
	} );
	btn3.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		primary3 = changeColor( primary3, 3 );
		update();
	    }
	} );
	btn4.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		secondary1 = changeColor( secondary1, 4 );
		update();
	    }
	} );
	btn5.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		secondary2 = changeColor( secondary2, 5 );
		update();
	    }
	} );
	btn6.addActionListener( new ActionListener() {

	    @Override
	    public void actionPerformed( final ActionEvent e ) {
		secondary3 = changeColor( secondary3, 6 );
		update();
	    }
	} );

    }


    private Color changeColor( final Color c, final int i ) {
	final Color newColor = JColorChooser.showDialog( this, "Farbe " + i + " auswählen", c );

	if( newColor == null ) {
	    return c;
	} else {
	    return newColor;
	}
    }


    public Properties getProperties() {
	prop.setProperty( "primary1.r", new Integer( primary1.getRed() ).toString() );
	prop.setProperty( "primary1.g", new Integer( primary1.getGreen() ).toString() );
	prop.setProperty( "primary1.b", new Integer( primary1.getBlue() ).toString() );
	prop.setProperty( "primary2.r", new Integer( primary2.getRed() ).toString() );
	prop.setProperty( "primary2.g", new Integer( primary2.getGreen() ).toString() );
	prop.setProperty( "primary2.b", new Integer( primary2.getBlue() ).toString() );
	prop.setProperty( "primary3.r", new Integer( primary3.getRed() ).toString() );
	prop.setProperty( "primary3.g", new Integer( primary3.getGreen() ).toString() );
	prop.setProperty( "primary3.b", new Integer( primary3.getBlue() ).toString() );

	prop.setProperty( "secondary1.r", new Integer( secondary1.getRed() ).toString() );
	prop.setProperty( "secondary1.g", new Integer( secondary1.getGreen() ).toString() );
	prop.setProperty( "secondary1.b", new Integer( secondary1.getBlue() ).toString() );
	prop.setProperty( "secondary2.r", new Integer( secondary2.getRed() ).toString() );
	prop.setProperty( "secondary2.g", new Integer( secondary2.getGreen() ).toString() );
	prop.setProperty( "secondary2.b", new Integer( secondary2.getBlue() ).toString() );
	prop.setProperty( "secondary3.r", new Integer( secondary3.getRed() ).toString() );
	prop.setProperty( "secondary3.g", new Integer( secondary3.getGreen() ).toString() );
	prop.setProperty( "secondary3.b", new Integer( secondary3.getBlue() ).toString() );
	return prop;
    }


    private void jbInit() {
	this.setLayout( borderLayout1 );
	centerPanel.setLayout( flexGridLayout1 );
	flexGridLayout1.setRows( 6 );
	flexGridLayout1.setColumns( 6 );
	flexGridLayout1.setHgap( 5 );
	flexGridLayout1.setVgap( 5 );
	lbl1.setText( "Farbe1" );
	lbl2.setText( "Farbe2" );
	lbl3.setText( "Farbe3" );
	lbl5.setText( "Farbe5" );
	lbl6.setText( "Farbe6" );
	btn5.setText( "Farbe 5 ändern" );
	btn3.setText( "Farbe 3 ändern" );
	btn4.setText( "Farbe 4 ändern" );
	lbl4.setText( "Farbe4" );
	btn6.setText( "Farbe 6 ändern" );
	btn1.setText( "Farbe 1 ändern" );
	btn2.setText( "Farbe 2 ändern" );
	secColor3.setEnabled( false );
	secColor3.setColumns( 5 );
	primColor1.setEnabled( false );
	primColor1.setColumns( 5 );
	primColor2.setEnabled( false );
	primColor2.setColumns( 5 );
	primColor3.setEnabled( false );
	primColor3.setDoubleBuffered( true );
	primColor3.setColumns( 5 );
	secColor1.setEnabled( false );
	secColor1.setColumns( 5 );
	secColor2.setEnabled( false );
	secColor2.setColumns( 5 );
	jPanel1.setLayout( flowLayout1 );
	flowLayout1.setAlignment( FlowLayout.LEFT );
	flowLayout1.setHgap( 0 );
	this.setOpaque( false );
	centerPanel.setOpaque( false );
	jPanel1.setOpaque( false );
	legend.setText( "jLabel1" );
	this.add( centerPanel, BorderLayout.CENTER );
	centerPanel.add( lbl1, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( lbl2, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( lbl3, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( primColor1, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( primColor2, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( primColor3, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( btn1, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( btn2, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( btn3, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( lbl4, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( lbl5, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( lbl6, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( secColor1, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( secColor2, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( secColor3, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( btn4, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( btn5, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	centerPanel.add( btn6, new FlexGridConstraints( 0, 0, FlexGridConstraints.C ) );
	this.add( jPanel1, BorderLayout.SOUTH );
	jPanel1.add( legend, null );
    }


    public void setPresenter( final ColorPresenter presenter ) {
	this.presenter = presenter;
    }


    private void update() {
	primColor1.setBackground( primary1 );
	primColor2.setBackground( primary2 );
	primColor3.setBackground( primary3 );

	secColor1.setBackground( secondary1 );
	secColor2.setBackground( secondary2 );
	secColor3.setBackground( secondary3 );

	revalidate();
    }
}