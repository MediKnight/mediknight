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

public class SplashWindow extends Window {

    public SplashWindow(String imagePath) {
        super(new Frame());

        URL imageUrl = SplashWindow.class.getClassLoader().getResource(
                imagePath);
        Image image = Toolkit.getDefaultToolkit().createImage(imageUrl);
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(image, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        add(new SplashImage(image), BorderLayout.NORTH);
        add(new Label("Die Applikation wird geladen..."), BorderLayout.SOUTH);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        setLocation((screenSize.width - getSize().width) / 2,
                (screenSize.height - getSize().height) / 2);
        setVisible(true);
    }
}

class SplashImage extends Component {
    Image image;

    public SplashImage(Image image) {
        this.image = image;
    }

    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(null), image.getHeight(null));
    }

    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}

