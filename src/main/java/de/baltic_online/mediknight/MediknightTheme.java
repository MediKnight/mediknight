/*
 * @(#)$Id$
 * Copyright (C) 2000-2001 Baltic Online Computer GmbH.  All rights reserved.
 */
package main.java.de.baltic_online.mediknight;

import java.awt.Font;
import java.util.Properties;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;


/**
 * This <i>Metal Look and Feel</i> theme is used to give our application a unique look. Use <code>MediknightTheme.install()</code> to install this theme. The
 * following features are redefined:
 * <ul>
 * <li>Tahoma is used as font for labels and input fields.
 * <li>The primary color is red, not Sun blue; the secondary color is steel-blue.
 * <li>The label color is black (as in JDK 1.4).
 * <li>Scrollbars have a different button layout.
 * <li>OptionPanes have right aligned buttons; standard labels are German.
 * <li>Editable Combo boxes have a smaller size and are colored like text fields.
 * <li>Push buttons and toggle button have a gradient paints.
 * <li>Menu Bar has gradient paint.
 * <li>Slightly larger edit fields which don't look so cramped.
 * </ul>
 * 
 * @see MediknightScrollBarUI
 * @see MediknightOptionPaneUI
 * @see MediknightComboBoxUI
 * @see MediknightButtonUI
 * @see MediknightMenuBarUI
 * @see MediknightMenuUI
 *
 * @author sma@baltic-online.de
 */
public class MediknightTheme extends DefaultMetalTheme {

    // green
    private static ColorUIResource      primary1;

    private static ColorUIResource      primary2;

    // aqua
    /*
     * private final ColorUIResource primary1 = new ColorUIResource(102, 153, 153); private final ColorUIResource primary2 = new ColorUIResource(128, 192, 192);
     * private final ColorUIResource primary3 = new ColorUIResource(159, 235, 235); private static final ColorUIResource secondary1 = new ColorUIResource(102,
     * 102, 102); private static final ColorUIResource secondary2 = new ColorUIResource(153, 153, 158); private static final ColorUIResource secondary3 = new
     * ColorUIResource(204, 204, 208);
     */

    private static ColorUIResource      primary3;
    private static ColorUIResource      secondary1;
    private static ColorUIResource      secondary2;

    private static ColorUIResource      secondary3;
    // Required to make the VM aware of all installed fonts, especially Tahoma,
    // see below
    static {
	if( System.getProperty( "java.version" ).compareTo( "1.3.0" ) < 0 ) {
	    java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	}
    }
    private static final FontUIResource controlTextFont = new FontUIResource( "Tahoma", Font.PLAIN, 12 );

    private static final FontUIResource userTextFont    = new FontUIResource( "Tahoma", Font.PLAIN, 12 );


    /**
     * Installs the theme.
     */
    public static void install( final Properties prop ) {
	try {
	    primary1 = new ColorUIResource( Integer.parseInt( prop.getProperty( "primary1.r" ) ), Integer.parseInt( prop.getProperty( "primary1.g" ) ),
		    Integer.parseInt( prop.getProperty( "primary1.b" ) ) );

	    primary2 = new ColorUIResource( Integer.parseInt( prop.getProperty( "primary2.r" ) ), Integer.parseInt( prop.getProperty( "primary2.g" ) ),
		    Integer.parseInt( prop.getProperty( "primary2.b" ) ) );

	    primary3 = new ColorUIResource( Integer.parseInt( prop.getProperty( "primary3.r" ) ), Integer.parseInt( prop.getProperty( "primary3.g" ) ),
		    Integer.parseInt( prop.getProperty( "primary3.b" ) ) );

	    secondary1 = new ColorUIResource( Integer.parseInt( prop.getProperty( "secondary1.r" ) ), Integer.parseInt( prop.getProperty( "secondary1.g" ) ),
		    Integer.parseInt( prop.getProperty( "secondary1.b" ) ) );

	    secondary2 = new ColorUIResource( Integer.parseInt( prop.getProperty( "secondary2.r" ) ), Integer.parseInt( prop.getProperty( "secondary2.g" ) ),
		    Integer.parseInt( prop.getProperty( "secondary2.b" ) ) );

	    secondary3 = new ColorUIResource( Integer.parseInt( prop.getProperty( "secondary3.r" ) ), Integer.parseInt( prop.getProperty( "secondary3.g" ) ),
		    Integer.parseInt( prop.getProperty( "secondary3.b" ) ) );
	} catch( final NumberFormatException e ) {
	    System.out.println( "Fehler in den Properties - Lade Default-Werte..." );
	    setColorUIResources();
	}
	MetalLookAndFeel.setCurrentTheme( new MediknightTheme() );
    }


    private static void setColorUIResources() {
	/*
	 * green primary1 = new ColorUIResource(51, 102, 51); primary2 = new ColorUIResource(102, 153, 102); primary3 = new ColorUIResource(153, 204, 153);
	 * secondary1 = new ColorUIResource(102, 102, 102); secondary2 = new ColorUIResource(153, 158, 153); secondary3 = new ColorUIResource(204, 208, 204);
	 */
	primary1 = new ColorUIResource( 87, 87, 47 );
	primary2 = new ColorUIResource( 159, 151, 111 );
	primary3 = new ColorUIResource( 199, 183, 143 );
	secondary1 = new ColorUIResource( 111, 111, 111 );
	secondary2 = new ColorUIResource( 159, 159, 159 );
	secondary3 = new ColorUIResource( 231, 215, 183 );
    }


    // Redefine defaults
    @Override
    public void addCustomEntriesToTable( final UIDefaults table ) {
	table.put( "Label.foreground", getBlack() );
	table.put( "TitledBorder.titleColor", getBlack() );
	table.put( "ScrollBarUI", "main.java.de.baltic_online.mediknight.MediknightScrollBarUI" );
	table.put( "OptionPaneUI", "main.java.de.baltic_online.mediknight.MediknightOptionPaneUI" );
	table.put( "OptionPane.yesButtonText", "Ja" );
	table.put( "OptionPane.noButtonText", "Nein" );
	table.put( "OptionPane.cancelButtonText", "Abbrechen" );
	table.put( "OptionPane.errorIcon", LookAndFeel.makeIcon( getClass(), "icons/Error.gif" ) );
	table.put( "OptionPane.informationIcon", LookAndFeel.makeIcon( getClass(), "icons/Inform.gif" ) );
	table.put( "OptionPane.warningIcon", LookAndFeel.makeIcon( getClass(), "icons/Warn.gif" ) );
	table.put( "OptionPane.questionIcon", LookAndFeel.makeIcon( getClass(), "icons/Question.gif" ) );
	table.put( "ComboBoxUI", "main.java.de.baltic_online.mediknight.MediknightComboBoxUI" );
	table.put( "ComboBox.font", table.getFont( "TextField.font" ) );
	table.put( "ComboBox.selectionBackground", primary3 );
	table.put( "ButtonUI", "main.java.de.baltic_online.mediknight.MediknightButtonUI" );
	table.put( "Button.defaultBackground", primary3 );
	table.put( "ToggleButtonUI", "main.java.de.baltic_online.mediknight.MediknightToggleButtonUI" );
	table.put( "ToggleButton.selectedBackground", primary3 );
	table.put( "MenuBarUI", "main.java.de.baltic_online.mediknight.MediknightMenuBarUI" );
	table.put( "MenuUI", "main.java.de.baltic_online.mediknight.MediknightMenuUI" );
	table.put( "List.font", table.getFont( "TextField.font" ) );
	table.put( "SplitPaneUI", "main.java.de.baltic_online.mediknight.MediknightSplitPaneUI" );

	// fine tune borders
	table.put( "Button.margin", new InsetsUIResource( 2, 16, 2, 16 ) );
	table.put( "ToggleButton.margin", table.get( "Button.margin" ) );
	table.put( "TextField.margin", new InsetsUIResource( 1, 1, 2, 2 ) );
	// XXX this makes TextAreas look right, but other panes look strange
	// now!
	table.put( "ScrollPane.viewportBorder", new BorderUIResource( new EmptyBorder( 2, 2, 2, 2 ) ) );
	table.put( "ScrollPane.background", table.get( "TextField.background" ) );
	table.put( "effect", primary2 );
    }


    @Override
    public FontUIResource getControlTextFont() {
	return controlTextFont;
    }


    /**
     * Returns the theme's name.
     */
    @Override
    public String getName() {
	return "Mediknight";
    }


    @Override
    protected ColorUIResource getPrimary1() {
	return primary1;
    }


    @Override
    protected ColorUIResource getPrimary2() {
	return primary2;
    }


    @Override
    protected ColorUIResource getPrimary3() {
	return primary3;
    }


    @Override
    protected ColorUIResource getSecondary1() {
	return secondary1;
    }


    @Override
    protected ColorUIResource getSecondary2() {
	return secondary2;
    }


    @Override
    protected ColorUIResource getSecondary3() {
	return secondary3;
    }


    @Override
    public FontUIResource getUserTextFont() {
	return userTextFont;
    }
}