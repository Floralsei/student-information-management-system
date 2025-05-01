package com.studentinfo.ui;

import com.studentinfo.database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private DatabaseConnection db;

    public LoginWindow() {
        setTitle("学生信息管理系统 - 登录");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        db = new DatabaseConnection();
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 标题
        JLabel titleLabel = new JLabel("学生信息管理系统");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // 用户名输入
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("用户名:"), gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(usernameField, gbc);

        // 密码输入
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("密码:"), gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordField, gbc);

        // 登录按钮
        JButton loginButton = new JButton("登录");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton.addActionListener(e -> login());
        mainPanel.add(loginButton, gbc);

        add(mainPanel);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名和密码", "错误", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection conn = db.connect();
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT role FROM users WHERE username = ? AND password = ?");
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                JOptionPane.showMessageDialog(this, "登录成功！欢迎" + role + "用户", "成功", JOptionPane.INFORMATION_MESSAGE);
                MainWindow mainWindow = new MainWindow(role);
                mainWindow.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误", "错误", JOptionPane.WARNING_MESSAGE);
            }

            rs.close();
            pstmt.close();
            db.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "数据库错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
} 