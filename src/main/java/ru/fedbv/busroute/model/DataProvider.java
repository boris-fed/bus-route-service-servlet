package ru.fedbv.busroute.model;

/**
 * Data provider interface
 */
public interface DataProvider {
    
    /**
     * Initialize data provider and loads data into storage.
     * 
     * @return load results statistics
     */
    public LoadResult init();
    
    /**
     * Create data store with defined default capacity.
     * 
     * @param capacity
     *        default capacity
     */
    public void createDataStore(int capacity);
    
    /**
     * Add bus route object into storage.
     * 
     * @param busRoute
     *        bus route object to add
     *        
     * @return if bus route was added successfully, returns {@code true} otherwise {@code false}
     */
    public boolean addBusRoute(BusRoute busRoute);
    
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
    public BusRoute findByTwoStopIds(Integer stopId1, Integer stopId2);
}
