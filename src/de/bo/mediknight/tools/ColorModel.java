package de.bo.mediknight.tools;

import java.io.*;
import java.util.Properties;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class ColorModel {

    private Properties prop;

    public ColorModel() {
        prop = loadUserPreferences();
    }

    public Properties getProperties() {
        return prop;
    }
    
    private Properties loadUserPreferences() {
    	Properties userProperties = new Properties();
		userProperties.setProperty("primary1.r", String.valueOf(51));
		userProperties.setProperty("primary1.g", String.valueOf(102));
		userProperties.setProperty("primary1.b", String.valueOf(51));

		userProperties.setProperty("primary2.r", String.valueOf(102));
		userProperties.setProperty("primary2.g", String.valueOf(153));
		userProperties.setProperty("primary2.b", String.valueOf(102));

		userProperties.setProperty("primary3.r", String.valueOf(153));
		userProperties.setProperty("primary3.g", String.valueOf(204));
		userProperties.setProperty("primary3.b", String.valueOf(153));

		userProperties.setProperty("secondary1.r", String.valueOf(102));
		userProperties.setProperty("secondary1.g", String.valueOf(102));
		userProperties.setProperty("secondary1.b", String.valueOf(102));

		userProperties.setProperty("secondary2.r", String.valueOf(153));
		userProperties.setProperty("secondary2.g", String.valueOf(158));
		userProperties.setProperty("secondary2.b", String.valueOf(153));

		userProperties.setProperty("secondary3.r", String.valueOf(204));
		userProperties.setProperty("secondary3.g", String.valueOf(208));
		userProperties.setProperty("secondary3.b", String.valueOf(204));

    	InputStream is = null;
    	
    	try {
			is = new FileInputStream(new File(System.getProperty("user.home"), ".mediknight.properties"));
			userProperties.load(is);
    	} catch (FileNotFoundException e) {
    		System.err.println("No user preferences.");
    	} catch (IOException e) {
    		System.err.println("Error reading user preferences.");
    	} finally {
    		if (is != null) {
    			try {
	    			is.close();
    			} catch (IOException e) {}
    		}
    	}
    	
    	return userProperties;
    }

    public void saveProperties(Properties prop) throws FileNotFoundException, IOException {
        this.prop = prop;
		try {
			prop.store(new FileOutputStream(new File(System.getProperty("user.home"), ".mediknight.properties")), "");
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
    }
}