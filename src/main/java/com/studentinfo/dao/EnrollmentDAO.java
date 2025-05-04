package com.studentinfo.dao;

import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    private final DatabaseConnection dbConnection;

    public EnrollmentDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void addEnrollment(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id, enrollment_date, grade) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setString(3, enrollment.getEnrollmentDate());
            pstmt.setString(4, enrollment.getGrade());
            pstmt.executeUpdate();
        }
    }

    public List<Enrollment> getAllEnrollments() throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Enrollment enrollment = new Enrollment(
                    rs.getInt("id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getString("enrollment_date"),
                    rs.getString("grade")
                );
                enrollments.add(enrollment);
            }
        }
        return enrollments;
    }

    public Enrollment getEnrollmentById(int id) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Enrollment(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("course_id"),
                        rs.getString("enrollment_date"),
                        rs.getString("grade")
                    );
                }
            }
        }
        return null;
    }

    public void updateEnrollment(Enrollment enrollment) throws SQLException {
        String sql = "UPDATE enrollments SET student_id = ?, course_id = ?, " +
                    "enrollment_date = ?, grade = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setString(3, enrollment.getEnrollmentDate());
            pstmt.setString(4, enrollment.getGrade());
            pstmt.setInt(5, enrollment.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteEnrollment(int id) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Enrollment> getEnrollmentsByStudentId(int studentId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE student_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Enrollment enrollment = new Enrollment(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("course_id"),
                        rs.getString("enrollment_date"),
                        rs.getString("grade")
                    );
                    enrollments.add(enrollment);
                }
            }
        }
        return enrollments;
    }

    public List<Enrollment> getEnrollmentsByCourseId(int courseId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE course_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Enrollment enrollment = new Enrollment(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("course_id"),
                        rs.getString("enrollment_date"),
                        rs.getString("grade")
                    );
                    enrollments.add(enrollment);
                }
            }
        }
        return enrollments;
    }
} 