/*
 * @(#)$Id$
 *
 * copied from Java(tm)-Spektrum 2/2001, p.56ff
 */
package main.java.de.baltic_online.mediknight.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.InsetsUIResource;


/**
 * this class implements a Look-and-Feel customizer that has the ability to read in key/value pairs for widget attributes from a file and customize a tree of
 * widgets using these. From: Java(tm)-Spektrum 2/2001, p.56ff
 *
 * @author chs@baltic-online.de
 * @version 1.0
 */
public class CopyOfLookAndFeelCustomizer {

    public static final int		LOOK_AND_FEEL	  = 0, COLOR = 1, STRING = 2, INTEGER = 3, INSETS = 4, DIMENSION = 5, ICON = 6, FONT = 7, BOOLEAN = 8;

    public static final List< String >	PREFIXES	  = Arrays.asList( new String[] { "*", "TextField", "TextArea", "Panel", "Label", "Button", "CheckBox",
	    "ComboBox", "List", "OptionPane", "FileChooser", "ColorChooser", "TitledBorder", "Table", "TableHeader", "Tree", "Desktop", "FileView",
	    "InternalFrame", "MenuItem", "ProgressBar", "Scrollbar", "ScrollPane", "SplitPane", "TabbedPane", "Separator", "Slider", "ToolBar" } );

    private final Map< String, Object >	defaultProperties = new HashMap<>();
    private String			rootName;


    @SuppressWarnings( "unchecked" )
    protected HashMap< String, Object > cloneIfNecessary( final HashMap< String, Object > defaultProperties, final HashMap< String, Object > childProperties,
							  final java.util.List< String > keys, final boolean isCloningNecessary ) {

	if( childProperties == null || childProperties.isEmpty() ) {
	    return defaultProperties;
	}

	HashMap< String, Object > newDefaultProperties = null;
	final int size = keys.size();
	for( int i = 0; i < size; i++ ) {
	    final String prefixKey = keys.get( i );
	    final HashMap< String, Object > newPropertiesForPrefix = (HashMap< String, Object >) childProperties.get( prefixKey );

	    if( newPropertiesForPrefix != null ) {

		if( newDefaultProperties == null ) {
		    if( isCloningNecessary ) {
			newDefaultProperties = (HashMap< String, Object >) defaultProperties.clone();
		    } else {
			newDefaultProperties = defaultProperties;
		    }
		}

		final HashMap< String, Object > oldPropertiesForPrefix = (HashMap< String, Object >) defaultProperties.get( prefixKey );
		if( oldPropertiesForPrefix != null ) {
		    oldPropertiesForPrefix.putAll( newPropertiesForPrefix );
		} else {
		    newDefaultProperties.put( prefixKey, newPropertiesForPrefix );
		}

	    }
	}

	if( newDefaultProperties == null ) {
	    return defaultProperties;
	} else {
	    return newDefaultProperties;
	}

    }


    @SuppressWarnings( "unchecked" )
    protected HashMap< String, Object > computeDefaultProperties( final JComponent component, final HashMap< String, Object > defaultPropertiesForComponent,
								  final HashMap< String, Object > defaultProperties, final java.util.List< String > keys ) {

	final String name = component.getName();
	if( rootName.equals( name ) ) {
	    return cloneIfNecessary( defaultPropertiesForComponent, (HashMap< String, Object >) defaultProperties.get( rootName ), keys, false );
	} else {

	    final JComponent parent = (JComponent) component.getParent();
	    final HashMap< String, Object > defaultPropertiesForParent = computeDefaultProperties( parent, defaultPropertiesForComponent, defaultProperties,
		    keys );
	    if( defaultProperties.isEmpty() ) {
		return defaultPropertiesForParent;
	    }

	    final HashMap< String, Object > defaultPropertiesForChild = (HashMap< String, Object >) ((HashMap< String, Object >) defaultProperties
		    .get( parent.getName() )).get( name );
	    defaultProperties.clear();
	    if( defaultPropertiesForChild == null ) {
		return defaultPropertiesForParent;
	    }

	    defaultProperties.put( name, defaultPropertiesForChild );
	    return cloneIfNecessary( defaultPropertiesForParent, defaultPropertiesForChild, keys, false );
	}
    }


    public void customizeComponent( final JComponent component ) {
	if( rootName == null ) {
	    return;
	}
	final String propertyKey = getPropertyKey( component );

	final HashMap< String, Object > defaultProperties = new HashMap< String, Object >();
	defaultProperties.put( rootName, this.defaultProperties.get( rootName ) );

	final HashMap< String, Object > defaultPropertiesForComponent = computeDefaultProperties( component, new HashMap< String, Object >(), defaultProperties,
		Arrays.asList( new String[] { "*", propertyKey } ) );

	if( !defaultPropertiesForComponent.isEmpty() ) {
	    customizeComponent( component, defaultPropertiesForComponent );
	}
    }


    @SuppressWarnings( "unchecked" )
    protected void customizeComponent( final JComponent component, final HashMap< String, Object > defaultProperties ) {
	final HashMap< String, Object > commonProperties = (HashMap< String, Object >) defaultProperties.get( "*" );
	final HashMap< String, Object > specialProperties = (HashMap< String, Object >) defaultProperties.get( getPropertyKey( component ) );
	HashMap< String, Object > componentProperties = null;

	if( commonProperties == null ) {
	    componentProperties = specialProperties;
	} else {
	    componentProperties = (HashMap< String, Object >) commonProperties.clone();
	    if( specialProperties != null ) {
		componentProperties.putAll( specialProperties );
	    }
	}

	if( componentProperties == null ) {
	    return;
	}
	final Iterator< String > keys = componentProperties.keySet().iterator();
	while( keys.hasNext() ) {
	    final String key = keys.next();
	    customizeProperty( component, key, componentProperties.get( key ) );
	}
    }


    protected void customizeProperty( final JComponent component, final String key, final Object value ) {
	if( value == null || value instanceof HashMap ) {
	    return;
	}
	try {
	    final Class< ? extends JComponent > componentClass = component.getClass();
	    final Method propertySetter = componentClass.getMethod( getPropertySetterName( key ), new Class[] { value.getClass().getSuperclass() } );

	    propertySetter.invoke( component, new Object[] { value } );
	} catch( final Exception ex ) {
	    System.out.println( "unable to set property" );
	}
    }


    @SuppressWarnings( "unchecked" )
    public void customizeUITree( final JComponent root ) {
	if( rootName == null ) {
	    return;
	}
	final HashMap< String, Object > rootProperties = (HashMap< String, Object >) defaultProperties.get( rootName );
	customizeUITree( root, cloneIfNecessary( new HashMap< String, Object >(), rootProperties, PREFIXES, false ), rootProperties );
    }


    @SuppressWarnings( "unchecked" )
    protected void customizeUITree( final JComponent root, final HashMap< String, Object > defaultProperties,
				    final HashMap< String, Object > specialProperties ) {
	customizeComponent( root, defaultProperties );
	final int componentCount = root.getComponentCount();

	for( int index = 0; index < componentCount; index++ ) {
	    final Component c = root.getComponent( index );
	    if( !(c instanceof JComponent) ) {
		continue;
	    }

	    final JComponent child = (JComponent) c;
	    final HashMap< String, Object > childProperties = specialProperties == null ? null
		    : (HashMap< String, Object >) specialProperties.get( child.getName() );
	    customizeUITree( child, cloneIfNecessary( defaultProperties, childProperties, PREFIXES, true ), childProperties );

	}
    }


    protected int determineResourceType( final String resourceKey ) {
	final String type = resourceKey.toLowerCase();
	if( type.equals( "lookandfeel" ) ) {
	    return LOOK_AND_FEEL;
	}
	if( type.endsWith( "font" ) ) {
	    return FONT;
	}
	if( type.endsWith( "color" ) ) {
	    return COLOR;
	}
	if( type.endsWith( "string" ) ) {
	    return STRING;
	}
	if( type.endsWith( "integer" ) ) {
	    return INTEGER;
	}
	if( type.endsWith( "insets" ) ) {
	    return INSETS;
	}
	if( type.endsWith( "dimension" ) ) {
	    return DIMENSION;
	}
	if( type.endsWith( "icon" ) ) {
	    return ICON;
	}
	return BOOLEAN;
    }


    protected String getPropertyKey( final JComponent component ) {
	String key = component.getClass().getName();
	key = key.substring( key.lastIndexOf( '.' ) + 1 );
	if( key.startsWith( "J" ) ) {
	    key = key.substring( 1 );
	}
	return key;
    }


    protected String getPropertySetterName( final String propertyKey ) {
	return "set" + (propertyKey.charAt( 0 ) - 'a' + 'A') + propertyKey.substring( 1 );
    }


    protected boolean isSystemProperty( final String resourceKey ) {
	final StringTokenizer st = new StringTokenizer( resourceKey, "." );
	final int tokenCount = st.countTokens();
	if( tokenCount == 1 ) {
	    return true;
	}
	if( tokenCount == 2 && PREFIXES.contains( st.nextToken() ) ) {
	    return true;
	}
	return false;
    }


    public void loadDefaults( final String fileName ) throws IOException {
	final BufferedReader in = new BufferedReader( new FileReader( fileName ) );
	String line = null;

	while( (line = in.readLine()) != null ) {
	    if( line.startsWith( "//" ) || line.equals( "" ) ) {
		continue;
	    }

	    final int separatorIndex = line.indexOf( "=" );
	    if( separatorIndex < 0 ) {
		continue;
	    }

	    final String resourceKey = line.substring( 0, separatorIndex );
	    final String resourceValueString = line.substring( separatorIndex + 1 );
	    Object resourceValue = null;
	    final int resourceType = determineResourceType( resourceKey );

	    try {
		final StringTokenizer st = new StringTokenizer( resourceValueString, "," );

		switch( resourceType ) {
		    case LOOK_AND_FEEL:
			resourceValue = new ColorUIResource( new Color( Integer.parseInt( st.nextToken() ), Integer.parseInt( st.nextToken() ),
				Integer.parseInt( st.nextToken() ), Integer.parseInt( st.nextToken() ) ) );
			break;
		    case STRING:
			resourceValue = st.nextToken();
			break;
		    case INTEGER:
			resourceValue = new Integer( st.nextToken() );
			break;
		    case INSETS:
			resourceValue = new InsetsUIResource( Integer.parseInt( st.nextToken() ), Integer.parseInt( st.nextToken() ),
				Integer.parseInt( st.nextToken() ), Integer.parseInt( st.nextToken() ) );
			break;
		    case DIMENSION:
			resourceValue = new DimensionUIResource( Integer.parseInt( st.nextToken() ), Integer.parseInt( st.nextToken() ) );
			break;
		    case ICON:
			resourceValue = new IconUIResource( new ImageIcon( st.nextToken() ) );
			break;
		    case FONT:
			resourceValue = new FontUIResource(
				new Font( st.nextToken(), Integer.parseInt( st.nextToken() ), Integer.parseInt( st.nextToken() ) ) );
			break;
		    case BOOLEAN:
			resourceValue = new Boolean( st.nextToken().equalsIgnoreCase( "true" ) );
		}

		if( resourceValue == null ) {
		    continue;
		}
		if( isSystemProperty( resourceKey ) ) {
		    if( resourceKey.startsWith( "*." ) ) {

			final int size = PREFIXES.size();
			for( int i = 1; i < size; i++ ) {
			    final String expandedResourceKey = PREFIXES.get( i ) + resourceKey.substring( resourceKey.indexOf( '.' ) );
			    if( UIManager.get( expandedResourceKey ) != null ) {
				UIManager.put( expandedResourceKey, resourceValue );
			    }
			}

		    } else {
			UIManager.put( resourceKey, resourceValue );
		    }

		} else {
		    final StringTokenizer st2 = new StringTokenizer( resourceKey, "." );
		    final int tokenCount = st2.countTokens();
		    Map< String, Object > map = defaultProperties;

		    for( int i = 1; i < tokenCount; i++ ) {
			final String name = st2.nextToken();

			if( i == 1 && rootName == null ) {
			    rootName = name;
			}

			@SuppressWarnings( "unchecked" )
			Map< String, Object > mapForName = (HashMap< String, Object >) map.get( name );
			if( mapForName == null ) {
			    map.put( name, mapForName = new HashMap< String, Object >() );
			}
			map = mapForName;

		    }
		    map.put( st2.nextToken(), resourceValue );
		}

	    } catch( final Exception ex ) {
		System.out.println( "couldn't understand\"" + line + "\"" );
	    }
	}

	in.close();
    }
}
