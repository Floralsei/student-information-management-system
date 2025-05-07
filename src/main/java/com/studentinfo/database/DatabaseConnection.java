package com.studentinfo.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.File;
import java.io.InputStream;

public class DatabaseConnection {
    private static final String CONFIG_FILE = "src/main/resources/db.properties";
    private static DatabaseConnection instance;
    private Connection connection;
    private String url = "";
    private String username = "";
    private String password = "";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }

    private DatabaseConnection() {
        try {
            Properties props = new Properties();
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties")) {
                if (is != null) {
                    props.load(is);
                    url = props.getProperty("db.url");
                    username = props.getProperty("db.username");
                    password = props.getProperty("db.password");
                    connection = DriverManager.getConnection(url);
                } else {
                    throw new IOException("无法找到数据库配置文件");
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public void initializeDatabase() throws SQLException {
        System.out.println("开始初始化数据库...");
        try (Connection conn = getConnection()) {
            try (Statement statement = conn.createStatement()) {
                // 创建用户表
                statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT NOT NULL UNIQUE," +
                        "password TEXT NOT NULL," +
                        "role TEXT NOT NULL," +
                        "email TEXT," +
                        "phone TEXT)");
                System.out.println("用户表创建成功");

                // 创建学生表
                statement.execute("CREATE TABLE IF NOT EXISTS students (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER," +
                        "student_number TEXT NOT NULL UNIQUE," +
                        "name TEXT NOT NULL," +
                        "gender TEXT," +
                        "birth_date TEXT," +
                        "address TEXT," +
                        "phone TEXT," +
                        "email TEXT," +
                        "enrollment_date TEXT," +
                        "status TEXT," +
                        "FOREIGN KEY (user_id) REFERENCES users(id))");
                System.out.println("学生表创建成功");

                // 创建课程表
                statement.execute("CREATE TABLE IF NOT EXISTS courses (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "course_code TEXT NOT NULL UNIQUE," +
                        "name TEXT NOT NULL," +
                        "credits INTEGER," +
                        "teacher_id INTEGER," +
                        "description TEXT," +
                        "FOREIGN KEY (teacher_id) REFERENCES users(id))");
                System.out.println("课程表创建成功");

                // 创建选课表
                statement.execute("CREATE TABLE IF NOT EXISTS enrollments (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "student_id INTEGER," +
                        "course_id INTEGER," +
                        "enrollment_date TEXT," +
                        "status TEXT," +
                        "FOREIGN KEY (student_id) REFERENCES students(id)," +
                        "FOREIGN KEY (course_id) REFERENCES courses(id))");
                System.out.println("选课表创建成功");

                // 创建成绩表
                statement.execute("CREATE TABLE IF NOT EXISTS grades (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "enrollment_id INTEGER," +
                        "score REAL," +
                        "record_time TEXT," +
                        "FOREIGN KEY (enrollment_id) REFERENCES enrollments(id))");
                System.out.println("成绩表创建成功");
            }
        } catch (SQLException e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            throw e;
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url);
            // 启用外键约束
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 