package com.studentinfo.ui;

import com.studentinfo.model.User;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainWindow extends JFrame {
    private final User currentUser;
    private final JTabbedPane tabbedPane;
    private static final Font DEFAULT_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);
    private static final Font MENU_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);

    public MainWindow(User user) throws SQLException {
        this.currentUser = user;
        setTitle("学生信息管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        // 设置全局字体
        setUIFont(DEFAULT_FONT);

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        fileMenu.setFont(MENU_FONT);
        JMenuItem exitMenuItem = new JMenuItem("退出");
        exitMenuItem.setFont(MENU_FONT);
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // 创建选项卡面板
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(DEFAULT_FONT);

        // 根据用户角色添加不同的面板
        switch (currentUser.getRole()) {
            case ADMIN:
                addAdminPanels();
                break;
            case TEACHER:
                addTeacherPanels();
                break;
            case STUDENT:
                addStudentPanels();
                break;
        }

        add(tabbedPane);
    }

    // 设置全局字体
    private void setUIFont(Font font) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, new javax.swing.plaf.FontUIResource(font));
            }
        }
    }

    private void addAdminPanels() throws SQLException {
        tabbedPane.addTab("用户管理", new UserManagementPanel());
        tabbedPane.addTab("学生管理", new StudentManagementPanel());
        tabbedPane.addTab("课程管理", new CourseManagementPanel());
        tabbedPane.addTab("选课管理", new EnrollmentManagementPanel());
    }

    private void addTeacherPanels() throws SQLException {
        tabbedPane.addTab("课程管理", new TeacherCoursePanel(currentUser.getId()));
        tabbedPane.addTab("成绩管理", new GradeManagementPanel(currentUser.getId()));
    }

    private void addStudentPanels() throws SQLException {
        tabbedPane.addTab("个人信息", new StudentProfilePanel(currentUser.getId()));
        tabbedPane.addTab("选课", new StudentEnrollmentPanel(currentUser));
        tabbedPane.addTab("成绩查询", new StudentGradePanel(currentUser.getId()));
    }
} 