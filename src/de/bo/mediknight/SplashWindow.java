package de.bo.mediknight;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Window;
import java.net.URL;


class SplashImage extends Component {

    private static final long serialVersionUID = 1L;
    Image		     image;


    public SplashImage( final Image image ) {
	this.image = image;
    }


    @Override
    public Dimension getPreferredSize() {
	return new Dimension( image.getWidth( null ), image.getHeight( null ) );
    }


    @Override
    public void paint( final Graphics g ) {
	g.drawImage( image, 0, 0, null );
    }
}


public class SplashWindow extends Window {

    private static final long serialVersionUID = 1L;


    public SplashWindow( final String imagePath ) {
	super( new Frame() );

	final URL imageUrl = SplashWindow.class.getClassLoader().getResource( imagePath );
	final Image image = Toolkit.getDefaultToolkit().createImage( imageUrl );
	final MediaTracker tracker = new MediaTracker( this );
	tracker.addImage( image, 0 );
	try {
	    tracker.waitForID( 0 );
	} catch( final InterruptedException e ) {
	    e.printStackTrace();
	}

	add( new SplashImage( image ), BorderLayout.NORTH );
	add( new Label( "Die Applikation wird geladen..." ), BorderLayout.SOUTH );
	pack();
	final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	setLocation( (screenSize.width - getSize().width) / 2, (screenSize.height - getSize().height) / 2 );
	setVisible( true );
    }
}
