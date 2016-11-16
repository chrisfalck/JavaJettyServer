package com.databaseserver.server.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.*;

public class QueryExecution {
    // Oracle stuff.
    static Connection connection;
    static Statement statement;
    public QueryExecution() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    // Always call this to establish a connection to the Oracle database.
    public static void openConnection() {
        try {
            connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:xe", "system", "password");
            statement = connection.createStatement();
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    // Always call this when done to close the connection to the database.
    public static void closeConnection() {
        try {
            connection.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public static String getData() {
        return ("Hello World");
    }
}
