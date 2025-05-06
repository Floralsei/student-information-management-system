package com.studentinfo.ui;

import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {
    private final User user;
    private final DatabaseConnection dbConnection;

    public MainWindow(User user, DatabaseConnection dbConnection) {
        this.user = user;
        this.dbConnection = dbConnection;
        
        // 设置窗口标题和大小
        setTitle("学生信息管理系统 - " + user.getRole());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        // 设置字体
        Font font = new Font("Microsoft YaHei", Font.PLAIN, 14);
        
        // 文件菜单
        JMenu fileMenu = new JMenu("文件");
        fileMenu.setFont(font);
        menuBar.add(fileMenu);
        
        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.setFont(font);
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // 用户菜单
        JMenu userMenu = new JMenu("用户");
        userMenu.setFont(font);
        menuBar.add(userMenu);
        
        JMenuItem logoutItem = new JMenuItem("退出登录");
        logoutItem.setFont(font);
        logoutItem.addActionListener(e -> {
            dispose();
            new LoginWindow(dbConnection).setVisible(true);
        });
        userMenu.add(logoutItem);
        
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);
        
        // 欢迎信息
        JLabel welcomeLabel = new JLabel("欢迎，" + user.getUsername() + "！", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        mainPanel.add(welcomeLabel, BorderLayout.CENTER);
    }
    
    @Override
    public void dispose() {
        try {
            dbConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.dispose();
    }
} 