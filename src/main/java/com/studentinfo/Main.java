package com.studentinfo;

import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.dao.UserDAO;
import com.studentinfo.model.User;
import com.studentinfo.ui.LoginWindow;

import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            // 初始化数据库连接
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            
            // 创建默认管理员用户（如果不存在）
            UserDAO userDAO = new UserDAO(dbConnection);
            if (userDAO.getUserByUsername("admin") == null) {
                User admin = new User(0, "admin", "admin123", "admin", "admin@example.com", "1234567890");
                userDAO.addUser(admin);
                System.out.println("已创建默认管理员用户");
            }
            
            // 启动登录窗口
            SwingUtilities.invokeLater(() -> {
                LoginWindow loginWindow = new LoginWindow(dbConnection);
                loginWindow.setVisible(true);
            });
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "数据库连接失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
} 