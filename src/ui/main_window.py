from PyQt6.QtWidgets import (QMainWindow, QWidget, QVBoxLayout, QHBoxLayout,
                            QPushButton, QLabel, QTabWidget, QMessageBox)
from PyQt6.QtCore import Qt
from PyQt6.QtGui import QFont, QAction
from .student_management import StudentManagementTab
from .grade_management import GradeManagementTab
from .user_management import UserManagementDialog

class MainWindow(QMainWindow):
    def __init__(self, user_role):
        super().__init__()
        self.user_role = user_role
        self.setWindowTitle("学生信息管理系统")
        self.setMinimumSize(800, 600)
        self.setup_ui()
        self.setup_menu()

    def setup_ui(self):
        central_widget = QWidget()
        self.setCentralWidget(central_widget)
        main_layout = QVBoxLayout(central_widget)

        # 顶部欢迎信息
        welcome_label = QLabel(f"欢迎使用学生信息管理系统 - 当前用户：{self.user_role}")
        welcome_label.setFont(QFont("Arial", 12))
        welcome_label.setAlignment(Qt.AlignmentFlag.AlignCenter)
        main_layout.addWidget(welcome_label)

        # 创建选项卡
        tab_widget = QTabWidget()
        main_layout.addWidget(tab_widget)

        # 添加学生管理选项卡（仅管理员可见）
        if self.user_role == 'admin':
            student_tab = StudentManagementTab()
            tab_widget.addTab(student_tab, "学生信息管理")

        # 添加成绩管理选项卡
        grade_tab = GradeManagementTab()
        tab_widget.addTab(grade_tab, "成绩管理")

    def setup_menu(self):
        menubar = self.menuBar()
        
        # 文件菜单
        file_menu = menubar.addMenu("文件")
        
        # 添加用户管理菜单项（仅管理员可见）
        if self.user_role == 'admin':
            user_management_action = QAction("用户管理", self)
            user_management_action.triggered.connect(self.show_user_management)
            file_menu.addAction(user_management_action)
            file_menu.addSeparator()

        logout_action = QAction("退出登录", self)
        logout_action.triggered.connect(self.logout)
        file_menu.addAction(logout_action)

        # 帮助菜单
        help_menu = menubar.addMenu("帮助")
        about_action = QAction("关于", self)
        about_action.triggered.connect(self.show_about)
        help_menu.addAction(about_action)

    def show_user_management(self):
        dialog = UserManagementDialog(self)
        dialog.exec()

    def logout(self):
        reply = QMessageBox.question(self, "确认", "确定要退出登录吗？",
                                   QMessageBox.StandardButton.Yes | QMessageBox.StandardButton.No)
        if reply == QMessageBox.StandardButton.Yes:
            from ui.login_window import LoginWindow
            self.login_window = LoginWindow()
            self.login_window.show()
            self.close()

    def show_about(self):
        QMessageBox.about(self, "关于",
                         "学生信息管理系统 v1.0\n\n"
                         "基于Python和PyQt6开发\n"
                         "用于管理学生信息和成绩") 