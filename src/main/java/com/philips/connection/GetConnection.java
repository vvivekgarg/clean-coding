package com.philips.connection;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class GetConnection {
    private static Connection conn = null;
    public PreparedStatement ps1 = null, ps2 = null, ps3 = null, ps4 = null, ps5 = null, ps6 = null;
    public ResultSet rs1 = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null;
    public java.sql.Statement stmt = null;
    public java.sql.CallableStatement cs1 = null, cs2 = null;
    public java.sql.ResultSetMetaData rsmd = null;

    private static FileReader fReader;
    private static Properties prop;

    static Logger logg = Logger.getLogger(GetConnection.class);
    public static String path = "";

    // one time activity make sure you will have this file name in path
    // specified to run this application
    static {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // change this path at deployment
            // path = "e:/";
            path = "C:/WorkGround/AshaClothing/";
        } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            path = "/Volumes/Jeeva/";
        }

        PropertyConfigurator.configure(path + "log4j.properties");
    }

    // Naveen 6th Feb 2015, to give the logger in every class
    public static Logger getLogger(Class name) {
        logg = Logger.getLogger(name);
        return logg;
    }

    public static Connection getMySQLConnection() {
        try {
            fReader = new FileReader(path + "db.properties");
            prop = new Properties();
            prop.load(fReader);

            Class.forName(prop.get("driver").toString());
            conn = DriverManager.getConnection(prop.getProperty("url") + prop.getProperty("db"),
                    prop.getProperty("uname"), prop.getProperty("password"));

            conn.prepareStatement("set global max_connections = 1200");

        } catch (Exception e) {
            logg.error("Error In Connection" + e, e);
        }
        return conn;
    }

    public static void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // public static void main(String[] args) {
    // getMySQLConnection();
    // }
}