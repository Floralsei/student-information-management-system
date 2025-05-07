package com.studentinfo.ui;

import com.studentinfo.dao.EnrollmentDAO;
import com.studentinfo.dao.StudentDAO;
import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.Enrollment;
import com.studentinfo.model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class StudentListPanel extends JPanel {
    private final int courseId;
    private final EnrollmentDAO enrollmentDAO;
    private final StudentDAO studentDAO;
    private JTable studentTable;
    private DefaultTableModel tableModel;

    public StudentListPanel(int courseId) throws SQLException {
        this.courseId = courseId;
        this.enrollmentDAO = new EnrollmentDAO(DatabaseConnection.getInstance().getConnection());
        this.studentDAO = new StudentDAO(DatabaseConnection.getInstance().getConnection());
        initializeUI();
        loadStudents();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // 创建表格模型
        String[] columnNames = {"学号", "姓名", "性别", "电话", "邮箱", "选课状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.getTableHeader().setReorderingAllowed(false);

        // 添加滚动面板
        JScrollPane scrollPane = new JScrollPane(studentTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadStudents() {
        try {
            tableModel.setRowCount(0);
            List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByCourseId(courseId);
            for (Enrollment enrollment : enrollments) {
                Student student = studentDAO.getStudentById(enrollment.getStudentId());
                if (student != null) {
                    Object[] rowData = {
                            student.getStudentNumber(),
                            student.getName(),
                            student.getGender(),
                            student.getPhone(),
                            student.getEmail(),
                            enrollment.getStatus()
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "加载学生名单时发生错误: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
} 