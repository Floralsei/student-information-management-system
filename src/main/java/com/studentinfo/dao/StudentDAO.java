package com.studentinfo.dao;

import com.studentinfo.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private final Connection connection;

    public StudentDAO(Connection connection) {
        this.connection = connection;
    }

    public void addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (student_number, name, gender, birth_date, address, phone, email, enrollment_date, status, political_status, dormitory) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, student.getStudentNumber());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getGender());
            stmt.setDate(4, Date.valueOf(student.getBirthDate()));
            stmt.setString(5, student.getAddress());
            stmt.setString(6, student.getPhone());
            stmt.setString(7, student.getEmail());
            stmt.setDate(8, Date.valueOf(student.getEnrollmentDate()));
            stmt.setString(9, student.getStatus());
            stmt.setString(10, student.getPoliticalStatus());
            stmt.setString(11, student.getDormitory());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    student.setId(rs.getInt(1));
                }
            }
        }
    }

    public void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET student_number = ?, name = ?, gender = ?, birth_date = ?, address = ?, phone = ?, email = ?, enrollment_date = ?, status = ?, political_status = ?, dormitory = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentNumber());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getGender());
            stmt.setDate(4, Date.valueOf(student.getBirthDate()));
            stmt.setString(5, student.getAddress());
            stmt.setString(6, student.getPhone());
            stmt.setString(7, student.getEmail());
            stmt.setDate(8, Date.valueOf(student.getEnrollmentDate()));
            stmt.setString(9, student.getStatus());
            stmt.setString(10, student.getPoliticalStatus());
            stmt.setString(11, student.getDormitory());
            stmt.setInt(12, student.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Student getStudentById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractStudentFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public Student getStudentByNumber(String studentNumber) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractStudentFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Student> getAllStudents() throws SQLException {
        String sql = "SELECT * FROM students";
        List<Student> students = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
        }
        return students;
    }

    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setStudentNumber(rs.getString("student_number"));
        student.setName(rs.getString("name"));
        student.setGender(rs.getString("gender"));
        student.setBirthDate(rs.getDate("birth_date").toLocalDate());
        student.setAddress(rs.getString("address"));
        student.setPhone(rs.getString("phone"));
        student.setEmail(rs.getString("email"));
        student.setEnrollmentDate(rs.getDate("enrollment_date").toLocalDate());
        student.setStatus(rs.getString("status"));
        student.setPoliticalStatus(rs.getString("political_status"));
        student.setDormitory(rs.getString("dormitory"));
        return student;
    }

    public List<Student> getStudentsByStatus(String status) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE status = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Student student = extractStudentFromResultSet(rs);
                    students.add(student);
                }
            }
        }
        return students;
    }

    public Student getStudentByUserId(int userId) throws SQLException {
        String sql = "SELECT s.* FROM students s JOIN users u ON s.student_number = u.username WHERE u.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractStudentFromResultSet(rs);
                }
            }
        }
        return null;
    }
} 