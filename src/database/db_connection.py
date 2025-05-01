import sqlite3
import os
from pathlib import Path

class DatabaseConnection:
    def __init__(self):
        self.db_path = Path(__file__).parent / 'student_info.db'
        self.conn = None
        self.cursor = None

    def connect(self):
        self.conn = sqlite3.connect(self.db_path)
        self.cursor = self.conn.cursor()
        return self.conn

    def close(self):
        if self.conn:
            self.conn.close()

    def initialize_database(self):
        self.connect()
        
        # 创建用户表
        self.cursor.execute('''
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL,
            role TEXT NOT NULL
        )
        ''')

        # 创建学生信息表
        self.cursor.execute('''
        CREATE TABLE IF NOT EXISTS students (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            student_id TEXT UNIQUE NOT NULL,
            name TEXT NOT NULL,
            gender TEXT NOT NULL,
            birth_date TEXT NOT NULL,
            class_name TEXT NOT NULL,
            phone TEXT,
            email TEXT,
            address TEXT
        )
        ''')

        # 创建成绩表
        self.cursor.execute('''
        CREATE TABLE IF NOT EXISTS grades (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            student_id TEXT NOT NULL,
            subject TEXT NOT NULL,
            score REAL NOT NULL,
            semester TEXT NOT NULL,
            FOREIGN KEY (student_id) REFERENCES students(student_id)
        )
        ''')

        # 创建默认管理员账户
        self.cursor.execute('''
        INSERT OR IGNORE INTO users (username, password, role)
        VALUES (?, ?, ?)
        ''', ('admin', 'admin123', 'admin'))

        self.conn.commit()
        self.close()

if __name__ == '__main__':
    db = DatabaseConnection()
    db.initialize_database() 