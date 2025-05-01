from PyQt5.QtWidgets import (QMainWindow, QWidget, QVBoxLayout, 
                            QHBoxLayout, QLabel, QLineEdit, 
                            QPushButton, QMessageBox, QComboBox,
                            QDialog, QFormLayout)
from PyQt5.QtCore import Qt
from database import Database
from main_window import MainWindow

class LoginWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.db = Database()
        self.init_ui()

    def init_ui(self):
        """初始化UI"""
        self.setWindowTitle('学生信息管理系统 - 登录')
        self.setFixedSize(400, 300)

        # 创建中央部件
        central_widget = QWidget()
        self.setCentralWidget(central_widget)

        # 创建主布局
        layout = QVBoxLayout(central_widget)
        layout.setAlignment(Qt.AlignCenter)

        # 用户类型选择
        user_type_layout = QHBoxLayout()
        user_type_label = QLabel('用户类型:')
        self.user_type_combo = QComboBox()
        self.user_type_combo.addItems(['学生', '管理员'])
        user_type_layout.addWidget(user_type_label)
        user_type_layout.addWidget(self.user_type_combo)

        # 用户名输入
        username_layout = QHBoxLayout()
        username_label = QLabel('用户名:')
        self.username_input = QLineEdit()
        username_layout.addWidget(username_label)
        username_layout.addWidget(self.username_input)

        # 密码输入
        password_layout = QHBoxLayout()
        password_label = QLabel('密码:')
        self.password_input = QLineEdit()
        self.password_input.setEchoMode(QLineEdit.Password)
        password_layout.addWidget(password_label)
        password_layout.addWidget(self.password_input)

        # 按钮布局
        button_layout = QHBoxLayout()
        
        # 登录按钮
        login_button = QPushButton('登录')
        login_button.clicked.connect(self.login)
        
        # 注册按钮
        register_button = QPushButton('注册')
        register_button.clicked.connect(self.show_register_dialog)
        
        button_layout.addWidget(login_button)
        button_layout.addWidget(register_button)

        # 添加所有部件到主布局
        layout.addLayout(user_type_layout)
        layout.addLayout(username_layout)
        layout.addLayout(password_layout)
        layout.addLayout(button_layout)

    def show_register_dialog(self):
        """显示注册对话框"""
        dialog = QDialog(self)
        dialog.setWindowTitle('注册新用户')
        dialog.setFixedSize(300, 250)
        
        layout = QFormLayout(dialog)
        
        # 用户类型选择
        user_type_combo = QComboBox()
        user_type_combo.addItems(['学生', '管理员'])
        layout.addRow('用户类型:', user_type_combo)
        
        # 用户名输入
        username_input = QLineEdit()
        layout.addRow('用户名:', username_input)
        
        # 密码输入
        password_input = QLineEdit()
        password_input.setEchoMode(QLineEdit.Password)
        layout.addRow('密码:', password_input)
        
        # 确认密码输入
        confirm_password_input = QLineEdit()
        confirm_password_input.setEchoMode(QLineEdit.Password)
        layout.addRow('确认密码:', confirm_password_input)
        
        # 管理员密钥输入（默认隐藏）
        admin_key_input = QLineEdit()
        admin_key_input.setEchoMode(QLineEdit.Password)
        admin_key_input.setVisible(False)
        layout.addRow('管理员密钥:', admin_key_input)
        
        # 当用户类型改变时，显示或隐藏管理员密钥输入框
        def toggle_admin_key():
            admin_key_input.setVisible(user_type_combo.currentText() == '管理员')
        
        user_type_combo.currentTextChanged.connect(toggle_admin_key)
        
        # 注册按钮
        register_button = QPushButton('注册')
        register_button.clicked.connect(lambda: self.register(
            'admin' if user_type_combo.currentText() == '管理员' else 'student',
            username_input.text(),
            password_input.text(),
            confirm_password_input.text(),
            admin_key_input.text(),
            dialog
        ))
        layout.addRow(register_button)
        
        dialog.exec_()

    def register(self, user_type, username, password, confirm_password, admin_key, dialog):
        """处理注册逻辑"""
        if not username or not password:
            QMessageBox.warning(dialog, '错误', '用户名和密码不能为空！')
            return
            
        if password != confirm_password:
            QMessageBox.warning(dialog, '错误', '两次输入的密码不一致！')
            return
            
        # 如果是管理员注册，验证密钥
        if user_type == 'admin':
            if not admin_key:
                QMessageBox.warning(dialog, '错误', '请输入管理员密钥！')
                return
            if not self.db.verify_admin_key(admin_key):
                QMessageBox.warning(dialog, '错误', '管理员密钥错误！')
                return
            
        if self.db.add_user(username, password, user_type):
            QMessageBox.information(dialog, '成功', '注册成功！')
            dialog.accept()
        else:
            QMessageBox.warning(dialog, '错误', '用户名已存在！')

    def login(self):
        """处理登录逻辑"""
        username = self.username_input.text()
        password = self.password_input.text()
        user_type = 'admin' if self.user_type_combo.currentText() == '管理员' else 'student'

        if not username or not password:
            QMessageBox.warning(self, '错误', '请输入用户名和密码！')
            return

        user = self.db.verify_user(username, password)
        if user and user[3] == user_type:  # user[3] 是 user_type 字段
            # 关闭登录窗口
            self.hide()
            # 打开主窗口
            self.main_window = MainWindow(username, user_type)
            self.main_window.show()
        else:
            QMessageBox.warning(self, '错误', '用户名或密码错误，或用户类型不匹配！')

    def closeEvent(self, event):
        """关闭窗口时关闭数据库连接"""
        self.db.close()
        event.accept() 