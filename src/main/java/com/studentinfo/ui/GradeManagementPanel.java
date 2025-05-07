package com.studentinfo.ui;

import com.studentinfo.dao.CourseDAO;
import com.studentinfo.dao.EnrollmentDAO;
import com.studentinfo.dao.GradeDAO;
import com.studentinfo.dao.StudentDAO;
import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.Course;
import com.studentinfo.model.Enrollment;
import com.studentinfo.model.Grade;
import com.studentinfo.model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class GradeManagementPanel extends JPanel {
    private final int teacherId;
    private final GradeDAO gradeDAO;
    private final StudentDAO studentDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final CourseDAO courseDAO;
    private JTable gradeTable;
    private DefaultTableModel tableModel;
    private JComboBox<Course> courseComboBox;

    public GradeManagementPanel(int teacherId) throws SQLException {
        this.teacherId = teacherId;
        this.gradeDAO = new GradeDAO(DatabaseConnection.getInstance().getConnection());
        this.studentDAO = new StudentDAO(DatabaseConnection.getInstance().getConnection());
        this.enrollmentDAO = new EnrollmentDAO(DatabaseConnection.getInstance().getConnection());
        this.courseDAO = new CourseDAO(DatabaseConnection.getInstance().getConnection());
        initializeUI();
        loadCourses();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // 创建工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // 添加课程选择下拉框
        courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(200, 25));
        courseComboBox.addActionListener(e -> loadGrades());
        toolBar.add(new JLabel("选择课程: "));
        toolBar.add(courseComboBox);
        toolBar.addSeparator();

        JButton addButton = new JButton("录入成绩");
        JButton editButton = new JButton("修改成绩");
        JButton deleteButton = new JButton("删除成绩");
        JButton refreshButton = new JButton("刷新");

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.add(refreshButton);

        // 创建表格模型
        String[] columnNames = {"学号", "姓名", "成绩", "评语"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gradeTable = new JTable(tableModel);
        gradeTable.getTableHeader().setReorderingAllowed(false);

        // 添加组件
        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(gradeTable), BorderLayout.CENTER);

        // 添加事件监听器
        addButton.addActionListener(e -> showAddGradeDialog());
        editButton.addActionListener(e -> showEditGradeDialog());
        deleteButton.addActionListener(e -> deleteSelectedGrade());
        refreshButton.addActionListener(e -> loadGrades());
    }

    private void loadCourses() {
        try {
            courseComboBox.removeAllItems();
            List<Course> courses = courseDAO.getCoursesByTeacherId(teacherId);
            for (Course course : courses) {
                courseComboBox.addItem(course);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "加载课程信息时发生错误: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadGrades() {
        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        if (selectedCourse == null) {
            return;
        }

        try {
            tableModel.setRowCount(0);
            List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByCourseId(selectedCourse.getId());
            for (Enrollment enrollment : enrollments) {
                Student student = studentDAO.getStudentById(enrollment.getStudentId());
                Grade grade = gradeDAO.getGradeByStudentAndCourse(enrollment.getStudentId(), selectedCourse.getId());
                if (student != null) {
                    Object[] rowData = {
                            student.getStudentNumber(),
                            student.getName(),
                            grade != null ? grade.getScore() : "未评分",
                            grade != null ? grade.getComment() : ""
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "加载成绩信息时发生错误: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddGradeDialog() {
        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this,
                    "请先选择一个课程",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = gradeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "请选择一个学生",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String studentNumber = (String) tableModel.getValueAt(selectedRow, 0);
            Student student = studentDAO.getStudentByNumber(studentNumber);
            if (student != null) {
                JTextField scoreField = new JTextField(10);
                JTextField commentField = new JTextField(20);

                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);

                gbc.gridx = 0; gbc.gridy = 0;
                panel.add(new JLabel("成绩:"), gbc);
                gbc.gridx = 1;
                panel.add(scoreField, gbc);

                gbc.gridx = 0; gbc.gridy = 1;
                panel.add(new JLabel("评语:"), gbc);
                gbc.gridx = 1;
                panel.add(commentField, gbc);

                int result = JOptionPane.showConfirmDialog(this, panel,
                        "录入成绩", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        int score = Integer.parseInt(scoreField.getText());
                        if (score < 0 || score > 100) {
                            throw new NumberFormatException();
                        }

                        Grade grade = new Grade();
                        grade.setStudentId(student.getId());
                        grade.setCourseId(selectedCourse.getId());
                        grade.setScore(score);
                        grade.setComment(commentField.getText());

                        gradeDAO.addGrade(grade);
                        loadGrades();
                        JOptionPane.showMessageDialog(this,
                                "成绩已录入",
                                "成功",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this,
                                "请输入0-100之间的有效成绩",
                                "错误",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "录入成绩时发生错误: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditGradeDialog() {
        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this,
                    "请先选择一个课程",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = gradeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "请选择一个学生",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String studentNumber = (String) tableModel.getValueAt(selectedRow, 0);
            Student student = studentDAO.getStudentByNumber(studentNumber);
            if (student != null) {
                Grade grade = gradeDAO.getGradeByStudentAndCourse(student.getId(), selectedCourse.getId());
                if (grade == null) {
                    JOptionPane.showMessageDialog(this,
                            "该学生尚未录入成绩",
                            "提示",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                JTextField scoreField = new JTextField(String.valueOf(grade.getScore()), 10);
                JTextField commentField = new JTextField(grade.getComment(), 20);

                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);

                gbc.gridx = 0; gbc.gridy = 0;
                panel.add(new JLabel("成绩:"), gbc);
                gbc.gridx = 1;
                panel.add(scoreField, gbc);

                gbc.gridx = 0; gbc.gridy = 1;
                panel.add(new JLabel("评语:"), gbc);
                gbc.gridx = 1;
                panel.add(commentField, gbc);

                int result = JOptionPane.showConfirmDialog(this, panel,
                        "修改成绩", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        int score = Integer.parseInt(scoreField.getText());
                        if (score < 0 || score > 100) {
                            throw new NumberFormatException();
                        }

                        grade.setScore(score);
                        grade.setComment(commentField.getText());

                        gradeDAO.updateGrade(grade);
                        loadGrades();
                        JOptionPane.showMessageDialog(this,
                                "成绩已更新",
                                "成功",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this,
                                "请输入0-100之间的有效成绩",
                                "错误",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "修改成绩时发生错误: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedGrade() {
        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this,
                    "请先选择一个课程",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = gradeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "请选择一个学生",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
                "确定要删除该成绩记录吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            try {
                String studentNumber = (String) tableModel.getValueAt(selectedRow, 0);
                Student student = studentDAO.getStudentByNumber(studentNumber);
                if (student != null) {
                    Grade grade = gradeDAO.getGradeByStudentAndCourse(student.getId(), selectedCourse.getId());
                    if (grade != null) {
                        gradeDAO.deleteGrade(grade.getId());
                        loadGrades();
                        JOptionPane.showMessageDialog(this,
                                "成绩已删除",
                                "成功",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "删除成绩时发生错误: " + e.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 