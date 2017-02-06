package ru.fedbv.busroute.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database data provider class
 */
public class DatabaseDataProvider extends AbstractDataProvider {
    public static Logger logger = LoggerFactory.getLogger(DatabaseDataProvider.class);

    /** 
     * Database data source property
     */
    private DataSource dataSource;
    
    /** 
     * Create database and fill it with data from source file
     * 
     * @return load results statistics
     */
    public LoadResult createDatabase() {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con=dataSource.getConnection();
            stmt=con.prepareStatement("CREATE TABLE bus_stops (route_id INTEGER, stop_id INTEGER)");
            stmt.execute();
        } catch (SQLException ignore) {
            
        } finally {
            closePreparedStatement(stmt);
            closeConnection(con);
        }
        
        return loadFile(getSourceFilePath());
    }
    
    /**
     * Loads data into storage
     * 
     * @return load results statistics
     */
    @Override
    public LoadResult init() {
        return loadFile(getSourceFilePath());
    }

    /**
     * Clear database. Capacity parameter is not used
     * 
     * @param capacity
     *        default capacity
     */
    @Override
    public void createDataStore(int capacity) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con=dataSource.getConnection();
            stmt=con.prepareStatement("DELETE FROM bus_stops");
            stmt.executeUpdate();
        } catch (SQLException ignore) {
            
        } finally {
            closePreparedStatement(stmt);
            closeConnection(con);
        }
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
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
                
        try {
            con=dataSource.getConnection();
            stmt=con.prepareStatement("SELECT t1.route_id FROM bus_stops t1, bus_stops t2 WHERE t1.stop_id=? AND t2.stop_id=? AND t1.route_id=t2.route_id");
            setInteger(stmt, 1, stopId1);
            setInteger(stmt, 2, stopId2);
            
            rs = stmt.executeQuery();
            if (rs.next()) {
                BusRoute busRoute = new BusRoute();
                busRoute.setId(rs.getInt(1));
                return busRoute;
            }
            
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(stmt);
            closeConnection(con);
        }
        return null;
    }

    /**
     * Add bus route object into storage
     * 
     * @param busRoute
     *        bus route object to add
     *        
     * @return if bus route was added successfully, returns {@code true} otherwise {@code false}
     */
    @Override
    public boolean addBusRoute(BusRoute busRoute) {
        if (busRoute == null) return false;
        if (busRoute.getStopIds() == null || busRoute.getStopIds().size() == 0) return false;
        
        Connection con = null;
        Statement stmt = null;        
        try {
            con=dataSource.getConnection();
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("INSERT INTO bus_stops (route_id, stop_id) VALUES ");
           
            busRoute.getStopIds().stream().forEach(x -> 
                queryBuilder.append("(").append(busRoute.getId()).append(",").append(x).append("),"));
            
            queryBuilder.delete(queryBuilder.length() - 1, queryBuilder.length());
            
            stmt = con.createStatement();
            stmt.executeUpdate(queryBuilder.toString());
            return true;
        } catch (SQLException e) {
            logger.error("Error adding bus rote (id = " + busRoute.getId() + " to database", e);
        } finally {
            closeConnection(con);
        }
        return false;
    }

    /**
     * Gets link to data source property {@link DatabaseDataProvider.dataSource}.
     * 
     * @return data source object
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Sets the value of data source property {@link DatabaseDataProvider.dataSource}.
     * 
     * @param sourceFilePath
     *        new source file path value
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
   
    /**
     * Close specified sql result set
     * 
     * @param rs
     *        result set to close
     */
    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignore) {
            }
        }
    }

    /**
     * Close specified sql prepared statement
     * 
     * @param stmt
     *        prepared statement to close
     */
    private void closePreparedStatement(PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignore) {
            }
        }
    }
    
    /**
     * Close specified sql connection
     * 
     * @param con
     *        connection to close
     */
    private void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ignore) {
            }
        }
    }
    
    /**
     * Close specified sql connection
     * 
     * @param con
     *        connection to close
     */
    /**
     * Sets the parameter value base on defined {@code Integer} value.
     * If {@code Integer} is {@code null}, the parameter value is set to {@code null} of SQL  INTEGER, 
     * otherwise the value is converted to SQL INTEGER.
     * 
     * @param stmt
     *        prepared statement which holds parameter
     * 
     * @param parameterIndex
     *        parameter index in the sql query
     * 
     * @param value
     *        Integer value to insert
     *        
     * @exception SQLException if parameterIndex does not correspond to a parameter marker in the SQL statement        
     */
    private void setInteger(PreparedStatement stmt, int parameterIndex, Integer value) throws SQLException {
        if (value == null) {
            stmt.setNull(parameterIndex, Types.INTEGER);
        } else {
            stmt.setInt(parameterIndex, value);
        }
    }
}
