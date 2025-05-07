package com.studentinfo.ui;

import com.studentinfo.dao.CourseDAO;
import com.studentinfo.dao.GradeDAO;
import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.Course;
import com.studentinfo.model.Grade;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StudentGradePanel extends JPanel {
    private final int studentId;
    private final GradeDAO gradeDAO;
    private final CourseDAO courseDAO;
    private JTable gradeTable;
    private DefaultTableModel tableModel;
    private JLabel gpaLabel;

    public StudentGradePanel(int studentId) throws SQLException {
        this.studentId = studentId;
        this.gradeDAO = new GradeDAO(DatabaseConnection.getInstance().getConnection());
        this.courseDAO = new CourseDAO(DatabaseConnection.getInstance().getConnection());
        initializeUI();
        loadGrades();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // 创建表格模型
        String[] columnNames = {"课程代码", "课程名称", "学分", "成绩", "绩点"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gradeTable = new JTable(tableModel);
        gradeTable.getTableHeader().setReorderingAllowed(false);

        // 添加滚动面板
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        add(scrollPane, BorderLayout.CENTER);

        // 创建GPA面板
        JPanel gpaPanel = new JPanel();
        gpaPanel.add(new JLabel("平均绩点(GPA)："));
        gpaLabel = new JLabel("0.00");
        gpaPanel.add(gpaLabel);
        add(gpaPanel, BorderLayout.SOUTH);
    }

    private void loadGrades() {
        try {
            tableModel.setRowCount(0);
            List<Grade> grades = gradeDAO.getGradesByStudentId(studentId);
            double totalGradePoints = 0;
            double totalCredits = 0;

            for (Grade grade : grades) {
                Course course = courseDAO.getCourseById(grade.getCourseId());
                if (course != null) {
                    double gradePoint = calculateGradePoint(grade.getScore());
                    double credits = course.getCredits();
                    totalGradePoints += gradePoint * credits;
                    totalCredits += credits;

                    Object[] rowData = {
                            course.getCourseCode(),
                            course.getName(),
                            credits,
                            grade.getScore(),
                            String.format("%.2f", gradePoint)
                    };
                    tableModel.addRow(rowData);
                }
            }

            // 计算并显示GPA
            double gpa = totalCredits > 0 ? totalGradePoints / totalCredits : 0;
            gpaLabel.setText(String.format("%.2f", gpa));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "加载成绩信息时发生错误: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calculateGradePoint(int score) {
        if (score >= 90) return 4.0;
        if (score >= 85) return 3.7;
        if (score >= 82) return 3.3;
        if (score >= 78) return 3.0;
        if (score >= 75) return 2.7;
        if (score >= 72) return 2.3;
        if (score >= 68) return 2.0;
        if (score >= 64) return 1.5;
        if (score >= 60) return 1.0;
        return 0.0;
    }
} 