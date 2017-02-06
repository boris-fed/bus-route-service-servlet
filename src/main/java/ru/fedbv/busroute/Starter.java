package ru.fedbv.busroute;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.fedbv.busroute.model.DataProvider;
import ru.fedbv.busroute.model.DataSourceFactory;
import ru.fedbv.busroute.model.DatabaseDataProvider;
import ru.fedbv.busroute.model.MemoryDataProvider;
import ru.fedbv.busroute.service.BusRouteServlet;
import ru.fedbv.busroute.service.ReloadDataServlet;

/**
 * Application starter class
 */
public class Starter {
    public static Logger logger = LoggerFactory.getLogger(Starter.class);
    private String loggingConfigPath;
    
    /**
     * Application entry point.
     * First application argument - folder path as application home value {@code BUS_ROUTE_HOME}
     * 
     * @param args
     *        array of string arguments
     */
    public static final void main(String[] args) {
        new Starter(args);
    }
    
    /** 
     * Constructs new application starter instance.
     * Get application home directory. 
     * Initialize logger.
     * Load application configuration.
     * Create Jetty on port 8088 (default) or specified port value from configuration.
     * Create Jersey servlet listening for /* paths.
     * Create Spring context base on data provider mode configuration (default = memory) and database type (defauld = derby).
     * Start Jetty server
     * 
     * @param args
     *        array of string arguments
     */
    public Starter(String[] args) {
        // Get application home directory
        initApplicationHome(args);
        
        // Init logger
        initLogger();

        // Loading application config
        Properties prop = loadConfig();

        // Starting Jetty
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        
        String sourceFilePath = System.getProperty("BUS_ROUTE_HOME") + "/" + prop.getProperty("source-file");
        
        DataProvider dataProvider = null;
        if (prop.getProperty("data-provider-mode","memory").equals("memory")) {
            MemoryDataProvider memoryDataProvider = new MemoryDataProvider();
            memoryDataProvider.setSourceFilePath(sourceFilePath);
            memoryDataProvider.init();
            dataProvider = memoryDataProvider;
        } else {
            DatabaseDataProvider databaseDataProvider = new DatabaseDataProvider();
            databaseDataProvider.setSourceFilePath(sourceFilePath);
            if (prop.getProperty("jdbc.database-type", "derby").equals("oracle")) {
                databaseDataProvider.setDataSource(DataSourceFactory.createOracleDataSource(prop));
            } else {
                databaseDataProvider.setDataSource(DataSourceFactory.createDerbyDataSource());
                databaseDataProvider.createDatabase();
            }
            dataProvider = databaseDataProvider;
        }

        BusRouteServlet busRouteServlet = new BusRouteServlet();
        busRouteServlet.setDataProvider(dataProvider);
        context.addServlet(new ServletHolder(busRouteServlet), "/api/direct");

        ReloadDataServlet reloadDataServlet = new ReloadDataServlet();
        reloadDataServlet.setDataProvider(dataProvider);
        context.addServlet(new ServletHolder(reloadDataServlet), "/api/reload-data");
        
        Server jettyServer = new Server(Integer.valueOf(prop.getProperty("port","8088")));
        jettyServer.setHandler(context);
        
        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e) {    
        } finally {
            jettyServer.destroy();
        }
    }
    
    /**
     * Initialize application home folder.
     * Application home folder is the first application launch parameter.
     * If the first parameter is absent, the application home folder is application JAR file location.
     * 
     * @param args
     *        array of string arguments
     */
    private void initApplicationHome(String[] args) {
        if (args.length == 0) {
            try {
                CodeSource codeSource = Starter.class.getProtectionDomain().getCodeSource();
                File jarFile = new File(codeSource.getLocation().toURI().getPath());
                String jarDir = jarFile.getParentFile().getPath();
                System.setProperty("BUS_ROUTE_HOME", jarDir);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            System.setProperty("BUS_ROUTE_HOME", args[0]);
        }
    }
    
    /**
     * Initialize logging.
     * Search application home folder for config/log4j.properties file.
     * If log4j.properties file is absent, the default META-INF/log4j.properties from class path is used.
     */
    private void initLogger() {
        Properties prop = loadPropertiesFromExternalFile("config/log4j.properties");
        if (prop == null) {
            prop = loadPropertiesFromClassPath("META-INF/log4j.properties");
        }    

        if (prop == null) {
            System.out.println("Error loading logging configuration log4j.properties");
            return;
        }
        PropertyConfigurator.configure(prop); 
        logger.info("Logger configuration loaded: " + loggingConfigPath);
    }
    
    /**
     * Load configuration.
     * Search application home folder for config/config.properties file.
     * If config/config.properties file is absent, the default META-INF/default_config.properties from class path is used.
     */
    private Properties loadConfig() {
        Properties prop = loadPropertiesFromExternalFile("config/config.properties");
        if (prop == null) {
            prop = loadPropertiesFromClassPath("META-INF/default_config.properties");
        }    

        if (prop == null) {
            logger.error("Error loading application config.properties");
        } else {
            logger.info("Application configuration loaded: " + loggingConfigPath);
        }
        return prop;
    }
        
    /**
     * Load property from external file.
     * Search application home folder for specified file (@code BUS_ROUTE_HOME).
     * If file is absent - return (@code null)
     * 
     * @return properties from file
     */
    private Properties loadPropertiesFromExternalFile(String filePath){
        InputStream inputStream = null;
        
        try {
            String applicationHome = System.getProperty("BUS_ROUTE_HOME", null);
            if (applicationHome != null) {
                Properties prop = new Properties();
                loggingConfigPath = applicationHome + (applicationHome.endsWith("/") ? "" : "/") + filePath;
                inputStream = new FileInputStream(loggingConfigPath);
                prop.load(inputStream);
                return prop;
            }
        } catch (IOException io) {
 
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                    
                }
            }
        }
        loggingConfigPath=null;
        return null;
    }
    
    /**
     * Load property from internal file.
     * Search application class path for specified file.
     * If file is absent - return (@code null)
     * 
     * @return properties from file
     */
    private Properties loadPropertiesFromClassPath(String filePath){
        InputStream inputStream = null;
        
        try {
            Properties prop = new Properties();
            loggingConfigPath = filePath;
            inputStream = Starter.class.getClassLoader().getResourceAsStream(loggingConfigPath);
            prop.load(inputStream);
            return prop;
        } catch (IOException io) {
 
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                    
                }
            }
        }
        loggingConfigPath=null;
        return null;
    }
}
