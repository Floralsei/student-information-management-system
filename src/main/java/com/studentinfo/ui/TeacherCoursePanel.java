package com.studentinfo.ui;

import com.studentinfo.dao.CourseDAO;
import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.Course;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TeacherCoursePanel extends JPanel {
    private final int teacherId;
    private final CourseDAO courseDAO;
    private JTable courseTable;
    private DefaultTableModel tableModel;

    public TeacherCoursePanel(int teacherId) throws SQLException {
        this.teacherId = teacherId;
        this.courseDAO = new CourseDAO(DatabaseConnection.getInstance().getConnection());
        initializeUI();
        loadCourses();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // 创建表格模型
        String[] columnNames = {"课程代码", "课程名称", "学分", "课程描述", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(tableModel);
        courseTable.getTableHeader().setReorderingAllowed(false);

        // 添加滚动面板
        JScrollPane scrollPane = new JScrollPane(courseTable);
        add(scrollPane, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        JButton viewStudentsButton = new JButton("查看学生名单");
        JButton manageGradesButton = new JButton("管理成绩");
        buttonPanel.add(viewStudentsButton);
        buttonPanel.add(manageGradesButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 添加事件监听器
        viewStudentsButton.addActionListener(e -> showStudentList());
        manageGradesButton.addActionListener(e -> showGradeManagement());
    }

    private void loadCourses() {
        try {
            tableModel.setRowCount(0);
            List<Course> courses = courseDAO.getCoursesByTeacherId(teacherId);
            for (Course course : courses) {
                Object[] rowData = {
                        course.getCourseCode(),
                        course.getName(),
                        course.getCredits(),
                        course.getDescription(),
                        course.getStatus()
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "加载课程信息时发生错误: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStudentList() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "请选择一个课程",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String courseCode = (String) tableModel.getValueAt(selectedRow, 0);
            Course course = courseDAO.getCourseByCode(courseCode);
            if (course != null) {
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                        "学生名单 - " + course.getName(),
                        true);
                dialog.setLayout(new BorderLayout());
                dialog.add(new StudentListPanel(course.getId()), BorderLayout.CENTER);
                dialog.setSize(600, 400);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "查看学生名单时发生错误: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showGradeManagement() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "请选择一个课程",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String courseCode = (String) tableModel.getValueAt(selectedRow, 0);
            Course course = courseDAO.getCourseByCode(courseCode);
            if (course != null) {
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                        "成绩管理 - " + course.getName(),
                        true);
                dialog.setLayout(new BorderLayout());
                dialog.add(new GradeManagementPanel(course.getId()), BorderLayout.CENTER);
                dialog.setSize(800, 600);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "打开成绩管理时发生错误: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
} 