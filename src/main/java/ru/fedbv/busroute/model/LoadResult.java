package ru.fedbv.busroute.model;

/** 
 * File load statistics class
 */
public class LoadResult {
    /** 
     * Number of loaded lines property
     */
    private int load_count;
    /** 
     * Number of error lines or errors on load property
     */
    private int error_count;
    
    /**
     * Gets the value of the number of error lines or errors on load property {@link LoadResult.error_count}.
     * 
     * @return number of error lines or errors on load
     */
    public int getError_count() {
        return error_count;
    }
    
    /**
     * Sets the value of number of error lines or errors on load property {@link LoadResult.error_count}.
     * 
     * @param error_count
     *        new number of errors value
     */
    public void setError_count(int error_count) {
        this.error_count = error_count;
    }
    
    /**
     * Gets the value of the number loaded lines property {@link LoadResult.load_count}.
     * 
     * @return number of loaded lines
     */
    public int getLoad_count() {
        return load_count;
    }
    
    /**
     * Sets the value of number of loaded lines property {@link LoadResult.load_count}.
     * 
     * @param load_count
     *        new loaded lines value
     */
    public void setLoad_count(int load_count) {
        this.load_count = load_count;
    }
    
    
}
