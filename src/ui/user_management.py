from PyQt6.QtWidgets import (QDialog, QVBoxLayout, QHBoxLayout, QPushButton,
                            QTableWidget, QTableWidgetItem, QMessageBox, QFormLayout,
                            QLineEdit, QComboBox)
from database.db_connection import DatabaseConnection
import sqlite3

class UserDialog(QDialog):
    def __init__(self, parent=None, user_data=None):
        super().__init__(parent)
        self.user_data = user_data
        self.setup_ui()

    def setup_ui(self):
        self.setWindowTitle("添加用户" if not self.user_data else "修改用户")
        layout = QFormLayout(self)

        self.username = QLineEdit()
        self.password = QLineEdit()
        self.password.setEchoMode(QLineEdit.EchoMode.Password)
        
        self.role_combo = QComboBox()
        self.role_combo.addItems(['admin', 'teacher', 'student'])

        if self.user_data:
            self.username.setText(self.user_data[0])
            self.role_combo.setCurrentText(self.user_data[2])
            self.username.setEnabled(False)

        layout.addRow("用户名:", self.username)
        layout.addRow("密码:", self.password)
        layout.addRow("角色:", self.role_combo)

        buttons = QHBoxLayout()
        save_button = QPushButton("保存")
        cancel_button = QPushButton("取消")
        save_button.clicked.connect(self.accept)
        cancel_button.clicked.connect(self.reject)
        buttons.addWidget(save_button)
        buttons.addWidget(cancel_button)
        layout.addRow(buttons)

class UserManagementDialog(QDialog):
    def __init__(self, parent=None):
        super().__init__(parent)
        self.db = DatabaseConnection()
        self.setup_ui()
        self.load_users()

    def setup_ui(self):
        self.setWindowTitle("用户管理")
        self.setMinimumSize(500, 400)
        layout = QVBoxLayout(self)

        # 按钮栏
        button_layout = QHBoxLayout()
        add_button = QPushButton("添加用户")
        edit_button = QPushButton("修改用户")
        delete_button = QPushButton("删除用户")
        add_button.clicked.connect(self.add_user)
        edit_button.clicked.connect(self.edit_user)
        delete_button.clicked.connect(self.delete_user)
        button_layout.addWidget(add_button)
        button_layout.addWidget(edit_button)
        button_layout.addWidget(delete_button)
        layout.addLayout(button_layout)

        # 用户列表
        self.table = QTableWidget()
        self.table.setColumnCount(3)
        self.table.setHorizontalHeaderLabels(["用户名", "密码", "角色"])
        self.table.setSelectionBehavior(QTableWidget.SelectionBehavior.SelectRows)
        self.table.setEditTriggers(QTableWidget.EditTrigger.NoEditTriggers)
        layout.addWidget(self.table)

    def load_users(self):
        conn = self.db.connect()
        cursor = conn.cursor()
        cursor.execute('SELECT username, password, role FROM users')
        users = cursor.fetchall()
        self.db.close()

        self.table.setRowCount(len(users))
        for row, user in enumerate(users):
            for col, value in enumerate(user):
                item = QTableWidgetItem('*' * 8 if col == 1 else str(value))
                self.table.setItem(row, col, item)

    def add_user(self):
        dialog = UserDialog(self)
        if dialog.exec() == QDialog.DialogCode.Accepted:
            conn = self.db.connect()
            cursor = conn.cursor()
            try:
                cursor.execute('''
                    INSERT INTO users (username, password, role)
                    VALUES (?, ?, ?)
                ''', (dialog.username.text(), dialog.password.text(),
                      dialog.role_combo.currentText()))
                conn.commit()
                self.load_users()
                QMessageBox.information(self, "成功", "用户添加成功")
            except sqlite3.IntegrityError:
                QMessageBox.warning(self, "错误", "用户名已存在")
            finally:
                self.db.close()

    def edit_user(self):
        selected_rows = self.table.selectedItems()
        if not selected_rows:
            QMessageBox.warning(self, "错误", "请选择要修改的用户")
            return

        row = selected_rows[0].row()
        user_data = [self.table.item(row, col).text() for col in range(3)]
        if user_data[0] == 'admin':
            QMessageBox.warning(self, "错误", "不能修改管理员账户")
            return

        dialog = UserDialog(self, user_data)
        if dialog.exec() == QDialog.DialogCode.Accepted:
            conn = self.db.connect()
            cursor = conn.cursor()
            cursor.execute('''
                UPDATE users 
                SET password=?, role=?
                WHERE username=?
            ''', (dialog.password.text(), dialog.role_combo.currentText(),
                  dialog.username.text()))
            conn.commit()
            self.db.close()
            self.load_users()
            QMessageBox.information(self, "成功", "用户修改成功")

    def delete_user(self):
        selected_rows = self.table.selectedItems()
        if not selected_rows:
            QMessageBox.warning(self, "错误", "请选择要删除的用户")
            return

        row = selected_rows[0].row()
        username = self.table.item(row, 0).text()
        
        if username == 'admin':
            QMessageBox.warning(self, "错误", "不能删除管理员账户")
            return

        reply = QMessageBox.question(self, "确认", 
                                   f"确定要删除用户 {username} 吗？",
                                   QMessageBox.StandardButton.Yes | QMessageBox.StandardButton.No)
        
        if reply == QMessageBox.StandardButton.Yes:
            conn = self.db.connect()
            cursor = conn.cursor()
            cursor.execute('DELETE FROM users WHERE username=?', (username,))
            conn.commit()
            self.db.close()
            self.load_users()
            QMessageBox.information(self, "成功", "用户删除成功") 