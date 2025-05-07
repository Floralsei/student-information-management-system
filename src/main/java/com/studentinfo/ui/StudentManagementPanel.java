package com.studentinfo.ui;

import com.studentinfo.dao.StudentDAO;
import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentManagementPanel extends JPanel {
    private final StudentDAO studentDAO;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private static final Font DEFAULT_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);
    private static final Font TABLE_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);

    public StudentManagementPanel() throws SQLException {
        this.studentDAO = new StudentDAO(DatabaseConnection.getInstance().getConnection());
        initializeUI();
        loadStudents();
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
        String[] columnNames = {"ID", "学号", "姓名", "性别", "出生日期", "地址", "电话", "邮箱", "入学日期", "状态"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setFont(TABLE_FONT);
        studentTable.setRowHeight(30); // 增加行高
        studentTable.getTableHeader().setFont(TABLE_FONT);
        JScrollPane scrollPane = new JScrollPane(studentTable);

        // 添加组件到面板
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 添加事件监听器
        addButton.addActionListener(e -> showAddStudentDialog());
        editButton.addActionListener(e -> showEditStudentDialog());
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        refreshButton.addActionListener(e -> loadStudents());
    }

    private void loadStudents() {
        try {
            List<Student> students = studentDAO.getAllStudents();
            tableModel.setRowCount(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Student student : students) {
                Object[] row = {
                    student.getId(),
                    student.getStudentNumber(),
                    student.getName(),
                    student.getGender(),
                    student.getBirthDate() != null ? student.getBirthDate().format(formatter) : "",
                    student.getAddress(),
                    student.getPhone(),
                    student.getEmail(),
                    student.getEnrollmentDate() != null ? student.getEnrollmentDate().format(formatter) : "",
                    student.getStatus()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "加载学生数据失败: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddStudentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加学生", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 创建输入字段
        JTextField studentNumberField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"MALE", "FEMALE", "OTHER"});
        JTextField birthDateField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField enrollmentDateField = new JTextField(20);
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "GRADUATED", "SUSPENDED"});

        // 添加组件
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("学号:"), gbc);
        gbc.gridx = 1;
        dialog.add(studentNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("姓名:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("性别:"), gbc);
        gbc.gridx = 1;
        dialog.add(genderComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("出生日期:"), gbc);
        gbc.gridx = 1;
        dialog.add(birthDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("地址:"), gbc);
        gbc.gridx = 1;
        dialog.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(new JLabel("电话:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        dialog.add(new JLabel("邮箱:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        dialog.add(new JLabel("入学日期:"), gbc);
        gbc.gridx = 1;
        dialog.add(enrollmentDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        dialog.add(new JLabel("状态:"), gbc);
        gbc.gridx = 1;
        dialog.add(statusComboBox, gbc);

        // 添加按钮
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        // 添加事件监听器
        saveButton.addActionListener(e -> {
            try {
                Student student = new Student();
                student.setStudentNumber(studentNumberField.getText());
                student.setName(nameField.getText());
                student.setGender((String) genderComboBox.getSelectedItem());
                student.setBirthDate(LocalDate.parse(birthDateField.getText()));
                student.setAddress(addressField.getText());
                student.setPhone(phoneField.getText());
                student.setEmail(emailField.getText());
                student.setEnrollmentDate(LocalDate.parse(enrollmentDateField.getText()));
                student.setStatus((String) statusComboBox.getSelectedItem());

                studentDAO.addStudent(student);
                dialog.dispose();
                loadStudents();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "添加学生失败: " + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditStudentDialog() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "请选择要编辑的学生",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int studentId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            Student student = studentDAO.getStudentById(studentId);
            if (student == null) {
                JOptionPane.showMessageDialog(this,
                    "未找到选中的学生",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "编辑学生", true);
            dialog.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // 创建输入字段
            JTextField studentNumberField = new JTextField(student.getStudentNumber(), 20);
            JTextField nameField = new JTextField(student.getName(), 20);
            JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"MALE", "FEMALE", "OTHER"});
            genderComboBox.setSelectedItem(student.getGender());
            JTextField birthDateField = new JTextField(student.getBirthDate() != null ? 
                student.getBirthDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "", 20);
            JTextField addressField = new JTextField(student.getAddress(), 20);
            JTextField phoneField = new JTextField(student.getPhone(), 20);
            JTextField emailField = new JTextField(student.getEmail(), 20);
            JTextField enrollmentDateField = new JTextField(student.getEnrollmentDate() != null ? 
                student.getEnrollmentDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "", 20);
            JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "GRADUATED", "SUSPENDED"});
            statusComboBox.setSelectedItem(student.getStatus());

            // 添加组件
            gbc.gridx = 0; gbc.gridy = 0;
            dialog.add(new JLabel("学号:"), gbc);
            gbc.gridx = 1;
            dialog.add(studentNumberField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            dialog.add(new JLabel("姓名:"), gbc);
            gbc.gridx = 1;
            dialog.add(nameField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            dialog.add(new JLabel("性别:"), gbc);
            gbc.gridx = 1;
            dialog.add(genderComboBox, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            dialog.add(new JLabel("出生日期:"), gbc);
            gbc.gridx = 1;
            dialog.add(birthDateField, gbc);

            gbc.gridx = 0; gbc.gridy = 4;
            dialog.add(new JLabel("地址:"), gbc);
            gbc.gridx = 1;
            dialog.add(addressField, gbc);

            gbc.gridx = 0; gbc.gridy = 5;
            dialog.add(new JLabel("电话:"), gbc);
            gbc.gridx = 1;
            dialog.add(phoneField, gbc);

            gbc.gridx = 0; gbc.gridy = 6;
            dialog.add(new JLabel("邮箱:"), gbc);
            gbc.gridx = 1;
            dialog.add(emailField, gbc);

            gbc.gridx = 0; gbc.gridy = 7;
            dialog.add(new JLabel("入学日期:"), gbc);
            gbc.gridx = 1;
            dialog.add(enrollmentDateField, gbc);

            gbc.gridx = 0; gbc.gridy = 8;
            dialog.add(new JLabel("状态:"), gbc);
            gbc.gridx = 1;
            dialog.add(statusComboBox, gbc);

            // 添加按钮
            JPanel buttonPanel = new JPanel();
            JButton saveButton = new JButton("保存");
            JButton cancelButton = new JButton("取消");

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            gbc.gridx = 0; gbc.gridy = 9;
            gbc.gridwidth = 2;
            dialog.add(buttonPanel, gbc);

            // 添加事件监听器
            saveButton.addActionListener(e -> {
                try {
                    student.setStudentNumber(studentNumberField.getText());
                    student.setName(nameField.getText());
                    student.setGender((String) genderComboBox.getSelectedItem());
                    student.setBirthDate(LocalDate.parse(birthDateField.getText()));
                    student.setAddress(addressField.getText());
                    student.setPhone(phoneField.getText());
                    student.setEmail(emailField.getText());
                    student.setEnrollmentDate(LocalDate.parse(enrollmentDateField.getText()));
                    student.setStatus((String) statusComboBox.getSelectedItem());

                    studentDAO.updateStudent(student);
                    dialog.dispose();
                    loadStudents();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "更新学生失败: " + ex.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "加载学生数据失败: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "请选择要删除的学生",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要删除选中的学生吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int studentId = (int) tableModel.getValueAt(selectedRow, 0);
                studentDAO.deleteStudent(studentId);
                loadStudents();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "删除学生失败: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 