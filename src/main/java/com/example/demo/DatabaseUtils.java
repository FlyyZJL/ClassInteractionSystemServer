package com.example.demo;

import java.sql.*;

public class DatabaseUtils {
    private static final String URL = "jdbc:mysql://localhost:3306/teaching_interaction_system?useTimezone=true&serverTimezone=Asia/Shanghai";


    private static final String USER = "root"; // 数据库用户名
//    private static final String PASSWORD = "o7Rhg2yzWr"; // 数据库密码
    private static final String PASSWORD = "root"; // 数据库密码



    // 加载JDBC驱动
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 关闭数据库资源（Connection、Statement、ResultSet）
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
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

    // 关闭数据库资源（Connection和Statement）
    public static void closeResources(Connection conn, Statement stmt) {
        closeResources(conn, stmt, null);
    }

    // 执行查询操作（SELECT）
    public static ResultSet executeQuery(String sql, Object[] params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        // 设置查询参数
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }

        // 执行查询并返回结果集
        return ps.executeQuery();
    }

    // 执行更新操作（INSERT, UPDATE, DELETE）
    public static int executeUpdate(String sql, Object[] params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        // 设置更新参数
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }

        // 执行更新并返回影响的行数
        return ps.executeUpdate();
    }

    // 执行批量操作（批量插入、更新、删除）
    public static int[] executeBatch(String sql, Object[][] paramsBatch) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        // 设置批量参数并执行批量更新
        for (Object[] params : paramsBatch) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.addBatch(); // 添加到批量操作中
        }

        return ps.executeBatch(); // 执行批量操作
    }

    // 获取单个查询结果（例如：COUNT，SUM等）
    public static Object getSingleResult(String sql, Object[] params) throws SQLException {
        ResultSet rs = executeQuery(sql, params);
        if (rs.next()) {
            return rs.getObject(1); // 获取查询结果的第一列
        }
        return null;
    }
}
