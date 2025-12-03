package com.eventmanagement.utils.dbconfig;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    private static Connection connection = null;
    private static String jdbcURL = "jdbc:postgresql://172.16.1.195:5331/dbtdemo";
    private static String username = "dbtuser";
    private static String password = "FF2awdXt";

    private DataBaseConnection() {
        System.out.println("DataBase connection Started");
    }

    public static Connection getConnection() {
        if (connection == null) {

            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(jdbcURL, username, password);
                // CreateDatabse.createDatabse();
                System.out.println("Database Connection Completed");

            } catch (SQLException sqlException) {
                System.out.println("Sql Connection error occured ");
                sqlException.printStackTrace();
            } catch (ClassNotFoundException classNotFoundException) {
                System.out.println("Database Driver Connection Not Found");
                classNotFoundException.printStackTrace();
            }

        }
        return connection;
    }

    public static void closeDataConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database Connection Closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        else
            System.out.println("Database Connection is closed already");

    }

}
