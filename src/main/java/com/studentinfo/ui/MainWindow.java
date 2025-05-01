package com.studentinfo.ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private String userRole;
    private JTabbedPane tabbedPane;

    public MainWindow(String userRole) {
        this.userRole = userRole;
        setTitle("学生信息管理系统");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        initUI();
        setupMenu();
    }

    private void initUI() {
        // 顶部欢迎信息
        JLabel welcomeLabel = new JLabel("欢迎使用学生信息管理系统 - 当前用户：" + userRole);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        // 创建选项卡
        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        // 添加学生管理选项卡（仅管理员可见）
        if ("admin".equals(userRole)) {
            StudentManagementTab studentTab = new StudentManagementTab();
            tabbedPane.addTab("学生信息管理", studentTab);
        }

        // 添加成绩管理选项卡
        GradeManagementTab gradeTab = new GradeManagementTab();
        tabbedPane.addTab("成绩管理", gradeTab);
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        // 文件菜单
        JMenu fileMenu = new JMenu("文件");
        
        // 添加用户管理菜单项（仅管理员可见）
        if ("admin".equals(userRole)) {
            JMenuItem userManagementItem = new JMenuItem("用户管理");
            userManagementItem.addActionListener(e -> showUserManagement());
            fileMenu.add(userManagementItem);
            fileMenu.addSeparator();
        }

        JMenuItem logoutItem = new JMenuItem("退出登录");
        logoutItem.addActionListener(e -> logout());
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);

        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助");
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void showUserManagement() {
        UserManagementDialog dialog = new UserManagementDialog(this);
        dialog.setVisible(true);
    }

    private void logout() {
        int reply = JOptionPane.showConfirmDialog(this, "确定要退出登录吗？", "确认",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (reply == JOptionPane.YES_OPTION) {
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
            dispose();
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "学生信息管理系统 v1.0\n\n" +
                "基于Java和Swing开发\n" +
                "用于管理学生信息和成绩",
                "关于", JOptionPane.INFORMATION_MESSAGE);
    }
} 