package net.sourceforge.jgeocoder.factory;

import net.sourceforge.jgeocoder.listener.ConfigListener;
import net.sourceforge.jgeocoder.util.Config;
import org.apache.log4j.Logger;

public class ApplicationFactory
{
    private static final Logger logger = Logger.getLogger(ApplicationFactory.class);

    /** Static factory instance */
    private static final ApplicationFactory factoryInstance = new ApplicationFactory();

    /** Dependency instances */
    private ConfigListener configListener;
    private Config config;

    /** Default values */
    private static String defaultPropertyFileName = "app.properties";
    private static String defaultTestPropertyFileName = "test.app.properties";

    private ApplicationFactory() {}

    /**
     * @return boolean - If true then build succeeded
     */
    public static boolean buildInstances()
    {
        return factoryInstance.build();
    }

    private boolean build()
    {
        try {
            this.configListener = new ConfigListener();
            this.config = new Config(defaultPropertyFileName, configListener);
            return true;
        }
        catch(Exception ex){
            logger.fatal("Failed to initialize dependencies. " + ex.getMessage());
        }
        return false;
    }

    public static Config getConfig()
    {
        return factoryInstance.config;
    }

}
