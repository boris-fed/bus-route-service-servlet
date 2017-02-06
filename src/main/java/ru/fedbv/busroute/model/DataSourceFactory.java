package ru.fedbv.busroute.model;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oracle.jdbc.pool.OracleDataSource;

public class DataSourceFactory {
    public static Logger logger = LoggerFactory.getLogger(DataSourceFactory.class);
    
    public static DataSource createDerbyDataSource() {
        try {
            BasicDataSource dataSource = (BasicDataSource) Class.forName("org.apache.commons.dbcp2.BasicDataSource").newInstance();
            dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
            dataSource.setUrl("jdbc:derby:" + System.getProperty("BUS_ROUTE_HOME") + "/database;create=true");
            return dataSource;
        } catch (Exception e) {
            logger.error("Error datasource creation", e);
        }
        return null;
    }
    
    public static DataSource createOracleDataSource(Properties prop) {
        try {
            OracleDataSource dataSource = (OracleDataSource) Class.forName("oracle.jdbc.pool.OracleDataSource").newInstance();
            dataSource.setURL(prop.getProperty("jdbc.url"));
            dataSource.setUser(prop.getProperty("jdbc.username"));
            dataSource.setPassword(prop.getProperty("jdbc.password"));
            dataSource.setConnectionCachingEnabled(true);
            return dataSource;
        } catch (Exception e) {
            logger.error("Error datasource creation", e);
        }
        return null;
    }
}
