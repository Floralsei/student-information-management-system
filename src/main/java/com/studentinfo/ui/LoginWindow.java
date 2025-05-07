package com.studentinfo.ui;

import com.studentinfo.dao.UserDAO;
import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.User;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginWindow extends JFrame {
    private final UserDAO userDAO;
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginWindow() throws SQLException {
        this.userDAO = new UserDAO(DatabaseConnection.getInstance().getConnection());
        setTitle("学生信息管理系统 - 登录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // 创建主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 添加标题标签
        JLabel titleLabel = new JLabel("学生信息管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 20, 5);
        mainPanel.add(titleLabel, gbc);

        // 重置insets
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 1;

        // 用户名输入
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(20);
        mainPanel.add(usernameField, gbc);

        // 密码输入
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);

        // 登录按钮
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JButton loginButton = new JButton("登录");
        loginButton.addActionListener(e -> handleLogin());
        mainPanel.add(loginButton, gbc);

        add(mainPanel);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请输入用户名和密码",
                    "登录错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            User user = userDAO.getUserByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                MainWindow mainWindow = new MainWindow(user);
                mainWindow.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "用户名或密码错误",
                        "登录错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "登录时发生错误: " + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
} 