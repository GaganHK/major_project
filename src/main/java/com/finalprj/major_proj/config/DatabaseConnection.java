package com.finalprj.major_proj.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

        private static final String URL = "jdbc:mysql://localhost:3306/registerdb"; // Update DB name
        private static final String USER = "root"; // Update DB username
        private static final String PASSWORD = "Gagan@2724"; // Update DB password

        public static Connection getConnection() {
            try {
                return DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

}

