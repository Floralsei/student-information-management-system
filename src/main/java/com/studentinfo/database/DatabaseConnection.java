package com.studentinfo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:student_info.db";
    private Connection connection;

    public DatabaseConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
    }

    public void initializeDatabase() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // 创建用户表
            statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL," +
                    "role TEXT NOT NULL," +
                    "email TEXT," +
                    "phone TEXT)");

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

            // 创建课程表
            statement.execute("CREATE TABLE IF NOT EXISTS courses (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "course_id TEXT NOT NULL UNIQUE," +
                    "name TEXT NOT NULL," +
                    "teacher TEXT," +
                    "credits INTEGER," +
                    "description TEXT," +
                    "max_students INTEGER)");

            // 创建选课表
            statement.execute("CREATE TABLE IF NOT EXISTS enrollments (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "student_id INTEGER," +
                    "course_id INTEGER," +
                    "enrollment_date TEXT," +
                    "grade TEXT," +
                    "FOREIGN KEY (student_id) REFERENCES students(id)," +
                    "FOREIGN KEY (course_id) REFERENCES courses(id))");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
} 