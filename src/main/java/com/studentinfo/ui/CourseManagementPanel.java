package com.studentinfo.ui;

import com.studentinfo.dao.CourseDAO;
import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.Course;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CourseManagementPanel extends JPanel {
    private final CourseDAO courseDAO;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private static final Font DEFAULT_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);
    private static final Font TABLE_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);
    private static final Font LABEL_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);

    public CourseManagementPanel() throws SQLException {
        this.courseDAO = new CourseDAO(DatabaseConnection.getInstance().getConnection());
        initializeUI();
        loadCourses();
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
        String[] columnNames = {"课程代码", "课程名称", "学分", "教师", "描述"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable = new JTable(tableModel);
        courseTable.setFont(TABLE_FONT);
        courseTable.setRowHeight(30); // 增加行高
        courseTable.getTableHeader().setFont(TABLE_FONT);
        JScrollPane scrollPane = new JScrollPane(courseTable);

        // 添加组件到面板
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 添加事件监听器
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteSelectedCourse());
        refreshButton.addActionListener(e -> loadCourses());
    }

    private void loadCourses() {
        try {
            tableModel.setRowCount(0);
            List<Course> courses = courseDAO.getAllCourses();
            for (Course course : courses) {
                Object[] row = {
                    course.getCourseCode(),
                    course.getName(),
                    course.getCredits(),
                    course.getTeacherId(),
                    course.getDescription()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "加载课程数据失败: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加课程", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 创建输入字段
        JTextField courseCodeField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField creditsField = new JTextField(20);
        JTextField teacherIdField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(3, 20);

        // 设置字体
        courseCodeField.setFont(DEFAULT_FONT);
        nameField.setFont(DEFAULT_FONT);
        creditsField.setFont(DEFAULT_FONT);
        teacherIdField.setFont(DEFAULT_FONT);
        descriptionArea.setFont(DEFAULT_FONT);

        // 创建标签
        JLabel courseCodeLabel = new JLabel("课程代码:");
        JLabel nameLabel = new JLabel("课程名称:");
        JLabel creditsLabel = new JLabel("学分:");
        JLabel teacherIdLabel = new JLabel("教师ID:");
        JLabel descriptionLabel = new JLabel("描述:");

        // 设置标签字体
        courseCodeLabel.setFont(LABEL_FONT);
        nameLabel.setFont(LABEL_FONT);
        creditsLabel.setFont(LABEL_FONT);
        teacherIdLabel.setFont(LABEL_FONT);
        descriptionLabel.setFont(LABEL_FONT);

        // 添加组件
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(courseCodeLabel, gbc);
        gbc.gridx = 1;
        dialog.add(courseCodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(nameLabel, gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(creditsLabel, gbc);
        gbc.gridx = 1;
        dialog.add(creditsField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(teacherIdLabel, gbc);
        gbc.gridx = 1;
        dialog.add(teacherIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(descriptionLabel, gbc);
        gbc.gridx = 1;
        dialog.add(new JScrollPane(descriptionArea), gbc);

        // 添加按钮
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");

        saveButton.setFont(BUTTON_FONT);
        cancelButton.setFont(BUTTON_FONT);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        // 添加事件监听器
        saveButton.addActionListener(e -> {
            try {
                Course course = new Course();
                course.setCourseCode(courseCodeField.getText());
                course.setName(nameField.getText());
                course.setCredits(Integer.parseInt(creditsField.getText()));
                course.setTeacherId(Integer.parseInt(teacherIdField.getText()));
                course.setDescription(descriptionArea.getText());

                courseDAO.addCourse(course);
                dialog.dispose();
                loadCourses();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "添加课程失败: " + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "请选择要编辑的课程",
                "提示",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String courseCode = (String) tableModel.getValueAt(selectedRow, 0);
            Course course = courseDAO.getCourseByCode(courseCode);
            if (course != null) {
                JTextField nameField = new JTextField(course.getName(), 20);
                JTextField creditsField = new JTextField(String.valueOf(course.getCredits()), 20);
                JTextField teacherIdField = new JTextField(String.valueOf(course.getTeacherId()), 20);
                JTextField descriptionField = new JTextField(course.getDescription(), 20);

                JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
                panel.add(new JLabel("课程编号:"));
                panel.add(new JLabel(course.getCourseCode()));
                panel.add(new JLabel("课程名称:"));
                panel.add(nameField);
                panel.add(new JLabel("学分:"));
                panel.add(creditsField);
                panel.add(new JLabel("教师ID:"));
                panel.add(teacherIdField);
                panel.add(new JLabel("描述:"));
                panel.add(descriptionField);

                int result = JOptionPane.showConfirmDialog(this,
                    panel,
                    "编辑课程",
                    JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    course.setName(nameField.getText());
                    course.setCredits(Integer.parseInt(creditsField.getText()));
                    course.setTeacherId(Integer.parseInt(teacherIdField.getText()));
                    course.setDescription(descriptionField.getText());

                    courseDAO.updateCourse(course);
                    loadCourses();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "编辑课程失败: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "请选择要删除的课程",
                "提示",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要删除选中的课程吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String courseCode = (String) tableModel.getValueAt(selectedRow, 0);
                Course course = courseDAO.getCourseByCode(courseCode);
                if (course != null) {
                    courseDAO.deleteCourse(course.getId());
                    loadCourses();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "删除课程失败: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 