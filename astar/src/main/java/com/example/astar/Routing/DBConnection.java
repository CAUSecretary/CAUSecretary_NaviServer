package com.example.astar.Routing;

import java.sql.Connection; 	//접속 정보를 담고 있는 클래스

import java.sql.DriverManager;	//Drivermanager

import java.sql.SQLException;	//sql 쿼리의 예외처리를 위한 import



public class DBConnection {

    private static final String DB_DRIVER_CLASS = "org.mariadb.jdbc.Driver";
    private static final String DB_URL = "jdbc:mariadb://127.0.0.1:3300/astar";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "bs970923";

    //접속부
    public static Connection getConnection() {
        Connection con = null; //connection 객체
        try {
            Class.forName(DB_DRIVER_CLASS);
            con = DriverManager.getConnection(DB_URL,DB_USERNAME,DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return con;
    }
}
