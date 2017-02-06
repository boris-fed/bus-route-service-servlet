package ru.fedbv.busroute.model;

import java.util.ArrayList;

/** 
 * Bus route entity class
 */
public class BusRoute {
    /** 
     * Bus route id property
     */
    private int id;
    
    /** 
     * Bus route stop id list property
     */
    private ArrayList<Integer> stopIds;

    /** 
     * Constructs new bus route object and initialize stops list
     */
    public BusRoute(){
        stopIds = new ArrayList<Integer>();
    }
    
    /** 
     * Constructs new bus route object and initialize stops list with defined capacity 
     * 
     * @param capacity
     *        Default stop list capacity
     */
    public BusRoute(int capacity) {
        stopIds = new ArrayList<Integer>(capacity);
    }

    /**
     * Gets the value of the bus route id property {@link BusRoute.id}.
     * 
     * @return bus route id value
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the bus route id property {@link BusRoute.id}.
     * 
     * @param id
     *        new bus route id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the value of the bus route stops list {@link BusRoute.stopIds}.
     * 
     * @return bus stops' ids list
     */
    public ArrayList<Integer> getStopIds() {
        return stopIds;
    }

    /**
     * Sets the value of the bus route id property {@link BusRoute.stopIds}.
     * 
     * @param stopIds
     *        new bus stops' ids list
     */
    public void setStopIds(ArrayList<Integer> stopIds) {
        this.stopIds = stopIds;
    }
    
 }
