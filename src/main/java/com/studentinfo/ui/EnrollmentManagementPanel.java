package com.studentinfo.ui;

import com.studentinfo.dao.EnrollmentDAO;
import com.studentinfo.dao.StudentDAO;
import com.studentinfo.dao.CourseDAO;
import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.Enrollment;
import com.studentinfo.model.Student;
import com.studentinfo.model.Course;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EnrollmentManagementPanel extends JPanel {
    private final EnrollmentDAO enrollmentDAO;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private JTable enrollmentTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Font DEFAULT_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);
    private static final Font TABLE_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);
    private static final Font LABEL_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);

    public EnrollmentManagementPanel() throws SQLException {
        this.enrollmentDAO = new EnrollmentDAO(DatabaseConnection.getInstance().getConnection());
        this.studentDAO = new StudentDAO(DatabaseConnection.getInstance().getConnection());
        this.courseDAO = new CourseDAO(DatabaseConnection.getInstance().getConnection());
        initializeUI();
        loadEnrollments();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // 创建工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        addButton = new JButton("添加");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");
        refreshButton = new JButton("刷新");

        // 设置按钮字体
        addButton.setFont(BUTTON_FONT);
        editButton.setFont(BUTTON_FONT);
        deleteButton.setFont(BUTTON_FONT);
        refreshButton.setFont(BUTTON_FONT);

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.add(refreshButton);

        // 创建表格
        String[] columnNames = {"学生", "课程", "选课日期", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enrollmentTable = new JTable(tableModel);
        enrollmentTable.setFont(TABLE_FONT);
        enrollmentTable.setRowHeight(30); // 增加行高
        enrollmentTable.getTableHeader().setFont(TABLE_FONT);
        JScrollPane scrollPane = new JScrollPane(enrollmentTable);

        // 添加组件到面板
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 添加事件监听器
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteSelectedEnrollment());
        refreshButton.addActionListener(e -> loadEnrollments());
    }

    private void loadEnrollments() {
        try {
            tableModel.setRowCount(0);
            List<Enrollment> enrollments = enrollmentDAO.getAllEnrollments();
            for (Enrollment enrollment : enrollments) {
                Student student = studentDAO.getStudentById(enrollment.getStudentId());
                Course course = courseDAO.getCourseById(enrollment.getCourseId());
                Object[] row = {
                    student != null ? student.getName() : "未知学生",
                    course != null ? course.getName() : "未知课程",
                    enrollment.getEnrollmentDate(),
                    enrollment.getStatus()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "加载选课数据失败: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        try {
            List<Student> students = studentDAO.getAllStudents();
            List<Course> courses = courseDAO.getAllCourses();

            JComboBox<Student> studentCombo = new JComboBox<>(students.toArray(new Student[0]));
            JComboBox<Course> courseCombo = new JComboBox<>(courses.toArray(new Course[0]));
            JTextField enrollmentDateField = new JTextField(20);
            JComboBox<String> statusCombo = new JComboBox<>(new String[]{"ACTIVE", "COMPLETED", "DROPPED"});

            // 设置字体
            studentCombo.setFont(DEFAULT_FONT);
            courseCombo.setFont(DEFAULT_FONT);
            enrollmentDateField.setFont(DEFAULT_FONT);
            statusCombo.setFont(DEFAULT_FONT);

            JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
            JLabel studentLabel = new JLabel("学生:");
            JLabel courseLabel = new JLabel("课程:");
            JLabel dateLabel = new JLabel("选课日期:");
            JLabel statusLabel = new JLabel("状态:");

            // 设置标签字体
            studentLabel.setFont(LABEL_FONT);
            courseLabel.setFont(LABEL_FONT);
            dateLabel.setFont(LABEL_FONT);
            statusLabel.setFont(LABEL_FONT);

            panel.add(studentLabel);
            panel.add(studentCombo);
            panel.add(courseLabel);
            panel.add(courseCombo);
            panel.add(dateLabel);
            panel.add(enrollmentDateField);
            panel.add(statusLabel);
            panel.add(statusCombo);

            int result = JOptionPane.showConfirmDialog(this,
                panel,
                "添加选课",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                Student selectedStudent = (Student) studentCombo.getSelectedItem();
                Course selectedCourse = (Course) courseCombo.getSelectedItem();
                String enrollmentDate = enrollmentDateField.getText();
                String status = (String) statusCombo.getSelectedItem();

                if (selectedStudent != null && selectedCourse != null) {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setStudentId(selectedStudent.getId());
                    enrollment.setCourseId(selectedCourse.getId());
                    enrollment.setEnrollmentDate(LocalDate.parse(enrollmentDate, DATE_FORMATTER));
                    enrollment.setStatus(status);

                    enrollmentDAO.addEnrollment(enrollment);
                    loadEnrollments();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "添加选课失败: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                "日期格式错误，请使用yyyy-MM-dd格式",
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditDialog() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "请选择要编辑的选课记录",
                "提示",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String studentName = (String) tableModel.getValueAt(selectedRow, 0);
            String courseName = (String) tableModel.getValueAt(selectedRow, 1);
            String enrollmentDate = (String) tableModel.getValueAt(selectedRow, 2);
            String status = (String) tableModel.getValueAt(selectedRow, 3);

            List<Student> students = studentDAO.getAllStudents();
            List<Course> courses = courseDAO.getAllCourses();

            JComboBox<Student> studentCombo = new JComboBox<>(students.toArray(new Student[0]));
            JComboBox<Course> courseCombo = new JComboBox<>(courses.toArray(new Course[0]));
            JTextField enrollmentDateField = new JTextField(enrollmentDate, 20);
            JComboBox<String> statusCombo = new JComboBox<>(new String[]{"ACTIVE", "COMPLETED", "DROPPED"});
            statusCombo.setSelectedItem(status);

            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            panel.add(new JLabel("学生:"));
            panel.add(studentCombo);
            panel.add(new JLabel("课程:"));
            panel.add(courseCombo);
            panel.add(new JLabel("选课日期:"));
            panel.add(enrollmentDateField);
            panel.add(new JLabel("状态:"));
            panel.add(statusCombo);

            int result = JOptionPane.showConfirmDialog(this,
                panel,
                "编辑选课",
                JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                Student selectedStudent = (Student) studentCombo.getSelectedItem();
                Course selectedCourse = (Course) courseCombo.getSelectedItem();
                String newEnrollmentDate = enrollmentDateField.getText();
                String newStatus = (String) statusCombo.getSelectedItem();

                if (selectedStudent != null && selectedCourse != null) {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setStudentId(selectedStudent.getId());
                    enrollment.setCourseId(selectedCourse.getId());
                    enrollment.setEnrollmentDate(LocalDate.parse(newEnrollmentDate, DATE_FORMATTER));
                    enrollment.setStatus(newStatus);

                    enrollmentDAO.updateEnrollment(enrollment);
                    loadEnrollments();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "编辑选课失败: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                "日期格式错误，请使用yyyy-MM-dd格式",
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedEnrollment() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "请选择要删除的选课记录",
                "提示",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要删除选中的选课记录吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String studentName = (String) tableModel.getValueAt(selectedRow, 0);
                String courseName = (String) tableModel.getValueAt(selectedRow, 1);
                List<Student> students = studentDAO.getAllStudents();
                List<Course> courses = courseDAO.getAllCourses();

                Student selectedStudent = null;
                Course selectedCourse = null;

                for (Student student : students) {
                    if (student.getName().equals(studentName)) {
                        selectedStudent = student;
                        break;
                    }
                }

                for (Course course : courses) {
                    if (course.getName().equals(courseName)) {
                        selectedCourse = course;
                        break;
                    }
                }

                if (selectedStudent != null && selectedCourse != null) {
                    enrollmentDAO.deleteEnrollment(selectedStudent.getId(), selectedCourse.getId());
                    loadEnrollments();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "删除选课记录失败: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 