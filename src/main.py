import sys
from PyQt6.QtWidgets import QApplication
from ui.login_window import LoginWindow
from ui.main_window import MainWindow
from database.db_connection import DatabaseConnection

print("程序开始运行...")  # 添加调试信息

class StudentInfoSystem:
    def __init__(self):
        print("初始化系统...")  # 添加调试信息
        self.app = QApplication(sys.argv)
        self.login_window = None
        self.main_window = None
        self.initialize_database()

    def initialize_database(self):
        print("初始化数据库...")  # 添加调试信息
        db = DatabaseConnection()
        db.initialize_database()
        db.close()

    def show_login(self):
        print("显示登录窗口...")  # 添加调试信息
        self.login_window = LoginWindow()
        self.login_window.show()

    def show_main(self, user_role):
        print(f"显示主窗口，用户角色: {user_role}")  # 添加调试信息
        self.main_window = MainWindow(user_role)
        self.main_window.show()
        if self.login_window:
            self.login_window.close()

    def run(self):
        print("运行系统...")  # 添加调试信息
        self.show_login()
        sys.exit(self.app.exec())

if __name__ == '__main__':
    print("创建系统实例...")  # 添加调试信息
    system = StudentInfoSystem()
    system.run() 