package cz.muni.fi.pv168.bandsproject;

import sun.tools.jar.Main;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;

/**
 * Created by Lenka on 30.4.2016.
 */
public class ConfigProperty extends Properties {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public ConfigProperty(){
        String propFileName = "config.properties";

        InputStream inputStream = DBUtils.class.getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            try {
                load(inputStream);
            } catch (IOException ex) {
                log.log(Level.SEVERE, "IOException in ConfigProperty:"+ex);
                throw new PropertyException("Cannot load property from input stream.");
            }
        } else {
            log.log(Level.SEVERE, "PropertyException in ConfigProperty: property file '" + propFileName + "' not found in the classpath");
            throw new PropertyException("property file '" + propFileName + "' not found in the classpath");
        }
    }
}
