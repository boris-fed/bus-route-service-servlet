package ru.fedbv.busroute.model;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Memory data provider class
 */
public class MemoryDataProvider extends AbstractDataProvider{
    public static Logger logger = LoggerFactory.getLogger(MemoryDataProvider.class);
    /** 
     * Bus routes store property
     */
    private ArrayList<BusRoute> busRoutes;
    
    /**
     * Initialize data provider and loads data into storage.
     * 
     * @return load results statistics
     */
    @Override
    public LoadResult init() {
        return loadFile(getSourceFilePath());
    }
    
    /**
     * Create data store with defined default capacity.
     * 
     * @param capacity
     *        default capacity
     */
    @Override
    public void createDataStore(int capacity) {
        busRoutes = new ArrayList<BusRoute>(capacity);
    }

    /**
     * Add bus route object into storage.
     * 
     * @param busRoute
     *        bus route object to add
     * @return if bus route was added successfully, returns {@code true} otherwise {@code false}
     */
    @Override
    public boolean addBusRoute(BusRoute busRoute) {
        if (busRoute == null) return false;

        busRoutes.add(busRoute);
        return true;
    }
    
    /**
     * Find bus route by two stop ids. Return {@link BusRoute} object which contains both stops.
     * Return {@code null} if one/or both stops are {@code null} or bus route does not contains both stops.
     * 
     * @param stopId1
     *        first stop id to find
     * 
     * @param stopId2
     *        second stop id to find
     * 
     * @return {@link BusRoute}
     *         bus route which contains both stops
     */
    @Override
    public BusRoute findByTwoStopIds(Integer stopId1, Integer stopId2) {
        if (stopId1 == null || stopId2 == null) return null;
        try {
            return busRoutes.stream().
                filter(x -> x.getStopIds().contains(stopId1) && x.getStopIds().contains(stopId2)).
                findFirst().get();
        } catch (NoSuchElementException ignore) {    
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
