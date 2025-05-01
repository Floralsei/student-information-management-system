package com.studentinfo;

import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.ui.LoginWindow;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            // 初始化数据库
            DatabaseConnection db = new DatabaseConnection();
            db.initializeDatabase();
            db.close();

            // 启动登录窗口
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
        } catch (SQLException e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
        }
    }
} 