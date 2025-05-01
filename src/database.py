import sqlite3
import os
from datetime import datetime

class Database:
    def __init__(self, db_path="database/student.db"):
        self.db_path = db_path
        self.conn = None
        self.cursor = None
        
        # 确保数据库目录存在
        os.makedirs(os.path.dirname(db_path), exist_ok=True)
        
        # 只在数据库文件不存在时创建新数据库
        is_new_db = not os.path.exists(db_path)
        
        self.connect()
        if is_new_db:
            self.create_tables()
            self.init_admin_key()  # 初始化管理员密钥

    def connect(self):
        """连接到数据库"""
        self.conn = sqlite3.connect(self.db_path)
        self.cursor = self.conn.cursor()

    def create_tables(self):
        """创建必要的数据库表"""
        # 用户表
        self.cursor.execute('''
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL,
            user_type TEXT NOT NULL,  -- 'admin' 或 'student'
            created_at TEXT NOT NULL
        )
        ''')

        # 管理员密钥表
        self.cursor.execute('''
        CREATE TABLE IF NOT EXISTS admin_key (
            key TEXT NOT NULL
        )
        ''')

        # 班级表
        self.cursor.execute('''
        CREATE TABLE IF NOT EXISTS classes (
            class_id TEXT PRIMARY KEY,
            class_name TEXT NOT NULL,
            department TEXT NOT NULL,
            grade TEXT NOT NULL,
            head_teacher TEXT,
            created_at TEXT NOT NULL
        )
        ''')

        # 学生信息表
        self.cursor.execute('''
        CREATE TABLE IF NOT EXISTS students (
            student_id TEXT PRIMARY KEY,
            name TEXT NOT NULL,
            gender TEXT NOT NULL,
            birth_date TEXT NOT NULL,
            political_status TEXT,
            address TEXT,
            phone TEXT,
            email TEXT,
            dormitory TEXT,
            class_id TEXT,
            created_at TEXT NOT NULL,
            FOREIGN KEY (class_id) REFERENCES classes (class_id)
        )
        ''')

        # 教师表
        self.cursor.execute('''
        CREATE TABLE IF NOT EXISTS teachers (
            teacher_id TEXT PRIMARY KEY,
            name TEXT NOT NULL,
            gender TEXT NOT NULL,
            birth_date TEXT NOT NULL,
            title TEXT,
            department TEXT NOT NULL,
            phone TEXT,
            email TEXT,
            created_at TEXT NOT NULL
        )
        ''')

        # 课程表
        self.cursor.execute('''
        CREATE TABLE IF NOT EXISTS courses (
            course_id TEXT PRIMARY KEY,
            course_name TEXT NOT NULL,
            credit REAL NOT NULL,
            teacher_id TEXT,
            semester TEXT NOT NULL,
            created_at TEXT NOT NULL,
            FOREIGN KEY (teacher_id) REFERENCES teachers (teacher_id)
        )
        ''')

        # 成绩表
        self.cursor.execute('''
        CREATE TABLE IF NOT EXISTS grades (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            student_id TEXT NOT NULL,
            course_id TEXT NOT NULL,
            score REAL NOT NULL,
            semester TEXT NOT NULL,
            created_at TEXT NOT NULL,
            FOREIGN KEY (student_id) REFERENCES students (student_id),
            FOREIGN KEY (course_id) REFERENCES courses (course_id)
        )
        ''')

        # 初始化管理员密钥
        self.cursor.execute("INSERT INTO admin_key (key) VALUES (?)", ("admin123",))
        
        self.conn.commit()

    def init_admin_key(self):
        """初始化管理员密钥"""
        self.cursor.execute("""
        CREATE TABLE IF NOT EXISTS admin_key (
            key TEXT NOT NULL
        )
        """)
        
        # 检查是否已设置密钥
        self.cursor.execute("SELECT key FROM admin_key")
        if not self.cursor.fetchone():
            # 设置默认管理员密钥
            self.cursor.execute("INSERT INTO admin_key (key) VALUES (?)", 
                              ("admin123",))  # 默认密钥为 admin123
            self.conn.commit()

    def verify_admin_key(self, key):
        """验证管理员密钥"""
        self.cursor.execute("SELECT key FROM admin_key")
        stored_key = self.cursor.fetchone()
        return stored_key and stored_key[0] == key

    def change_admin_key(self, old_key, new_key):
        """修改管理员密钥"""
        if self.verify_admin_key(old_key):
            self.cursor.execute("UPDATE admin_key SET key = ?", (new_key,))
            self.conn.commit()
            return True
        return False

    def add_user(self, username, password, user_type='student'):
        """添加用户"""
        try:
            self.cursor.execute("""
            INSERT INTO users (username, password, user_type, created_at)
            VALUES (?, ?, ?, ?)
            """, (username, password, user_type, datetime.now().strftime('%Y-%m-%d %H:%M:%S')))
            self.conn.commit()
            return True
        except sqlite3.IntegrityError:
            return False

    def verify_user(self, username, password):
        """验证用户"""
        self.cursor.execute("""
        SELECT * FROM users 
        WHERE username = ? AND password = ?
        """, (username, password))
        return self.cursor.fetchone()

    def add_class(self, class_data):
        """添加班级"""
        try:
            self.cursor.execute("""
            INSERT INTO classes 
            (class_id, class_name, department, grade, head_teacher, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """, class_data + (datetime.now().strftime('%Y-%m-%d %H:%M:%S'),))
            self.conn.commit()
            return True
        except sqlite3.IntegrityError:
            return False

    def add_student(self, student_data):
        """添加学生信息"""
        try:
            self.cursor.execute("""
            INSERT INTO students 
            (student_id, name, gender, birth_date, political_status, 
             address, phone, email, dormitory, class_id, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, student_data + (datetime.now().strftime('%Y-%m-%d %H:%M:%S'),))
            self.conn.commit()
            return True
        except sqlite3.IntegrityError:
            return False

    def add_teacher(self, teacher_data):
        """添加教师信息"""
        try:
            self.cursor.execute("""
            INSERT INTO teachers 
            (teacher_id, name, gender, birth_date, title, 
             department, phone, email, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, teacher_data + (datetime.now().strftime('%Y-%m-%d %H:%M:%S'),))
            self.conn.commit()
            return True
        except sqlite3.IntegrityError:
            return False

    def add_course(self, course_data):
        """添加课程"""
        try:
            self.cursor.execute("""
            INSERT INTO courses 
            (course_id, course_name, credit, teacher_id, semester, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """, course_data + (datetime.now().strftime('%Y-%m-%d %H:%M:%S'),))
            self.conn.commit()
            return True
        except sqlite3.IntegrityError:
            return False

    def add_grade(self, grade_data):
        """添加成绩"""
        try:
            self.cursor.execute("""
            INSERT INTO grades 
            (student_id, course_id, score, semester, created_at)
            VALUES (?, ?, ?, ?, ?)
            """, grade_data + (datetime.now().strftime('%Y-%m-%d %H:%M:%S'),))
            self.conn.commit()
            return True
        except sqlite3.Error:
            return False

    def get_student_grades(self, student_id):
        """获取学生所有成绩"""
        self.cursor.execute("""
        SELECT c.course_name, g.score, c.credit, g.semester
        FROM grades g
        JOIN courses c ON g.course_id = c.course_id
        WHERE g.student_id = ?
        """, (student_id,))
        return self.cursor.fetchall()

    def calculate_gpa(self, student_id):
        """计算学生GPA"""
        self.cursor.execute("""
        SELECT SUM(g.score * c.credit) / SUM(c.credit) as gpa
        FROM grades g
        JOIN courses c ON g.course_id = c.course_id
        WHERE g.student_id = ?
        """, (student_id,))
        result = self.cursor.fetchone()
        return result[0] if result[0] is not None else 0.0

    def get_class_students(self, class_id):
        """获取班级所有学生"""
        self.cursor.execute("""
        SELECT * FROM students WHERE class_id = ?
        """, (class_id,))
        return self.cursor.fetchall()

    def get_teacher_courses(self, teacher_id):
        """获取教师所有课程"""
        self.cursor.execute("""
        SELECT * FROM courses WHERE teacher_id = ?
        """, (teacher_id,))
        return self.cursor.fetchall()

    def close(self):
        """关闭数据库连接"""
        if self.conn:
            self.conn.close() 