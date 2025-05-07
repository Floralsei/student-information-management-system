package com.studentinfo.ui;

import com.studentinfo.dao.CourseDAO;
import com.studentinfo.dao.EnrollmentDAO;
import com.studentinfo.dao.StudentDAO;
import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.Course;
import com.studentinfo.model.Enrollment;
import com.studentinfo.model.Student;
import com.studentinfo.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class StudentEnrollmentPanel extends JPanel {
    private final int studentId;
    private final EnrollmentDAO enrollmentDAO;
    private final CourseDAO courseDAO;
    private final StudentDAO studentDAO;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JButton enrollButton;
    private JButton dropButton;

    public StudentEnrollmentPanel(User user) throws SQLException {
        this.studentDAO = new StudentDAO(DatabaseConnection.getInstance().getConnection());
        Student student = studentDAO.getStudentByUserId(user.getId());
        if (student == null) {
            throw new IllegalStateException("找不到对应的学生信息");
        }
        this.studentId = student.getId();
        this.enrollmentDAO = new EnrollmentDAO(DatabaseConnection.getInstance().getConnection());
        this.courseDAO = new CourseDAO(DatabaseConnection.getInstance().getConnection());
        initializeUI();
        loadAvailableCourses();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // 创建工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        enrollButton = new JButton("选课");
        dropButton = new JButton("退课");
        JButton refreshButton = new JButton("刷新");

        toolBar.add(enrollButton);
        toolBar.add(dropButton);
        toolBar.add(refreshButton);

        // 创建表格
        String[] columnNames = {"课程代码", "课程名称", "学分", "教师", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(courseTable);

        // 添加组件
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 添加事件监听器
        enrollButton.addActionListener(e -> enrollSelectedCourse());
        dropButton.addActionListener(e -> dropSelectedCourse());
        refreshButton.addActionListener(e -> loadAvailableCourses());
    }

    private void loadAvailableCourses() {
        try {
            tableModel.setRowCount(0);
            List<Course> courses = courseDAO.getAvailableCourses();
            for (Course course : courses) {
                Enrollment enrollment = enrollmentDAO.getEnrollmentByStudentAndCourse(studentId, course.getId());
                Object[] row = {
                    course.getCourseCode(),
                    course.getName(),
                    course.getCredits(),
                    course.getTeacherId(),
                    enrollment != null ? enrollment.getStatus() : "未选"
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "加载课程信息失败: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enrollSelectedCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "请选择要选修的课程",
                "提示",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String courseCode = (String) tableModel.getValueAt(selectedRow, 0);
            Course course = courseDAO.getCourseByCode(courseCode);
            if (course != null) {
                Enrollment enrollment = new Enrollment();
                enrollment.setStudentId(studentId);
                enrollment.setCourseId(course.getId());
                enrollment.setStatus("ACTIVE");
                enrollment.setEnrollmentDate(LocalDate.now());

                enrollmentDAO.addEnrollment(enrollment);
                loadAvailableCourses();
                JOptionPane.showMessageDialog(this,
                    "选课成功",
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "选课失败: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dropSelectedCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "请选择要退选的课程",
                "提示",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String courseCode = (String) tableModel.getValueAt(selectedRow, 0);
            Course course = courseDAO.getCourseByCode(courseCode);
            if (course != null) {
                Enrollment enrollment = enrollmentDAO.getEnrollmentByStudentAndCourse(studentId, course.getId());
                if (enrollment != null) {
                    enrollmentDAO.deleteEnrollment(studentId, course.getId());
                    loadAvailableCourses();
                    JOptionPane.showMessageDialog(this,
                        "退课成功",
                        "成功",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "退课失败: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 