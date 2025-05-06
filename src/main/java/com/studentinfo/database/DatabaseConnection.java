package com.studentinfo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class DatabaseConnection {
    private static final String DB_NAME = "student_info.db";
    private static final String DB_URL;
    private static DatabaseConnection instance;
    private Connection connection;

    static {
        // 获取当前工作目录
        String currentDir = System.getProperty("user.dir");
        // 创建数据库文件路径
        String dbPath = currentDir + File.separator + DB_NAME;
        DB_URL = "jdbc:sqlite:" + dbPath;
        System.out.println("数据库文件路径: " + dbPath);
    }

    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("正在连接数据库: " + DB_URL);
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("数据库连接成功");
            // 设置自动提交为true
            connection.setAutoCommit(true);
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC驱动未找到: " + e.getMessage());
            throw new SQLException("SQLite JDBC driver not found", e);
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            throw e;
        }
    }

    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public void initializeDatabase() throws SQLException {
        System.out.println("开始初始化数据库...");
        try (Statement statement = connection.createStatement()) {
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
                    "student_id TEXT NOT NULL UNIQUE," +
                    "name TEXT NOT NULL," +
                    "gender TEXT," +
                    "birth_date TEXT," +
                    "major TEXT," +
                    "email TEXT," +
                    "phone TEXT)");
            System.out.println("学生表创建成功");

            // 创建课程表
            statement.execute("CREATE TABLE IF NOT EXISTS courses (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "course_id TEXT NOT NULL UNIQUE," +
                    "name TEXT NOT NULL," +
                    "teacher TEXT," +
                    "credits INTEGER," +
                    "description TEXT," +
                    "max_students INTEGER)");
            System.out.println("课程表创建成功");

            // 创建选课表
            statement.execute("CREATE TABLE IF NOT EXISTS enrollments (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "student_id INTEGER," +
                    "course_id INTEGER," +
                    "enrollment_date TEXT," +
                    "grade TEXT," +
                    "FOREIGN KEY (student_id) REFERENCES students(id)," +
                    "FOREIGN KEY (course_id) REFERENCES courses(id))");
            System.out.println("选课表创建成功");
        } catch (SQLException e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            throw e;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            System.out.println("正在关闭数据库连接...");
            connection.close();
            instance = null;
            System.out.println("数据库连接已关闭");
        }
    }
} 