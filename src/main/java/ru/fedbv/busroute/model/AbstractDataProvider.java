package ru.fedbv.busroute.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract data provider class
 */
public abstract class AbstractDataProvider implements DataProvider{
    public static Logger logger = LoggerFactory.getLogger(AbstractDataProvider.class);
    
    /** 
     * Source file path property
     */
    private String sourceFilePath = null;
    
    /** 
     * Processed lines from loaded file
     */
    private int lineCount = 0;
    
    /** 
     * Internal counter for processed lines
     */
    private int tmpLineCount = 0;
    
    /** 
     * Number of loaded lines property
     */
    private int loadCount = 0;
    
    /** 
     * Number of error lines or errors on load property
     */
    private int errorCount = 0;
    
    /**
     * Add bus route object into storage.
     * 
     * @param busRoute
     *        bus route object to add
     *        
     * @return if bus route was added successfully, returns {@code true} otherwise {@code false}
     */
    public abstract boolean addBusRoute(BusRoute busRoute);

    /**
     * Gets the value source file path property {@link AbstractDataProvider.sourceFilePath}.
     * 
     * @return source file path value
     */
    public String getSourceFilePath() {
        return sourceFilePath;
    }

    /**
     * Sets the value of source file path property {@link AbstractDataProvider.sourceFilePath}.
     * 
     * @param sourceFilePath
     *        new source file path value
     */
    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }
    
    /**
     * Loads data from specified file path into storage
     * 
     * @param filePath
     *        source data file path value
     * 
     * @return load results statistics
     */
    protected LoadResult loadFile(String filePath) {
        lineCount = 0;
        tmpLineCount = 0;
        loadCount = 0;
        errorCount = 0;
        
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            logger.info("Start loading file: " + filePath);
            
            stream.forEach(x -> { processLine(x); });

            logger.info("End loading file: " + filePath);
            
        } catch (IOException e) {
           logger.error(e.getClass().getName() + ": " + e.getMessage());
           errorCount++;
        }
        
        LoadResult loadResult = new LoadResult();
        loadResult.setLoad_count(loadCount);
        loadResult.setError_count(errorCount);
        return loadResult;
    }
    
    /**
     * Parse line from source file
     * 
     * @param line
     *        source line value
     */
    protected void processLine(String line) {
        if (tmpLineCount >= 100) {
            logger.info("Lines loaded: " + lineCount);
            tmpLineCount = 0;
        }

        try {
            if (lineCount == 0) {
                try {
                    int estimatedLinesCount = Integer.valueOf(line);
                    createDataStore(estimatedLinesCount);
                    logger.info("Estimated lines count: " + estimatedLinesCount);
                } catch (NumberFormatException nfe) {
                    logger.error("Error line " + lineCount + " parsing: the line should contain only numbers"); 
                }    
                return;
            }
            
            BusRoute busRoute = new BusRoute();
            String[] values = line.split(" ");
            if (values.length < 3) {
                logger.error("Error line " + lineCount + " parsing: the line elements count is less then 3");
                errorCount++;
                return;
            }
            
            busRoute.getStopIds().ensureCapacity(values.length - 1);
            boolean firstElement = true;
            
            try {
                for (String val : values) {
                    if (firstElement) {
                        busRoute.setId(Integer.valueOf(val));
                        firstElement = false;
                    } else {
                        busRoute.getStopIds().add(Integer.valueOf(val));
                    }
                }
            } catch (NumberFormatException nfe) {
                logger.error("Error line " + lineCount + " parsing: the line should contain only numbers");
                errorCount++;
                return;
            }
            
            if (addBusRoute(busRoute)) {
                loadCount++;
            } else {
                errorCount++;
            }

        } finally {
            lineCount++;
            tmpLineCount++;
        }    
    }
}
