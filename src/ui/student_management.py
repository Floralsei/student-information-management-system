from PyQt6.QtWidgets import (QWidget, QVBoxLayout, QHBoxLayout, QPushButton,
                            QTableWidget, QTableWidgetItem, QLineEdit, QLabel,
                            QMessageBox, QDialog, QFormLayout, QDateEdit)
from PyQt6.QtCore import Qt, QDate
from database.db_connection import DatabaseConnection

class StudentDialog(QDialog):
    def __init__(self, parent=None, student_data=None):
        super().__init__(parent)
        self.student_data = student_data
        self.setup_ui()

    def setup_ui(self):
        self.setWindowTitle("添加学生" if not self.student_data else "修改学生信息")
        layout = QFormLayout(self)

        self.student_id = QLineEdit()
        self.name = QLineEdit()
        self.gender = QLineEdit()
        self.birth_date = QDateEdit()
        self.birth_date.setCalendarPopup(True)
        self.class_name = QLineEdit()
        self.phone = QLineEdit()
        self.email = QLineEdit()
        self.address = QLineEdit()

        if self.student_data:
            self.student_id.setText(self.student_data[0])
            self.name.setText(self.student_data[1])
            self.gender.setText(self.student_data[2])
            self.birth_date.setDate(QDate.fromString(self.student_data[3], "yyyy-MM-dd"))
            self.class_name.setText(self.student_data[4])
            self.phone.setText(self.student_data[5] or "")
            self.email.setText(self.student_data[6] or "")
            self.address.setText(self.student_data[7] or "")
            self.student_id.setEnabled(False)

        layout.addRow("学号:", self.student_id)
        layout.addRow("姓名:", self.name)
        layout.addRow("性别:", self.gender)
        layout.addRow("出生日期:", self.birth_date)
        layout.addRow("班级:", self.class_name)
        layout.addRow("电话:", self.phone)
        layout.addRow("邮箱:", self.email)
        layout.addRow("地址:", self.address)

        buttons = QHBoxLayout()
        save_button = QPushButton("保存")
        cancel_button = QPushButton("取消")
        save_button.clicked.connect(self.accept)
        cancel_button.clicked.connect(self.reject)
        buttons.addWidget(save_button)
        buttons.addWidget(cancel_button)
        layout.addRow(buttons)

class StudentManagementTab(QWidget):
    def __init__(self):
        super().__init__()
        self.db = DatabaseConnection()
        self.setup_ui()
        self.load_students()

    def setup_ui(self):
        layout = QVBoxLayout(self)

        # 搜索栏
        search_layout = QHBoxLayout()
        self.search_input = QLineEdit()
        self.search_input.setPlaceholderText("输入学号或姓名搜索")
        search_button = QPushButton("搜索")
        search_button.clicked.connect(self.search_students)
        search_layout.addWidget(self.search_input)
        search_layout.addWidget(search_button)
        layout.addLayout(search_layout)

        # 按钮栏
        button_layout = QHBoxLayout()
        add_button = QPushButton("添加学生")
        edit_button = QPushButton("修改信息")
        delete_button = QPushButton("删除学生")
        add_button.clicked.connect(self.add_student)
        edit_button.clicked.connect(self.edit_student)
        delete_button.clicked.connect(self.delete_student)
        button_layout.addWidget(add_button)
        button_layout.addWidget(edit_button)
        button_layout.addWidget(delete_button)
        layout.addLayout(button_layout)

        # 学生列表
        self.table = QTableWidget()
        self.table.setColumnCount(8)
        self.table.setHorizontalHeaderLabels(["学号", "姓名", "性别", "出生日期", "班级", "电话", "邮箱", "地址"])
        self.table.setSelectionBehavior(QTableWidget.SelectionBehavior.SelectRows)
        self.table.setEditTriggers(QTableWidget.EditTrigger.NoEditTriggers)
        layout.addWidget(self.table)

    def load_students(self):
        conn = self.db.connect()
        cursor = conn.cursor()
        cursor.execute('SELECT * FROM students')
        students = cursor.fetchall()
        self.db.close()

        self.table.setRowCount(len(students))
        for row, student in enumerate(students):
            for col, value in enumerate(student[1:]):  # 跳过id列
                item = QTableWidgetItem(str(value) if value else "")
                self.table.setItem(row, col, item)

    def search_students(self):
        search_text = self.search_input.text()
        if not search_text:
            self.load_students()
            return

        conn = self.db.connect()
        cursor = conn.cursor()
        cursor.execute('''
            SELECT * FROM students 
            WHERE student_id LIKE ? OR name LIKE ?
        ''', (f'%{search_text}%', f'%{search_text}%'))
        students = cursor.fetchall()
        self.db.close()

        self.table.setRowCount(len(students))
        for row, student in enumerate(students):
            for col, value in enumerate(student[1:]):
                item = QTableWidgetItem(str(value) if value else "")
                self.table.setItem(row, col, item)

    def add_student(self):
        dialog = StudentDialog(self)
        if dialog.exec() == QDialog.DialogCode.Accepted:
            conn = self.db.connect()
            cursor = conn.cursor()
            try:
                cursor.execute('''
                    INSERT INTO students (student_id, name, gender, birth_date, 
                                        class_name, phone, email, address)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ''', (dialog.student_id.text(), dialog.name.text(), dialog.gender.text(),
                      dialog.birth_date.date().toString("yyyy-MM-dd"), dialog.class_name.text(),
                      dialog.phone.text(), dialog.email.text(), dialog.address.text()))
                conn.commit()
                self.load_students()
                QMessageBox.information(self, "成功", "学生信息添加成功")
            except sqlite3.IntegrityError:
                QMessageBox.warning(self, "错误", "学号已存在")
            finally:
                self.db.close()

    def edit_student(self):
        selected_rows = self.table.selectedItems()
        if not selected_rows:
            QMessageBox.warning(self, "错误", "请选择要修改的学生")
            return

        row = selected_rows[0].row()
        student_data = [self.table.item(row, col).text() for col in range(8)]
        
        dialog = StudentDialog(self, student_data)
        if dialog.exec() == QDialog.DialogCode.Accepted:
            conn = self.db.connect()
            cursor = conn.cursor()
            cursor.execute('''
                UPDATE students 
                SET name=?, gender=?, birth_date=?, class_name=?, 
                    phone=?, email=?, address=?
                WHERE student_id=?
            ''', (dialog.name.text(), dialog.gender.text(),
                  dialog.birth_date.date().toString("yyyy-MM-dd"), dialog.class_name.text(),
                  dialog.phone.text(), dialog.email.text(), dialog.address.text(),
                  dialog.student_id.text()))
            conn.commit()
            self.db.close()
            self.load_students()
            QMessageBox.information(self, "成功", "学生信息修改成功")

    def delete_student(self):
        selected_rows = self.table.selectedItems()
        if not selected_rows:
            QMessageBox.warning(self, "错误", "请选择要删除的学生")
            return

        row = selected_rows[0].row()
        student_id = self.table.item(row, 0).text()
        
        reply = QMessageBox.question(self, "确认", 
                                   f"确定要删除学号为 {student_id} 的学生信息吗？",
                                   QMessageBox.StandardButton.Yes | QMessageBox.StandardButton.No)
        
        if reply == QMessageBox.StandardButton.Yes:
            conn = self.db.connect()
            cursor = conn.cursor()
            cursor.execute('DELETE FROM students WHERE student_id=?', (student_id,))
            conn.commit()
            self.db.close()
            self.load_students()
            QMessageBox.information(self, "成功", "学生信息删除成功") 