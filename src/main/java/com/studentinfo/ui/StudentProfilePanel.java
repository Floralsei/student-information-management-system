package com.studentinfo.ui;

import com.studentinfo.dao.StudentDAO;
import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.Student;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class StudentProfilePanel extends JPanel {
    private final int studentId;
    private final StudentDAO studentDAO;
    private Student student;

    private JTextField studentNumberField;
    private JTextField nameField;
    private JComboBox<String> genderComboBox;
    private JTextField birthDateField;
    private JTextField addressField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField enrollmentDateField;
    private JComboBox<String> statusComboBox;
    private JTextField politicalStatusField;
    private JTextField dormitoryField;

    public StudentProfilePanel(int studentId) throws SQLException {
        this.studentId = studentId;
        this.studentDAO = new StudentDAO(DatabaseConnection.getInstance().getConnection());
        initializeUI();
        loadStudentData();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 创建输入字段
        studentNumberField = new JTextField(20);
        nameField = new JTextField(20);
        genderComboBox = new JComboBox<>(new String[]{"男", "女"});
        birthDateField = new JTextField(20);
        addressField = new JTextField(20);
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        enrollmentDateField = new JTextField(20);
        statusComboBox = new JComboBox<>(new String[]{"在读", "休学", "退学", "毕业"});
        politicalStatusField = new JTextField(20);
        dormitoryField = new JTextField(20);

        // 添加组件
        int row = 0;
        addLabelAndField("学号:", studentNumberField, gbc, row++);
        addLabelAndField("姓名:", nameField, gbc, row++);
        addLabelAndField("性别:", genderComboBox, gbc, row++);
        addLabelAndField("出生日期:", birthDateField, gbc, row++);
        addLabelAndField("地址:", addressField, gbc, row++);
        addLabelAndField("电话:", phoneField, gbc, row++);
        addLabelAndField("邮箱:", emailField, gbc, row++);
        addLabelAndField("入学日期:", enrollmentDateField, gbc, row++);
        addLabelAndField("学籍状态:", statusComboBox, gbc, row++);
        addLabelAndField("政治面貌:", politicalStatusField, gbc, row++);
        addLabelAndField("宿舍:", dormitoryField, gbc, row++);

        // 添加保存按钮
        JButton saveButton = new JButton("保存");
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        add(saveButton, gbc);

        saveButton.addActionListener(e -> saveStudentData());
    }

    private void addLabelAndField(String labelText, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        add(field, gbc);
    }

    private void loadStudentData() {
        try {
            student = studentDAO.getStudentById(studentId);
            if (student != null) {
                studentNumberField.setText(student.getStudentNumber());
                nameField.setText(student.getName());
                genderComboBox.setSelectedItem(student.getGender());
                birthDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(student.getBirthDate()));
                addressField.setText(student.getAddress());
                phoneField.setText(student.getPhone());
                emailField.setText(student.getEmail());
                enrollmentDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(student.getEnrollmentDate()));
                statusComboBox.setSelectedItem(student.getStatus());
                politicalStatusField.setText(student.getPoliticalStatus());
                dormitoryField.setText(student.getDormitory());

                // 设置字段为只读
                studentNumberField.setEditable(false);
                nameField.setEditable(false);
                genderComboBox.setEnabled(false);
                birthDateField.setEditable(false);
                enrollmentDateField.setEditable(false);
                statusComboBox.setEnabled(false);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "加载学生信息时发生错误: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveStudentData() {
        try {
            if (student != null) {
                student.setAddress(addressField.getText());
                student.setPhone(phoneField.getText());
                student.setEmail(emailField.getText());
                student.setPoliticalStatus(politicalStatusField.getText());
                student.setDormitory(dormitoryField.getText());

                studentDAO.updateStudent(student);
                JOptionPane.showMessageDialog(this,
                        "个人信息已更新",
                        "成功",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "保存学生信息时发生错误: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
} 