package com.studentinfo.ui;

import com.studentinfo.dao.UserDAO;
import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private final UserDAO userDAO;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private static final Font DEFAULT_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);
    private static final Font TABLE_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);

    public UserManagementPanel() throws SQLException {
        this.userDAO = new UserDAO(DatabaseConnection.getInstance().getConnection());
        initializeUI();
        loadUsers();
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
        String[] columnNames = {"ID", "用户名", "角色", "邮箱", "电话"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setFont(TABLE_FONT);
        userTable.setRowHeight(30); // 增加行高
        userTable.getTableHeader().setFont(TABLE_FONT);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // 添加组件到面板
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 添加事件监听器
        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        refreshButton.addActionListener(e -> loadUsers());
    }

    private void loadUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            tableModel.setRowCount(0);
            for (User user : users) {
                Object[] row = {
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    user.getEmail(),
                    user.getPhone()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "加载用户数据失败: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加用户", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 创建输入字段
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"ADMIN", "TEACHER", "STUDENT"});
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);

        // 添加组件
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1;
        dialog.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("密码:"), gbc);
        gbc.gridx = 1;
        dialog.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("角色:"), gbc);
        gbc.gridx = 1;
        dialog.add(roleComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("邮箱:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel("电话:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        // 添加按钮
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        // 添加事件监听器
        saveButton.addActionListener(e -> {
            try {
                User user = new User();
                user.setUsername(usernameField.getText());
                user.setPassword(new String(passwordField.getPassword()));
                user.setRole(User.Role.valueOf((String) roleComboBox.getSelectedItem()));
                user.setEmail(emailField.getText());
                user.setPhone(phoneField.getText());

                userDAO.addUser(user);
                dialog.dispose();
                loadUsers();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "添加用户失败: " + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "请选择要编辑的用户",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            User user = userDAO.getUserById(userId);
            if (user == null) {
                JOptionPane.showMessageDialog(this,
                    "未找到选中的用户",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "编辑用户", true);
            dialog.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // 创建输入字段
            JTextField usernameField = new JTextField(user.getUsername(), 20);
            JPasswordField passwordField = new JPasswordField(20);
            JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"ADMIN", "TEACHER", "STUDENT"});
            roleComboBox.setSelectedItem(user.getRole().toString());
            JTextField emailField = new JTextField(user.getEmail(), 20);
            JTextField phoneField = new JTextField(user.getPhone(), 20);

            // 添加组件
            gbc.gridx = 0; gbc.gridy = 0;
            dialog.add(new JLabel("用户名:"), gbc);
            gbc.gridx = 1;
            dialog.add(usernameField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            dialog.add(new JLabel("密码:"), gbc);
            gbc.gridx = 1;
            dialog.add(passwordField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            dialog.add(new JLabel("角色:"), gbc);
            gbc.gridx = 1;
            dialog.add(roleComboBox, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            dialog.add(new JLabel("邮箱:"), gbc);
            gbc.gridx = 1;
            dialog.add(emailField, gbc);

            gbc.gridx = 0; gbc.gridy = 4;
            dialog.add(new JLabel("电话:"), gbc);
            gbc.gridx = 1;
            dialog.add(phoneField, gbc);

            // 添加按钮
            JPanel buttonPanel = new JPanel();
            JButton saveButton = new JButton("保存");
            JButton cancelButton = new JButton("取消");

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            gbc.gridx = 0; gbc.gridy = 5;
            gbc.gridwidth = 2;
            dialog.add(buttonPanel, gbc);

            // 添加事件监听器
            saveButton.addActionListener(e -> {
                try {
                    user.setUsername(usernameField.getText());
                    String newPassword = new String(passwordField.getPassword());
                    if (!newPassword.isEmpty()) {
                        user.setPassword(newPassword);
                    }
                    user.setRole(User.Role.valueOf((String) roleComboBox.getSelectedItem()));
                    user.setEmail(emailField.getText());
                    user.setPhone(phoneField.getText());

                    userDAO.updateUser(user);
                    dialog.dispose();
                    loadUsers();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "更新用户失败: " + ex.getMessage(),
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
                "加载用户数据失败: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "请选择要删除的用户",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要删除选中的用户吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int userId = (int) tableModel.getValueAt(selectedRow, 0);
                userDAO.deleteUser(userId);
                loadUsers();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "删除用户失败: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 