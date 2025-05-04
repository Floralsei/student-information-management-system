package com.studentinfo.dao;

import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private final DatabaseConnection dbConnection;

    public StudentDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (student_id, name, gender, birth_date, major, email, phone) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getGender());
            pstmt.setString(4, student.getBirthDate());
            pstmt.setString(5, student.getMajor());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getPhone());
            pstmt.executeUpdate();
        }
    }

    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Student student = new Student(
                    rs.getInt("id"),
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getString("birth_date"),
                    rs.getString("major"),
                    rs.getString("email"),
                    rs.getString("phone")
                );
                students.add(student);
            }
        }
        return students;
    }

    public Student getStudentById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getInt("id"),
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("gender"),
                        rs.getString("birth_date"),
                        rs.getString("major"),
                        rs.getString("email"),
                        rs.getString("phone")
                    );
                }
            }
        }
        return null;
    }

    public void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET student_id = ?, name = ?, gender = ?, " +
                    "birth_date = ?, major = ?, email = ?, phone = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getGender());
            pstmt.setString(4, student.getBirthDate());
            pstmt.setString(5, student.getMajor());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getPhone());
            pstmt.setInt(8, student.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
} 