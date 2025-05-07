package com.studentinfo;

import com.studentinfo.dao.UserDAO;
import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.User;
import com.studentinfo.ui.LoginWindow;

import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            // 初始化数据库连接
            DatabaseConnection.getInstance();

            // 创建默认管理员用户（如果不存在）
            UserDAO userDAO = new UserDAO(DatabaseConnection.getInstance().getConnection());
            User adminUser = userDAO.getUserByUsername("admin");
            if (adminUser == null) {
                adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword("admin123");
                adminUser.setRole(User.Role.ADMIN);
                adminUser.setEmail("admin@example.com");
                adminUser.setPhone("1234567890");
                userDAO.addUser(adminUser);
                System.out.println("已创建默认管理员用户");
            }

            // 打印所有用户信息
            System.out.println("系统用户列表：");
            for (User user : userDAO.getAllUsers()) {
                System.out.println(user.getUsername() + " (" + user.getRole() + ")");
            }

            // 启动登录窗口
            SwingUtilities.invokeLater(() -> {
                try {
                    LoginWindow loginWindow = new LoginWindow();
                    loginWindow.setVisible(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "启动登录窗口时发生错误: " + e.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "初始化系统时发生错误: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
} 