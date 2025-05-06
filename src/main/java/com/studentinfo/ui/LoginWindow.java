package com.studentinfo.ui;

import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.dao.UserDAO;
import com.studentinfo.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private DatabaseConnection db;
    private UserDAO userDAO;

    public LoginWindow(DatabaseConnection dbConnection) {
        this.db = dbConnection;
        this.userDAO = new UserDAO(dbConnection);
        
        // 设置窗口标题和大小
        setTitle("学生信息管理系统");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // 设置字体
        Font font = new Font("Microsoft YaHei", Font.PLAIN, 14);
        
        // 用户名标签和输入框
        JLabel usernameLabel = new JLabel("用户名：");
        usernameLabel.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(usernameLabel, gbc);
        
        usernameField = new JTextField(20);
        usernameField.setFont(font);
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(usernameField, gbc);
        
        // 密码标签和输入框
        JLabel passwordLabel = new JLabel("密码：");
        passwordLabel.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(passwordLabel, gbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(font);
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(passwordField, gbc);
        
        // 登录按钮
        JButton loginButton = new JButton("登录");
        loginButton.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(loginButton, gbc);
        
        // 添加登录按钮事件监听器
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                try {
                    if (userDAO.validateUser(username, password)) {
                        User user = userDAO.getUserByUsername(username);
                        if (user != null) {
                            dispose(); // 关闭登录窗口
                            new MainWindow(user, dbConnection); // 打开主窗口
                        }
                    } else {
                        JOptionPane.showMessageDialog(LoginWindow.this,
                            "用户名或密码错误！",
                            "登录失败",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(LoginWindow.this,
                        "数据库错误：" + ex.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // 添加主面板到窗口
        add(mainPanel);
    }
    
    @Override
    public void dispose() {
        try {
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.dispose();
    }
} 