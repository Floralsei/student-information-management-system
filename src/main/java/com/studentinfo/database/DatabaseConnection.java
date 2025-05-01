package com.studentinfo.database;

import java.sql.*;
import java.nio.file.Paths;

public class DatabaseConnection {
    private String dbPath;
    private Connection conn;
    private Statement stmt;

    public DatabaseConnection() {
        this.dbPath = Paths.get("student_info.db").toAbsolutePath().toString();
    }

    public Connection connect() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            stmt = conn.createStatement();
        }
        return conn;
    }

    public void close() throws SQLException {
        if (stmt != null) stmt.close();
        if (conn != null) conn.close();
    }

    public void initializeDatabase() throws SQLException {
        connect();
        
        // 创建用户表
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                role TEXT NOT NULL
            )
        """);

        // 创建学生信息表
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS students (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id TEXT UNIQUE NOT NULL,
                name TEXT NOT NULL,
                gender TEXT NOT NULL,
                birth_date TEXT NOT NULL,
                class_name TEXT NOT NULL,
                phone TEXT,
                email TEXT,
                address TEXT
            )
        """);

        // 创建成绩表
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS grades (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id TEXT NOT NULL,
                subject TEXT NOT NULL,
                score REAL NOT NULL,
                semester TEXT NOT NULL,
                FOREIGN KEY (student_id) REFERENCES students(student_id)
            )
        """);

        // 创建默认管理员账户
        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT OR IGNORE INTO users (username, password, role) VALUES (?, ?, ?)")) {
            pstmt.setString(1, "admin");
            pstmt.setString(2, "admin123");
            pstmt.setString(3, "admin");
            pstmt.executeUpdate();
        }

        conn.commit();
        close();
    }
} 