package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/teaching_interaction_system?useTimezone=true&serverTimezone=Asia/Shanghai";
    //private static final String URL = "jdbc:mysql://localhost:3306/teaching_interaction_system?useTimezone=true&serverTimezone=Asia/Shanghai";
    private static final String USER = "root"; // 请替换为您的数据库用户名
    //private static final String PASSWORD = "root";
    private static final String PASSWORD = "o7Rhg2yzWr"; // 数据库密码
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    // 关闭所有资源

    public static void close(ResultSet rs, java.sql.Statement stmt, java.sql.Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void close(java.sql.Statement stmt,Connection conn) {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void close(java.sql.Statement stmt1,java.sql.Statement stmt2,Connection conn) {
        try {
            if (stmt1 != null) {
                stmt1.close();
            }
            if (stmt2 != null) {
                stmt2.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}