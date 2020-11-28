package com.github.mike.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Connect {
    private Connection conn;
    private ComboPooledDataSource ds;

    public Connect(String[] args) {
        ds = new ComboPooledDataSource();
        ds.setJdbcUrl(args[1]);
        ds.setUser(args[2]);
        ds.setPassword(args[3]);
       // ds.setMinIdle(2);
       // ds.setMaxIdle(3);
        ds.setMaxIdleTime(7000);
        ds.setMaxPoolSize(5);
        ds.setIdleConnectionTestPeriod(6000);
       // ds.setMaxOpenPreparedStatements(100);
        //ds.setTestOnBorrow(true);
        //ds.setValidationQuery("select 1");
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConn() throws SQLException {
            return ds.getConnection();
    }

    public void closeConn() {
        try {
            conn.close();
        } catch (Exception e) {
        }
    }
}
