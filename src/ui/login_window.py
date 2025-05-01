from PyQt6.QtWidgets import (QMainWindow, QWidget, QVBoxLayout, 
                            QLabel, QLineEdit, QPushButton, QMessageBox)
from PyQt6.QtCore import Qt
from PyQt6.QtGui import QFont
from database.db_connection import DatabaseConnection

class LoginWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("学生信息管理系统 - 登录")
        self.setFixedSize(400, 300)
        self.setup_ui()
        self.db = DatabaseConnection()
        self.main_window = None

    def setup_ui(self):
        central_widget = QWidget()
        self.setCentralWidget(central_widget)
        layout = QVBoxLayout(central_widget)
        layout.setAlignment(Qt.AlignmentFlag.AlignCenter)

        # 标题
        title_label = QLabel("学生信息管理系统")
        title_label.setFont(QFont("Arial", 20, QFont.Weight.Bold))
        title_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        layout.addWidget(title_label)

        # 用户名输入
        self.username_input = QLineEdit()
        self.username_input.setPlaceholderText("用户名")
        self.username_input.setFixedWidth(200)
        layout.addWidget(self.username_input)

        # 密码输入
        self.password_input = QLineEdit()
        self.password_input.setPlaceholderText("密码")
        self.password_input.setEchoMode(QLineEdit.EchoMode.Password)
        self.password_input.setFixedWidth(200)
        layout.addWidget(self.password_input)

        # 登录按钮
        login_button = QPushButton("登录")
        login_button.setFixedWidth(200)
        login_button.clicked.connect(self.login)
        layout.addWidget(login_button)

        # 设置布局间距
        layout.addSpacing(20)

    def login(self):
        username = self.username_input.text()
        password = self.password_input.text()

        if not username or not password:
            QMessageBox.warning(self, "错误", "请输入用户名和密码")
            return

        conn = self.db.connect()
        cursor = conn.cursor()
        cursor.execute('''
            SELECT role FROM users WHERE username = ? AND password = ?
        ''', (username, password))
        
        result = cursor.fetchone()
        self.db.close()

        if result:
            role = result[0]
            QMessageBox.information(self, "成功", f"登录成功！欢迎{role}用户")
            from ui.main_window import MainWindow
            self.main_window = MainWindow(role)
            self.main_window.show()
            self.close()
        else:
            QMessageBox.warning(self, "错误", "用户名或密码错误") 