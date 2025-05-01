from PyQt5.QtWidgets import (QMainWindow, QWidget, QVBoxLayout, QHBoxLayout,
                            QPushButton, QLabel, QStackedWidget, QMessageBox)
from PyQt5.QtCore import Qt
from database import Database

class MainWindow(QMainWindow):
    def __init__(self, username, user_type):
        super().__init__()
        self.username = username
        self.user_type = user_type
        self.db = Database()
        self.init_ui()

    def init_ui(self):
        """初始化UI"""
        self.setWindowTitle(f'学生信息管理系统 - {self.username}')
        self.setFixedSize(800, 600)

        # 创建中央部件
        central_widget = QWidget()
        self.setCentralWidget(central_widget)

        # 创建主布局
        main_layout = QHBoxLayout(central_widget)

        # 创建左侧菜单
        menu_widget = QWidget()
        menu_layout = QVBoxLayout(menu_widget)
        menu_layout.setAlignment(Qt.AlignTop)

        # 根据用户类型显示不同的菜单
        if self.user_type == 'admin':
            # 管理员菜单
            self.add_menu_button(menu_layout, '学生管理', self.show_student_management)
            self.add_menu_button(menu_layout, '班级管理', self.show_class_management)
            self.add_menu_button(menu_layout, '教师管理', self.show_teacher_management)
            self.add_menu_button(menu_layout, '课程管理', self.show_course_management)
            self.add_menu_button(menu_layout, '成绩管理', self.show_grade_management)
        else:
            # 学生菜单
            self.add_menu_button(menu_layout, '个人信息', self.show_student_info)
            self.add_menu_button(menu_layout, '课程信息', self.show_course_info)
            self.add_menu_button(menu_layout, '成绩查询', self.show_grade_info)

        # 添加退出按钮
        self.add_menu_button(menu_layout, '退出登录', self.logout)

        # 创建右侧内容区域
        self.content_widget = QStackedWidget()
        
        # 添加菜单和内容到主布局
        main_layout.addWidget(menu_widget, 1)
        main_layout.addWidget(self.content_widget, 4)

    def add_menu_button(self, layout, text, callback):
        """添加菜单按钮"""
        button = QPushButton(text)
        button.setFixedHeight(40)
        button.clicked.connect(callback)
        layout.addWidget(button)

    def show_student_management(self):
        """显示学生管理界面"""
        QMessageBox.information(self, '提示', '学生管理功能开发中...')

    def show_class_management(self):
        """显示班级管理界面"""
        QMessageBox.information(self, '提示', '班级管理功能开发中...')

    def show_teacher_management(self):
        """显示教师管理界面"""
        QMessageBox.information(self, '提示', '教师管理功能开发中...')

    def show_course_management(self):
        """显示课程管理界面"""
        QMessageBox.information(self, '提示', '课程管理功能开发中...')

    def show_grade_management(self):
        """显示成绩管理界面"""
        QMessageBox.information(self, '提示', '成绩管理功能开发中...')

    def show_student_info(self):
        """显示学生个人信息"""
        QMessageBox.information(self, '提示', '个人信息功能开发中...')

    def show_course_info(self):
        """显示课程信息"""
        QMessageBox.information(self, '提示', '课程信息功能开发中...')

    def show_grade_info(self):
        """显示成绩信息"""
        QMessageBox.information(self, '提示', '成绩查询功能开发中...')

    def logout(self):
        """退出登录"""
        self.db.close()
        self.close()

    def closeEvent(self, event):
        """关闭窗口时关闭数据库连接"""
        self.db.close()
        event.accept() 