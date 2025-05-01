from PyQt6.QtWidgets import (QWidget, QVBoxLayout, QHBoxLayout, QPushButton,
                            QTableWidget, QTableWidgetItem, QLineEdit, QLabel,
                            QMessageBox, QDialog, QFormLayout, QComboBox)
from PyQt6.QtCore import Qt
from database.db_connection import DatabaseConnection

class GradeDialog(QDialog):
    def __init__(self, parent=None, grade_data=None):
        super().__init__(parent)
        self.grade_data = grade_data
        self.db = DatabaseConnection()
        self.setup_ui()

    def setup_ui(self):
        self.setWindowTitle("录入成绩" if not self.grade_data else "修改成绩")
        layout = QFormLayout(self)

        # 获取所有学生
        conn = self.db.connect()
        cursor = conn.cursor()
        cursor.execute('SELECT student_id, name FROM students')
        students = cursor.fetchall()
        self.db.close()

        # 学生选择下拉框
        self.student_combo = QComboBox()
        for student_id, name in students:
            self.student_combo.addItem(f"{student_id} - {name}", student_id)
        
        self.subject = QLineEdit()
        self.score = QLineEdit()
        self.semester = QLineEdit()

        if self.grade_data:
            # 设置当前选中的学生
            index = self.student_combo.findData(self.grade_data[0])
            if index >= 0:
                self.student_combo.setCurrentIndex(index)
            self.subject.setText(self.grade_data[1])
            self.score.setText(str(self.grade_data[2]))
            self.semester.setText(self.grade_data[3])
            self.student_combo.setEnabled(False)
            self.subject.setEnabled(False)
            self.semester.setEnabled(False)

        layout.addRow("学生:", self.student_combo)
        layout.addRow("科目:", self.subject)
        layout.addRow("分数:", self.score)
        layout.addRow("学期:", self.semester)

        buttons = QHBoxLayout()
        save_button = QPushButton("保存")
        cancel_button = QPushButton("取消")
        save_button.clicked.connect(self.accept)
        cancel_button.clicked.connect(self.reject)
        buttons.addWidget(save_button)
        buttons.addWidget(cancel_button)
        layout.addRow(buttons)

class GradeManagementTab(QWidget):
    def __init__(self):
        super().__init__()
        self.db = DatabaseConnection()
        self.setup_ui()
        self.load_grades()

    def setup_ui(self):
        layout = QVBoxLayout(self)

        # 搜索栏
        search_layout = QHBoxLayout()
        self.search_input = QLineEdit()
        self.search_input.setPlaceholderText("输入学号或姓名搜索")
        search_button = QPushButton("搜索")
        search_button.clicked.connect(self.search_grades)
        search_layout.addWidget(self.search_input)
        search_layout.addWidget(search_button)
        layout.addLayout(search_layout)

        # 按钮栏
        button_layout = QHBoxLayout()
        add_button = QPushButton("录入成绩")
        edit_button = QPushButton("修改成绩")
        delete_button = QPushButton("删除成绩")
        rank_button = QPushButton("成绩排名")
        add_button.clicked.connect(self.add_grade)
        edit_button.clicked.connect(self.edit_grade)
        delete_button.clicked.connect(self.delete_grade)
        rank_button.clicked.connect(self.show_rank)
        button_layout.addWidget(add_button)
        button_layout.addWidget(edit_button)
        button_layout.addWidget(delete_button)
        button_layout.addWidget(rank_button)
        layout.addLayout(button_layout)

        # 成绩列表
        self.table = QTableWidget()
        self.table.setColumnCount(5)
        self.table.setHorizontalHeaderLabels(["学号", "姓名", "科目", "分数", "学期"])
        self.table.setSelectionBehavior(QTableWidget.SelectionBehavior.SelectRows)
        self.table.setSelectionMode(QTableWidget.SelectionMode.MultiSelection)  # 允许多选
        self.table.setEditTriggers(QTableWidget.EditTrigger.NoEditTriggers)
        layout.addWidget(self.table)

    def load_grades(self):
        conn = self.db.connect()
        cursor = conn.cursor()
        cursor.execute('''
            SELECT g.student_id, s.name, g.subject, g.score, g.semester
            FROM grades g
            JOIN students s ON g.student_id = s.student_id
        ''')
        grades = cursor.fetchall()
        self.db.close()

        self.table.setRowCount(len(grades))
        for row, grade in enumerate(grades):
            for col, value in enumerate(grade):
                item = QTableWidgetItem(str(value))
                self.table.setItem(row, col, item)

    def search_grades(self):
        search_text = self.search_input.text()
        if not search_text:
            self.load_grades()
            return

        conn = self.db.connect()
        cursor = conn.cursor()
        cursor.execute('''
            SELECT g.student_id, s.name, g.subject, g.score, g.semester
            FROM grades g
            JOIN students s ON g.student_id = s.student_id
            WHERE g.student_id LIKE ? OR s.name LIKE ?
        ''', (f'%{search_text}%', f'%{search_text}%'))
        grades = cursor.fetchall()
        self.db.close()

        self.table.setRowCount(len(grades))
        for row, grade in enumerate(grades):
            for col, value in enumerate(grade):
                item = QTableWidgetItem(str(value))
                self.table.setItem(row, col, item)

    def add_grade(self):
        dialog = GradeDialog(self)
        if dialog.exec() == QDialog.DialogCode.Accepted:
            conn = self.db.connect()
            cursor = conn.cursor()
            try:
                cursor.execute('''
                    INSERT INTO grades (student_id, subject, score, semester)
                    VALUES (?, ?, ?, ?)
                ''', (dialog.student_combo.currentData(), dialog.subject.text(),
                      float(dialog.score.text()), dialog.semester.text()))
                conn.commit()
                self.load_grades()
                QMessageBox.information(self, "成功", "成绩录入成功")
            except ValueError:
                QMessageBox.warning(self, "错误", "请输入有效的分数")
            except sqlite3.IntegrityError:
                QMessageBox.warning(self, "错误", "该学生该科目的成绩已存在")
            finally:
                self.db.close()

    def edit_grade(self):
        selected_rows = self.table.selectedItems()
        if not selected_rows:
            QMessageBox.warning(self, "错误", "请选择要修改的成绩")
            return

        row = selected_rows[0].row()
        grade_data = [self.table.item(row, col).text() for col in range(5)]
        
        dialog = GradeDialog(self, grade_data)
        if dialog.exec() == QDialog.DialogCode.Accepted:
            conn = self.db.connect()
            cursor = conn.cursor()
            try:
                cursor.execute('''
                    UPDATE grades 
                    SET score=?
                    WHERE student_id=? AND subject=? AND semester=?
                ''', (float(dialog.score.text()), dialog.student_combo.currentData(),
                      dialog.subject.text(), dialog.semester.text()))
                conn.commit()
                self.db.close()
                self.load_grades()
                QMessageBox.information(self, "成功", "成绩修改成功")
            except ValueError:
                QMessageBox.warning(self, "错误", "请输入有效的分数")

    def delete_grade(self):
        selected_rows = self.table.selectedItems()
        if not selected_rows:
            QMessageBox.warning(self, "错误", "请选择要删除的成绩")
            return

        # 获取所有选中的行
        selected_rows_set = set()
        for item in selected_rows:
            selected_rows_set.add(item.row())
        
        # 获取要删除的成绩信息
        grades_to_delete = []
        for row in selected_rows_set:
            student_id = self.table.item(row, 0).text()
            subject = self.table.item(row, 2).text()
            semester = self.table.item(row, 4).text()
            grades_to_delete.append((student_id, subject, semester))

        # 确认删除
        reply = QMessageBox.question(self, "确认", 
                                   f"确定要删除选中的 {len(grades_to_delete)} 条成绩记录吗？",
                                   QMessageBox.StandardButton.Yes | QMessageBox.StandardButton.No)
        
        if reply == QMessageBox.StandardButton.Yes:
            conn = self.db.connect()
            cursor = conn.cursor()
            try:
                for student_id, subject, semester in grades_to_delete:
                    cursor.execute('''
                        DELETE FROM grades 
                        WHERE student_id=? AND subject=? AND semester=?
                    ''', (student_id, subject, semester))
                conn.commit()
                self.load_grades()
                QMessageBox.information(self, "成功", f"成功删除 {len(grades_to_delete)} 条成绩记录")
            finally:
                self.db.close()

    def show_rank(self):
        conn = self.db.connect()
        cursor = conn.cursor()
        cursor.execute('''
            SELECT g.student_id, s.name, g.subject, g.score, g.semester
            FROM grades g
            JOIN students s ON g.student_id = s.student_id
            ORDER BY g.score DESC
        ''')
        grades = cursor.fetchall()
        self.db.close()

        self.table.setRowCount(len(grades))
        for row, grade in enumerate(grades):
            for col, value in enumerate(grade):
                item = QTableWidgetItem(str(value))
                self.table.setItem(row, col, item) 