package com.lostfound.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接工具类
 * 负责加载 MySQL 数据库驱动、建立连接、以及关闭资源
 * 支持从环境变量或 db.properties 文件读取数据库配置信息
 */
public class DatabaseConnection {
    // 数据库连接 URL
    private static final String URL;
    // 数据库用户名
    private static final String USER;
    // 数据库密码
    private static final String PASSWORD;
    // MySQL JDBC 驱动类名
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // 静态代码块：类加载时初始化数据库配置
    static {
        // 首先尝试从环境变量读取数据库配置
        String url = System.getenv("SUPABASE_DB_URL");
        String user = System.getenv("SUPABASE_DB_USER");
        String password = System.getenv("SUPABASE_DB_PASSWORD");

        // 如果环境变量未配置，则从 classpath 下的 db.properties 文件读取
        if (url == null || url.isEmpty()) {
            try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (is != null) {
                    Properties props = new Properties();
                    props.load(is);
                    url = props.getProperty("db.url", url);
                    user = props.getProperty("db.user", user);
                    password = props.getProperty("db.password", password);
                }
            } catch (IOException e) {
                System.err.println("Failed to load db.properties: " + e.getMessage());
            }
        }

        // 将读取到的配置赋值给静态常量
        URL = url;
        USER = user;
        PASSWORD = password;

        // 加载并注册 MySQL JDBC 驱动
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found", e);
        }
    }

    /**
     * 获取数据库连接
     * @return JDBC Connection 对象
     * @throws SQLException 当数据库配置缺失或连接失败时抛出
     */
    public static Connection getConnection() throws SQLException {
        if (URL == null || URL.isEmpty()) {
            throw new SQLException("Database URL not configured. Set SUPABASE_DB_URL or db.properties");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * 静默关闭资源，忽略异常
     * @param closeable 需要关闭的资源（如 Connection、Statement、ResultSet）
     */
    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 忽略关闭异常，避免影响正常流程
            }
        }
    }
}
